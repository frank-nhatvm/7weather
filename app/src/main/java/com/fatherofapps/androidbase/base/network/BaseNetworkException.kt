package com.fatherofapps.androidbase.base.network

import org.json.JSONObject

class BaseNetworkException (
    val responseMessage: String? = null,
    val responseCode: Int = -1
) : Exception() {

    var mainMessage = ""

    fun parseFromString(errorBody: String) {
        val json = JSONObject(errorBody)
        mainMessage = json.getString("message")
    }
}