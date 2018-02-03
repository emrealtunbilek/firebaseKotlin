package com.emrealtunbilek.firebasekotlin

import com.emrealtunbilek.firebasekotlin.model.FCMModel
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST

/**
 * Created by Emre on 3.02.2018.
 */
interface FCMInterface {

    @POST("send")
    fun bildirimGonder(
            @HeaderMap headers:Map<String, String>,
            @Body bildirimMesaj:FCMModel
    ):Call<Response<FCMModel>>
}