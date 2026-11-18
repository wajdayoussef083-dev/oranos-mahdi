package com.example.data.local

import android.content.Context
import com.example.BuildConfig

object AlternativeBillingManager {
    private const val PREFS_NAME = "oranos_pro_suite_prefs"
    private const val KEY_PRO_ALT_REF = "oranos_pro_alt_ref"
    private const val KEY_ENGINEER_TASK = "oranos_engineer_task"
    private const val KEY_CUSTOM_GLOBAL_URL = "custom_global_checkout_url"
    private const val KEY_CUSTOM_PAYONEER_TARGET = "custom_payoneer_target"

    const val PRO_PRICE_USD = "9.99"

    private var cachedProRef: String = ""
    private var cachedEngineerTask: String = ""
    private var cachedCustomGlobalUrl: String = ""
    private var cachedCustomPayoneerTarget: String = ""

    fun initialize(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        cachedProRef = prefs.getString(KEY_PRO_ALT_REF, "").orEmpty()
        cachedEngineerTask = prefs.getString(KEY_ENGINEER_TASK, "").orEmpty()
        cachedCustomGlobalUrl = prefs.getString(KEY_CUSTOM_GLOBAL_URL, "").orEmpty()
        cachedCustomPayoneerTarget = prefs.getString(KEY_CUSTOM_PAYONEER_TARGET, "").orEmpty()
    }

    fun getEffectiveGlobalCheckoutUrl(): String {
        val local = cachedCustomGlobalUrl.trim()
        if (isValidHttpUrl(local)) return local
        val buildVal = BuildConfig.GLOBAL_CHECKOUT_URL.trim()
        if (isValidHttpUrl(buildVal)) return buildVal
        return ""
    }

    fun getEffectivePayoneerTarget(): String {
        val local = cachedCustomPayoneerTarget.trim()
        if (isConfiguredPayoneerTarget(local)) return local
        val buildVal = BuildConfig.PAYONEER_TARGET.trim()
        if (isConfiguredPayoneerTarget(buildVal)) return buildVal
        return ""
    }

    fun isValidHttpUrl(url: String): Boolean {
        val trimmed = url.trim()
        if (trimmed.isBlank() || trimmed == "YOUR_GLOBAL_CHECKOUT_URL") return false
        return trimmed.startsWith("https://", ignoreCase = true) ||
            trimmed.startsWith("http://", ignoreCase = true)
    }

    fun isConfiguredPayoneerTarget(target: String): Boolean {
        val trimmed = target.trim()
        return trimmed.isNotBlank() && trimmed != "YOUR_PAYONEER_TARGET"
    }

    fun getSavedProReference(): String = cachedProRef

    fun saveProReference(context: Context, ref: String) {
        val clean = ref.trim()
        cachedProRef = clean
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_PRO_ALT_REF, clean)
            .apply()
    }

    fun saveChannelConfig(context: Context, globalUrl: String, payoneerTarget: String) {
        val cleanUrl = globalUrl.trim()
        val cleanPayoneer = payoneerTarget.trim()
        cachedCustomGlobalUrl = cleanUrl
        cachedCustomPayoneerTarget = cleanPayoneer
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_CUSTOM_GLOBAL_URL, cleanUrl)
            .putString(KEY_CUSTOM_PAYONEER_TARGET, cleanPayoneer)
            .apply()
    }

    fun getSavedEngineerTask(): String = cachedEngineerTask

    fun saveEngineerTask(context: Context, task: String) {
        val clean = task.trim()
        cachedEngineerTask = clean
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_ENGINEER_TASK, clean)
            .apply()
    }

    fun clearEngineerTask(context: Context) {
        cachedEngineerTask = ""
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .remove(KEY_ENGINEER_TASK)
            .apply()
    }
}
