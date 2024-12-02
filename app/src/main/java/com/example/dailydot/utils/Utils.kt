package com.example.dailydot.utils

import java.util.UUID

object Utils {
    fun generateRandomUID(text: String): String {
        return "${text.hashCode()}-${UUID.randomUUID()}"
    }
}