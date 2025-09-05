package com.example.periksaaplikasi.model

import com.google.gson.annotations.SerializedName

data class MemberResponse(
    @SerializedName("success")
    val success: Boolean?,

    @SerializedName("message")
    val message: String?,

    @SerializedName("data")
    val data: Member?
)

data class Member(
    @SerializedName("_id")
    val _id: String?,

    @SerializedName("nama_lengkap")
    val nama_lengkap: String?,

    @SerializedName("nis")
    val nis: String?,

    @SerializedName("kelas")
    val kelas: String?,

    // Tambahkan field lain jika ada
    @SerializedName("created_at")
    val created_at: String?,

    @SerializedName("updated_at")
    val updated_at: String?
)
