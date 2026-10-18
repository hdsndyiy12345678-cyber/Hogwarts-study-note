# Hogwarts Study Diary — Responsive Tablet Edition v1.0

An unofficial personal fantasy-academic study notebook for Android tablets. Unlike earlier editions, this is a **responsive interface** inspired by approved mockups, not those mockup screenshots pasted into the UI. Portrait/landscape and 4:3/16:10 display sizes reflow the same interactive components.

## Features
- 23 original notebook categories, date-scoped daily / weekly / monthly pages
- Tablet-responsive navy-gold parchment layout, separate icon/texture/paper illustrations, and 8 original stationery preview thumbnails
- S Pen strokes on a per-page canvas; finger input disabled by default and toggleable from toolbar and settings
- Ink colors, pen width, erase strokes, undo and redo
- SELECT mode to use checkboxes/fields. Done tasks award +5 points, reversal removes those points in the same ledger
- House-points display, local simulated Owl Post, study-time log, offline SQLite storage, JSON backup and restore
- Android back returns to index instead of closing the application

## Build
Open **Actions → Android APK** and download the newest successful `HogwartsStudyDiary-android-debug` artifact. Install app-debug.apk. Debug APKs use a GitHub Actions signing certificate and may be incompatible with prior manually re-signed versions: back up data before replacing them.

## Note
This is a work-in-progress, device S Pen calibration needs on-device review; built-in Owl Post is fictional local content, not a live messaging service. No official affiliation with the Harry Potter copyright owners.
