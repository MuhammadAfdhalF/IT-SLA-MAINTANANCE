package com.acuy.sla_maintenance.adapter


import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.acuy.sla_maintenance.HalamanDetailReport
import com.acuy.sla_maintenance.R
import com.acuy.sla_maintenance.config.Images
import com.acuy.sla_maintenance.databinding.LayoutReportListBinding
import com.acuy.sla_maintenance.model.DataReport2
import java.util.Locale
import java.util.concurrent.TimeUnit

class ReportAdapter(
    private var listReport: List<DataReport2?>?
) : RecyclerView.Adapter<ReportAdapter.ViewHolder>() {

    private val previousData = mutableListOf<DataReport2?>()
    // Method untuk menambahkan data baru ke adapter
    fun addData(newData: List<DataReport2?>) {
        listReport = listReport.orEmpty().toMutableList().apply {
            addAll(newData)
        }
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: LayoutReportListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // Tambahkan context sebagai parameter konstruktor
        constructor(context: Context, binding: LayoutReportListBinding) : this(binding) {
            this.context = context
        }

        private lateinit var context: Context

        fun onBindItem(dataReport: DataReport2?) {
//            val lokasiText = getLokasiText(dataReport?.lokasiId)
            binding.panggilLokasiListReport.text = dataReport?.locationName
            binding.panggilIdListReport.text = dataReport?.id.toString()
            binding.panggilCatatanListReport.text = dataReport?.catatan?: "Tidak ada catatan"
            binding.panggilPelapor.text = dataReport?.namaUser





            // Set status text
            val status = dataReport?.status ?: ""
            val finalStatus = if (status.isBlank()) "menunggu" else status
            binding.panggilStatusReport.text = finalStatus

            // Set background color for cardStatusReportList based on status
            val backgroundColor = getStatusBackgroundColor(itemView.context, status)
            binding.cardStatusReportList.setCardBackgroundColor(backgroundColor)

            // Date
            val originalFormat =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val targetFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
            val date = originalFormat.parse(dataReport?.createdAt.toString())
            val formattedDate = targetFormat.format(date)
            binding.panggilTanggalReportList.text = formattedDate


        }
    }

    private fun getStatusBackgroundColor(context: Context, status: String): Int {
        return when (status) {
            "pending" -> ContextCompat.getColor(context, R.color.red)
            "process" -> ContextCompat.getColor(context, R.color.yellow)
            "done" -> ContextCompat.getColor(context, R.color.green)
            else -> ContextCompat.getColor(context, R.color.blue)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            LayoutReportListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(parent.context, binding) // Menambahkan context sebagai parameter
    }

//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.onBindItem(listReport?.get(position))
//    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sortedList = sortListByStatus(listReport)
        val dataReport = sortedList?.get(position)
        holder.onBindItem(dataReport)

////         Atur margin bottom untuk item kecuali untuk item terakhir
//        val layoutParams = holder.itemView.layoutParams as RecyclerView.LayoutParams
//        holder.itemView.layoutParams = layoutParams
//



        // Tambahkan onClickListener untuk membuka HalamanDetailReport saat item diklik
        holder.itemView.setOnClickListener {
            // Pastikan dataReport tidak null sebelum membuka HalamanDetailReport
            dataReport?.let {
                val intent =
                    Intent(holder.itemView.context, HalamanDetailReport::class.java).apply {
                        // Kirim data laporan ke HalamanDetailReport menggunakan intent
                        putExtra("id", it.id)
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
                        val imageUrl = "${images.BASE_URL}${dataReport?.fotoAwal}"
                        putExtra("foto_awal", imageUrl)

                        // Penyesuaian untuk penanganan waktu
                        putExtra("created_at", it.createdAt)
                        putExtra("ended_at", it.endedAt)
                        putExtra("kondisi_akhir", it.kondisiAkhir)
                        putExtra("status", it.status)
                        putExtra("nama_user", it.namaUser)
                        putExtra("waktu_pengerjaan", it.waktuPengerjaan)
                        putExtra("kategori_activity", it.kategoriActivity)

//                    putExtra("category", it.categoryName)


                        // Pengecekan null sebelum parsing tanggal
                        val originalFormat =
                            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                        val dateReport = it.createdAt?.let { originalFormat.parse(it) }
                        val dateSelesai = it.endedAt?.let { originalFormat.parse(it) }

                        if (dateReport != null && dateSelesai != null) {
                            val selisihMillis = dateSelesai.time - dateReport.time
                            val detik = TimeUnit.MILLISECONDS.toSeconds(selisihMillis) % 60
                            val menit = TimeUnit.MILLISECONDS.toMinutes(selisihMillis) % 60
                            val jam = TimeUnit.MILLISECONDS.toHours(selisihMillis) % 24
                            val hari = TimeUnit.MILLISECONDS.toDays(selisihMillis)

                            val waktuHandle = String.format(
                                "%d Hari %d Jam %d Menit %d Detik",
                                hari,
                                jam,
                                menit,
                                detik
                            )

                            putExtra("waktu_handle", waktuHandle)
                        } else {
                            val waktuHandle: String? =
                                null // Menggunakan tipe data String? dan memberi nilai default null
                            putExtra("waktu_handle", waktuHandle)
                        }

                        //fotoAKhir
                        val imageUrlAkhir = "${images.BASE_URL}${dataReport?.fotoAkhir}"
                        putExtra("foto_akhir", imageUrlAkhir)


                    }
                holder.itemView.context.startActivity(intent)
            }
        }
    }

    // Fungsi untuk mengurutkan list berdasarkan status
    private fun sortListByStatus(list: List<DataReport2?>?): List<DataReport2?>? {
        return list?.sortedBy {
            when (it?.status) {
                "pending" -> 1
                "process" -> 2
                "done" -> 3
                else -> 4
            }
        }
    }


    override fun getItemCount(): Int {
        return listReport?.size ?: 0
    }


}


