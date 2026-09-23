# 📚 Plottwist (discontinued/firebase disabled)

**Discover & Exchange** — an Android app for finding books and trading them with other users.

Plottwist lets you search for books by ISBN, author, or title using the Google Books API, save them to your personal reading list, and share or trade them through a shared community list backed by Firebase.

## Features

- **Book search** — look up books by ISBN, author, or title via the Google Books API
- **Personal list** — save books locally to your device (persisted with `SharedPreferences`)
- **Community trading list** — upload books to a shared Firebase Realtime Database so others can find and trade for them
- **Availability check** — see at a glance whether a book you found is already in the shared trading pool
- **Bulk upload** — push your entire personal list to the shared list in one tap
- **Built with Jetpack Compose** — modern, reactive UI with a custom dark navy/gold theme

## Tech stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose (Material 3) |
| Navigation | Navigation Compose |
| Networking | OkHttp |
| JSON parsing | Moshi (API responses), Gson (local storage) |
| Image loading | Coil |
| Remote data | Firebase Realtime Database |
| Local storage | Android `SharedPreferences` |
| Book data source | [Google Books API](https://developers.google.com/books) |

## Project structure

```
app/src/main/java/
├── API_Handling/        # Google Books API client, request/response models, JSON parsing
├── FirePain/             # Firebase Realtime Database access layer
├── UserView/             # Local book list storage + navigation graph
└── com/example/plottwist/
    ├── MainActivity.kt   # App entry point, search screen
    ├── UserView/MyBookScreen.kt  # "My List" screen
    └── ui/theme/         # Colors, typography, decorative UI elements
```

## Getting started

### Prerequisites

- [Android Studio](https://developer.android.com/studio) (Ladybug or newer recommended)
- JDK 11
- A Firebase project with **Realtime Database** enabled

### Setup

1. Clone the repository
   ```bash
   git clone https://github.com/Blackscreen21/Plottwist.git
   cd Plottwist
   ```

2. Add your Firebase config
   Download `google-services.json` from your Firebase project console and place it in `app/google-services.json`.
   > **Note:** This repo currently ships with a `google-services.json` committed. If you fork this project, swap it for your own Firebase project's file rather than reusing this one, and consider gitignoring it going forward.

3. Open the project in Android Studio and let Gradle sync, or build from the command line:
   ```bash
   ./gradlew assembleDebug
   ```

4. Run on an emulator or physical device (min SDK 24 / Android 7.0+)

## How it works

1. **Search** — enter an ISBN, author, or title (e.g. `isbn:9780141439518`, `author:Jane Austen`, or just a plain title) to query the Google Books API.
2. **Add to your list** — save any result to your personal, locally-stored list.
3. **Upload to trade** — push a book from your list to the shared Firebase database so other users can see it's available.
4. **Trade** — books marked "Available" in search results exist in the shared database and can be requested/removed via the trade action.

## Roadmap

- [ ] User authentication (currently the shared list is fully public/anonymous)
- [ ] Result formatting/prettifying for raw API responses (see `ApiCaller.kt` TODO)
- [ ] Proper Firebase Security Rules for the trading database
- [ ] Trade request/negotiation flow beyond simple delete-on-trade

## License

This project stands under the MIT license
