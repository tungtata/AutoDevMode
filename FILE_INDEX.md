# USB Debug Auto - File Structure & Index

## 📁 Complete File Tree

```
d:/Documents/GitHub/AutoDevMode/
│
├── README.md                           ✅ Main documentation
├── SETUP_GUIDE.md                      ✅ Step-by-step setup guide
├── PROJECT_SUMMARY.md                  ✅ Project overview & checklist
├── ADB_COMMANDS.md                     ✅ ADB commands reference
├── .gitignore                          ✅ Git ignore file
│
├── build.gradle.kts                    ✅ Top-level gradle config
├── settings.gradle.kts                 ✅ Settings gradle config
├── gradle.properties                   ✅ Gradle properties
│
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar          ✅ Gradle wrapper JAR
│       └── gradle-wrapper.properties   ✅ Gradle wrapper properties
│
├── gradlew                             ✅ Gradle wrapper (Linux/Mac)
├── gradlew.bat                         ✅ Gradle wrapper (Windows)
│
└── app/
    ├── build.gradle.kts                ✅ App-level gradle config
    ├── proguard-rules.pro              ✅ ProGuard obfuscation rules
    │
    ├── src/main/
    │   ├── AndroidManifest.xml         ✅ App manifest with permissions & receiver
    │   │
    │   ├── kotlin/com/example/usbdebugauto/
    │   │   │
    │   │   ├── MainActivity.kt         ✅ Main activity with Compose setup
    │   │   │
    │   │   ├── viewmodel/
    │   │   │   └── MainViewModel.kt    ✅ ViewModel - state management & coordination
    │   │   │
    │   │   ├── receiver/
    │   │   │   └── UsbStateReceiver.kt ✅ BroadcastReceiver - listens to USB events
    │   │   │
    │   │   ├── controller/
    │   │   │   └── SecureSettingsController.kt ✅ Settings controller - enables/disables features
    │   │   │
    │   │   ├── usb/
    │   │   │   └── UsbDetectionEvaluator.kt ✅ USB evaluator - determines data vs charge
    │   │   │
    │   │   ├── repository/
    │   │   │   ├── SettingsRepository.kt  ✅ DataStore preferences management
    │   │   │   └── LogRepository.kt       ✅ In-memory logging system
    │   │   │
    │   │   ├── model/
    │   │   │   └── Models.kt            ✅ Enums, data classes, sealed classes
    │   │   │
    │   │   └── ui/
    │   │       ├── MainScreen.kt        ✅ Jetpack Compose UI - main layout
    │   │       └── theme/
    │   │           └── Theme.kt         ✅ Material 3 theme & colors
    │   │
    │   └── res/
    │       ├── values/
    │       │   ├── strings.xml          ✅ String resources
    │       │   ├── themes.xml           ✅ Legacy themes
    │       │   └── colors.xml           ✅ Color palette
    │       ├── values-night/
    │       │   └── themes.xml           ✅ Dark mode themes
    │       ├── mipmap/
    │       │   ├── ic_launcher.xml      ✅ Adaptive icon launcher
    │       │   ├── ic_launcher_round.xml ✅ Round icon
    │       │   ├── ic_launcher_background.xml ✅ Icon background
    │       │   ├── ic_launcher_foreground.xml ✅ Icon foreground
    │       │   ├── ic_launcher.webp     ✅ Icon resources (various densities)
    │       │   ├── ic_launcher_round.webp ✅ Round icon resources
    │       │   └── [other density folders]
    │       ├── xml/
    │       │   ├── backup_rules.xml     ✅ Backup rules
    │       │   └── data_extraction_rules.xml ✅ Data extraction rules
    │       └── drawable/
    │           ├── ic_launcher_background.xml ✅ Drawable launcher background
    │           └── ic_launcher_foreground.xml ✅ Drawable launcher foreground
    │
    ├── src/test/
    │   └── java/com/samfw/autodevmode/
    │       └── ExampleUnitTest.kt       (Optional - for unit tests)
    │
    └── src/androidTest/
        └── java/com/samfw/autodevmode/
            └── ExampleInstrumentedTest.kt (Optional - for instrumented tests)
```

