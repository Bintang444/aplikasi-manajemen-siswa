# Aplikasi Manajemen Siswa

Aplikasi Android untuk mengelola data siswa dan jurusan, dibangun dengan Kotlin sebagai tugas PKL SMK Mahardhika.

## Fitur

- **Autentikasi** — Login/Register dengan session via SharedPreferences
- **Dashboard** — Ringkasan jumlah siswa & jurusan, grafik distribusi gender, aktivitas terbaru
- **Data Siswa** — CRUD siswa (NIS, nama, jenis kelamin, alamat, jurusan), cari & filter
- **Data Jurusan** — CRUD jurusan, cari
- **Profile** — Informasi akun & logout
- **Dark Mode** — Tema gelap otomatis (Material 3 DayNight)

## Tech Stack

| | |
|---|---|
| **Bahasa** | Kotlin 2.0 |
| **Minimum SDK** | 24 (Android 7.0) |
| **Target SDK** | 36 (Android 15) |
| **Arsitektur** | Fragment-based, Activity/Fragment |
| **UI** | Material 3, ConstraintLayout, CardView, RecyclerView |
| **Networking** | Retrofit 2 + Gson + OkHttp logging |
| **Chart** | MPAndroidChart |
| **Backend** | PHP API (MySQL), endpoint di `http://192.168.1.100:8000/` |

## API Endpoints

| Method | Endpoint | Deskripsi |
|--------|----------|-----------|
| POST | `login.php` | Login user |
| POST | `register.php` | Registrasi user |
| GET | `get_jurusan.php` | Ambil semua jurusan |
| POST | `tambah_jurusan.php` | Tambah jurusan |
| POST | `update_jurusan.php` | Edit jurusan |
| POST | `delete_jurusan.php` | Hapus jurusan |
| GET | `get_siswa.php` | Ambil semua siswa |
| POST | `tambah_siswa.php` | Tambah siswa |
| POST | `update_siswa.php` | Edit siswa |
| POST | `delete_siswa.php` | Hapus siswa |

## Build & Run

```bash
./gradlew assembleDebug
```

Ubah base URL API di `build.gradle.kts` jika perlu:

```kotlin
buildConfigField("String", "API_BASE_URL", "\"http://192.168.1.100:8000/\"")
```

## Lisensi

© SMK Mahardhika — 2025
