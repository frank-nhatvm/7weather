package com.fatherofapps.androidbase.base.network


object NetworkHelper {

    fun getDefaultHeader(): Map<String, String> {
        val headers = mutableMapOf<String, String>()
        headers["Content-Type"] = "application/json"

        return headers.toMap()
    }

    fun getDefaultHeaderForCustomer(): Map<String, String> {
        val headers = mutableMapOf<String, String>()
        headers["Content-Type"] = "application/json"
        return headers.toMap()
    }

}