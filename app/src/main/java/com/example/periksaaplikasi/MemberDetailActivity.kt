package com.example.periksaaplikasi

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.periksaaplikasi.data.model.GenericResponse
import com.example.periksaaplikasi.data.model.NilaiRequest
import com.example.periksaaplikasi.data.model.Tugas
import com.example.periksaaplikasi.data.model.TugasResponse
import com.example.periksaaplikasi.data.network.RetrofitClient
import com.example.periksaaplikasi.databinding.ActivityMemberDetailBinding
import com.example.periksaaplikasi.model.MemberResponse
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
    private lateinit var spinnerTugas: Spinner
    private lateinit var etNilai: EditText
    private lateinit var btnSubmitNilai: Button

    private var memberId: String? = null

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
                        android.R.layout.simple_spinner_item,
                        tugasList.map { it.judul }
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerTugas.adapter = adapter
                } else {
                    Toast.makeText(this@MemberDetailActivity, "Gagal load tugas", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<TugasResponse>, t: Throwable) {
                Toast.makeText(this@MemberDetailActivity, "Error koneksi tugas: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun submitNilai() {
        val memberId = this.memberId ?: run {
            Toast.makeText(this, "Member ID tidak tersedia", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedPosition = spinnerTugas.selectedItemPosition
        if (selectedPosition == AdapterView.INVALID_POSITION || tugasList.isEmpty()) {
            Toast.makeText(this, "Pilih tugas dulu", Toast.LENGTH_SHORT).show()
            return
        }

        val nilaiText = etNilai.text.toString()
        if (nilaiText.isBlank()) {
            Toast.makeText(this, "Masukkan nilai", Toast.LENGTH_SHORT).show()
            return
        }

        val nilaiInt = nilaiText.toIntOrNull()
        if (nilaiInt == null || nilaiInt < 0) {
            Toast.makeText(this, "Nilai tidak valid", Toast.LENGTH_SHORT).show()
            return
        }

        val tugasId = tugasList[selectedPosition]._id

        val now = Date()
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        val tanggalString = sdf.format(now)

        val body = NilaiRequest(
            member_id = memberId,
            tugas_id = tugasId,
            nilai = nilaiInt,
            tanggal = tanggalString
        )

        RetrofitClient.instance.postNilai(memberId, body).enqueue(object : Callback<GenericResponse> {
            override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(this@MemberDetailActivity, "Nilai berhasil disimpan", Toast.LENGTH_SHORT).show()
                    etNilai.text.clear()
                } else {
                    Toast.makeText(this@MemberDetailActivity, "Gagal menyimpan nilai", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                Toast.makeText(this@MemberDetailActivity, "Error koneksi: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
