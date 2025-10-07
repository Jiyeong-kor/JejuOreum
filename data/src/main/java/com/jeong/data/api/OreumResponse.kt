package com.jeong.data.api

import com.google.gson.annotations.SerializedName
import com.jeong.domain.entity.ResultSummary

data class OreumData(
    val resultCode: String,
    @field:SerializedName("resultMsg") val resultMsg: String,
    val resultSummary: List<ResultSummary>
)
