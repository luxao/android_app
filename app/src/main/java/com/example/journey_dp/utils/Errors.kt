package com.example.journey_dp.utils

open class Errors<out T>(private val content: T) {
    fun getErrorMessage(): T? {
        return content
    }
}