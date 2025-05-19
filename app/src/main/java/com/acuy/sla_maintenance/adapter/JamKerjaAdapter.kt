package com.acuy.sla_maintenance.adapter

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.acuy.sla_maintenance.HalamanDetailReportJam
import com.acuy.sla_maintenance.R
import com.acuy.sla_maintenance.databinding.LayoutListJamKerjaBinding
import com.acuy.sla_maintenance.model.DataItemJamKerja
import java.util.Locale

class JamKerjaAdapter(private val jamKerjaList: List<DataItemJamKerja>) :
    RecyclerView.Adapter<JamKerjaAdapter.JamKerjaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JamKerjaViewHolder {
        val binding =
            LayoutListJamKerjaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JamKerjaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: JamKerjaViewHolder, position: Int) {
        val currentItem = jamKerjaList[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int {
        return jamKerjaList.size
    }

    inner class JamKerjaViewHolder(private val binding: LayoutListJamKerjaBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(jamKerja: DataItemJamKerja) {
            // Format startTime
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            inputFormat.timeZone =
                TimeZone.getTimeZone("UTC") // Sesuaikan dengan zona waktu database

            val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
            outputFormat.timeZone = TimeZone.getDefault() // Gunakan zona waktu perangkat

            val formattedStartTime = jamKerja.startTime?.let {
                try {
                    val date = inputFormat.parse(it)
                    date?.let { outputFormat.format(date) }
                } catch (e: Exception) {
                    "Waktu Mulai Kosong"
                }
            } ?: "Waktu Mulai Kosong"

// Format workDuration
            val formattedWorkDuration = jamKerja.workDuration?.let {
                val parts = it.split(":")
                if (parts.size == 3) {
                    "${parts[0]} jam ${parts[1]} menit ${parts[2]} detik"
                } else {
                    "Durasi Kerja Kosong"
                }
            } ?: "Durasi Kerja Kosong"

// Set the formatted text
            binding.panggilIdActivity.text = jamKerja.activityId?.toString() ?: "ID Activity Kosong"
            binding.panggilJamKerja.text = formattedWorkDuration
            binding.tglJamKerja.text = formattedStartTime


            // Set status text and color
            binding.panggilStatus.text = jamKerja.status ?: "Status Kosong"
            val statusColor = when (jamKerja.status) {
                "pending" -> ContextCompat.getColor(binding.root.context, R.color.red)
                "done" -> ContextCompat.getColor(binding.root.context, R.color.green)
                else -> ContextCompat.getColor(binding.root.context, android.R.color.black)
            }
            binding.cardStatus.setCardBackgroundColor(statusColor)

            binding.root.setOnClickListener {
                val context = binding.root.context
                val intent = Intent(context, HalamanDetailReportJam::class.java).apply {
                    putExtra("EXTRA_ACTIVITY_ID", jamKerja.activityId)

                }
                context.startActivity(intent)
            }

        }
    }

}


//package com.acuy.sla_maintenance.adapter
//
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import com.acuy.sla_maintenance.databinding.LayoutListJamKerjaBinding
//import com.acuy.sla_maintenance.model.DataItemJamKerja
//class JamKerjaAdapter(private val jamKerjaList: List<DataItemJamKerja>) :
//    RecyclerView.Adapter<JamKerjaAdapter.JamKerjaViewHolder>() {
//
//    // Simpan semua idActivity di sini
//    private val idActivityList = mutableListOf<Int>()
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JamKerjaViewHolder {
//        val binding = LayoutListJamKerjaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return JamKerjaViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: JamKerjaViewHolder, position: Int) {
//        val currentItem = jamKerjaList[position]
//        holder.bind(currentItem)
//
//        // Simpan idActivity dari setiap item ke dalam list
//        currentItem.activityId?.let { idActivityList.add(it) }
//    }
//
//    override fun getItemCount(): Int {
//        return jamKerjaList.size
//    }
//
//    inner class JamKerjaViewHolder(private val binding: LayoutListJamKerjaBinding) : RecyclerView.ViewHolder(binding.root) {
//
//        fun bind(jamKerja: DataItemJamKerja) {
//            binding.panggilIdActivity.text = jamKerja.activityId?.toString() ?: "ID Activity Kosong"
//            binding.panggilJamKerja.text = jamKerja.workDuration ?: "Durasi Kerja Kosong"
//            binding.tglJamKerja.text = jamKerja.startTime ?: "Waktu Mulai Kosong"
//        }
//    }
//
//    // Fungsi untuk mendapatkan semua idActivity dari jamKerjaList
//    fun getAllIdActivities(): List<Int> {
//        val idActivities = mutableListOf<Int>()
//        for (jamKerja in jamKerjaList) {
//            jamKerja.activityId?.let { idActivities.add(it) }
//        }
//        return idActivities
//    }
//}
