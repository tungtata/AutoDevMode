# This is a configuration file for ProGuard.
# https://www.guardsquare.com/proguard

# Preserve line numbers for crash reports
-keepattributes SourceFile,LineNumberTable

# Keep the app's MainActivity and other components
-keep public class com.example.usbdebugauto.MainActivity { *; }
-keep public class com.example.usbdebugauto.receiver.** { *; }

# Keep ViewModel classes
-keep public class com.example.usbdebugauto.viewmodel.** { *; }

# Keep data classes
-keep public class com.example.usbdebugauto.model.** { *; }

# Keep enums
-keepclassmembers enum com.example.usbdebugauto.** {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Preserve source file names and line numbers for debugging
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# For Kotlin coroutines
-keep class kotlinx.coroutines.** { *; }
-keepclassmembers class kotlinx.coroutines.** {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# For Jetpack Compose
-keep class androidx.compose.** { *; }
