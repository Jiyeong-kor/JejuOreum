package com.jeong.jejuoreum.data.remote.oreum.api

import retrofit2.Response
import retrofit2.http.GET

interface OreumRetrofitInterface {

    @GET("getOleumADetailList")
    suspend fun getOreumList(): Response<OreumData>
}
