# USB Debug Auto - Android App

Một ứng dụng Android cho phép tự động bật/tắt Developer Options và USB Debugging khi phát hiện kết nối USB dữ liệu.

## Chức năng chính

- ✅ Theo dõi thay đổi trạng thái kết nối USB
- ✅ Tự động bật Developer Options và USB Debugging khi phát hiện USB data connection
- ✅ Tự động tắt lại các tính năng trên khi rút cáp USB (tuỳ chọn)
~~- ✅ 3 chế độ phát hiện USB: STRICT, BALANCED, LOOSE~~
- ✅ Hỗ trợ độ trễ (0s, 2s, 5s, 10s) trước khi tự động bật
- ✅ Hiển thị nhật ký chi tiết về các sự kiện USB
- ✅ Không cần root, chỉ cần cấp quyền WRITE_SECURE_SETTINGS qua ADB

## Yêu cầu hệ thống

- Android 8.0 (API 26) trở lên
- Quyền WRITE_SECURE_SETTINGS được cấp qua ADB

## Cấu trúc dự án

```
app/
├── src/main/
│   ├── kotlin/com/example/usbdebugauto/
│   │   ├── MainActivity.kt                    # Activity chính
│   │   ├── viewmodel/
│   │   │   └── MainViewModel.kt               # ViewModel quản lý UI state
│   │   ├── receiver/
│   │   │   └── UsbStateReceiver.kt            # BroadcastReceiver cho USB events
│   │   ├── controller/
│   │   │   └── SecureSettingsController.kt    # Điều khiển system settings
│   │   ├── usb/
│   │   │   └── UsbDetectionEvaluator.kt       # Logic phát hiện USB data vs charge
│   │   ├── repository/
│   │   │   ├── SettingsRepository.kt          # DataStore preferences
│   │   │   └── LogRepository.kt               # Quản lý logs
│   │   ├── model/
│   │   │   └── Models.kt                      # Enum, data class, sealed class
│   │   └── ui/
│   │       ├── MainScreen.kt                  # Jetpack Compose UI
│   │       └── theme/
│   │           └── Theme.kt                   # Compose theme
│   ├── res/
│   │   ├── values/
│   │   │   ├── strings.xml
│   │   │   └── themes.xml
│   │   └── mipmap/                            # App icons
│   └── AndroidManifest.xml
├── build.gradle.kts
```

## Cài đặt và sử dụng

### 1. Yêu cầu quyền WRITE_SECURE_SETTINGS

Quyền này **không thể tự cấp** và phải được cấp bởi **ADB** (qua máy tính):

```bash
adb shell pm grant com.tungtata.usbdebugauto android.permission.WRITE_SECURE_SETTINGS
```

Bạn cần:
- Kết nối điện thoại qua USB với máy tính
- Bật USB Debugging trước (Settings → Developer Options → USB Debugging)
- Chạy lệnh ADB trên máy tính

### 2. Tính năng chính

#### Permission Status
- Kiểm tra xem app đã được cấp quyền WRITE_SECURE_SETTINGS chưa
- Hiển thị lệnh ADB cần chạy
- Nút "Copy ADB Command" sao chép lệnh vào clipboard

#### Enable Automation
- Master switch bật/tắt tự động hoá toàn bộ

#### Settings
- **Auto Enable Developer Options**: Tự động bật Developer Options khi phát hiện USB data
- **Auto Enable USB Debugging**: Tự động bật USB Debugging
- **Auto Disable Developer Options on Disconnect**: Tự động tắt Developer Options khi rút USB
- **Auto Disable USB Debugging on Disconnect**: Tự động tắt USB Debugging khi rút USB
~~
~~- **USB Detection Mode**: Chọn cách phát hiện USB data
  - **STRICT**: Chỉ coi là data nếu configuration > 0
  - **BALANCED**: Coi là data nếu configuration > 0 HOẶC có mtp/ptp/rndis/adb (mặc định)
  - **LOOSE**: Coi mọi kết nối USB là data~~
  **Giờ mặc định chỉ chế độ LOOSE vì các chế độ khác hoạt động không ổn sau khi test**
- **Delay Before Auto-Enable**: Độ trễ trước khi tự động bật (0/2/5/10 giây)

#### Manual Controls
- Các nút bật/tắt thủ công Developer Options và USB Debugging
- Để test quyền WRITE_SECURE_SETTINGS

#### Activity Logs
- Hiển thị nhật ký chi tiết các sự kiện:
  - USB connection/disconnection
  - Kết quả phát hiện (data vs charge)
  - Bật/tắt settings
  - Lỗi permission hoặc exception

## Logic phát hiện USB

### USB State Broadcast
App nghe `android.hardware.usb.action.USB_STATE` broadcast được gửi bởi Android khi:
- Cắm USB vào điện thoại
- Rút USB khỏi điện thoại
~~- Chuyển đổi USB mode (MTP, PTP, RNDIS, ADB, v.v.)~~

### Parsing
```kotlin
val connected: Boolean       // Có kết nối USB
val mtp: Boolean             // Media Transfer Protocol
val ptp: Boolean             // Picture Transfer Protocol
val rndis: Boolean           // Remote Network Device Interface Specification
val adb: Boolean             // Android Debug Bridge
val configuration: Int       // USB configuration number (0 = charge only)
val charging: Boolean        // Đang sạc
val charging_dp: Boolean     // Sạc qua display port
```

