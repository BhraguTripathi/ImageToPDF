# 📄 ImageToPDF — Android App

A clean, modern Android app built with **Jetpack Compose** that lets users convert images into PDF files, manage their documents, and authenticate securely via Supabase.

---

## ✨ Features

- 🖼️ **Image to PDF Conversion** — Select up to 20 images and convert them into a single PDF
- 📁 **My Documents** — Browse, search, and open previously created PDFs
- 👤 **User Authentication** — Email/password login, signup, forgot password with OTP verification, and password reset
- 🔔 **In-App Update Checker** — Automatically checks GitHub for newer versions and prompts the user to download and install
- 🎨 **Custom UI** — Gradient headers, branded color theme, smooth splash screen with animated loading dots
- 📤 **Share & Download** — Share PDFs directly or save them to device storage
- 🔐 **Secure Key Storage** — Supabase credentials are stored in `local.properties` and never committed to version control

---

## 📸 App Screens

| Screen | Description |
|---|---|
| Splash Screen | Animated intro with pulsing loading dots |
| Login | Email & password login with "Forgot Password" |
| Sign Up | Full name, email, password registration |
| Forgot Password | Email input → OTP verification → Reset password |
| Home | Central hub with "Create New" button and update dialog |
| Before Conversion | Image picker grid, PDF name input, convert button |
| After Conversion | Success screen with PDF stats, download, share, and view actions |
| My Documents | Searchable list of saved PDFs |
| Account | Profile picture, user info, settings menu, sign out |
| Coming Soon | Placeholder screen for features under development |

---

## 🛠️ Tech Stack

| Technology | Usage |
|---|---|
| **Kotlin** | Primary language |
| **Jetpack Compose** | Declarative UI |
| **Navigation Compose** | Screen routing |
| **ViewModel + StateFlow** | State management |
| **Supabase (auth-kt)** | Authentication (login, signup, OTP, password reset) |
| **Ktor (Android client)** | HTTP client for Supabase |
| **Coil** | Async image loading |
| **Android PdfDocument API** | Native PDF creation |
| **DownloadManager** | APK update download |
| **FileProvider** | Secure file sharing |

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Hedgehog or later
- Android SDK 24+
- A [Supabase](https://supabase.com) project with **Email Auth** enabled

### 1. Clone the Repository

```bash
git clone https://github.com/BhraguTripathi/ImageToPDF.git
cd ImageToPDF
```

### 2. Add Your Supabase Credentials

Create a `local.properties` file in the **root** of the project (next to `settings.gradle.kts`) and add:

```properties
SUPABASE_URL=https://your-project-id.supabase.co
SUPABASE_KEY=your-anon-public-key
```

> ⚠️ `local.properties` is listed in `.gitignore` — your keys will never be committed to the repository.

### 3. Build & Run

Open the project in Android Studio and click **Run ▶**, or build via:

```bash
./gradlew assembleDebug
```

---

## 📁 Project Structure

```
app/src/main/java/com/example/imagetopdf/
│
├── MainActivity.kt                  # Entry point
│
├── navigation/
│   ├── NavGraph.kt                  # All route definitions and navigation logic
│   ├── Screen.kt                    # Sealed class of route strings
│   └── BottomNavItem.kt             # Bottom bar navigation items
│
├── network/
│   └── SupabaseClient.kt            # Supabase client singleton
│
├── ui/
│   ├── components/                  # Reusable Composables (TopBar, BottomBar, CustomTextField, etc.)
│   ├── screens/
│   │   ├── authentication/          # Login, Signup, OTP, AuthViewModel
│   │   ├── conversion/              # Before/After conversion screens, PDFViewModel
│   │   ├── home/                    # HomeScreen with update checker
│   │   ├── mydoc/                   # My Documents screen
│   │   ├── account/                 # Account & settings screen
│   │   ├── password/                # Forgot & Reset Password screens
│   │   ├── splashscreen/            # Animated Splash Screen
│   │   └── comingsoonfeature/       # Coming Soon placeholder screen
│   └── theme/
│       ├── Color.kt                 # App color palette
│       ├── Type.kt                  # Typography styles
│       └── Theme.kt                 # Light/dark MaterialTheme setup
│
└── utils/
    ├── PdfConverter.kt              # Image-to-PDF conversion logic
    └── UpdateChecker.kt             # GitHub-based in-app update checker
```

---

## 🔄 In-App Update System

The app automatically checks for updates on every launch by fetching the `version.json` file from this repository:

```
https://raw.githubusercontent.com/BhraguTripathi/ImageToPDF/main/version.json
```

**`version.json` format:**

```json
{
  "latest_version": "1.1",
  "update_message": "Bug fixes and performance improvements",
  "download_url": "https://github.com/BhraguTripathi/ImageToPDF/releases/download/v1.1/app-release.apk"
}
```

To release an update, bump `latest_version`, update `download_url`, and push the JSON file to the `main` branch.

---

## 🎨 Color Palette

| Name | Hex | Usage |
|---|---|---|
| `BrandPurple` | `#4D4FC0` | Primary buttons, active icons, gradient start |
| `BrandBlueLight` | `#8894FF` | Gradient end |
| `TextPrimary` | `#333333` | Main headings and body text |
| `TextSecondary` | `#828282` | Subtitles, descriptions |
| `AccentOrange` | `#F2994A` | Settings icon tint |
| `SignOutRed` | `#FF4B4B` | Sign out button |
| `SuccessGreen` | `#27AE60` | Conversion success icon |

---

## 📦 Key Dependencies

```toml
# Supabase Auth
supabase-gotrue = "io.github.jan-tennert.supabase:auth-kt:3.4.0"
ktor-client-android = "io.ktor:ktor-client-android:3.0.2"

# Navigation
androidx.navigation:navigation-compose:2.8.0

# Image Loading
io.coil-kt:coil-compose:2.6.0

# Extended Material Icons
androidx.compose.material:material-icons-extended
```

---

## 🔐 Permissions

| Permission | Reason |
|---|---|
| `INTERNET` | Supabase auth calls and update checker |
| `WRITE_EXTERNAL_STORAGE` (API ≤ 28) | Saving PDFs to external storage on older devices |
| `REQUEST_INSTALL_PACKAGES` | Installing downloaded APK updates |

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature-name`
3. Commit your changes: `git commit -m "Add: your feature description"`
4. Push to the branch: `git push origin feature/your-feature-name`
5. Open a Pull Request

---

## 📃 License

This project is open source and available under the [MIT License](LICENSE).

---

## 👨‍💻 Author

**Bhragu Tripathi**  
[GitHub](https://github.com/BhraguTripathi)