//            val status = dataReport?.status ?: ""
//            val backgroundColor = getStatusBackgroundColor(context, status)
//            binding.panggilStatusReport.text = status
//            binding.panggilStatusReport.setBackgroundColor(backgroundColor)

//
//// Fungsi untuk mengembalikan teks lokasi berdasarkan lokasiId
//private fun getLokasiText(lokasiId: Int?): String {
//    return when (lokasiId) {
//        1 -> "Bira Barat"
//        2 -> "GTO 1 Bira Barat"
//        3 -> "Bira Barat GRD 02"
//        4 -> "Bira Barat Plaza"
//        5 -> "Bira Barat RTM"
//        6 -> "Gardu dan Plaza BRK"
//        7 -> "GTO 1 Biringkanaya"
//        8 -> "GTO 2 Biringkanaya"
//        10 -> "GTO 3 Biringkanaya"
//        11 -> "GRD 4 Biringkanaya"
//        12 -> "GRD 5 Biringkanaya"
//        13 -> "PCS Biringkanaya"
//        15 -> "RTM Biringkanay"
//        16 -> "Bira Timur"
//        17 -> "GTO 1 Bira Timur"
//        18 -> "GTO 02 Bira Timur"
//        19 -> "Gardu 03 Bira Timur"
//        20 -> "PCS Bira Timur"
//        21 -> "RTM Bira Timur"
//        22 -> "Cambaya"
//        23 -> "GTO 1 Cambaya"
//        24 -> "GTO 2 Cambaya"
//        25 -> "GRD3 CAMBAYA"
//        26 -> "GRD4 CAMBAYA"
//        27 -> "GTO 5 Cambaya"
//        else -> "Unknown Location"
//    }
//}


