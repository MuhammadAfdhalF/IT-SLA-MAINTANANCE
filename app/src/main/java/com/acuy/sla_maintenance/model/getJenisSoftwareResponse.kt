package com.acuy.sla_maintenance.model

import com.google.gson.annotations.SerializedName

data class GetJenisSoftwareResponse(

	@field:SerializedName("getJenisSoftwareResponse")
	val getJenisSoftwareResponse: List<GetJenisSoftwareResponseItem?>? = null
)

data class GetJenisSoftwareResponseItem(

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("nama_software")
	val namaSoftware: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)
