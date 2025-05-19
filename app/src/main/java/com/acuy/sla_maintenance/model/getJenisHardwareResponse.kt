package com.acuy.sla_maintenance.model

import com.google.gson.annotations.SerializedName

data class GetJenisHardwareResponseItem(

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("nama_hardware")
	val namaHardware: String? = null
)


data class GetJenisHardwareResponse(
	@field:SerializedName("getJenisHardwareResponse")
	val getJenisHardwareResponse: List<GetJenisHardwareResponseItem>? = null
)






//import com.google.gson.annotations.SerializedName
//
//
//data class GetJenisHardwareResponseItem(
//    @field:SerializedName("id")
//    val id: Int? = null,
//
//    @field:SerializedName("nama_hardware")
//    val namaHardware: String? = null,
//
//    @field:SerializedName("created_at")
//    val createdAt: String? = null,
//
//    @field:SerializedName("updated_at")
//    val updatedAt: String? = null
//)
//
//
//data class GetJenisHardwareResponse(
//    @field:SerializedName("jenisHardwareList")
//    val jenisHardwareList: List<GetJenisHardwareResponseItem>? = null
//)
//