### Đánh giá theo Detection Mode

~~**STRICT**: `connected && configuration > 0`
- Chỉ coi là data khi configuration được set (máy tính đã recognize)
- Ít false positives nhất

**BALANCED** (mặc định): `connected && (configuration > 0 || mtp || ptp || rndis || adb)`
- Coi là data nếu có bất kỳ dấu hiệu data mode
- Cân bằng giữa độ nhạy và sai số

**LOOSE**: `connected`
- Mọi kết nối USB đều coi là data
- Có thể có false positives (charge-only bị nhận dạng thành data)~~

## Kiến trúc code

### MainViewModel
- Quản lý toàn bộ UI state dùng StateFlow
- Điều phối giữa repositories, controllers, và evaluators
- Đăng ký/hủy đăng ký USB receiver
- Cung cấp hàm thay đổi settings và manual controls

### UsbStateReceiver
- BroadcastReceiver nghe USB_STATE broadcasts
- Đã export trong manifest để nhận broadcasts ngay cả khi app không chạy
- Khi nhận broadcast:
  1. Parse USB state
  2. Đọc settings từ DataStore
  3. Đánh giá USB state
  4. Thực hiện các hành động cần thiết (enable/disable settings)
  5. Ghi log

### SecureSettingsController
- Đọc/ghi Settings.Secure.adb_enabled và development_settings_enabled
- Kiểm tra WRITE_SECURE_SETTINGS permission trước khi ghi
- Trả về SettingOperationResult (Success hoặc Error)

### UsbDetectionEvaluator
- Parse USB_STATE intent thành UsbState data class
- Đánh giá USB state theo Detection Mode
- Trả về UsbDetectionResult (IsDataConnection, IsChargeOnly, Disconnected)

### SettingsRepository
- Lưu/đọc toàn bộ preferences dùng DataStore
- Cung cấp Flow<T> để observe thay đổi settings
- Settings được persist và khôi phục khi app restart

### LogRepository
- Lưu logs trong memory (StateFlow)
- Giới hạn 100 logs gần nhất
- Cung cấp hàm để add log, add error, clear logs

## Limitations và ghi chú quan trọng

### 1. Permission WRITE_SECURE_SETTINGS
- **Không có cách nào tự cấp quyền này**
- App sẽ được cấp khi user chạy lệnh ADB trên máy tính
- Không cần root, nhưng cần access ADB shell
~~
### 2. USB Detection không 100% chính xác trên mọi máy
- Một số OEM Android có thể không gửi đầy đủ broadcast extras
~~- BALANCED mode là cách an toàn nhất để xử lý khác biệt OEM
- Nếu app không hoạt động đúng, hãy try STRICT hoặc LOOSE mode~~
~~
### 3. Broadcast Receiver Sticky vs Dynamic
- Manifest receiver (XML): Nhận broadcasts ngay cả khi app không chạy
- Để xử lý case app không mở lúc phát hiện USB, receiver được export
- Khi app mở, receiver được đăng ký động thêm lần nữa (để capture sticky broadcast)
- Có thể có 2 receivers active cùng lúc, nhưng cả 2 đều xử lý OK

### 4. Timing issues
- Có thể xảy ra race condition giữa USB detection và DataStore read
- Delay option giúp bypass issue này
- Nếu vẫn có vấn đề, tăng delay

### 5. Android version compatibility
- minSdk = 26 (Android 8.0)
- Settings.Secure API khả dụng trên tất cả versions
- IntentFilter API có slight khác biệt trên Android 12+, code đã xử lý

### 6. Multiple USB modes
- Một số thiết bị hỗ trợ multiple USB modes đồng thời
- Khi chuyển mode, broadcast được gửi nhiều lần
- App xử lý hợp lý bằng cách check timestamps và states

### 7. DataStore persistence
- Settings được lưu persistent trong DataStore
- Nếu uninstall app, tất cả settings sẽ mất
- Nếu install app mới, settings sẽ reset về mặc định

## Build và chạy

### Yêu cầu
- Android Studio Giraffe hoặc mới hơn
- Kotlin 1.9+
- Android SDK 34

### Steps
1. Clone hoặc download dự án
2. Mở trong Android Studio
3. Build: `./gradlew build`
4. Run: Chọn device hoặc emulator trong Android Studio
5. Sau khi app cài đặt, chạy ADB command để cấp quyền:
   ```bash
   adb shell pm grant com.tungtata.usbdebugauto android.permission.WRITE_SECURE_SETTINGS
   ```

## Troubleshooting

### "WRITE_SECURE_SETTINGS not granted"
- Kiểm tra đã chạy ADB command chưa
- Kiểm tra package name có đúng không: com.tungtata.usbdebugauto
- Thử chạy: `adb shell pm list permissions | grep WRITE_SECURE_SETTINGS`

### Auto-enable không hoạt động
1. Kiểm tra Automation toggle có bật không
2. Kiểm tra checkbox cho tính năng tương ứng (Enable Dev Options / Enable ADB)
3. Kiểm tra Detection Mode
4. Mở Logs xem có lỗi gì không
5. Try BALANCED hoặc LOOSE mode

### USB không được phát hiện
1. Thử LOOSE mode
2. Kiểm tra Settings → Developer Options → USB Debugging có bật không
3. Xem lại logs xem có broadcast được gửi không
4. Thử kết nối với máy tính khác

## License

MIT
