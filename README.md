# 🚀 PeriksaAplikasi

Aplikasi Android untuk memudahkan pemeriksaan data member siswa, menampilkan detail lengkap, serta memberikan nilai untuk tugas yang diambil dari server melalui API.

---

## ✨ Fitur Utama

- 📱 **Scan QR Code** untuk mendapatkan ID member secara cepat dan akurat.  
- 👤 Menampilkan detail member seperti nama lengkap, NIS, dan kelas secara realtime dari API.  
- 📝 Menampilkan daftar tugas yang dapat dipilih untuk pemberian nilai.  
- 💾 Memasukkan dan mengirim nilai tugas siswa ke server melalui API.  
- ✅ Validasi input nilai dan feedback berupa pesan sukses/gagal.

---

## 🛠️ Teknologi yang Digunakan

- Kotlin  
- Retrofit2 untuk komunikasi jaringan (REST API)  
- ViewBinding untuk memudahkan akses UI  
- Material Design Components untuk UI modern  
- ZXing Embedded untuk scan QR Code  
- REST API dengan format JSON  

---

## ⚙️ Setup & Instalasi

1. **Clone repository**

   ```bash
   git clone https://github.com/username/periksaaplikasi.git
   cd periksaaplikasi
## Import ke Android Studio

Tambahkan dependencies di build.gradle jika belum ada

gradle
Copy code
implementation 'com.squareup.retrofit2:retrofit:2.9.0'
implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
implementation 'androidx.appcompat:appcompat:1.5.1'
implementation 'com.google.android.material:material:1.6.0'
implementation 'com.journeyapps:zxing-android-embedded:4.3.0' // untuk scan QR Code
Jalankan aplikasi di emulator atau perangkat fisik

## 🎯 Cara Menggunakan
Jalankan aplikasi dan scan QR code siswa untuk mendapatkan ID member.

Setelah ID berhasil didapatkan, aplikasi akan otomatis menampilkan detail member (nama, NIS, kelas).

Pilih tugas dari dropdown yang mengambil data tugas terbaru dari API.

Masukkan nilai siswa pada input nilai.

Tekan tombol Submit Nilai untuk menyimpan nilai ke server.

Aplikasi memberikan notifikasi berhasil atau gagal menyimpan nilai.

## 📁 Struktur Folder
data.network — konfigurasi Retrofit dan API client

data.model — data model request dan response API

model — model data response member

ui — activity dan fragment UI

## 📄 License
MIT License © 2025 Luthfi

## 📬 Kontak
GitHub: https://github.com/upiw256

Email: bilqimlb@gmail.com

🙏 Terima kasih telah menggunakan aplikasi ini!
Jika ada pertanyaan atau ide fitur baru, silakan buat issue di repo ini atau hubungi saya.
