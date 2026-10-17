# Hogwarts Study Diary — Android Tablet

An original, unofficial fantasy-school study diary app for Android tablets. 23 PDF-inspired editable page types, handwriting and typing input, owl post, house point transaction history, virtual classmates, and offline local storage.

## APK
Open **Actions → Android APK → latest successful run → HogwartsStudyDiary-android-debug**, download the ZIP artifact and install `app-debug.apk`. Android may ask permission to install apps from your downloads app.

## Technology
Java WebView shell, bundled HTML/CSS/JavaScript, local SQLite snapshot, JSON export/import, offline by default. No cloud connection or external services required. Data is device local; export a backup before uninstalling.

## Design
The source PDF is treated as an editorial/visual reference. Forms are rendered as interactive controls; handwritten examples from the scanned PDF are not imported. The visual design uses parchment, dark wood, antique gold, and a navy ribbon. This project is not affiliated with Warner Bros. or the Harry Potter franchise.

## Implementation status
This is an experimental debug build. Handwriting needs real Samsung S Pen testing, and the script's simulated letters and notes are not live AI or real user messages.

## v0.3 changes
Daily priorities are now distinct from other tasks (maximum 3); focus timer logs measured minutes without awarding house points; the full-page writing mode enlarges the central paper. The separate image-rich source bundle contains 19 approved visual assets. GitHub APK uses SVG fallbacks until those binaries are uploaded to `app/src/main/assets/img/`.
