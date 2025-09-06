package com.example.periksaaplikasi

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.periksaaplikasi.model.Member
import com.example.periksaaplikasi.model.MemberResponse
import com.example.periksaaplikasi.data.network.RetrofitClient
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScanActivity : ComponentActivity() {

    private lateinit var barcodeScanner: DecoratedBarcodeView
    private lateinit var tvMemberName: TextView
    private lateinit var tvMemberNis: TextView
    private lateinit var tvMemberClass: TextView
    private lateinit var btnAddNilai: Button

    private var scannedId: String? = null
    private var currentMember: Member? = null
    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startScanner()
        } else {
            Toast.makeText(this, "Izin kamera diperlukan untuk scan QR code", Toast.LENGTH_LONG).show()
            finish()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        barcodeScanner = findViewById(R.id.barcode_scanner)
        tvMemberName = findViewById(R.id.tvMemberName)
        tvMemberNis = findViewById(R.id.tvMemberNis)
        tvMemberClass = findViewById(R.id.tvMemberClass)
        btnAddNilai = findViewById(R.id.btnAddNilai)

        checkCameraPermission()

        // Jalankan scanner
        barcodeScanner.decodeContinuous { result ->
            result?.text?.let { qrContent ->
                if (qrContent != scannedId) {
                    scannedId = qrContent
                    fetchMemberById(qrContent)
                }
            }
        }

        // Tombol Tambah Nilai
        btnAddNilai.setOnClickListener {
            if (currentMember != null && !currentMember?._id.isNullOrEmpty()) {
                Log.d("DEBUG", "Navigasi ke MemberDetail dengan ID: ${currentMember?._id}")
                val intent = Intent(this, MemberDetailActivity::class.java)
                intent.putExtra("MEMBER_ID", currentMember?._id)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Tunggu sebentar... Data belum siap", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                startScanner()
            }
            else -> {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun startScanner() {
        // Jalankan scanner
        barcodeScanner.decodeContinuous { result ->
            result?.text?.let { qrContent ->
                if (qrContent != scannedId) {
                    scannedId = qrContent
                    fetchMemberById(qrContent)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        barcodeScanner.resume()
    }

    override fun onPause() {
        super.onPause()
        barcodeScanner.pause()
    }

    // Ambil data member dari API
    private fun fetchMemberById(memberId: String) {
        RetrofitClient.instance.getMember(memberId).enqueue(object : Callback<MemberResponse> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<MemberResponse>, response: Response<MemberResponse>) {
                if (response.isSuccessful) {
                    val member = response.body()?.data
                    if (member != null) {
                        currentMember = member
                        Toast.makeText(this@ScanActivity, "Siswa ditemukan ${member.nama_lengkap}", Toast.LENGTH_SHORT).show()
                        tvMemberName.text = "Nama: ${member.nama_lengkap}"
                        tvMemberNis.text = "NIS: ${member.nis}"
                        tvMemberClass.text = "Kelas: ${member.kelas}"

                        Log.d("MEMBERS", "Data member ditemukan: $member")
                    } else {
                        Log.e("MEMBERS", "Data member kosong meskipun respons berhasil")
                        Toast.makeText(this@ScanActivity, "Data member kosong", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorBody = try {
                        response.errorBody()?.string()
                    } catch (e: Exception) {
                        "Gagal membaca error body: ${e.message}"
                    }

                    Log.e("MEMBERS", "Gagal mengambil data member (status ${response.code()}): $errorBody")
                    Toast.makeText(this@ScanActivity, "Data siswa tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MemberResponse>, t: Throwable) {
                Log.e("MEMBERS", "Gagal koneksi API: ${t.message}", t)
                Toast.makeText(this@ScanActivity, "Gagal koneksi ke server", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