## 📋 File Descriptions

### Root Documentation Files

| File | Description | Lines | Language |
|------|-------------|-------|----------|
| README.md | Main project documentation, features, architecture | ~250 | Markdown |
| SETUP_GUIDE.md | Detailed setup & troubleshooting guide | ~300 | Markdown |
| PROJECT_SUMMARY.md | Project overview & completion checklist | ~200 | Markdown |
| ADB_COMMANDS.md | ADB commands reference & cheat sheet | ~300 | Markdown |

### Build Configuration

| File | Description |
|------|-------------|
| build.gradle.kts | Top-level Gradle configuration |
| settings.gradle.kts | Project settings & module inclusion |
| gradle.properties | Gradle properties (optimization, etc.) |
| app/build.gradle.kts | App-level dependencies & build config |
| app/proguard-rules.pro | ProGuard obfuscation rules |
| gradle/wrapper/gradle-wrapper.properties | Gradle wrapper version config |

### Kotlin Source Files

#### MainActivity.kt
```kotlin
// Purpose: Entry point of the app
// Size: ~30 lines
// Responsibilities:
//   - Initialize ViewModel
//   - Set up Compose UI
//   - Trigger ViewModel initialization
```

#### viewmodel/MainViewModel.kt
```kotlin
// Purpose: State management & orchestration
// Size: ~250 lines
// Responsibilities:
//   - Manage UI state (flows)
//   - Coordinate repositories & controllers
//   - Provide action functions (enable/disable/etc.)
//   - Register/unregister USB receiver
//   - Handle logs collection
```

#### receiver/UsbStateReceiver.kt
```kotlin
// Purpose: Receive USB state broadcasts
// Size: ~150 lines
// Responsibilities:
//   - Listen to android.hardware.usb.action.USB_STATE
//   - Parse USB state from intent
//   - Evaluate USB state
//   - Apply settings based on user preferences
//   - Handle async operations with coroutines
```

#### controller/SecureSettingsController.kt
```kotlin
// Purpose: System settings manipulation
// Size: ~150 lines
// Responsibilities:
//   - Check WRITE_SECURE_SETTINGS permission
//   - Enable/disable developer options
//   - Enable/disable USB debugging
//   - Read current state
//   - Error handling & logging
```

#### usb/UsbDetectionEvaluator.kt
```kotlin
// Purpose: USB state evaluation logic
// Size: ~100 lines
// Responsibilities:
//   - Parse USB_STATE intent
//   - Evaluate based on detection mode
//   - Determine if data connection or charge-only
//   - Log results
```

#### repository/SettingsRepository.kt
```kotlin
// Purpose: Persistent settings storage
// Size: ~150 lines
// Responsibilities:
//   - DataStore integration
//   - Preference key definitions
//   - Provide Flow<T> for each setting
//   - Update settings (suspend functions)
```

#### repository/LogRepository.kt
```kotlin
// Purpose: In-memory logging
// Size: ~50 lines
// Responsibilities:
//   - Store logs in StateFlow
//   - Add log entries with timestamp
//   - Clear logs
//   - Limit to 100 logs
```

#### model/Models.kt
```kotlin
// Purpose: Data structures & enums
// Size: ~100 lines
// Contains:
//   - enum DetectionMode
//   - sealed class UsbDetectionResult
//   - data class UsbState
//   - sealed class SettingOperationResult
//   - data class LogEntry
```

#### ui/MainScreen.kt
```kotlin
// Purpose: Jetpack Compose UI
// Size: ~550 lines
// Components:
//   - MainScreen (main layout)
//   - PermissionStatusCard
//   - SettingCheckbox
//   - DetectionModeSelector
//   - DelaySelector
//   - Manual control buttons
//   - Logs display
```

