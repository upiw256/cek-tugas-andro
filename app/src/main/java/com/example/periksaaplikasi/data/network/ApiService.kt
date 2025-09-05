package com.example.periksaaplikasi.network

import com.example.periksaaplikasi.data.model.GenericResponse
import com.example.periksaaplikasi.data.model.NilaiRequest
import com.example.periksaaplikasi.data.model.TugasResponse
import com.example.periksaaplikasi.model.MemberResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @GET("members/{id}")
    fun getMember(@Path("id") id: String): Call<MemberResponse>

    @GET("tugas")
    fun getTugas(): Call<TugasResponse>

    @POST("nilai/{member_id}")
    fun postNilai(
        @Path("member_id") memberId: String,
        @Body nilaiBody: NilaiRequest
    ): Call<GenericResponse>
}
