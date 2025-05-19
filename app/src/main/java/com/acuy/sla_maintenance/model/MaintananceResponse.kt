package com.acuy.sla_maintenance.model

import com.google.gson.annotations.SerializedName

data class MaintananceResponse(
	@SerializedName("message") val message: String,
)
