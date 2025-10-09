package com.jeong.jejuoreum.data.api

import retrofit2.Response
import retrofit2.http.GET

interface OreumRetrofitInterface {

    @GET("getOleumADetailList")
    suspend fun getOreumList(): Response<OreumData>
}
