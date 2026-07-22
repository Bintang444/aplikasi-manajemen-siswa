package com.example.tugaspkl.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tugaspkl.R
import com.example.tugaspkl.model.Jurusan

class JurusanAdapter(
    private var jurusanList: MutableList<Jurusan>,
    private val onEditClick: (Jurusan) -> Unit,
    private val onDeleteClick: (Jurusan) -> Unit
) : RecyclerView.Adapter<JurusanAdapter.ViewHolder>() {

    // fungsi filter
    fun filterList(filteredList: List<Jurusan>) {
        jurusanList.clear()
        jurusanList.addAll(filteredList)
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNamaJurusan: TextView = view.findViewById(R.id.tvNamaJurusan)
        val btnEditJurusan: Button = view.findViewById(R.id.btnEditJurusan)
        val btnHapusJurusan: Button = view.findViewById(R.id.btnHapusJurusan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_jurusan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val jurusan = jurusanList[position]
        holder.tvNamaJurusan.text = "Jurusan: ${jurusan.nama_jurusan}"

        holder.btnEditJurusan.setOnClickListener { onEditClick(jurusan) }
        holder.btnHapusJurusan.setOnClickListener { onDeleteClick(jurusan) }
    }

    override fun getItemCount(): Int = jurusanList.size
}