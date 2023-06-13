package com.example.memovie.core.utils

import okhttp3.ResponseBody
import org.json.JSONObject

fun convertErrorMessage(errorBody: ResponseBody?, getFrom: String = "status_message"): String {
    return JSONObject(errorBody!!.charStream().readText()).getString(getFrom)
}