#### ui/theme/Theme.kt
```kotlin
// Purpose: Material 3 theme
// Size: ~50 lines
// Contains:
//   - Dark color scheme
//   - Light color scheme
//   - Theme composition
```

### Android Resources

#### AndroidManifest.xml
```xml
- Permissions: WRITE_SECURE_SETTINGS
- Activities: MainActivity
- Receivers: UsbStateReceiver (exported, receives USB_STATE)
- Intent filters: android.hardware.usb.action.USB_STATE
```

#### strings.xml
```xml
- App name, labels for all UI elements
- Total: ~20 strings
```

#### colors.xml & themes.xml
```xml
- Material 3 color palette
- Primary, secondary, error, surface colors
- Light and dark theme variants
```

#### mipmap/ & drawable/
```
- App launcher icons (various densities: mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi)
- Adaptive icon components (foreground, background)
- WebP format for modern devices
```

## 📊 Project Statistics

### Kotlin Code
- **Total files**: 9 main Kotlin files
- **Total lines**: ~2000 lines of Kotlin code
- **Main components**:
  - 1 Activity
  - 1 ViewModel (250 lines)
  - 1 BroadcastReceiver (150 lines)
  - 1 Controller (150 lines)
  - 1 Evaluator (100 lines)
  - 2 Repositories (200 lines combined)
  - 1 Models file (100 lines)
  - 2 UI files (600 lines combined)

### Documentation
- **Total docs**: 4 markdown files
- **Total lines**: ~1000 lines of documentation
- Coverage: Setup, troubleshooting, ADB commands, project summary

### Resources
- **String resources**: ~20
- **Colors**: ~6
- **Icons**: Multiple densities
- **Themes**: Light + Dark

## 🔍 Key Files to Understand

### For beginners:
1. **README.md** - Start here for overview
2. **SETUP_GUIDE.md** - Follow this to get started
3. **MainActivity.kt** - Simple entry point
4. **MainScreen.kt** - UI code

### For intermediate:
1. **MainViewModel.kt** - State management
2. **SettingsRepository.kt** - DataStore usage
3. **UsbStateReceiver.kt** - Broadcast handling
4. **Models.kt** - Data structures

### For advanced:
1. **SecureSettingsController.kt** - System integration
2. **UsbDetectionEvaluator.kt** - Detection logic
3. **build.gradle.kts** - Dependencies & build config
4. **AndroidManifest.xml** - Manifest configuration

## 🚀 Build Output

After building:
```
app/build/outputs/
├── apk/
│   ├── debug/
│   │   └── app-debug.apk         ← Use this for testing
│   └── release/
│       └── app-release.apk       ← Use this for distribution
├── bundle/
│   └── release/
│       └── app-release.aab       ← For Play Store
└── logs/
    └── manifest-merger-report.txt
```

## 📦 Dependencies

All dependencies are defined in `app/build.gradle.kts`:
- AndroidX Core: 1.12.0
- Lifecycle: 2.7.0
- Jetpack Compose: 2024.01.00
- Material 3: 1.2.0
- DataStore: 1.0.0
- Coroutines: 1.7.3

## ✅ Quality Assurance Checklist

- ✅ All 9 Kotlin files created
- ✅ AndroidManifest.xml properly configured
- ✅ All build.gradle files with correct dependencies
- ✅ Jetpack Compose UI with all required components
- ✅ DataStore persistence implemented
- ✅ BroadcastReceiver properly registered
- ✅ Resource files (strings, colors, icons)
- ✅ 4 comprehensive documentation files
- ✅ Git repository initialized with commits
- ✅ No compilation errors (ready to build)
- ✅ Ready for Android Studio import

---

**Total project size**: ~100 KB (source code)
**Ready to build**: Yes ✅
**Ready to run**: Yes ✅ (after ADB permission grant)
