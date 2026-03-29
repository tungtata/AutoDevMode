# USB Debug Auto - Hướng dẫn chi tiết

## I. Chuẩn bị trước

### Yêu cầu
- Điện thoại Android với Android 8.0+ (API 26+)
- Cáp USB để kết nối với máy tính
- Android SDK (hoặc chỉ cần ADB)
- Android Studio (để build app)

### Kiểm tra ADB
Trên máy tính, mở terminal/command prompt và chạy:
```bash
adb version
```
Nếu lệnh không nhận dạng, hãy cài đặt Android SDK Platform Tools.

## II. Build app

### Cách 1: Dùng Android Studio
1. Mở Android Studio
2. File → Open → Chọn thư mục project
3. Đợi Gradle sync hoàn tất
4. Run → Run 'app'
5. Chọn device / emulator

### Cách 2: Dùng Gradle command line
```bash
cd /path/to/AutoDevMode
./gradlew build
```
File APK sẽ nằm ở: `app/build/outputs/apk/release/app-release.apk`

## III. Cấp quyền WRITE_SECURE_SETTINGS

**Bước này QUAN TRỌNG** - nếu không cấp quyền, app không thể bật/tắt settings.

### Trên Windows:
```cmd
adb shell pm grant com.example.usbdebugauto android.permission.WRITE_SECURE_SETTINGS
```

### Trên macOS/Linux:
```bash
adb shell pm grant com.example.usbdebugauto android.permission.WRITE_SECURE_SETTINGS
```

### Kiểm tra xem quyền đã được cấp chưa:
```bash
adb shell pm list permissions | grep WRITE_SECURE_SETTINGS
```
Hoặc mở app và nhấn "Check Permission" - nếu hiệu "GRANTED" thì OK.

## IV. Sử dụng app

### 1. Mở app
Nhấn vào icon "USB Debug Auto" trên home screen

### 2. Màn hình chính
- **Permission Status**: Hiển thị xem đã cấp quyền chưa
- **Enable Automation**: Master switch bật/tắt tự động hoá
- **Settings**: Các tuỳ chọn chi tiết
- **Manual Controls**: Nút bật/tắt thủ công để test
- **Activity Logs**: Nhật ký sự kiện

### 3. Cấu hình

#### Auto Enable Developer Options
Khi phát hiện USB data connection, tự động bật Developer Options

#### Auto Enable USB Debugging
Khi phát hiện USB data connection, tự động bật USB Debugging

#### Auto Disable Developer Options on USB Disconnect
Khi rút USB, tự động tắt Developer Options (nếu auto-enable đã bật trước đó)

#### Auto Disable USB Debugging on USB Disconnect
Khi rút USB, tự động tắt USB Debugging (nếu auto-enable đã bật trước đó)

#### USB Detection Mode
Cách phát hiện kết nối USB là data hay charge-only:

- **STRICT**: Chỉ coi là data nếu `configuration > 0`
  - Dùng khi máy chỉ recognize USB khi configuration được set
  - Ít false positives

- **BALANCED** (mặc định): Data nếu `configuration > 0` HOẶC có mtp/ptp/rndis/adb
  - Phù hợp với hầu hết các máy
  - Cân bằng độ nhạy

- **LOOSE**: Mọi kết nối USB đều coi là data
  - Dùng khi 2 mode trên không hoạt động
  - Có thể có false positives

#### Delay Before Auto-Enable
Độ trễ trước khi tự động bật (0/2/5/10 giây):
- **0 giây**: Bật ngay lập tức
- **2 giây**: Chờ 2 giây trước khi bật (giảm false positives)
- **5 giây**: Chờ 5 giây
- **10 giây**: Chờ 10 giây

Nếu auto-enable không hoạt động, try tăng delay.

### 4. Manual Controls
- **Enable Developer Options / Disable Developer Options**: Bật/tắt thủ công
- **Enable USB Debugging / Disable USB Debugging**: Bật/tắt thủ công

Dùng những nút này để test xem quyền WRITE_SECURE_SETTINGS đã được cấp chưa.

### 5. Activity Logs
Hiển thị nhật ký chi tiết:
- USB event được nhận
- Kết quả phát hiện (DATA hay CHARGE ONLY)
- Bật/tắt settings
- Lỗi nếu có

**Tìm kiếm vấn đề**: Nếu app không hoạt động đúng, hãy xem logs xem có lỗi gì.

## V. Test workflow

### Test 1: Kiểm tra quyền
1. Mở app
2. Xem "Permission Status" - nếu là "GRANTED", OK
3. Nếu là "NOT GRANTED":
   - Sao chép lệnh ADB từ nút "Copy ADB Command"
   - Chạy lệnh trên máy tính
   - Nhấn "Check Permission" trên app

### Test 2: Manual control
1. Kiểm tra quyền đã được cấp (Test 1)
2. Nhấn "Enable Developer Options"
3. Xem logs - nếu thấy "Successfully enabling Developer Options" tức là OK
4. Kiểm tra Settings → Developer Options → xem có enable không
5. Tương tự với "Enable USB Debugging"

