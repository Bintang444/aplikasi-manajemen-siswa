package com.example.tugaspkl.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tugaspkl.R
import com.example.tugaspkl.model.Siswa

class SiswaAdapter(
    private var siswaList: MutableList<Siswa>,
    private val onEditClick: (Siswa) -> Unit,
    private val onDeleteClick: (Siswa) -> Unit
) : RecyclerView.Adapter<SiswaAdapter.ViewHolder>() {

    // fungsi filter
    fun filterList(filteredList: List<Siswa>) {
        siswaList.clear()
        siswaList.addAll(filteredList)
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNama: TextView = view.findViewById(R.id.tvNama)
        val tvNis: TextView = view.findViewById(R.id.tvNis)
        val tvJK: TextView = view.findViewById(R.id.tvJK)
        val tvAlamat: TextView = view.findViewById(R.id.tvAlamat)
        val tvJurusan: TextView = view.findViewById(R.id.tvJurusan)
        val btnEdit: Button = view.findViewById(R.id.btnEdit)
        val btnHapus: Button = view.findViewById(R.id.btnHapus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_siswa, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val siswa = siswaList[position]
        holder.tvNama.text = siswa.nama
        holder.tvNis.text = "NIS: ${siswa.nis}"
        holder.tvJK.text = "Jenis Kelamin: ${siswa.jenis_kelamin}"
        holder.tvAlamat.text = "Alamat: ${siswa.alamat}"
        holder.tvJurusan.text = "Jurusan: ${siswa.nama_jurusan}"

        holder.btnEdit.setOnClickListener { onEditClick(siswa) }
        holder.btnHapus.setOnClickListener { onDeleteClick(siswa) }
    }

    override fun getItemCount(): Int = siswaList.size
}