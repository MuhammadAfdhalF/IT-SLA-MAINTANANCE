<!doctype html>
<html lang="id">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <meta name="description" content="IT SLA Maintenance — Aplikasi Maintenance Internal IT untuk PT Nusantara Infrastructure Tbk (PT Makassar Metro Network & PT Makassar Airport Network). Dibangun dengan Kotlin & Retrofit, terintegrasi Web Admin & Mobile.">
  <title>IT SLA Maintenance – PT Nusantara Infrastructure Tbk</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
  <style>
    :root {
      --brand-start: #0ea5e9; /* sky-500 */
      --brand-end: #2563eb;   /* indigo-600 */
    }
    html { scroll-behavior: smooth; }
    .hero {
      background: linear-gradient(135deg, var(--brand-start), var(--brand-end));
      color: #fff;
      border-bottom-left-radius: 1rem;
      border-bottom-right-radius: 1rem;
    }
    .badge-tech {
      background: rgba(255,255,255,.15);
      border: 1px solid rgba(255,255,255,.35);
      color: #fff;
      backdrop-filter: blur(6px);
    }
    .section-title { font-weight: 700; letter-spacing: .2px; }
    .card-icon { font-size: 1.75rem; line-height: 1; }
    .toc a { text-decoration: none; }
    pre code { white-space: pre-wrap; }
    .feature-check li::marker { color: #16a34a; }
    .footer { background: #0b1220; color: #cbd5e1; }
    .shadow-soft { box-shadow: 0 10px 30px rgba(2,6,23,.12); }
    .anchor-offset { scroll-margin-top: 90px; }
  </style>
</head>
<body>

  <!-- NAVBAR -->
  <nav class="navbar navbar-expand-lg bg-body-tertiary sticky-top border-bottom">
    <div class="container">
      <a class="navbar-brand fw-semibold" href="#top">IT SLA Maintenance</a>
      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#nav" aria-controls="nav" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
      </button>
      <div id="nav" class="collapse navbar-collapse">
        <ul class="navbar-nav ms-auto mb-2 mb-lg-0">
          <li class="nav-item"><a class="nav-link" href="#about">Tentang</a></li>
          <li class="nav-item"><a class="nav-link" href="#tech">Teknologi</a></li>
          <li class="nav-item"><a class="nav-link" href="#architecture">Arsitektur</a></li>
          <li class="nav-item"><a class="nav-link" href="#features">Fitur</a></li>
          <li class="nav-item"><a class="nav-link" href="#impact">Dampak</a></li>
          <li class="nav-item"><a class="nav-link" href="#getting-started">Instalasi</a></li>
          <li class="nav-item"><a class="nav-link" href="#author">Author</a></li>
        </ul>
      </div>
    </div>
  </nav>

  <!-- HERO -->
  <header id="top" class="hero py-5">
    <div class="container py-3">
      <div class="row align-items-center g-4">
        <div class="col-lg-7">
          <h1 class="display-5 fw-bold mb-3">🔧 IT SLA Maintenance – PT Nusantara Infrastructure Tbk</h1>
          <p class="lead mb-4">
            Aplikasi <strong>Maintenance Internal IT</strong> untuk divisi IT di
            <strong>PT Makassar Metro Network</strong> dan <strong>PT Makassar Airport Network</strong>.
            Memudahkan pengelolaan inventaris & pekerjaan maintenance, serta memantau
            <em>jam kerja teknisi</em> dan performa berdasarkan SLA.
          </p>
          <div class="d-flex flex-wrap gap-2 mb-4">
            <span class="badge rounded-pill badge-tech">📱 Kotlin (Android)</span>
            <span class="badge rounded-pill badge-tech">🔗 Retrofit (RESTful API)</span>
            <span class="badge rounded-pill badge-tech">🌐 Admin Web Terintegrasi</span>
            <span class="badge rounded-pill badge-tech">🗄️ SQL Database</span>
          </div>
          <div class="d-flex gap-2">
            <a href="#getting-started" class="btn btn-light text-dark fw-semibold shadow-soft">Mulai Cepat</a>
            <a href="#features" class="btn btn-outline-light fw-semibold">Lihat Fitur</a>
          </div>
        </div>
        <div class="col-lg-5">
          <div class="card shadow-soft border-0">
            <div class="card-body p-4">
              <div class="d-flex align-items-center mb-3">
                <div class="card-icon me-3">⏱️</div>
                <div>
                  <h5 class="mb-1">SLA-first Workflow</h5>
                  <p class="mb-0 text-secondary">Semua alur kerja berfokus pada pencapaian target SLA.</p>
                </div>
              </div>
              <div class="d-flex align-items-center mb-3">
                <div class="card-icon me-3">🔄</div>
                <div>
                  <h5 class="mb-1">Sinkron Real-time</h5>
                  <p class="mb-0 text-secondary">Mobile (User) & Web (Admin) terhubung melalui REST API.</p>
                </div>
              </div>
              <div class="d-flex align-items-center">
                <div class="card-icon me-3">📊</div>
                <div>
                  <h5 class="mb-1">Monitoring Kinerja</h5>
                  <p class="mb-0 text-secondary">Pantau jam kerja teknisi & performa maintenance.</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </header>

  <!-- TABLE OF CONTENTS -->
  <section class="py-5">
    <div class="container">
      <div class="row g-4">
        <div class="col-lg-4">
          <div class="card border-0 shadow-soft">
            <div class="card-body">
              <h5 class="card-title mb-3">📚 Daftar Isi</h5>
              <div class="list-group toc">
                <a class="list-group-item list-group-item-action" href="#about">📌 Tentang Proyek</a>
                <a class="list-group-item list-group-item-action" href="#tech">🛠️ Teknologi</a>
                <a class="list-group-item list-group-item-action" href="#architecture">🌐 Arsitektur Sistem</a>
                <a class="list-group-item list-group-item-action" href="#features">⚙️ Fitur Utama</a>
                <a class="list-group-item list-group-item-action" href="#impact">📈 Hasil & Dampak</a>
                <a class="list-group-item list-group-item-action" href="#getting-started">⚡ Cara Instal & Run</a>
                <a class="list-group-item list-group-item-action" href="#author">🙋‍♂️ Author</a>
              </div>
            </div>
          </div>
        </div>
        <div class="col-lg-8">
          <!-- ABOUT -->
          <div id="about" class="anchor-offset mb-5">
            <h2 class="section-title mb-3">📌 Tentang Proyek</h2>
            <p>
              <strong>IT SLA Maintenance</strong> adalah aplikasi internal untuk membantu divisi IT pada
              <strong>PT Makassar Metro Network</strong> dan <strong>PT Makassar Airport Network</strong>
              (grup <strong>PT Nusantara Infrastructure Tbk</strong>) dalam mengelola
              <em>maintenance</em> peralatan & inventaris IT serta memastikan
              <abbr title="Service Level Agreement">SLA</abbr> tercapai.
            </p>
            <p>
              Aplikasi mobile (Android) dibangun dengan <strong>Kotlin</strong> dan komunikasi data
              menggunakan <strong>Retrofit RESTful API</strong>. Sistem terintegrasi dengan
              <strong>website admin</strong> untuk manajemen data, pelaporan, dan analitik performa.
            </p>
            <blockquote class="blockquote ps-3 border-start border-3">
              <p class="mb-0">“SLA Maintenance App – Solusi digital untuk monitoring dan manajemen maintenance IT.”</p>
            </blockquote>
          </div>

          <!-- TECHNOLOGIES -->
          <div id="tech" class="anchor-offset mb-5">
            <h2 class="section-title mb-3">🛠️ Teknologi yang Digunakan</h2>
            <div class="row g-3">
              <div class="col-md-6">
                <div class="card h-100 border-0 shadow-sm">
                  <div class="card-body">
                    <div class="card-icon">📱</div>
                    <h5 class="mt-2">Mobile App</h5>
                    <p class="text-secondary mb-0">Kotlin (Android) + Material Design UI.</p>
                  </div>
                </div>
              </div>
              <div class="col-md-6">
                <div class="card h-100 border-0 shadow-sm">
                  <div class="card-body">
                    <div class="card-icon">🔗</div>
                    <h5 class="mt-2">API Communication</h5>
                    <p class="text-secondary mb-0">Retrofit 2 – RESTful API, JSON, Interceptor.</p>
                  </div>
                </div>
              </div>
              <div class="col-md-6">
                <div class="card h-100 border-0 shadow-sm">
                  <div class="card-body">
                    <div class="card-icon">🌐</div>
                    <h5 class="mt-2">Backend / Admin Web</h5>
                    <p class="text-secondary mb-0">Dashboard admin terintegrasi (manajemen user, inventaris, laporan).</p>
                  </div>
                </div>
              </div>
              <div class="col-md-6">
                <div class="card h-100 border-0 shadow-sm">
                  <div class="card-body">
                    <div class="card-icon">🗄️</div>
                    <h5 class="mt-2">Database</h5>
                    <p class="text-secondary mb-0">Relational DB (SQL-based), audit log & timestamps.</p>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- ARCHITECTURE -->
          <div id="architecture" class="anchor-offset mb-5">
            <h2 class="section-title mb-3">🌐 Arsitektur Sistem</h2>
            <div class="row g-3">
              <div class="col-md-4">
                <div class="card h-100 border-0 shadow-sm">
                  <div class="card-body">
                    <h6 class="text-uppercase text-secondary">Mobile (User/Technician)</h6>
                    <ul class="mb-0 small">
                      <li>Input & update tiket maintenance</li>
                      <li>Lihat jadwal & riwayat pekerjaan</li>
                      <li>Tracking status & SLA</li>
                    </ul>
                  </div>
                </div>
              </div>
              <div class="col-md-4">
                <div class="card h-100 border-0 shadow-sm">
                  <div class="card-body">
                    <h6 class="text-uppercase text-secondary">API Layer</h6>
                    <ul class="mb-0 small">
                      <li>RESTful endpoints (JSON)</li>
                      <li>Auth, rate-limit, logging</li>
                      <li>Sinkronisasi real-time</li>
                    </ul>
                  </div>
                </div>
              </div>
              <div class="col-md-4">
                <div class="card h-100 border-0 shadow-sm">
                  <div class="card-body">
                    <h6 class="text-uppercase text-secondary">Admin Web</h6>
                    <ul class="mb-0 small">
                      <li>Manajemen inventaris & asset</li>
                      <li>Monitoring jam kerja teknisi</li>
                      <li>Laporan performa & SLA</li>
                    </ul>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- FEATURES -->
          <div id="features" class="anchor-offset mb-5">
            <h2 class="section-title mb-3">⚙️ Fitur Utama</h2>
            <div class="row g-3">
              <div class="col-md-6">
                <div class="card h-100 border-0 shadow-sm">
                  <div class="card-body">
                    <h5>📥 Tiket Maintenance</h5>
                    <p class="mb-0 text-secondary">Buat, perbarui, dan tutup tiket dengan lampiran foto & catatan.</p>
                  </div>
                </div>
              </div>
              <div class="col-md-6">
                <div class="card h-100 border-0 shadow-sm">
                  <div class="card-body">
                    <h5>⏱️ SLA Tracking</h5>
                    <p class="mb-0 text-secondary">Status SLA per tiket (response & resolution time) terlihat jelas.</p>
                  </div>
                </div>
              </div>
              <div class="col-md-6">
                <div class="card h-100 border-0 shadow-sm">
                  <div class="card-body">
                    <h5>👨‍🔧 Monitoring Teknisi</h5>
                    <p class="mb-0 text-secondary">Rekap jam kerja & performa maintenance per teknisi.</p>
                  </div>
                </div>
              </div>
              <div class="col-md-6">
                <div class="card h-100 border-0 shadow-sm">
                  <div class="card-body">
                    <h5>📦 Inventaris & Peralatan</h5>
                    <p class="mb-0 text-secondary">Manajemen barang/peralatan internal IT beserta histori pemeliharaan.</p>
                  </div>
                </div>
              </div>
              <div class="col-md-6">
                <div class="card h-100 border-0 shadow-sm">
                  <div class="card-body">
                    <h5>🔔 Notifikasi</h5>
                    <p class="mb-0 text-secondary">Pemberitahuan status tiket & pengingat tenggat SLA.</p>
                  </div>
                </div>
              </div>
              <div class="col-md-6">
                <div class="card h-100 border-0 shadow-sm">
                  <div class="card-body">
                    <h5>📊 Laporan & Dashboard</h5>
                    <p class="mb-0 text-secondary">Ringkasan kinerja, SLA compliance, dan metrik operasional.</p>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- IMPACT -->
          <div id="impact" class="anchor-offset mb-5">
            <h2 class="section-title mb-3">📈 Hasil & Dampak</h2>
            <ul class="feature-check">
              <li><strong>Efisiensi waktu:</strong> alur pencatatan & pelaporan lebih cepat.</li>
              <li><strong>Akurasi data:</strong> histori maintenance tercatat otomatis & rapi.</li>
              <li><strong>Transparansi:</strong> manajemen dapat memantau progres real-time.</li>
              <li><strong>Evaluasi objektif:</strong> performa teknisi & kepatuhan SLA terukur.</li>
            </ul>
          </div>

          <!-- GETTING STARTED -->
          <div id="getting-started" class="anchor-offset mb-5">
            <h2 class="section-title mb-3">⚡ Cara Instal & Run (Mobile)</h2>
            <ol class="mb-4">
              <li>Clone proyek Android (Kotlin) ke mesin lokal.</li>
              <li>Buka di <strong>Android Studio</strong> (Giraffe+).</li>
              <li>Set <code>BASE_URL</code> Retrofit ke endpoint REST API backend Anda.</li>
              <li>Tambahkan dependensi utama pada <code>build.gradle</code> (Module):</li>
            </ol>
<pre class="bg-light p-3 rounded border"><code>// build.gradle (Module)
dependencies {
    implementation "com.squareup.retrofit2:retrofit:2.11.0"
    implementation "com.squareup.retrofit2:converter-gson:2.11.0"
    implementation "com.squareup.okhttp3:logging-interceptor:4.12.0"

    implementation "androidx.core:core-ktx:1.13.1"
    implementation "androidx.appcompat:appcompat:1.7.0"
    implementation "com.google.android.material:material:1.12.0"
}
</code></pre>
            <ol start="5">
              <li>Konfigurasi <strong>permissions</strong> yang diperlukan (internet, kamera jika ada dokumentasi foto).</li>
              <li>Jalankan aplikasi di emulator atau perangkat fisik.</li>
            </ol>
          </div>

          <!-- AUTHOR -->
          <div id="author" class="anchor-offset mb-4">
            <h2 class="section-title mb-3">🙋‍♂️ Author</h2>
            <div class="card border-0 shadow-sm">
              <div class="card-body">
                <h5 class="mb-1">PT Nusantara Infrastructure Tbk – Divisi IT</h5>
                <p class="mb-2 text-secondary">
                  Proyek untuk: <strong>PT Makassar Metro Network</strong> &amp; <strong>PT Makassar Airport Network</strong>.
                </p>
                <ul class="mb-0">
                  <li>Teknologi: Kotlin, Retrofit RESTful API, Admin Web, SQL Database</li>
                  <li>Integrasi: Website (Admin) &amp; Mobile (User)</li>
                </ul>
              </div>
            </div>
          </div>

          <div class="text-end">
            <a href="#top" class="btn btn-outline-secondary btn-sm">⬆️ Kembali ke atas</a>
          </div>
        </div>
      </div>
    </div>
  </section>

  <footer class="footer py-4">
    <div class="container text-center small">
      © <span id="year"></span> IT SLA Maintenance • PT Nusantara Infrastructure Tbk
    </div>
  </footer>

  <script>
    document.getElementById('year').textContent = new Date().getFullYear();
  </script>
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHzQ" crossorigin="anonymous"></script>
</body>
</html>
