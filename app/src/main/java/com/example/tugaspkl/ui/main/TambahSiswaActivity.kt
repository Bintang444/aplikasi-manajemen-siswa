package com.example.tugaspkl.ui.main

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.tugaspkl.R
import com.example.tugaspkl.api.ApiClient
import com.example.tugaspkl.model.Jurusan
import com.example.tugaspkl.model.Siswa
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TambahSiswaActivity : AppCompatActivity() {


    private lateinit var etNIS: EditText
    private lateinit var etNama: EditText
    private lateinit var spinnerJenisKelamin: Spinner
    private lateinit var etAlamat: EditText
    private lateinit var spinnerJurusan: Spinner
    private lateinit var btnSimpan: Button
    private lateinit var tvHeaderSiswa: TextView

    private var jurusanList: List<Jurusan> = emptyList()
    private var editMode = false
    private var editIdSiswa: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_siswa)

        val btnBack = findViewById<ImageView>(R.id.btnBackSiswa)
        btnBack.setOnClickListener{
            finish()
        }

        tvHeaderSiswa = findViewById(R.id.tvHeaderSiswa)

        // bind view
        etNIS = findViewById(R.id.etNIS)
        etNama = findViewById(R.id.etNama)
        spinnerJenisKelamin = findViewById(R.id.spinnerJenisKelamin)
        etAlamat = findViewById(R.id.etAlamat)
        spinnerJurusan = findViewById(R.id.spinnerJurusan)
        btnSimpan = findViewById(R.id.btnSimpan)

        setupSpinnerJenisKelamin()
        loadJurusan()

        // cek edit mode
        editMode = intent.getBooleanExtra("edit_mode", false)
        if (editMode) {
            // ubah header & tombol
            tvHeaderSiswa.text = "EDIT SISWA"
            btnSimpan.text = "Update"

            // isi data ke form
            editIdSiswa = intent.getStringExtra("id_siswa")!!.toInt()
            etNIS.setText(intent.getStringExtra("nis"))
            etNama.setText(intent.getStringExtra("nama"))
            val jk = intent.getStringExtra("jk")
            spinnerJenisKelamin.setSelection(if (jk == "Laki-laki") 0 else 1)
            etAlamat.setText(intent.getStringExtra("alamat"))
        }

        btnSimpan.setOnClickListener {
            if(editMode) updateSiswa() else simpanData()
        }
    }

    private fun setupSpinnerJenisKelamin() {
        val jenis = listOf("Laki-laki", "Perempuan")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, jenis)
        spinnerJenisKelamin.adapter = adapter
    }

    private fun loadJurusan() {
        ApiClient.instance.getJurusan().enqueue(object : Callback<List<Jurusan>> {
            override fun onResponse(call: Call<List<Jurusan>>, response: Response<List<Jurusan>>) {
                if (response.isSuccessful && response.body() != null) {
                    jurusanList = response.body()!!
                    val namaJurusan = jurusanList.map { it.nama_jurusan }
                    val adapter = ArrayAdapter(
                        this@TambahSiswaActivity,
                        android.R.layout.simple_spinner_dropdown_item,
                        namaJurusan
                    )
                    spinnerJurusan.adapter = adapter

                    if(editMode) {
                        val jurusanName = intent.getStringExtra("jurusan")
                        spinnerJurusan.setSelection(getJurusanPosition(jurusanName))
                    }
                } else {
                    Toast.makeText(this@TambahSiswaActivity, "Gagal memuat jurusan", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Jurusan>>, t: Throwable) {
                Toast.makeText(this@TambahSiswaActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getJurusanPosition(namaJurusan: String?): Int {
        jurusanList.forEachIndexed { index, jurusan ->
            if(jurusan.nama_jurusan == namaJurusan) return index
        }
        return 0
    }

    private fun simpanData() {
        val nis = etNIS.text.toString()
        val nama = etNama.text.toString()
        val jenisKelamin = spinnerJenisKelamin.selectedItem.toString()
        val alamat = etAlamat.text.toString()
        val idJurusan = jurusanList[spinnerJurusan.selectedItemPosition].id_jurusan

        if (nis.isEmpty() || nama.isEmpty()) {
            Toast.makeText(this, "Isi semua field!", Toast.LENGTH_SHORT).show()
            return
        }

        ApiClient.instance.tambahSiswa(nis, nama, jenisKelamin, alamat, idJurusan)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@TambahSiswaActivity, "Berhasil tambah siswa!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@TambahSiswaActivity, "Gagal menambah siswa", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(this@TambahSiswaActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun updateSiswa() {
        val nis = etNIS.text.toString()
        val nama = etNama.text.toString()
        val jenisKelamin = spinnerJenisKelamin.selectedItem.toString()
        val alamat = etAlamat.text.toString()
        val idJurusan = jurusanList[spinnerJurusan.selectedItemPosition].id_jurusan

        if (nis.isEmpty() || nama.isEmpty()) {
            Toast.makeText(this, "Isi semua field!", Toast.LENGTH_SHORT).show()
            return
        }

        ApiClient.instance.updateSiswa(editIdSiswa, nis, nama, jenisKelamin, alamat, idJurusan)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if(response.isSuccessful) {
                        Toast.makeText(this@TambahSiswaActivity, "Data berhasil diupdate", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@TambahSiswaActivity, "Gagal update data", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(this@TambahSiswaActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}