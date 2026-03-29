# USB Debug Auto - Quick Start Guide (5 minutes)

## 🚀 Tóm tắt 5 bước

### Bước 1: Build app (1 phút)
```bash
cd d:\Documents\GitHub\AutoDevMode
./gradlew build
```

### Bước 2: Install app (30 giây)
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### Bước 3: Grant permission (30 giây) ⭐ **IMPORTANT**
```bash
adb shell pm grant com.example.usbdebugauto android.permission.WRITE_SECURE_SETTINGS
```

### Bước 4: Launch app (1 phút)
Nhấn vào icon "USB Debug Auto" trên điện thoại, hoặc:
```bash
adb shell am start -n com.example.usbdebugauto/.MainActivity
```

### Bước 5: Test it! (2 phút)
1. Kiểm tra "Permission Status" → should be "GRANTED"
2. Bật toggle "Enable Automation"
3. Bật checkboxes "Auto Enable Developer Options" + "Auto Enable USB Debugging"
4. Cắm USB → Xem logs → Thấy "Successfully enabling..."

**Done! ✅**

---

## 🎯 Test Scenarios

### Test 1: Check Permission
```
✅ Expected:
   - Permission Status = GRANTED
   - Manual buttons hoạt động
```

### Test 2: Manual Enable
1. Nhấn "Enable Developer Options"
2. Kiểm tra Settings → Developer Options
3. Nó phải được enable

### Test 3: Auto Enable on USB
1. Bật Automation
2. Bật Auto Enable options
3. Cắm USB data (không charge-only)
4. Xem logs: "USB Data Connection detected" + "Successfully enabling..."
5. Kiểm tra Settings

### Test 4: Auto Disable on USB Disconnect
1. Bật Auto Disable options
2. Cắm USB (auto-enable)
3. Rút USB
4. Xem logs: "Disabling..."
5. Kiểm trace Settings: features tắt

---

## 📱 Expected Logs

### Khi cắm USB:
```
[11:30:45.123] INFO: USB event received: connected=true, mtp=false, adb=false, configuration=1
[11:30:45.234] INFO: Detection result (BALANCED): DATA CONNECTION
[11:30:45.345] INFO: Manual: Enabling Developer Options...
[11:30:45.456] INFO: Successfully enabling Developer Options
[11:30:45.567] INFO: Manual: Enabling USB Debugging...
[11:30:45.678] INFO: Successfully enabling USB Debugging (adb_enabled)
```

### Khi rút USB:
```
[11:31:00.123] INFO: USB disconnected
[11:31:00.234] INFO: Auto-disabling Developer Options...
[11:31:00.345] INFO: Successfully disabling Developer Options
[11:31:00.456] INFO: Auto-disabling USB Debugging...
[11:31:00.567] INFO: Successfully disabling USB Debugging (adb_enabled)
```

---

## ⚠️ If Something Goes Wrong

### ❌ Permission NOT GRANTED
```bash
# Verify permission command
adb shell pm grant com.example.usbdebugauto android.permission.WRITE_SECURE_SETTINGS

# Check if it worked
adb shell pm list permissions | grep WRITE_SECURE_SETTINGS

# If still not granted, try:
adb uninstall com.example.usbdebugauto
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell pm grant com.example.usbdebugauto android.permission.WRITE_SECURE_SETTINGS
```

### ❌ Build fails
```bash
# Clean build
./gradlew clean
./gradlew build

# If still fails, check:
# 1. Is Android SDK installed?
# 2. Is Java 8+ installed?
# 3. Is JAVA_HOME set?
```

### ❌ USB not detected
1. Try different Detection Mode (LOOSE instead of BALANCED)
2. Increase Delay (try 2 or 5 seconds)
3. Reconnect USB
4. Check logs for errors

### ❌ Manual buttons don't work
Permission not granted! Go back to "Bước 3" dan grant permission.

---

## 📚 Next Steps

After basic test works:

1. **Read README.md** for full documentation
2. **Read SETUP_GUIDE.md** for detailed troubleshooting
3. **Check ADB_COMMANDS.md** for more ADB tips
4. **Explore MainScreen.kt** for UI code
5. **Check UsbDetectionEvaluator.kt** for detection logic

---

## 🎓 Customization Options

### Change Detection Mode
Settings → USB Detection Mode → Choose one:
- STRICT: Only if configuration > 0
- BALANCED: Recommended (default)
- LOOSE: Any USB connection

### Change Delay
Settings → Delay Before Auto-Enable:
- 0 seconds: Instant
- 2 seconds: Recommended if you see false positives
- 5 or 10 seconds: For slower devices

### Disable Auto-disable
Uncheck "Auto Disable Developer Options on USB Disconnect" if you want to keep settings enabled after USB disconnect.

---

## 💡 Tips

- **Always grant permission with ADB** - it's a one-time thing per installation
- **BALANCED mode works best** for most devices
- **Check logs** if something doesn't work
- **Try LOOSE mode** if app doesn't detect USB
- **Increase delay** if auto-enable triggers when it shouldn't

---

## 📋 Minimal Command Reference

```bash
# Build
./gradlew build

# Install
adb install -r app/build/outputs/apk/debug/app-debug.apk

# Grant permission (CRITICAL!)
adb shell pm grant com.example.usbdebugauto android.permission.WRITE_SECURE_SETTINGS

# Launch
adb shell am start -n com.example.usbdebugauto/.MainActivity

# View logs
adb logcat -v time | grep usbdebugauto

# Uninstall
adb uninstall com.example.usbdebugauto
```

---

**Estimated total time: 5-10 minutes** ⏱️

**Ready to go? Start with Bước 1!** 🚀
