package com.acuy.sla_maintenance.model

import com.google.gson.annotations.SerializedName

data class GetITdanTolResponse(

	@field:SerializedName("getITdanTolResponse")
	val getITdanTolResponse: List<GetITdanTolResponseItem?>? = null
)

data class GetITdanTolResponseItem(

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("nama_aplikasiTol")
	val namaAplikasiTol: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)
