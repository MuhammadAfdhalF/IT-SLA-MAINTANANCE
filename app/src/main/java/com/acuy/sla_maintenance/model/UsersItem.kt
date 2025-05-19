package com.acuy.sla_maintenance.model

import com.google.gson.annotations.SerializedName

data class UsersItem(
	@SerializedName("id")
	val id: Int? = null,

	@SerializedName("username")
	val username: String? = null,

	@SerializedName("email")
	val email: String? = null,

	@SerializedName("foto")
	val foto: Any? = null,

	@SerializedName("ttd")
	val ttd: Any? = null,

	@SerializedName("role")
	val role: String? = null,

	@SerializedName("created_at")
	val createdAt: String? = null,

	@SerializedName("updated_at")
	val updatedAt: String? = null
)
