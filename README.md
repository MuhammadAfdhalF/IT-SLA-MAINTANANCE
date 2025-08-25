<!doctype html>
<html lang="id">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>IT SLA Maintenance – PT Nusantara Infrastructure Tbk</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <style>
    .hero {
      background: linear-gradient(135deg, #0ea5e9, #2563eb);
      color: #fff;
      padding: 3rem 0;
      border-bottom-left-radius: 1rem;
      border-bottom-right-radius: 1rem;
    }
    .section-title { font-weight: 700; margin-bottom: 1rem; }
    .card-icon { font-size: 1.5rem; }
    .footer { background: #0b1220; color: #cbd5e1; padding: 1rem 0; }
  </style>
</head>
<body>

  <!-- HERO -->
  <header class="hero text-center">
    <div class="container">
      <h1 class="fw-bold">🔧 IT SLA Maintenance</h1>
      <p class="lead mb-0">Aplikasi Maintenance Internal IT • PT Nusantara Infrastructure Tbk</p>
      <p class="small">PT Makassar Metro Network & PT Makassar Airport Network</p>
    </div>
  </header>

  <main class="container my-5">

    <!-- ABOUT -->
    <section id="about" class="mb-5">
      <h2 class="section-title">📌 Tentang Proyek</h2>
      <p>
        <strong>IT SLA Maintenance</strong> adalah aplikasi internal divisi IT pada 
        <strong>PT Makassar Metro Network</strong> dan <strong>PT Makassar Airport Network</strong> 
        (bagian dari <strong>PT Nusantara Infrastructure Tbk</strong>). 
        Aplikasi ini digunakan untuk mengelola <em>maintenance</em> barang dan peralatan IT, 
        serta memantau <strong>jam kerja teknisi</strong> berdasarkan performa pemeliharaan 
        sesuai <abbr title="Service Level Agreement">SLA</abbr>.
      </p>
    </section>

    <!-- TECHNOLOGIES -->
    <section id="tech" class="mb-5">
      <h2 class="section-title">🛠️ Teknologi</h2>
      <ul>
        <li>📱 <strong>Kotlin</strong> – Mobile Android</li>
        <li>🔗 <strong>Retrofit</strong> – RESTful API communication</li>
        <li>🌐 <strong>Website Admin</strong> – Manajemen data & laporan</li>
        <li>🗄️ <strong>SQL Database</strong> – Penyimpanan inventaris & histori maintenance</li>
      </ul>
    </section>

    <!-- ARCHITECTURE -->
    <section id="architecture" class="mb-5">
      <h2 class="section-title">🌐 Arsitektur Sistem</h2>
      <div class="row g-3">
        <div class="col-md-4">
          <div class="card h-100">
            <div class="card-body">
              <div class="card-icon">📱</div>
              <h5 class="mt-2">Mobile App (User)</h5>
              <p class="small mb-0">Input laporan maintenance, cek jadwal, tracking SLA, riwayat pekerjaan.</p>
            </div>
          </div>
        </div>
        <div class="col-md-4">
          <div class="card h-100">
            <div class="card-body">
              <div class="card-icon">🔗</div>
              <h5 class="mt-2">API Layer</h5>
              <p class="small mb-0">Retrofit + RESTful API untuk sinkronisasi real-time antara Mobile & Web.</p>
            </div>
          </div>
        </div>
        <div class="col-md-4">
          <div class="card h-100">
            <div class="card-body">
              <div class="card-icon">🌐</div>
              <h5 class="mt-2">Admin Web</h5>
              <p class="small mb-0">Monitoring teknisi, manajemen inventaris, laporan SLA & performa.</p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- FEATURES -->
    <section id="features" class="mb-5">
      <h2 class="section-title">⚙️ Fitur Utama</h2>
      <ul>
        <li>📥 Input & update tiket maintenance dengan foto/lampiran</li>
        <li>⏱️ Tracking SLA: response time & resolution time</li>
        <li>👨‍🔧 Monitoring kinerja teknisi & jam kerja</li>
        <li>📦 Manajemen inventaris & histori peralatan IT</li>
        <li>🔔 Notifikasi status tiket & pengingat SLA</li>
        <li>📊 Dashboard laporan performa SLA</li>
      </ul>
    </section>

    <!-- IMPACT -->
    <section id="impact" class="mb-5">
      <h2 class="section-title">📈 Hasil & Dampak</h2>
      <ul>
        <li>✅ Efisiensi waktu dalam pencatatan & laporan</li>
        <li>✅ Akurasi & transparansi data maintenance</li>
        <li>✅ Monitoring real-time progres teknisi</li>
        <li>✅ Evaluasi SLA lebih objektif & terukur</li>
      </ul>
    </section>

    <!-- AUTHOR -->
    <section id="author" class="mb-5">
      <h2 class="section-title">🙋‍♂️ Author</h2>
      <p><strong>Divisi IT – PT Nusantara Infrastructure Tbk</strong></p>
      <p class="small mb-0">Proyek untuk: PT Makassar Metro Network & PT Makassar Airport Network.</p>
    </section>

  </main>

  <footer class="footer text-center">
    <div class="container small">
      © <span id="year"></span> IT SLA Maintenance • PT Nusantara Infrastructure Tbk
    </div>
  </footer>

  <script>
    document.getElementById('year').textContent = new Date().getFullYear();
  </script>
</body>
</html>
