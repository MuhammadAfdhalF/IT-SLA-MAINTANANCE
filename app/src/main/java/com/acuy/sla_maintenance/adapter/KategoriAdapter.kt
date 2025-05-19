package com.acuy.sla_maintenance.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.acuy.sla_maintenance.HalamanAddLaporan
import com.acuy.sla_maintenance.R

class KategoriAdapter(context: Context, resource: Int, val data: List<HalamanAddLaporan.KategoriItem>) :
    ArrayAdapter<HalamanAddLaporan.KategoriItem>(context, resource, data) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.list_company, parent, false)

        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.text = data[position].namaKategori

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getView(position, convertView, parent)
    }
}
