package com.acuy.sla_maintenance.model

import com.google.gson.annotations.SerializedName

data class ProsesResponse(

	@field:SerializedName("data")
	val data: DataProses? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class DataProses(

	@field:SerializedName("start_time")
	val startTime: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("activity_id")
	val activityId: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)
