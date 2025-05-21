<p align="center">
 <img width="100px" src="https://i0.wp.com/is4-ssl.mzstatic.com/image/thumb/Purple112/v4/a7/85/b1/a785b1cb-ec98-546f-6ab3-2ed139151e38/AppIcon-0-0-1x_U007emarketing-0-0-0-7-0-0-sRGB-0-0-0-GLES2_U002c0-512MB-85-220-0-0.png/512x512bb.jpg?w=1200&ssl=1" align="center" alt="Rush Hour Solver" />
 <h2 align="center">Rush Hour Solver</h2>
 <p align="center">Implementasi UCS, Greedy, A*, dan Simulated Annealing untuk permainan puzzle Rush Hour</p>
</p>
<p align="center">
  <a href="https://github.com/rlukassa/Tucil3_13523131_13523158">
    <img alt="GitHub repo" src="https://img.shields.io/badge/github-repo-green?style=flat&logo=github" />
  </a>
  <a href="LICENSE">
    <img alt="License" src="https://img.shields.io/badge/license-MIT-blue?style=flat" />
  </a>
</p>

## 📝 Deskripsi

Proyek ini mengimplementasikan empat algoritma berbeda untuk menyelesaikan puzzle Rush Hour:
1. **Uniform Cost Search (UCS)** - Pendekatan breadth-first yang mengembangkan simpul berdasarkan biaya jalur
2. **Greedy Best-First Search** - Mengembangkan simpul berdasarkan jarak heuristik ke tujuan
3. **A\* Search** - Menggabungkan pendekatan UCS dan Greedy untuk solusi optimal
4. **Simulated Annealing** - Teknik probabilistik untuk optimasi global secara pendekatan

Puzzle Rush Hour mengharuskan pemindahan kendaraan pada grid untuk memungkinkan mobil tertentu (potongan utama) keluar dari papan. Implementasi ini menyediakan antarmuka CLI dan GUI untuk menyelesaikan puzzle Rush Hour.

## 📋 Fitur

- **Beberapa Algoritma Pencarian**: Pilih antara UCS, Greedy, A*, atau Simulated Annealing
- **Beberapa Heuristik**: Beberapa fungsi heuristik tersedia untuk algoritma pencarian terinformasi:
  - Jarak Manhattan
  - Manhattan + Blocking (dengan penalti kendaraan penghalang)
  - Heuristik yang Ditingkatkan (menggabungkan beberapa faktor)
  - Biaya SA Kustom (khusus untuk Simulated Annealing)
- **GUI Interaktif**: Antarmuka Java Swing modern dengan:
  - Toggle tema Gelap/Terang
  - Pemutaran solusi beranimasi
  - Navigasi solusi langkah demi langkah
  - Tampilan statistik terperinci
  - Pelacakan riwayat gerakan
- **Antarmuka Command Line**: Untuk solusi cepat tanpa overhead GUI

## 🚀 Memulai

### Prasyarat
- Java Development Kit (JDK) 11 atau lebih tinggi
- Git (untuk mengkloning repositori)

### Instalasi

1. Kloning repositori:
```bash
git clone https://github.com/rlukassa/Tucil3_13523131_13523158
cd Tucil3_13523131_13523158
```

2. Kompilasi proyek:
```bash
javac -d bin  src/*.java src/components/*.java src/utils/*.java src/view/*.java
```

### Menjalankan Aplikasi

#### Mode GUI
```bash
java -cp bin view.GUI
```

#### Mode CLI
```bash
java -cp bin src.Main
```

## 💡 Penggunaan

### Format File Puzzle

File input harus ditempatkan di direktori `test` dan memiliki format sebagai berikut:

```
<baris> <kolom>
<jumlah_potongan_non-utama>
<orientasi_potongan_utama> <y_potongan_utama> <x_potongan_utama> <ukuran_potongan_utama>
<orientasi_potongan1> <y_potongan1> <x_potongan1> <ukuran_potongan1>
<orientasi_potongan2> <y_potongan2> <x_potongan2> <ukuran_potongan2>
...
<k>
```

Dimana:
- `orientasi`: 0 untuk horizontal, 1 untuk vertikal
- `y`: koordinat baris (indeks-0)
- `x`: koordinat kolom (indeks-0)
- `ukuran`: panjang kendaraan (biasanya 2 atau 3)
- `k` : titik keluar (lebih jelasnya terlihat di folder test, dengan spasi menandakan berada di baris dan kolom berapa)

### Menggunakan GUI

1. **Pilih file puzzle**: Klik "Browse" untuk memilih file puzzle dari direktori test
2. **Pilih algoritma**: Pilih UCS, Greedy, A*, atau Simulated Annealing dari dropdown
3. **Pilih heuristik** (untuk pencarian terinformasi): Pilih heuristik yang ingin Anda gunakan
4. **Klik "Solve"**: Aplikasi akan menemukan solusi dan menampilkannya
5. **Langkah demi langkah**: Gunakan kontrol navigasi untuk melangkah melalui solusi
   - Gunakan tombol Sebelumnya/Selanjutnya untuk bergerak satu langkah sekaligus
   - Gunakan Play/Pause untuk animasi otomatis
   - Sesuaikan kecepatan animasi dengan slider
6. **Lihat statistik**: Periksa panel Statistik untuk info kinerja algoritma
7. **Riwayat gerakan**: Lihat semua gerakan di panel Riwayat

### Menggunakan CLI

1. Jalankan antarmuka CLI
2. Masukkan nama file puzzle (dari direktori test)
3. Pilih algoritma
4. Pilih heuristik (jika berlaku)
5. Lihat langkah-langkah solusi dan statistik

## 🔧 Menyesuaikan Parameter

### Heuristik

Anda dapat memodifikasi fungsi heuristik di `GameBoard.java` untuk menguji pendekatan berbeda:
- `getManhattanDistance()`: Perhitungan jarak dasar
- `getManhattanWithBlocking()`: Menambahkan penalti untuk kendaraan penghalang
- `getEnhancedHeuristic()`: Menggabungkan beberapa faktor
- `getSimulatedAnnealingCost()`: Khusus untuk algoritma SA

### Parameter Simulated Annealing

Di `Solver.java`, Anda dapat menyesuaikan parameter SA:
- `initialTemperature`: Temperatur awal (default: 1000.0)
- `finalTemperature`: Temperatur berhenti (default: 0.1)
- `coolingRate`: Jadwal pendinginan (default: 0.95)
- `maxIterations`: Iterasi SA maksimum (default: 10000)

## 📊 Analisis Kinerja

Algoritma berkinerja berbeda tergantung pada kompleksitas puzzle:
- **UCS**: Solusi optimal, tetapi dapat lambat untuk puzzle kompleks
- **Greedy**: Solusi cepat, tetapi mungkin tidak optimal
- **A\***: Biasanya memberikan solusi optimal secara efisien
- **Simulated Annealing**: Baik untuk puzzle yang sangat kompleks, tetapi mungkin tidak menemukan solusi optimal

## 👥 Kontributor

<p align="center">
  <table>
    <tr align="center">
      <td>
        <img src="https://github.com/Awfidz.png" width="100" height="100"><br>
        <b>Ahmad Wafi Idzharulhaqq</b><br>
        13523131
      </td>
      <td>
        <img src="https://github.com/rlukassa.png" width="100" height="100"><br>
        <b>Lukas Raja Agripa</b><br>
        13523158
      </td>
    </tr>
  </table>
</p>

