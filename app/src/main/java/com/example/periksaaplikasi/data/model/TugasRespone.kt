package com.example.periksaaplikasi.data.model

import com.google.gson.annotations.SerializedName

data class Tugas(
    @SerializedName("_id")
    val _id: String,
    @SerializedName("judul")
    val judul: String,
    @SerializedName("deskripsi")
    val deskripsi: String,
    @SerializedName("deadline")
    val deadline: String
)

data class TugasResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("data")
    val data: List<Tugas>
)

data class NilaiRequest(
    @SerializedName("member_id")
    val member_id: String,
    @SerializedName("tugas_id")
    val tugas_id: String,
    @SerializedName("nilai")
    val nilai: Int,
    @SerializedName("tanggal")
    val tanggal: String // format ISO 8601 (contoh: 2025-09-06T00:00:00Z)
)

data class GenericResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String?
)

