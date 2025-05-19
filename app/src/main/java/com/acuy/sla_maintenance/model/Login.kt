package com.acuy.sla_maintenance.model

data class User(
	val id: Int,
	val username: String,
	val email: String,
	val foto: String?,
	val ttd: String?,
	val role: String,
	val created_at: String,
	val updated_at: String
)

data class Login(
	val message: String,
	val user: User,
	val token: String
)
