package com.example.tugaspkl.ui.main

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.tugaspkl.R
import com.example.tugaspkl.api.ApiClient
import com.example.tugaspkl.model.Jurusan
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TambahJurusanActivity : AppCompatActivity() {


    private lateinit var etNamaJurusan: EditText
    private lateinit var btnSimpanJurusan: Button
    private lateinit var tvHeaderJurusan: TextView

    private var editMode = false
    private var editIdJurusan: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_jurusan)

        val btnBack = findViewById<ImageView>(R.id.btnBackJurusan)
        btnBack.setOnClickListener{
            finish()
        }

        tvHeaderJurusan = findViewById(R.id.tvHeaderJurusan)

        // bind view
        etNamaJurusan = findViewById(R.id.etNamaJurusan)
        btnSimpanJurusan = findViewById(R.id.btnSimpanJurusan)

        // cek edit mode
        editMode = intent.getBooleanExtra("edit_mode", false)
        if(editMode) {
            // ubah header & tombol
            tvHeaderJurusan.text = "EDIT JURUSAN"
            btnSimpanJurusan.text = "Update"

            editIdJurusan = intent.getIntExtra("id_jurusan", 0) // default 0 kalau null
            val namaJurusan = intent.getStringExtra("nama_jurusan") ?: ""
            etNamaJurusan.setText(namaJurusan)
        }

        btnSimpanJurusan.setOnClickListener {
            if(editMode) updateJurusan() else simpanData()
        }
    }

    private fun simpanData() {
        val jurusan = etNamaJurusan.text.toString()

        if (jurusan.isEmpty()) {
            Toast.makeText(this, "Isi field!", Toast.LENGTH_SHORT).show()
            return
        }

        ApiClient.instance.tambahJurusan(jurusan)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@TambahJurusanActivity, "Berhasil tambah jurusan!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@TambahJurusanActivity, "Gagal menambah jurusan", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(this@TambahJurusanActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun updateJurusan() {
        val nama_jurusan = etNamaJurusan.text.toString()

        if (nama_jurusan.isEmpty()) {
            Toast.makeText(this, "Isi semua field!", Toast.LENGTH_SHORT).show()
            return
        }

        ApiClient.instance.updateJurusan(editIdJurusan, nama_jurusan)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if(response.isSuccessful) {
                        Toast.makeText(this@TambahJurusanActivity, "Data berhasil diupdate", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@TambahJurusanActivity, "Gagal update data", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(this@TambahJurusanActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}