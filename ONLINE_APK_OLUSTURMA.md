# İnternetten APK Oluşturma (GitHub Actions)

Bu proje, bilgisayarında Android Studio veya Android SDK olmadan GitHub sunucularında APK derlemek için hazırdır.

## 1) GitHub deposu oluştur
1. https://github.com adresine gir ve hesabına giriş yap.
2. Sağ üstte `+` > `New repository` seç.
3. Repository name: `YagSatis-Android`
4. İstersen `Private` seç.
5. `Create repository` butonuna bas.

## 2) Dosyaları yükle
1. Bu ZIP'i bilgisayarında çıkar.
2. `YagSatis_Android` klasörünün İÇİNDEKİ tüm dosya ve klasörleri GitHub deposuna yükle.
3. GitHub'da `Add file` > `Upload files` kullanabilirsin. Klasörü tarayıcıya sürükleyip bırakmak da desteklenir.
4. `.github` klasörünün de yüklendiğinden emin ol.
5. `Commit changes` de.

## 3) APK'yı derle
1. Repo sayfasında üst menüden `Actions` sekmesine gir.
2. Soldan `Build Android APK` seç.
3. `Run workflow` > tekrar `Run workflow` de.
4. Derleme tamamlanınca çalışmanın içine gir.
5. Sayfanın altındaki `Artifacts` bölümünde `YagSatis-Android-APK` dosyasını indir.
6. İnen ZIP'i aç; içinde `YagSatis-v1.0.0-debug.apk` bulunur.

## 4) Telefona kur
1. APK'yı telefona gönder.
2. Telefonda APK'ya dokun.
3. Android isterse `Bu kaynaktan uygulama yüklemeye izin ver` seçeneğini aç.
4. `Yükle` de.

## Not
Bu çıktı DEBUG APK'dır ve telefonuna kurup uygulamayı test etmek için uygundur. Play Store için daha sonra imzalı RELEASE AAB/APK oluşturulmalıdır.
