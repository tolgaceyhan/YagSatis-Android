# Yağ Satış - Android v1.0.0

Bu paket, **Yağ Satış & Kampanya Yönetimi v10** arayüzü ve iş mantığının Android uygulama kabuğuna alınmış sürümüdür.

## Telefonda çalışan özellikler
- İnternetsiz kullanım
- Ürün ve fiyat yönetimi
- Stok giriş / çıkış hareketleri
- Müşteri yönetimi
- Sepet ve çoklu ürün satışı
- Farklı ürünlerden çoklu bedelsiz ürün
- Peşin / kart / cari satış
- Tahsilat ve müşteri cari hareketleri
- Satış geçmişi ve iptal işlemleri
- JSON yedek alma
- Telefonda dosya seçerek JSON yedeğini geri yükleme
- Android geri tuşu
- Uygulama içindeki verilerin cihazda kalıcı saklanması

## En kolay APK üretme - Windows
Android Studio bir kez kurulmuş ve Android SDK 35 mevcutsa:

1. `build-apk.bat` dosyasına çift tıklayın.
2. İlk derlemede Gradle ve Android bağımlılıkları internetten indirilir.
3. İşlem sonunda proje klasöründe:
   `YagSatis-v1.0.0.apk`
   oluşur.
4. APK'yı telefona gönderip kurabilirsiniz.

### USB ile doğrudan telefona kurma
Telefonunuzda **Geliştirici Seçenekleri > USB hata ayıklama** açıkken telefonu PC'ye bağlayın ve:

`install-phone.bat`

dosyasına çift tıklayın.

## Android Studio ile
1. Android Studio > **Open**.
2. `YagSatis_Android` klasörünü seçin.
3. Gradle Sync tamamlanınca **Build > Build App Bundle(s) / APK(s) > Build APK(s)**.
4. APK yolu:
   `app/build/outputs/apk/debug/app-debug.apk`

## Teknik bilgiler
- Paket adı: `com.yagsatis.mobile`
- Sürüm: `1.0.0`
- Minimum Android: Android 7.0 / API 24
- Compile / Target SDK: API 35
- Arayüz: Android WebView + Yağ Satış v10
- Veri: Android WebView uygulama alanında yerel depolama
- Yedek: Android Storage Access Framework üzerinden JSON

## Veri güvenliği
Uygulama kaldırılırsa veya Android Ayarları'ndan uygulama verileri temizlenirse cihazdaki yerel veriler silinebilir. Bu nedenle uygulamadaki **Yedeği İndir** işleviyle düzenli JSON yedeği alın.

## Sonraki üretim sürümü
Bu sürüm tek cihazda çevrimdışı kullanım için hazırlanmıştır. Bir sonraki mimari yükseltme aşamasında veri katmanı SQLite'a taşınabilir ve sonrasında sunucu senkronizasyonu, çoklu kullanıcı, barkod, Bluetooth yazıcı ve bulut yedek eklenebilir.
