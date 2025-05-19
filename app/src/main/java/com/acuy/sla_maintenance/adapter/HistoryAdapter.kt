package com.acuy.sla_maintenance.adapter

import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.acuy.sla_maintenance.HalamanDetailReport
import com.acuy.sla_maintenance.config.Images
import com.acuy.sla_maintenance.databinding.LayoutHistoryBinding
import com.acuy.sla_maintenance.model.DataItem
import java.util.Locale
import java.util.concurrent.TimeUnit

class HistoryAdapter(
    private val historyData: List<DataItem?>?
) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: LayoutHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private lateinit var context: Context

        // Tambahkan context sebagai parameter konstruktor
        constructor(context: Context, binding: LayoutHistoryBinding) : this(binding) {
            this.context = context
        }

        fun onBindItem(dataItem: DataItem?) {
            binding.panggilId.text = dataItem?.id.toString()
            binding.panggilLokasi.text = dataItem?.locationName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(parent.context, binding) // Menambahkan context sebagai parameter
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dataItem = historyData?.get(position)
        holder.onBindItem(dataItem)

        // Atur margin bottom untuk item kecuali untuk item terakhir
        val layoutParams = holder.itemView.layoutParams as RecyclerView.LayoutParams
        if (position < itemCount - 1) {
            layoutParams.bottomMargin = 0
        } else {
            // Jika item terakhir, atur margin bottom menjadi 0
            layoutParams.bottomMargin = 0
        }
        holder.itemView.layoutParams = layoutParams

        // Tambahkan onClickListener untuk membuka HalamanDetailReport saat item diklik
        holder.itemView.setOnClickListener {
            // Pastikan dataItem tidak null sebelum membuka HalamanDetailReport
            dataItem?.let {
                val intent = Intent(holder.itemView.context, HalamanDetailReport::class.java).apply {
                    // Kirim data laporan ke HalamanDetailReport menggunakan intent
                    putExtra("id", it.id)
//                    putExtra("lokasi_id", it.lokasiId?.let { getFormattedLocationName(it) } ?: "Unknown Location")
                    putExtra("locationName", it.locationName)

                    putExtra("catatan", it.catatan)
                    putExtra("date", it.createdAt)
                    putExtra("company", it.company)
                    putExtra("jenisHardware", it.jenisHardware)
                    putExtra("uraianHardware", it.uraianHardware)
                    putExtra("standartAplikasi", it.standartAplikasi)
                    putExtra("uraianAplikasi", it.uraianAplikasi)
                    putExtra("aplikasiItTol", it.aplikasiItTol)
                    putExtra("uraianItTol", it.uraianItTol)
                    putExtra("biaya", it.biaya)
                    putExtra("shift", it.shift)
                    val images = Images()
                    val imageUrl = "${images.BASE_URL}${it?.fotoAwal}"
                    putExtra("foto_awal", imageUrl)
                    putExtra("status", it.status)
                    putExtra("nama_user", it.namaUser)
                    putExtra("waktu_pengerjaan", it.waktuPengerjaan)



                    // Penyesuaian untuk penanganan waktu
                    putExtra("created_at", it.createdAt)
                    putExtra("ended_at", it.endedAt)
                    putExtra("kondisi_akhir", it.kondisiAkhir)
                    putExtra("kategori_activity", it.kategoriActivity)


//                    // Pengecekan null sebelum parsing tanggal
//                    val originalFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
//                    val dateReport = it.createdAt?.let { originalFormat.parse(it) }
//                    val dateSelesai = it.endedAt?.let { originalFormat.parse(it) }
//
//                    if (dateReport != null && dateSelesai != null) {
//                        val selisihMillis = dateSelesai.time - dateReport.time
//                        val detik = TimeUnit.MILLISECONDS.toSeconds(selisihMillis) % 60
//                        val menit = TimeUnit.MILLISECONDS.toMinutes(selisihMillis) % 60
//                        val jam = TimeUnit.MILLISECONDS.toHours(selisihMillis) % 24
//                        val hari = TimeUnit.MILLISECONDS.toDays(selisihMillis)
//
//                        val waktuHandle = String.format(
//                            "%d Hari %d Jam %d Menit",
//                            hari,
//                            jam,
//                            menit
//                        )
//
//                        putExtra("waktu_handle", waktuHandle)
//                    } else {
//                        val waktuHandle: String? = null // Menggunakan tipe data String? dan memberi nilai default null
//                        putExtra("waktu_handle", waktuHandle)
//                    }

                    //fotoAKhir
                    val imageUrlAkhir = "${images.BASE_URL}${it?.fotoAkhir}"
                    putExtra("foto_akhir", imageUrlAkhir)
                }
                holder.itemView.context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return historyData?.size ?: 0
    }


    fun getFormattedLocationName(lokasiId: Int): String {
        return when (lokasiId) {
            1 -> "Bira Barat"
            2 -> "GTO 1 Bira Barat"
            3 -> "Bira Barat GRD 02"
            4 -> "Bira Barat Plaza"
            5 -> "Bira Barat RTM"
            6 -> "Gardu dan Plaza BRK"
            7 -> "GTO 1 Biringkanaya"
            8 -> "GTO 2 Biringkanaya"
            10 -> "GTO 3 Biringkanaya"
            11 -> "GRD 4 Biringkanaya"
            12 -> "GRD 5 Biringkanaya"
            13 -> "PCS Biringkanaya"
            15 -> "RTM Biringkanay"
            16 -> "Bira Timur"
            17 -> "GTO 1 Bira Timur"
            18 -> "GTO 02 Bira Timur"
            19 -> "Gardu 03 Bira Timur"
            20 -> "PCS Bira Timur"
            21 -> "RTM Bira Timur"
            22 -> "Cambaya"
            23 -> "GTO 1 Cambaya"
            24 -> "GTO 2 Cambaya"
            25 -> "GRD3 CAMBAYA"
            26 -> "GRD4 CAMBAYA"
            27 -> "GTO 5 Cambaya"
            else -> "Unknown Location"
        }
    }

}






//class HistoryAdapter(
//    private val historyReport: List<DataReport2?>?
//) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {
//
//    inner class ViewHolder(private val binding: LayoutHistoryBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//
//
//        // Tambahkan context sebagai parameter konstruktor
//        constructor(context: Context, binding: LayoutHistoryBinding) : this(binding) {
//            this.context = context
//        }
//
//        private lateinit var context: Context
//
//        fun onBindItem(dataReport: DataReport2?) {
//            binding.panggilId.text = dataReport?.id.toString()
//            binding.panggilLokasi.text = dataReport?.locationName
//        }
//    }
//
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryAdapter.ViewHolder {
//        val binding = LayoutHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return ViewHolder(parent.context, binding) // Menambahkan context sebagai parameter
//    }
//
//    override fun onBindViewHolder(holder: ReportAdapter.ViewHolder, position: Int) {
//        val dataReport = historyReport?.get(position)
//        holder.onBindItem(dataReport)
//    }
//
//
//    override fun getItemCount(): Int {
//        return historyReport?.size ?: 0
//    }
//
//}
