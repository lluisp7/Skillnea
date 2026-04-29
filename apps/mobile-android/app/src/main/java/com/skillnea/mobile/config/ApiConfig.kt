package com.skillnea.mobile.config

import com.skillnea.mobile.BuildConfig
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

data class ApiConfig(
    val baseUrl: String,
    val deploymentId: String,
) {
    val endpoint: String
        get() = when {
            baseUrl.isNotBlank() -> baseUrl.trim().trimEnd('/')
            deploymentId.isNotBlank() -> "https://script.google.com/macros/s/${deploymentId.trim()}/exec"
            else -> ""
        }

    val isRemoteEnabled: Boolean
        get() = endpoint.isNotBlank()

    fun buildUrl(
        action: String,
        queryParams: Map<String, String> = emptyMap(),
    ): String {
        val encodedQuery = buildList {
            add("action=${encode(action)}")
            queryParams.forEach { (key, value) ->
                add("${encode(key)}=${encode(value)}")
            }
        }.joinToString("&")
        return "$endpoint?$encodedQuery"
    }

    private fun encode(value: String): String =
        URLEncoder.encode(value, StandardCharsets.UTF_8.toString())

    companion object {
        fun fromBuildConfig(): ApiConfig = ApiConfig(
            baseUrl = BuildConfig.APPS_SCRIPT_BASE_URL,
            deploymentId = BuildConfig.APPS_SCRIPT_DEPLOYMENT_ID,
        )
    }
}