### Test 3: Auto-enable khi cắm USB
1. Kiểm tra quyền đã được cấp (Test 1)
2. Bật toggle "Enable Automation"
3. Bật checkbox "Auto Enable Developer Options"
4. Bật checkbox "Auto Enable USB Debugging"
5. Chọn Detection Mode (ưu tiên BALANCED)
6. Cắm USB vào máy tính
7. Xem logs - nếu thấy "USB Data Connection detected" và "Successfully enabling..." tức là OK
8. Kiểm tra Settings → xem có enable không

### Test 4: Auto-disable khi rút USB
1. Hoàn thành Test 3
2. Bật checkbox "Auto Disable Developer Options on USB Disconnect"
3. Bật checkbox "Auto Disable USB Debugging on USB Disconnect"
4. Rút USB
5. Xem logs - nếu thấy "USB disconnected" và "Successfully disabling..." tức là OK

## VI. Troubleshooting

### Vấn đề 1: "Permission NOT GRANTED" hoặc "WRITE_SECURE_SETTINGS not granted" trong logs

**Nguyên nhân**: Quyền chưa được cấp qua ADB

**Giải pháp**:
1. Kết nối điện thoại với máy tính qua USB
2. Bật USB Debugging: Settings → Developer Options → USB Debugging
3. Chạy: `adb shell pm grant com.example.usbdebugauto android.permission.WRITE_SECURE_SETTINGS`
4. Mở app, nhấn "Check Permission"

### Vấn đề 2: Manual enable/disable không hoạt động

**Nguyên nhân**: Quyền WRITE_SECURE_SETTINGS không được cấp

**Giải pháp**: Làm theo Vấn đề 1

### Vấn đề 3: Auto-enable không kích hoạt khi cắm USB

**Nguyên nhân có thể**:
1. Automation toggle chưa bật
2. Checkboxes "Auto Enable..." chưa bật
3. USB không được nhận dạng là data connection
4. Receiver broadcast chưa nhận được

**Giải pháp**:
1. Kiểm tra "Enable Automation" có bật không
2. Kiểm tra checkboxes "Auto Enable Developer Options" và "Auto Enable USB Debugging" có bật không
3. Thử đổi Detection Mode:
   - BALANCED (mặc định) → LOOSE
   - Hoặc STRICT
4. Tăng Delay từ 0 → 2 hoặc 5 giây
5. Xem logs xem có "USB event received" không

### Vấn đề 4: USB không được phát hiện

**Nguyên nhân có thể**:
1. USB Debugging chưa bật
2. Device chưa được add vào ADB allowed devices
3. Broadcast không được gửi (OEM-specific)

**Giải pháp**:
1. Bật USB Debugging: Settings → Developer Options → USB Debugging
2. Rút USB, sau đó cắm lại
3. Thử kết nối với máy tính khác
4. Thử LOOSE mode
5. Kiểm tra logs

### Vấn đề 5: Có cảnh báo "Automation disabled"

**Nguyên nhân**: Automation toggle chưa bật

**Giải pháp**: Bật toggle "Enable Automation"

### Vấn đề 6: App crash hoặc không mở được

**Giải pháp**:
1. Uninstall: `adb uninstall com.example.usbdebugauto`
2. Build lại: `./gradlew build`
3. Install: `adb install app/build/outputs/apk/debug/app-debug.apk`
4. Cấp quyền: `adb shell pm grant com.example.usbdebugauto android.permission.WRITE_SECURE_SETTINGS`

## VII. ADB command reference

### Cấp quyền
```bash
adb shell pm grant com.example.usbdebugauto android.permission.WRITE_SECURE_SETTINGS
```

### Xoá quyền
```bash
adb shell pm revoke com.example.usbdebugauto android.permission.WRITE_SECURE_SETTINGS
```

### Uninstall app
```bash
adb uninstall com.example.usbdebugauto
```

### Install app
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### Clear app data
```bash
adb shell pm clear com.example.usbdebugauto
```

### Xem logcat (debug logs từ app)
```bash
adb logcat | grep "USBDebugAuto"
```

### Dump USB state (debug)
```bash
adb shell dumpsys usb
```

## VIII. Ghi chú quan trọng

1. **Quyền WRITE_SECURE_SETTINGS không thể tự cấp** - phải qua ADB
2. **USB detection có thể khác nhau trên các OEM** - nếu một mode không hoạt động, thử mode khác
3. **Broadcast receiver có thể không hoạt động nếu app bị force-stop** - hãy chắc chắn app đang chạy
4. **Settings changes có thể bị override bởi system** - tùy OEM
5. **Delay option giúp tránh race conditions** - nếu vấn đề vẫn xảy ra, tăng delay

## IX. Liên hệ / Báo lỗi

Nếu bạn gặp lỗi hoặc có câu hỏi:
1. Kiểm tra phần Troubleshooting trên
2. Xem Activity Logs trong app
3. Chạy `adb logcat` để xem debug logs
