package com.elena.hogwartsdiary;
import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.widget.FrameLayout;
import android.view.ViewGroup;
import android.util.Base64;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
/** Offline tablet diary: UI lives in bundled assets and notebook state lives in SQLite. */
public final class MainActivity extends Activity {
 private static final String HOST="appassets.androidplatform.net";
 private static final String BASE="https://appassets.androidplatform.net/assets/index.html";
 private static final int EXPORT=709, IMPORT=710;
 private WebView view;
 private Store db;
 private String exportPending;
 static final class Store extends SQLiteOpenHelper {
  Store(Context context){super(context,"hogwarts_study_diary.db",null,1);}
  public void onCreate(SQLiteDatabase db){db.execSQL("CREATE TABLE IF NOT EXISTS snapshot (id TEXT PRIMARY KEY, content TEXT NOT NULL, updated_at INTEGER NOT NULL)");}
  public void onUpgrade(SQLiteDatabase db,int from,int to){}
  synchronized String read(){
   try(Cursor c=getReadableDatabase().rawQuery("SELECT content FROM snapshot WHERE id='main'",null)){return c.moveToFirst()?c.getString(0):"";}
   catch(Exception e){android.util.Log.e("Diary","read",e);return "";}
  }
  synchronized boolean write(String s){
   if(s==null||s.length()>24*1024*1024)return false;
   SQLiteDatabase db=getWritableDatabase();db.beginTransaction();
   try{ContentValues v=new ContentValues();v.put("id","main");v.put("content",s);v.put("updated_at",System.currentTimeMillis());
    if(db.insertWithOnConflict("snapshot",null,v,SQLiteDatabase.CONFLICT_REPLACE)<0)return false;
    db.setTransactionSuccessful();return true;
   }catch(Exception e){android.util.Log.e("Diary","write",e);return false;}
   finally{db.endTransaction();}
  }
 }
 private final class Bridge {
  @JavascriptInterface public String readState(){return db.read();}
  @JavascriptInterface public boolean saveState(String s){return db.write(s);}
  @JavascriptInterface public void exportJson(String s){runOnUiThread(()->{
   exportPending=s;Intent i=new Intent(Intent.ACTION_CREATE_DOCUMENT);i.addCategory(Intent.CATEGORY_OPENABLE);i.setType("application/json");i.putExtra(Intent.EXTRA_TITLE,"Elena_Diary_Backup.json");startActivityForResult(i,EXPORT);});}
  @JavascriptInterface public void importJson(){runOnUiThread(()->{
   Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);i.addCategory(Intent.CATEGORY_OPENABLE);i.setType("application/json");startActivityForResult(i,IMPORT);});}
 }
 public void onCreate(Bundle state){super.onCreate(state);db=new Store(this);
  view=new WebView(this);view.setBackgroundColor(0xff132923);
  view.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
  setContentView(view);
  WebSettings s=view.getSettings();s.setJavaScriptEnabled(true);s.setDomStorageEnabled(true);
  s.setAllowFileAccess(false);s.setAllowContentAccess(false);s.setSupportZoom(false);
  s.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);s.setJavaScriptCanOpenWindowsAutomatically(false);
  view.addJavascriptInterface(new Bridge(),"Android");
  view.setWebChromeClient(new WebChromeClient());
  view.setWebViewClient(new WebViewClient(){
   public WebResourceResponse shouldInterceptRequest(WebView w,WebResourceRequest r){
    Uri u=r.getUrl();if(!"https".equals(u.getScheme())||!HOST.equals(u.getHost()))return null;
    String p=u.getPath();if(p==null||!p.startsWith("/assets/")||p.contains(".."))return null;
    String file=p.substring(8);String m=file.endsWith(".js")?"application/javascript":file.endsWith(".css")?"text/css":file.endsWith(".svg")?"image/svg+xml":file.endsWith(".webp")?"image/webp":"text/html";
    try{return new WebResourceResponse(m,"UTF-8",getAssets().open(file));}
    catch(Exception e){return new WebResourceResponse("text/plain","UTF-8",404,"Not Found",java.util.Collections.emptyMap(),new ByteArrayInputStream(new byte[0]));}
   }
   public boolean shouldOverrideUrlLoading(WebView w,WebResourceRequest r){
    Uri u=r.getUrl();return !("https".equals(u.getScheme())&&HOST.equals(u.getHost())&&u.getPath()!=null&&u.getPath().startsWith("/assets/"));
   }
  });
  view.loadUrl(BASE);
 }
 protected void onActivityResult(int requestCode,int resultCode,Intent data){super.onActivityResult(requestCode,resultCode,data);
  if(resultCode!=RESULT_OK||data==null||data.getData()==null){exportPending=null;return;}
  try{
   if(requestCode==EXPORT&&exportPending!=null){try(OutputStream out=getContentResolver().openOutputStream(data.getData())){if(out!=null)out.write(exportPending.getBytes(StandardCharsets.UTF_8));}exportPending=null;}
   else if(requestCode==IMPORT){try(InputStream in=getContentResolver().openInputStream(data.getData())){
    if(in==null)return;ByteArrayOutputStream bytes=new ByteArrayOutputStream();byte[] chunk=new byte[8192];int n,all=0;
    while((n=in.read(chunk))!=-1){all+=n;if(all>12000000)throw new IOException("Backup too large");bytes.write(chunk,0,n);}
    String enc=Base64.encodeToString(bytes.toByteArray(),Base64.NO_WRAP);
    view.evaluateJavascript("window.receiveNativeImport('"+enc+"')",null);
   }}
  }catch(Exception e){android.util.Log.e("Diary","file",e);}
 }
 @Override public void onBackPressed(){
  if(view!=null)view.evaluateJavascript("window.appGoBack && window.appGoBack()",null);
 }
 @Override public void onPause(){if(view!=null)view.evaluateJavascript("window.flush && window.flush()",null);super.onPause();}
 @Override public void onDestroy(){if(view!=null){view.removeJavascriptInterface("Android");view.destroy();view=null;}if(db!=null)db.close();super.onDestroy();}
}
