# USB Debug Auto - ADB Commands & Cheat Sheet

## ⚡ Essential ADB Commands

### 1. Cấp quyền WRITE_SECURE_SETTINGS (Bắt buộc)
```bash
adb shell pm grant com.example.usbdebugauto android.permission.WRITE_SECURE_SETTINGS
```
**Mục đích**: Cho phép app thay đổi Developer Options và USB Debugging settings

### 2. Kiểm tra quyền đã được cấp
```bash
adb shell pm list permissions | grep WRITE_SECURE_SETTINGS
```

### 3. Xoá quyền
```bash
adb shell pm revoke com.example.usbdebugauto android.permission.WRITE_SECURE_SETTINGS
```

### 4. Cài đặt app
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### 5. Gỡ cài đặt app
```bash
adb uninstall com.example.usbdebugauto
```

### 6. Clear app data (reset settings)
```bash
adb shell pm clear com.example.usbdebugauto
```

## 🔧 System Settings Commands (Manual)

### Bật Developer Options
```bash
adb shell settings put secure development_settings_enabled 1
```

### Tắt Developer Options
```bash
adb shell settings put secure development_settings_enabled 0
```

### Bật USB Debugging
```bash
adb shell settings put secure adb_enabled 1
```

### Tắt USB Debugging
```bash
adb shell settings put secure adb_enabled 0
```

### Kiểm tra trạng thái Developer Options
```bash
adb shell settings get secure development_settings_enabled
# Output: 0 hoặc 1
```

### Kiểm tra trạng thái USB Debugging
```bash
adb shell settings get secure adb_enabled
# Output: 0 hoặc 1
```

## 🔍 Debugging & Logging

### Xem logcat (realtime logs)
```bash
adb logcat
```

### Xem logcat chỉ cho app
```bash
adb logcat | grep "usbdebugauto"
```

### Xem logcat với timestamp
```bash
adb logcat -v time
```

### Save logcat to file
```bash
adb logcat > logcat.txt
```

### Clear logcat buffer
```bash
adb logcat -c
```

## 📱 USB Device Management

### List connected devices
```bash
adb devices
```

### Get device info
```bash
adb shell getprop ro.build.version.sdk
# Android API level

adb shell getprop ro.build.version.release
# Android version
```

### Dump USB state
```bash
adb shell dumpsys usb
```
Xem chi tiết USB connection state, modes, etc.

### Dump settings
```bash
adb shell settings list secure | grep -E "(adb_enabled|development_settings_enabled)"
```

## 🚀 Build Commands

### Clean build
```bash
./gradlew clean
```

### Build debug APK
```bash
./gradlew assembleDebug
# Output: app/build/outputs/apk/debug/app-debug.apk
```

### Build release APK
```bash
./gradlew assembleRelease
# Output: app/build/outputs/apk/release/app-release.apk
```

### Run on device
```bash
./gradlew run
```

### Build and run
```bash
./gradlew build installDebug
```

## 📋 Setup Workflow

### Complete setup from scratch:
```bash
# 1. Build app
./gradlew build

# 2. Install app
adb install -r app/build/outputs/apk/debug/app-debug.apk

# 3. Grant permission (IMPORTANT!)
adb shell pm grant com.example.usbdebugauto android.permission.WRITE_SECURE_SETTINGS

# 4. Verify permission
adb shell pm list permissions | grep WRITE_SECURE_SETTINGS

# 5. Clear previous data (if needed)
adb shell pm clear com.example.usbdebugauto

# 6. Launch app
adb shell am start -n com.example.usbdebugauto/.MainActivity

# 7. View logs (in another terminal)
adb logcat -v time | grep "usbdebugauto"
```

## ✅ Verification Checklist

### Check 1: App installed
```bash
adb shell pm list packages | grep com.example.usbdebugauto
# Should output: package:com.example.usbdebugauto
```

