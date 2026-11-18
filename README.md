# KantinKu — Aplikasi Pemesanan Makanan Kantin

Aplikasi Android untuk memesan makanan kantin. Dibuat untuk Tugas Proyek
mata kuliah **Mobile Application Development** (Semester 20261).

Alur: isi form pesanan → data disimpan ke SQLite → daftar pesanan → detail pesanan.

## Fitur

- Hero header gradien dengan ringkasan jam buka dan jumlah menu.
- Form pesanan berseksi bernomor: data pemesan, pilih menu, tambahan, jumlah porsi, ringkasan.
- Pemilih menu horizontal: kartu menu yang menyala saat dipilih, lengkap dengan harga.
- Topping berupa baris yang berubah warna dan garis tepi saat dicentang.
- Jumlah porsi lewat stepper tombol + / − dan slider SeekBar yang tersinkron.
- Total berjalan (count-up animation) plus rincian harga menu, topping, dan porsi.
- Validasi nama dan password dengan pesan error dan auto-scroll ke kolom bermasalah.
- Penyimpanan pesanan ke database SQLite lokal.
- Daftar pesanan dengan avatar inisial, jumlah pesanan, dan kartu yang bisa ditekan.
- Halaman detail dengan hero total, rincian pesanan, dan hapus pesanan.
- Empty state berilustrasi dengan tombol "Buat pesanan".
- Transisi antar layar (slide + fade) dan tema Material 3 dengan mode gelap.

## Pemenuhan Requirement

| Requirement | Implementasi |
|---|---|
| Intent dan Activity | 3 Activity: `MainActivity` → `OrderListActivity` → `OrderDetailActivity`. Perpindahan memakai `Intent` eksplisit, data pesanan dikirim lewat `Intent` extra (`order_id`). |
| Text Input | `TextInputLayout` + `TextInputEditText` untuk nama pemesan (`inputName`). |
| Password input | `TextInputEditText` dengan `inputType="textPassword"` dan tombol tampil/sembunyikan password (`inputPassword`). |
| Selection (Spinner) | `Spinner` daftar menu beserta harga (`spinnerMenu`). |
| Checkbox / Radio Button | 3 `CheckBox` topping: keju, telur, sambal. |
| Button | `MaterialButton` "Pesan Sekarang", "Lihat Pesanan", dan "Hapus Pesanan". |
| SeekBar | `SeekBar` jumlah porsi 1–10 (`seekPortion`), total ikut berubah. |
| Database | SQLite via `OrderDbHelper` (`SQLiteOpenHelper`): tabel `orders`, operasi insert, query, dan delete. |
| Workflow & desain | Alur form → simpan → daftar → detail → hapus; UI material 3 dengan hero, kartu berseksi, animasi total, transisi layar, dan mode gelap. |

## Struktur Kode

```
app/src/main/java/com/oswaldvalois/canteenorder/
├── MainActivity.java        # Form pesanan + validasi + simpan ke SQLite
├── OrderListActivity.java   # Daftar pesanan (RecyclerView)
├── OrderDetailActivity.java # Detail pesanan + hapus
├── OrderAdapter.java        # Adapter RecyclerView
├── OrderDbHelper.java       # SQLiteOpenHelper (CRUD tabel orders)
├── Order.java               # Model pesanan
├── MenuItem.java            # Model menu (nama + harga)
├── Money.java               # Format rupiah
└── BaseActivity.java        # Penanganan system bar insets
```

## Cara Build

Prasyarat: Android Studio (JDK 17+) dan Android SDK.

```bash
./gradlew assembleDebug
```

APK hasil build: `app/build/outputs/apk/debug/app-debug.apk`.

Atau buka folder proyek di Android Studio, lalu Run.

## Screenshot

| Form Pesanan | Form Terisi | Daftar Pesanan |
|---|---|---|
| ![Form](docs/screenshots/01-form.png) | ![Form terisi](docs/screenshots/02-form-terisi.png) | ![Daftar](docs/screenshots/03-daftar-pesanan.png) |

| Detail Pesanan | Daftar Kosong |
|---|---|
| ![Detail](docs/screenshots/04-detail-pesanan.png) | ![Kosong](docs/screenshots/05-kosong.png) |

## Spesifikasi

- Bahasa: Java
- minSdk 24, targetSdk 37, compileSdk 37
- Android Gradle Plugin 9.3.2, Gradle 9.5
- Library: AndroidX AppCompat, Material Components, RecyclerView
