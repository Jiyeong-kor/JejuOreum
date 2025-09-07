package com.jeong.jjoreum.data.model.api

import com.google.gson.annotations.SerializedName
import com.jeong.domain.entity.ResultSummary

data class OreumData(
    val resultCode: String,
    @SerializedName("resultMsg") val resultMsg: String,
    val resultSummary: List<ResultSummary>
)
