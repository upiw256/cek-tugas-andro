package com.example.periksaaplikasi

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.periksaaplikasi.data.model.GenericResponse
import com.example.periksaaplikasi.data.model.NilaiRequest
import com.example.periksaaplikasi.data.model.Tugas
import com.example.periksaaplikasi.data.model.TugasResponse
import com.example.periksaaplikasi.data.network.RetrofitClient
import com.example.periksaaplikasi.databinding.ActivityMemberDetailBinding
import com.example.periksaaplikasi.model.MemberResponse
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class MemberDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMemberDetailBinding

    private var tugasList = listOf<Tugas>()
    private lateinit var spinnerTugas: AutoCompleteTextView
    private lateinit var etNilai: EditText
    private lateinit var btnSubmitNilai: Button

    private var memberId: String? = null
    private var selectedTugasId: String? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMemberDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        memberId = intent.getStringExtra("MEMBER_ID")

        if (memberId != null) {
            fetchMemberDetail(memberId!!)
            setupViews()
            fetchTugas()
        } else {
            binding.txtNama.text = "❌ Data tidak ditemukan"
        }
    }

    private fun setupViews() {
        spinnerTugas = binding.root.findViewById(R.id.spinnerTugas)
        etNilai = binding.root.findViewById(R.id.etNilai)
        btnSubmitNilai = binding.root.findViewById(R.id.btnSubmitNilai)

        btnSubmitNilai.setOnClickListener {
            submitNilai()
        }
    }

    private fun fetchMemberDetail(id: String) {
        RetrofitClient.instance.getMember(id).enqueue(object : Callback<MemberResponse> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<MemberResponse>, response: Response<MemberResponse>) {
                if (response.isSuccessful) {
                    val member = response.body()?.data
                    if (member != null) {
                        binding.txtNama.text = member.nama_lengkap
                        binding.txtNis.text = "NIS: ${member.nis}"
                        binding.txtKelas.text = "Kelas: ${member.kelas}"
                    } else {
                        binding.txtNama.text = "❌ Data tidak ditemukan"
                    }
                } else {
                    binding.txtNama.text = "❌ Gagal load data (${response.code()})"
                }
            }

            @SuppressLint("SetTextI18n")
            override fun onFailure(call: Call<MemberResponse>, t: Throwable) {
                binding.txtNama.text = "⚠️ Error koneksi: ${t.localizedMessage}"
            }
        })
    }

    private fun fetchTugas() {
        RetrofitClient.instance.getTugas().enqueue(object : Callback<TugasResponse> {
            override fun onResponse(call: Call<TugasResponse>, response: Response<TugasResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    tugasList = response.body()?.data ?: emptyList()

                    val adapter = ArrayAdapter(
                        this@MemberDetailActivity,
                        android.R.layout.simple_dropdown_item_1line,
                        tugasList.map { it.judul }
                    )
                    spinnerTugas.setAdapter(adapter)

                    spinnerTugas.setOnItemClickListener { _, _, position, _ ->
                        selectedTugasId = tugasList[position]._id
                    }

                } else {
                    showCustomToast("Gagal load tugas", false)
                }
            }

            override fun onFailure(call: Call<TugasResponse>, t: Throwable) {
                showCustomToast("Error koneksi tugas: ${t.localizedMessage}", false)
            }
        })
    }

    private fun submitNilai() {
        val memberId = this.memberId ?: run {
            showCustomToast("Member ID tidak tersedia", false)
            return
        }

        if (selectedTugasId == null) {
            showCustomToast("Pilih tugas dulu", false)
            return
        }

        val nilaiText = etNilai.text.toString()
        if (nilaiText.isBlank()) {
            showCustomToast("Masukkan nilai", false)
            return
        }

        val nilaiInt = nilaiText.toIntOrNull()
        if (nilaiInt == null || nilaiInt < 0) {
            showCustomToast("Nilai tidak valid", false)
            return
        }

        val now = Date()
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        val tanggalString = sdf.format(now)

        val body = NilaiRequest(
            member_id = memberId,
            tugas_id = selectedTugasId!!,
            nilai = nilaiInt,
            tanggal = tanggalString
        )

        RetrofitClient.instance.postNilai(memberId, body).enqueue(object : Callback<GenericResponse> {
            override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    showCustomToast("Nilai berhasil disimpan", true)
                    etNilai.text.clear()
                    spinnerTugas.text.clear()
                    selectedTugasId = null

                    val intent = Intent(this@MemberDetailActivity, ScanActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                } else {
                    // baca pesan dari backend
                    val errorMessage = try {
                        val errorJson = response.errorBody()?.string()
                        val jsonObj = JSONObject(errorJson ?: "{}")
                        jsonObj.optString("message", "Gagal menyimpan nilai")
                    } catch (e: Exception) {
                        "Gagal menyimpan nilai"
                    }

                    showCustomToast(errorMessage, false)
                }
            }

            override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                showCustomToast("Error koneksi: ${t.localizedMessage}", false)
            }
        })
    }

    @SuppressLint("InflateParams")
    private fun showCustomToast(message: String, isSuccess: Boolean) {
        val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)

        if (isSuccess) {
            snackbar.setBackgroundTint(ContextCompat.getColor(this, R.color.success_color))
            snackbar.setTextColor(Color.WHITE)
            snackbar.setAction("✔") { /* optional: dismiss */ }
        } else {
            snackbar.setBackgroundTint(ContextCompat.getColor(this, R.color.error_color))
            snackbar.setTextColor(Color.WHITE)
            snackbar.setAction("✖") { /* optional: dismiss */ }
        }

        snackbar.show()
    }
}
