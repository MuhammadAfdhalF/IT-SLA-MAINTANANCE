//package com.acuy.sla_maintenance.model
//
//import com.google.gson.annotations.SerializedName
//
//data class ResponseListReport3(
//
//	@field:SerializedName("data")
//	val data: Data? = null
//)
//
//data class DataItem(
//
//	@field:SerializedName("standart_aplikasi")
//	val standartAplikasi: String? = null,
//
//	@field:SerializedName("category_name")
//	val categoryName: String? = null,
//
//	@field:SerializedName("kondisi_akhir")
//	val kondisiAkhir: String? = null,
//
//	@field:SerializedName("shift")
//	val shift: String? = null,
//
//	@field:SerializedName("catatan")
//	val catatan: String? = null,
//
//	@field:SerializedName("created_at")
//	val createdAt: String? = null,
//
//	@field:SerializedName("uraian_it_tol")
//	val uraianItTol: String? = null,
//
//	@field:SerializedName("jenis_hardware")
//	val jenisHardware: String? = null,
//
//	@field:SerializedName("foto_awal")
//	val fotoAwal: String? = null,
//
//	@field:SerializedName("location_name")
//	val locationName: String? = null,
//
//	@field:SerializedName("uraian_hardware")
//	val uraianHardware: String? = null,
//
//	@field:SerializedName("biaya")
//	val biaya: Int? = null,
//
//	@field:SerializedName("updated_at")
//	val updatedAt: String? = null,
//
//	@field:SerializedName("company")
//	val company: String? = null,
//
//	@field:SerializedName("id")
//	val id: Int? = null,
//
//	@field:SerializedName("nama_user")
//	val namaUser: String? = null,
//
//	@field:SerializedName("tanggal")
//	val tanggal: String? = null,
//
//	@field:SerializedName("aplikasi_it_tol")
//	val aplikasiItTol: String? = null,
//
//	@field:SerializedName("foto_akhir")
//	val fotoAkhir: String? = null,
//
//	@field:SerializedName("ended_at")
//	val endedAt: String? = null,
//
//	@field:SerializedName("uraian_aplikasi")
//	val uraianAplikasi: String? = null,
//
//	@field:SerializedName("status")
//	val status: String? = null
//)
//
//data class Data(
//
//	@field:SerializedName("per_page")
//	val perPage: Int? = null,
//
//	@field:SerializedName("data")
//	val data: List<DataItem?>? = null,
//
//	@field:SerializedName("last_page")
//	val lastPage: Int? = null,
//
//	@field:SerializedName("next_page_url")
//	val nextPageUrl: Any? = null,
//
//	@field:SerializedName("prev_page_url")
//	val prevPageUrl: Any? = null,
//
//	@field:SerializedName("first_page_url")
//	val firstPageUrl: String? = null,
//
//	@field:SerializedName("path")
//	val path: String? = null,
//
//	@field:SerializedName("total")
//	val total: Int? = null,
//
//	@field:SerializedName("last_page_url")
//	val lastPageUrl: String? = null,
//
//	@field:SerializedName("from")
//	val from: Int? = null,
//
//	@field:SerializedName("links")
//	val links: List<LinksItem?>? = null,
//
//	@field:SerializedName("to")
//	val to: Int? = null,
//
//	@field:SerializedName("current_page")
//	val currentPage: Int? = null
//)
//
//data class LinksItem(
//
//	@field:SerializedName("active")
//	val active: Boolean? = null,
//
//	@field:SerializedName("label")
//	val label: String? = null,
//
//	@field:SerializedName("url")
//	val url: Any? = null
//)
