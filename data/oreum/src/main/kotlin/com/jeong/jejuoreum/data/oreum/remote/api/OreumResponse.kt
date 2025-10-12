package com.jeong.jejuoreum.data.oreum.remote.api

import com.google.gson.annotations.SerializedName
import com.jeong.jejuoreum.domain.oreum.entity.ResultSummary

data class OreumData(
    val resultCode: String,
    @field:SerializedName("resultMsg") val resultMsg: String,
    val resultSummary: List<ResultSummary>
)
