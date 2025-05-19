package com.acuy.sla_maintenance.model

import com.google.gson.annotations.SerializedName

data class GrafikJamKerjaResponse(

	@field:SerializedName("data")
	val data: List<DataItemGrafik?>? = null,

    @field:SerializedName("start_year")
    val startYear: Int? = null,

	@field:SerializedName("message")
	val message: String? = null,

    @field:SerializedName("end_year")
    val endYear: Int? = null


)

data class DataItemGrafik(

	@field:SerializedName("total")
	val total: Int? = null,


	@field:SerializedName("user_id")
	val userId: Int? = null,


	@field:SerializedName("durations")
	val durations: List<Int?>? = null
)
