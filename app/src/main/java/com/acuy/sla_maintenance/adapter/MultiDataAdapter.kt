package com.acuy.sla_maintenance.adapter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.acuy.sla_maintenance.databinding.LayoutListJamKerjaBinding
import com.acuy.sla_maintenance.model.DataItemJamKerja
import com.acuy.sla_maintenance.model.DataReport2

class MultiDataAdapter(
    private var jamKerjaList: List<DataItemJamKerja>,
    private var reportList: List<DataReport2>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_JAM_KERJA = 1
    private val VIEW_TYPE_REPORT = 2

    fun updateData(newJamKerjaList: List<DataItemJamKerja>, newReportList: List<DataReport2>) {
        jamKerjaList = newJamKerjaList
        reportList = newReportList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_JAM_KERJA -> {
                val binding = LayoutListJamKerjaBinding.inflate(inflater, parent, false)
                JamKerjaViewHolder(binding)
            }
            VIEW_TYPE_REPORT -> {
                val binding = LayoutListJamKerjaBinding.inflate(inflater, parent, false)
                ReportViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            VIEW_TYPE_JAM_KERJA -> {
                val jamKerjaHolder = holder as JamKerjaViewHolder
                jamKerjaHolder.bind(jamKerjaList[position])
            }
            VIEW_TYPE_REPORT -> {
                val reportHolder = holder as ReportViewHolder
                reportHolder.bind(reportList[position - jamKerjaList.size])
            }
        }
    }

    override fun getItemCount(): Int {
        return jamKerjaList.size + reportList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < jamKerjaList.size) {
            VIEW_TYPE_JAM_KERJA
        } else {
            VIEW_TYPE_REPORT
        }
    }

    inner class JamKerjaViewHolder(private val binding: LayoutListJamKerjaBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(dataItemJamKerja: DataItemJamKerja) {
            binding.apply {
                panggilIdActivity.text = dataItemJamKerja.activityId.toString()
                panggilJamKerja.text = dataItemJamKerja.workDuration
                tglJamKerja.text = dataItemJamKerja.startTime
            }
        }
    }

    inner class ReportViewHolder(private val binding: LayoutListJamKerjaBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(dataReport2: DataReport2) {
            binding.apply {
//                outputLokasi.text = dataReport2.locationName
            }
        }
    }
}
