package com.example.tugaspkl.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tugaspkl.R
import com.example.tugaspkl.api.ApiClient
import com.example.tugaspkl.model.Jurusan
import com.example.tugaspkl.utils.RecentActivityManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import okhttp3.ResponseBody
import android.widget.EditText
import android.text.TextWatcher
import android.text.Editable
import androidx.appcompat.app.AlertDialog

class JurusanFragment : Fragment() {

    private lateinit var rvJurusan: RecyclerView
    private lateinit var jurusanAdapter: JurusanAdapter
    private val jurusanList = mutableListOf<Jurusan>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_jurusan, container, false)
        rvJurusan = view.findViewById(R.id.rvJurusan)
        rvJurusan.layoutManager = LinearLayoutManager(requireContext())

        val etSearchJurusan = view.findViewById<EditText>(R.id.etSearchJurusan) // pastikan ada di layout

        view.findViewById<View>(R.id.btnTambahJurusan).setOnClickListener {
            startActivity(Intent(requireContext(), TambahJurusanActivity::class.java))
        }

        // inisialisasi adapter
        jurusanAdapter = JurusanAdapter(jurusanList,
            onEditClick = { jurusan ->
                val intent = Intent(requireContext(), TambahJurusanActivity::class.java)
                intent.putExtra("edit_mode", true)
                intent.putExtra("id_jurusan", jurusan.id_jurusan)
                intent.putExtra("nama_jurusan", jurusan.nama_jurusan)
                startActivity(intent)
            },
            onDeleteClick = { jurusan ->
                showDeleteConfirmationDialog(jurusan)
            }
        )
        rvJurusan.adapter = jurusanAdapter

        // TextWatcher untuk filter
        etSearchJurusan.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val filteredList = jurusanList.filter {
                    it.nama_jurusan.contains(s.toString(), ignoreCase = true)
                }
                jurusanAdapter.filterList(filteredList)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // panggil data dari API
        loadDataJurusan()

        return view
    }

    private fun loadDataJurusan() {
        ApiClient.instance.getJurusan().enqueue(object : Callback<List<Jurusan>> {
            override fun onResponse(call: Call<List<Jurusan>>, response: Response<List<Jurusan>>) {
                if (response.isSuccessful && response.body() != null) {
                    jurusanList.clear()
                    jurusanList.addAll(response.body()!!.toMutableList())
                    jurusanAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(requireContext(), "Gagal ambil data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Jurusan>>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showDeleteConfirmationDialog(jurusan: Jurusan) {
        AlertDialog.Builder(requireContext())
            .setTitle("Konfirmasi Hapus")
            .setMessage("Yakin mau hapus jurusan ${jurusan.nama_jurusan}?")
            .setPositiveButton("Hapus") { _, _ ->
                // Kalau user pilih "Hapus"
                deleteJurusanConfirmed(jurusan)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun deleteJurusanConfirmed(jurusan: Jurusan) {
        ApiClient.instance.deleteJurusan(jurusan.id_jurusan.toInt())
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        jurusanList.remove(jurusan)
                        jurusanAdapter.notifyDataSetChanged()
                        RecentActivityManager.addActivity("Hapus jurusan ${jurusan.nama_jurusan}")
                        Toast.makeText(requireContext(), "Jurusan berhasil dihapus", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Gagal hapus jurusan", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}