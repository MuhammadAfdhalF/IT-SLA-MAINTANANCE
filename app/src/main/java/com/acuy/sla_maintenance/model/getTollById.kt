package com.acuy.sla_maintenance.model

import com.google.gson.annotations.SerializedName

data class DataItemId(

	@field:SerializedName("standart_aplikasi")
	val standartAplikasi: Any? = null,

	@field:SerializedName("category_deadline")
	val categoryDeadline: Int? = null,

	@field:SerializedName("category_name")
	val categoryName: String? = null,

	@field:SerializedName("kondisi_akhir")
	val kondisiAkhir: String? = null,

	@field:SerializedName("shift")
	val shift: String? = null,

	@field:SerializedName("catatan")
	val catatan: Any? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("uraian_it_tol")
	val uraianItTol: Any? = null,

	@field:SerializedName("jenis_hardware")
	val jenisHardware: String? = null,

	@field:SerializedName("kategori_activity")
	val kategoriActivity: String? = null,

	@field:SerializedName("foto_awal")
	val fotoAwal: String? = null,

	@field:SerializedName("location_name")
	val locationName: String? = null,

	@field:SerializedName("waktu_pengerjaan")
	val waktuPengerjaan: String? = null,

	@field:SerializedName("uraian_hardware")
	val uraianHardware: String? = null,

	@field:SerializedName("biaya")
	val biaya: Any? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("company")
	val company: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("nama_user")
	val namaUser: String? = null,

	@field:SerializedName("aplikasi_it_tol")
	val aplikasiItTol: Any? = null,

	@field:SerializedName("foto_akhir")
	val fotoAkhir: String? = null,

	@field:SerializedName("ended_at")
	val endedAt: String? = null,

	@field:SerializedName("uraian_aplikasi")
	val uraianAplikasi: Any? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class LinksItemId(

	@field:SerializedName("active")
	val active: Boolean? = null,

	@field:SerializedName("label")
	val label: String? = null,

	@field:SerializedName("url")
	val url: Any? = null
)

data class DataId(

	@field:SerializedName("per_page")
	val perPage: Int? = null,

	@field:SerializedName("data")
	val data: List<DataItemId?>? = null,

	@field:SerializedName("last_page")
	val lastPage: Int? = null,

	@field:SerializedName("next_page_url")
	val nextPageUrl: Any? = null,

	@field:SerializedName("prev_page_url")
	val prevPageUrl: Any? = null,

	@field:SerializedName("first_page_url")
	val firstPageUrl: String? = null,

	@field:SerializedName("path")
	val path: String? = null,

	@field:SerializedName("total")
	val total: Int? = null,

	@field:SerializedName("last_page_url")
	val lastPageUrl: String? = null,

	@field:SerializedName("from")
	val from: Int? = null,

	@field:SerializedName("links")
	val links: List<LinksItemId?>? = null,

	@field:SerializedName("to")
	val to: Int? = null,

	@field:SerializedName("current_page")
	val currentPage: Int? = null
)

data class GetTollById(

	@field:SerializedName("data")
	val data: DataId? = null
)
