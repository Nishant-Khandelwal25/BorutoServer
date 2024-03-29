package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse(
    val success: Boolean,
    val message: String? = null,
    val prevPage: Int? = null,
    val nextPage: Int? = null,
    val lastUpdated: Long? = null,
    val heroes: List<Hero> = emptyList()
)
