package com.acuy.sla_maintenance.model

import com.google.gson.annotations.SerializedName

data class GetIJenisKategoriResponse(

	@field:SerializedName("getIJenisKategoriResponse")
	val getIJenisKategoriResponse: List<GetIJenisKategoriResponseItem?>? = null
)

data class GetIJenisKategoriResponseItem(

	@field:SerializedName("deadline_duration")
	val deadlineDuration: Int? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("nama_kategori")
	val namaKategori: String? = null
)
