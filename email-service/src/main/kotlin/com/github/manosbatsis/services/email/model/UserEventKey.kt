package com.github.manosbatsis.services.email.model

import java.util.Date

data class UserEventKey(
    val userId: Long,
    val datetime: Date,
) {
    fun asString() = "$userId-${datetime.time}"
}
