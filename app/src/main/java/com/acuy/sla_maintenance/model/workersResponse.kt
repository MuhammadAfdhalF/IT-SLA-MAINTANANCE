package com.acuy.sla_maintenance.model

import com.google.gson.annotations.SerializedName

data class WorkersResponse(

	@field:SerializedName("workersResponse")
	val workersResponse: List<WorkersResponseItem?>? = null


)

data class WorkersResponseItem(

	@field:SerializedName("deskripsi_pending")
	val deskripsiPending: String? = null,

	@field:SerializedName("work_duration")
	val workDuration: String? = null,

	@field:SerializedName("start_time")
	val startTime: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("activity_id")
	val activityId: Int? = null,

	@field:SerializedName("end_time")
	val endTime: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("status")
	val status: String? = null
)
