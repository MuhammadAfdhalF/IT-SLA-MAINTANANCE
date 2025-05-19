package com.acuy.sla_maintenance.model

import com.google.gson.annotations.SerializedName

data class GetLokasiResponse(

	@field:SerializedName("getLokasiResponse")
	val getLokasiResponse: List<GetLokasiResponseItem?>? = null
)

data class GetLokasiResponseItem(

	@field:SerializedName("nama_lokasi")
	val namaLokasi: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)
