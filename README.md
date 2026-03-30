# USB Debug Auto

[English](#english) | [Tiếng Việt](#tiếng-việt)

---

## English

**USB Debug Auto** is an Android app that automatically enables/disables Developer Options and USB Debugging when a USB data connection is detected.

### Features

- ✅ Automatically enable Developer Options and USB Debugging on USB connection
- ✅ Automatically disable on USB disconnection (optional)
- ✅ Customizable delay before auto-enabling (0s, 2s, 5s, 10s)
- ✅ No root required - uses ADB-granted permission only
- ✅ Real-time activity log
- ✅ Light/Dark/Auto theme support
- ✅ Works even when app is closed

### Quick Start

1. **Install the app** on your Android device
2. **Grant permission** via ADB on your computer:
   ```bash
   adb shell pm grant com.tungtata.usbdebugauto android.permission.WRITE_SECURE_SETTINGS
   ```
3. **Configure automation rules** in the app
4. **Done!** The app will automatically handle USB connections

### Requirements

- Android 8.0 (API 26) or higher
- USB Debugging enabled on device
- ADB access to grant `WRITE_SECURE_SETTINGS` permission

### How It Works

The app listens for USB connection broadcasts and automatically:
1. Detects USB data connection (vs charging-only)
2. Reads your automation settings
3. Enables/disables Developer Options and USB Debugging
4. Logs all actions

### Limitations

- **Permission**: `WRITE_SECURE_SETTINGS` cannot be self-granted. Must be granted via ADB.
- **Force Close**: If app is force-stopped, automation will stop. Use a Foreground Service for 24/7 monitoring.

### Build

```bash
./gradlew build
```

### License

MIT

---

## Tiếng Việt

**USB Debug Auto** là ứng dụng Android tự động bật/tắt Developer Options và USB Debugging khi phát hiện kết nối USB dữ liệu.

### Tính năng

- ✅ Tự động bật Developer Options và USB Debugging khi cắm USB
- ✅ Tự động tắt khi rút USB (tuỳ chọn)
- ✅ Độ trễ tùy chỉnh (0s, 2s, 5s, 10s) trước khi bật
- ✅ Không cần root - chỉ dùng quyền được cấp qua ADB
- ✅ Nhật ký hoạt động real-time
- ✅ Hỗ trợ chế độ Light/Dark/Auto
- ✅ Hoạt động ngay cả khi app bị đóng

### Cài đặt nhanh

1. **Cài đặt app** trên điện thoại Android
2. **Cấp quyền** qua ADB trên máy tính:
   ```bash
   adb shell pm grant com.tungtata.usbdebugauto android.permission.WRITE_SECURE_SETTINGS
   ```
3. **Cấu hình quy tắc tự động** trong app
4. **Hoàn tất!** App sẽ tự động xử lý các kết nối USB

### Yêu cầu

- Android 8.0 (API 26) trở lên
- USB Debugging được bật trên thiết bị
- Quyền ADB để cấp `WRITE_SECURE_SETTINGS`

### Cách hoạt động

App lắng nghe các broadcast kết nối USB và tự động:
1. Phát hiện kết nối USB dữ liệu (khác charging-only)
2. Đọc cài đặt tự động hóa của bạn
3. Bật/tắt Developer Options và USB Debugging
4. Ghi nhật ký tất cả hành động

### Giới hạn

- **Quyền**: `WRITE_SECURE_SETTINGS` không thể tự cấp. Phải cấp qua ADB.
- **Force Close**: Nếu app bị buộc đóng, tự động hóa sẽ dừng. Sử dụng Foreground Service để giám sát 24/7.

### Build

```bash
./gradlew build
```

### Giấy phép

MIT
