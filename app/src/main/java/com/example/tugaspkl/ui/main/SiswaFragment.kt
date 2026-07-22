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
import com.example.tugaspkl.model.Siswa
import com.example.tugaspkl.utils.RecentActivityManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import okhttp3.ResponseBody
import android.widget.EditText
import android.text.Editable
import android.text.TextWatcher
import android.widget.Spinner
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import com.example.tugaspkl.model.Jurusan

class SiswaFragment : Fragment() {

    private lateinit var rvSiswa: RecyclerView
    private lateinit var siswaAdapter: SiswaAdapter
    private val siswaList = mutableListOf<Siswa>()
    private val allSiswaList = mutableListOf<Siswa>()
    private lateinit var spinnerJurusan: Spinner
    private val jurusanList = mutableListOf("Semua Jurusan")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_siswa, container, false)
        rvSiswa = view.findViewById(R.id.rvSiswa)
        rvSiswa.layoutManager = LinearLayoutManager(requireContext())

        val etSearch = view.findViewById<EditText>(R.id.etSearch)
        spinnerJurusan = view.findViewById(R.id.spinnerJurusan)

        val initialAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listOf("Semua Jurusan"))
        initialAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerJurusan.adapter = initialAdapter

        view.findViewById<View>(R.id.btnTambahSiswa).setOnClickListener {
            startActivity(Intent(requireContext(), TambahSiswaActivity::class.java))
        }

        siswaAdapter = SiswaAdapter(siswaList,
            onEditClick = { siswa ->
                val intent = Intent(requireContext(), TambahSiswaActivity::class.java)
                intent.putExtra("edit_mode", true)
                intent.putExtra("id_siswa", siswa.id_siswa)
                intent.putExtra("nis", siswa.nis)
                intent.putExtra("nama", siswa.nama)
                intent.putExtra("jk", siswa.jenis_kelamin)
                intent.putExtra("alamat", siswa.alamat)
                intent.putExtra("jurusan", siswa.nama_jurusan)
                startActivity(intent)
            },
            onDeleteClick = { siswa ->
                showDeleteConfirmationDialog(siswa)
            }
        )
        rvSiswa.adapter = siswaAdapter

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterData(etSearch.text.toString(), spinnerJurusan.selectedItem.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        spinnerJurusan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                filterData(etSearch.text.toString(), jurusanList[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        loadDataSiswa()
        view?.findViewById<EditText>(R.id.etSearch)?.setText("")
        view?.findViewById<Spinner>(R.id.spinnerJurusan)?.setSelection(0)
    }

    private fun loadDataSiswa() {
        ApiClient.instance.getSiswa().enqueue(object : Callback<List<Siswa>> {
            override fun onResponse(call: Call<List<Siswa>>, response: Response<List<Siswa>>) {
                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!
                    allSiswaList.clear()
                    allSiswaList.addAll(data)

                    siswaList.clear()
                    siswaList.addAll(data)
                    siswaAdapter.notifyDataSetChanged()

                    val uniqueJurusan = data.map { it.nama_jurusan }.distinct()
                    jurusanList.clear()
                    jurusanList.add("Semua Jurusan")
                    jurusanList.addAll(uniqueJurusan)

                    val spinnerAdapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        jurusanList
                    )
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerJurusan.adapter = spinnerAdapter
                } else {
                    Toast.makeText(requireContext(), "Gagal ambil data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Siswa>>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showDeleteConfirmationDialog(siswa: Siswa) {
        AlertDialog.Builder(requireContext())
            .setTitle("Konfirmasi Hapus")
            .setMessage("Yakin mau hapus siswa ${siswa.nama}?")
            .setPositiveButton("Hapus") { _, _ ->
                deleteSiswaConfirmed(siswa)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun deleteSiswaConfirmed(siswa: Siswa) {
        ApiClient.instance.deleteSiswa(siswa.id_siswa)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        allSiswaList.remove(siswa)
                        siswaList.remove(siswa)
                        siswaAdapter.notifyDataSetChanged()

                        RecentActivityManager.addActivity("Hapus siswa ${siswa.nama}")
                        Toast.makeText(requireContext(), "Siswa berhasil dihapus", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Gagal hapus siswa", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun filterData(keyword: String, jurusan: String) {
        val filteredList = allSiswaList.filter { siswa ->
            val matchKeyword = keyword.isEmpty() || siswa.nis.contains(keyword, true) || siswa.nama.contains(keyword, true)
            val matchJurusan = jurusan == "Semua Jurusan" || siswa.nama_jurusan.equals(jurusan, true)
            matchKeyword && matchJurusan
        }

        siswaList.clear()
        siswaList.addAll(filteredList)
        siswaAdapter.notifyDataSetChanged()
    }
}
