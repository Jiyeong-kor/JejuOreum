package com.jeong.jjoreum.data.model.api

import retrofit2.Response
import retrofit2.http.GET

/**
 * 오름 API를 호출하는 Retrofit 인터페이스
 */
interface OreumRetrofitInterface {

    /**
     * 오름 리스트를 가져오는 API 호출
     * @return 오름 데이터를 포함하는 Response 객체
     */
    @GET("getOleumADetailList")
    suspend fun getOreumList(): Response<OreumData>

    companion object {
        /**
         * Retrofit 인터페이스의 인스턴스를 생성하는 함수
         * @return OreumRetrofitInterface 인스턴스
         */
        fun create(): OreumRetrofitInterface {
            return RetrofitOkHttpManager.oreumRetrofitBuilder.create(OreumRetrofitInterface::class.java)
        }
    }
}
