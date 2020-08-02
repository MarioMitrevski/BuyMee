package com.buymee.model

import java.net.URL
import java.time.LocalDateTime
import java.util.*

data class Shop(
    val shopId: UUID? = null,
    val shopName: String? = null,
    val shopDescription: String? = null,
    val shopCategory: Long? = null,
    val createdDate: LocalDateTime? = null,
    val shopLogo: URL? = null
)