//package com.acuy.sla_maintenance.adapter
//
//import android.content.Context
//import android.icu.text.SimpleDateFormat
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.core.content.ContextCompat
//import androidx.recyclerview.widget.RecyclerView
//import com.acuy.sla_maintenance.R
//import com.acuy.sla_maintenance.databinding.LayoutReportListBinding
//import com.acuy.sla_maintenance.model.DataReport
//import java.util.Locale
//
//class ReportAdapter(
//
//    private val listReport: List<DataReport?>?
//
//) : RecyclerView.Adapter<ReportAdapter.ViewHolder>() {
//    inner class ViewHolder(val LayoutReportListBinding: LayoutReportListBinding) :
//        RecyclerView.ViewHolder(LayoutReportListBinding.root) {
//        fun onBindItem(dataReport: DataReport?) {
//
//            val lokasiText = getLokasiText(dataReport?.lokasiId)
//            LayoutReportListBinding.panggilLokasiListReport.text = lokasiText
//            LayoutReportListBinding.panggilIdListReport.text = dataReport?.id.toString()
//            LayoutReportListBinding.panggilCatatanListReport.text = dataReport?.catatan
////            LayoutReportListBinding.panggilStatusReport.text = dataReport?.status
//
//            val status = dataReport?.status ?: ""
//            val backgroundColor = getStatusBackgroundColor(status)
//            LayoutReportListBinding.panggilStatusReport.text = status
//            LayoutReportListBinding.panggilStatusReport.setBackgroundColor(backgroundColor)
//
//
//            //date
//            val originalFormat =
//                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
//            val targetFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
//            val date = originalFormat.parse(dataReport?.createdAt.toString())
//            val formattedDate = targetFormat.format(date)
//            LayoutReportListBinding.panggilTanggalReportList.text = formattedDate
//        }
//    }
//
//    private fun getStatusBackgroundColor(status: String): Int {
//        return when (status) {
//            "" -> ContextCompat.getColor(LayoutReportListBinding.root.context, R.color.blue)
//            "proses" -> ContextCompat.getColor(LayoutReportListBinding.root.context, R.color.yellow)
//            "done" -> ContextCompat.getColor(LayoutReportListBinding.root.context, R.color.green)
//            else -> ContextCompat.getColor(
//                LayoutReportListBinding.root.context,
//                R.color.blue
//            )
//        }
//    }
//
//    // Fungsi untuk mengembalikan teks lokasi berdasarkan lokasiId
//    private fun getLokasiText(lokasiId: Int?): String {
//        return when (lokasiId) {
//            1 -> "Bira Barat"
//            2 -> "GTO 1 Bira Barat"
//            3 -> "Bira Barat GRD 02"
//            4 -> "Bira Barat Plaza"
//            5 -> "Bira Barat RTM"
//            6 -> "Gardu dan Plaza BRK"
//            7 -> "GTO 1 Biringkanaya"
//            8 -> "GTO 2 Biringkanaya"
//            10 -> "GTO 3 Biringkanaya"
//            11 -> "GRD 4 Biringkanaya"
//            12 -> "GRD 5 Biringkanaya"
//            13 -> "PCS Biringkanaya"
//            15 -> "RTM Biringkanay"
//            16 -> "Bira Timur"
//            17 -> "GTO 1 Bira Timur"
//            18 -> "GTO 02 Bira Timur"
//            19 -> "Gardu 03 Bira Timur"
//            20 -> "PCS Bira Timur"
//            21 -> "RTM Bira Timur"
//            22 -> "Cambaya"
//            23 -> "GTO 1 Cambaya"
//            24 -> "GTO 2 Cambaya"
//            25 -> "GRD3 CAMBAYA"
//            26 -> "GRD4 CAMBAYA"
//            27 -> "GTO 5 Cambaya"
//            else -> "Unknown Lokasi"
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportAdapter.ViewHolder {
//        val binding =
//            LayoutReportListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return ViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: ReportAdapter.ViewHolder, position: Int) {
//        holder.onBindItem(listReport?.get(position))
//    }
//
//    override fun getItemCount(): Int {
//        return listReport?.size ?: 0
//    }
//
//}


//            LayoutReportListBinding.panggilTanggalReportList.text = dataReport?.createdAt.toString()
//            LayoutReportListBinding.panggilLokasiListReport.text = dataReport?.lokasiId.toString()