### Check 2: Permission granted
```bash
adb shell pm list permissions | grep WRITE_SECURE_SETTINGS
# Should show in granted permissions
```

### Check 3: Current developer settings state
```bash
adb shell settings get secure development_settings_enabled
adb shell settings get secure adb_enabled
```

### Check 4: USB state
```bash
adb shell dumpsys usb | grep -A 10 "mUsbNotificationId"
```

## 🐛 Troubleshooting Commands

### If app won't install
```bash
adb uninstall com.example.usbdebugauto
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### If permission not granted
```bash
# Verify device is connected
adb devices

# Try granting again
adb shell pm grant com.example.usbdebugauto android.permission.WRITE_SECURE_SETTINGS

# Check if granted
adb shell cmd permission list | grep WRITE_SECURE_SETTINGS
```

### If can't find adb
```bash
# Add to PATH or use full path to ADB
# Windows: C:\Users\YourUsername\AppData\Local\Android\Sdk\platform-tools\adb.exe
# macOS: ~/Library/Android/sdk/platform-tools/adb
# Linux: ~/Android/Sdk/platform-tools/adb
```

### If USB detection not working
```bash
# Check USB state
adb shell dumpsys usb

# Check if broadcast receiver is working
adb logcat -v time | grep "android.hardware.usb.action.USB_STATE"

# Try reconnecting USB
# Unplug USB, wait 2 seconds, plug back in
```

## 📦 Package Name Reference

- **Package**: `com.example.usbdebugauto`
- **Activity**: `com.example.usbdebugauto.MainActivity`
- **Receiver**: `com.example.usbdebugauto.receiver.UsbStateReceiver`

## 🔗 Useful Intent Actions

### USB State Broadcast
```
Action: android.hardware.usb.action.USB_STATE
Extras:
  - connected (Boolean)
  - mtp (Boolean)
  - ptp (Boolean)
  - rndis (Boolean)
  - adb (Boolean)
  - configuration (Int)
  - charging (Boolean)
  - charging_dp (Boolean)
```

### Launch MainActivity
```bash
adb shell am start -n com.example.usbdebugauto/.MainActivity
```

## 💡 Tips & Tricks

### Watch USB broadcasts in realtime
```bash
adb logcat -v time | grep "USB_STATE"
```

### Monitor settings changes
```bash
adb shell cmd settings watch secure development_settings_enabled
adb shell cmd settings watch secure adb_enabled
```

### Send test USB state broadcast (if available)
```bash
adb shell am broadcast -a android.hardware.usb.action.USB_STATE --es connected true --es mtp true
```

### Get app info
```bash
adb shell pm dump com.example.usbdebugauto | grep -A 20 "Package"
```

### Kill app process
```bash
adb shell am force-stop com.example.usbdebugauto
```

### Restart app
```bash
adb shell am force-stop com.example.usbdebugauto
adb shell am start -n com.example.usbdebugauto/.MainActivity
```

## 📞 Emergency Uninstall (If app is broken)

```bash
adb uninstall com.example.usbdebugauto
adb shell pm clear com.example.usbdebugauto  # Clear data if still exists
```

## 🎯 One-liner Scripts

### Complete setup:
```bash
./gradlew build && adb install -r app/build/outputs/apk/debug/app-debug.apk && adb shell pm grant com.example.usbdebugauto android.permission.WRITE_SECURE_SETTINGS && echo "Setup complete!"
```

### Test everything:
```bash
adb devices && adb shell pm list packages | grep com.example.usbdebugauto && adb shell pm list permissions | grep WRITE_SECURE_SETTINGS && echo "All checks passed!"
```

### View logs in color (macOS/Linux):
```bash
adb logcat -v time | grep "usbdebugauto" | while IFS= read -r line; do echo "$(tput setaf 2)$line$(tput sgr0)"; done
```

---

**Note**: Tất cả những lệnh này giả định `adb` đã được thêm vào PATH.
Nếu không, hãy dùng đường dẫn đầy đủ đến adb executable.
