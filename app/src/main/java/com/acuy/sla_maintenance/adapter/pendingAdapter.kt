package com.acuy.sla_maintenance.adapter

import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.acuy.sla_maintenance.databinding.LayoutPendingBinding
import com.acuy.sla_maintenance.model.DataItemIdWorker
import java.util.Locale

class PendingAdapter(private val dataList: List<DataItemIdWorker>) :
    RecyclerView.Adapter<PendingAdapter.PendingViewHolder>() {

    inner class PendingViewHolder(private val binding: LayoutPendingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(dataItem: DataItemIdWorker) {
            binding.apply {
// Format waktu
                val originalFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                originalFormat.timeZone = TimeZone.getTimeZone("UTC") // Sesuaikan dengan zona waktu database

                val targetFormat = SimpleDateFormat("dd MMMM yyyy '||' HH:mm:ss", Locale.getDefault())
                targetFormat.timeZone = TimeZone.getDefault() // Gunakan zona waktu perangkat

                val formattedStartTime = dataItem.startTime?.let {
                    try {
                        val date = originalFormat.parse(it)
                        date?.let { targetFormat.format(date) }
                    } catch (e: Exception) {
                        "-"
                    }
                } ?: "-"

                val formattedEndTime = dataItem.endTime?.let {
                    try {
                        val date = originalFormat.parse(it)
                        date?.let { targetFormat.format(date) }
                    } catch (e: Exception) {
                        "-"
                    }
                } ?: "-"

                val waktuHandle = dataItem.workDuration

// Mengonversi waktu ke format jam menit
                val waktuHandleText = waktuHandle?.let {
                    val parts = it.split(":")
                    if (parts.size == 3) {
                        "${parts[0]} jam ${parts[1]} menit ${parts[2]} detik"
                    } else {
                        "Format waktu tidak valid"
                    }
                } ?: "-"
                // Bind data ke tampilan UI di sini
                panggilUsernamPending.text = dataItem.username ?: "-"
                panggilTanggalReportPending.text = formattedStartTime
                panggilTanggalSelesaiPending.text = formattedEndTime
                binding.panggilWaktuHandlePending.text = waktuHandleText
                panggilDeskripsiPending.text = dataItem.deskripsiPending ?: "-"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingViewHolder {
        val binding = LayoutPendingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PendingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PendingViewHolder, position: Int) {
        val dataItem = dataList[position]
        holder.bind(dataItem)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}
