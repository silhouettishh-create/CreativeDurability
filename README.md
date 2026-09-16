# Creative Durability

Mod Fabric untuk Minecraft **26.2** yang membuat item kehilangan durability
di Creative Mode, persis seperti di Survival — bisa dinyalakan/dimatikan
lewat command.

## Cara build jadi .jar lewat GitHub Actions (paling gampang)

Proyek ini sudah termasuk `.github/workflows/build.yml` yang otomatis
build pakai JDK 25 + Gradle 9.5.1 di server GitHub (yang punya internet,
beda dengan sandbox saya).

1. Buat repo GitHub baru, misalnya bernama **`creative-durability`**.
2. Push semua isi folder ini ke repo itu (lewat web upload, `git push`, atau
   GitHub Desktop — bebas).
3. Buka tab **Actions** di repo tersebut → workflow "Build Creative
   Durability" akan otomatis jalan setelah push.
4. Tunggu sampai selesai (tanda centang hijau), lalu buka run itu → di
   bagian **Artifacts** ada `creativedurability-jar` → download & extract,
   isinya file `.jar` yang sudah jadi.

Kalau workflow ini gagal (merah), buka log run-nya — biasanya akan
menunjukkan persis baris kode mana yang perlu disesuaikan.

## Cara build jadi .jar (di komputer kamu)

1. Pasang **JDK 25** (Temurin/Adoptium OK).
2. Buka folder proyek ini di terminal, lalu jalankan:
   - Kalau sudah punya Gradle 9.5+ terpasang: `gradle wrapper` (sekali saja,
     ini akan membuat `gradlew` dan `gradlew.bat`), setelah itu pakai
     `./gradlew build` seterusnya.
   - Atau langsung `gradle build` tanpa wrapper sama sekali.
   - Atau buka folder ini di **IntelliJ IDEA** — IDE akan otomatis
     membuatkan wrapper dan mendownload semua dependency.
3. Hasil jar ada di `build/libs/creativedurability-1.0.0.jar`.

Build pertama akan mengunduh Minecraft 26.2 + Fabric Loom, jadi butuh
waktu beberapa menit dan koneksi internet aktif.

## Cara install & uji

1. Pasang **Fabric Loader 0.19.3+** dan **Fabric API 0.153.0+26.2** (atau
   lebih baru) untuk Minecraft 26.2.
2. Taruh `creativedurability-1.0.0.jar` di folder `mods/`.
3. Jalankan game/server, buat dunia Creative, lalu di chat (atau lewat
   command block) ketik:
