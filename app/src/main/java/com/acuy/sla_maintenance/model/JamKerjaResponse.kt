package com.acuy.sla_maintenance.model

import com.google.gson.annotations.SerializedName

data class JamKerjaResponse(
	val data: List<DataItemJamKerja?>? = null,
	val message: String? = null
)

data class DataItemJamKerja(
	@SerializedName("deskripsi_pending")
	val deskripsiPending: String? = null,

	@SerializedName("work_duration")
	val workDuration: String? = null,

	@SerializedName("start_time")
	val startTime: String? = null,

	@SerializedName("updated_at")
	val updatedAt: String? = null,

	@SerializedName("user_id")
	val userId: Int? = null,

	@SerializedName("activity_id")
	val activityId: Int? = null,

	@SerializedName("end_time")
	val endTime: String? = null,

	@SerializedName("created_at")
	val createdAt: String? = null,

	val id: Int? = null,

	val status: String? = null
)
