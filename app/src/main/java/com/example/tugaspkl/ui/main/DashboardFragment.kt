package com.example.tugaspkl.ui.main

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tugaspkl.R
import com.example.tugaspkl.api.ApiClient
import com.example.tugaspkl.model.Jurusan
import com.example.tugaspkl.model.RecentActivity
import com.example.tugaspkl.model.Siswa
import com.example.tugaspkl.utils.RecentActivityManager
import com.example.tugaspkl.utils.SessionManager
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class DashboardFragment : Fragment() {

    private lateinit var rvRecentActivity: RecyclerView
    private lateinit var recentAdapter: RecentActivityAdapter
    private val recentList = mutableListOf<RecentActivity>()
    private lateinit var sessionManager: SessionManager
    private lateinit var tvJumlahSiswa: TextView
    private lateinit var tvJumlahJurusan: TextView
    private lateinit var barChart: BarChart
    private lateinit var spinnerJurusan: Spinner

    private var jurusanList: List<Jurusan> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        val menuSiswa = view.findViewById<View>(R.id.menuDataSiswa)
        val menuJurusan = view.findViewById<View>(R.id.menuJurusan)

        menuSiswa.setOnClickListener {
            (activity as? MainActivity)?.switchTab(R.id.nav_siswa)
        }

        menuJurusan.setOnClickListener {
            (activity as? MainActivity)?.switchTab(R.id.nav_jurusan)
        }

        // Salam
        sessionManager = SessionManager(requireContext())
        val username = sessionManager.getUsername() ?: "User"
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val greeting = when (hour) {
            in 0..11 -> "Selamat pagi"
            in 12..15 -> "Selamat siang"
            in 16..18 -> "Selamat sore"
            else -> "Selamat malam"
        }
        view.findViewById<TextView>(R.id.tvGreeting).text = "$greeting, $username 👋"

        // Setup
        tvJumlahSiswa = view.findViewById(R.id.tvJumlahSiswa)
        tvJumlahJurusan = view.findViewById(R.id.tvJumlahJurusan)
        rvRecentActivity = view.findViewById(R.id.rvRecentActivity)
        rvRecentActivity.layoutManager = LinearLayoutManager(requireContext())
        recentAdapter = RecentActivityAdapter(recentList)
        rvRecentActivity.adapter = recentAdapter
        rvRecentActivity.layoutAnimation =
            AnimationUtils.loadLayoutAnimation(requireContext(), R.anim.layout_fade_in)

        barChart = view.findViewById(R.id.barChart)
        spinnerJurusan = view.findViewById(R.id.spinnerJurusan)

        loadSummaryData()
        loadJurusanList()

        return view
    }

    override fun onResume() {
        super.onResume()
        recentList.clear()
        recentList.addAll(RecentActivityManager.activityList)
        rvRecentActivity.scheduleLayoutAnimation()
        recentAdapter.notifyDataSetChanged()
        loadSummaryData()
    }

    private fun loadSummaryData() {
        ApiClient.instance.getSiswa().enqueue(object : Callback<List<Siswa>> {
            override fun onResponse(call: Call<List<Siswa>>, response: Response<List<Siswa>>) {
                tvJumlahSiswa.text = response.body()?.size?.toString() ?: "-"
            }

            override fun onFailure(call: Call<List<Siswa>>, t: Throwable) {
                tvJumlahSiswa.text = "-"
            }
        })

        ApiClient.instance.getJurusan().enqueue(object : Callback<List<Jurusan>> {
            override fun onResponse(call: Call<List<Jurusan>>, response: Response<List<Jurusan>>) {
                tvJumlahJurusan.text = response.body()?.size?.toString() ?: "-"
            }

            override fun onFailure(call: Call<List<Jurusan>>, t: Throwable) {
                tvJumlahJurusan.text = "-"
            }
        })
    }

    private fun loadJurusanList() {
        ApiClient.instance.getJurusan().enqueue(object : Callback<List<Jurusan>> {
            override fun onResponse(call: Call<List<Jurusan>>, response: Response<List<Jurusan>>) {
                if (response.isSuccessful && response.body() != null) {
                    jurusanList = response.body()!!
                    val adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        jurusanList.map { it.nama_jurusan }
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerJurusan.adapter = adapter

                    spinnerJurusan.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                val selected = jurusanList[position]
                                loadChartData(selected.nama_jurusan)
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {}
                        }

                    loadChartData(jurusanList.first().nama_jurusan)
                }
            }

            override fun onFailure(call: Call<List<Jurusan>>, t: Throwable) {}
        })
    }

    private fun loadChartData(selectedJurusan: String) {
        ApiClient.instance.getSiswa().enqueue(object : Callback<List<Siswa>> {
            override fun onResponse(call: Call<List<Siswa>>, response: Response<List<Siswa>>) {
                if (response.isSuccessful && response.body() != null) {
                    val siswaList = response.body()!!
                        .filter { it.nama_jurusan == selectedJurusan }

                    val laki = siswaList.count { it.jenis_kelamin == "Laki-laki" }.toFloat()
                    val perempuan = siswaList.count { it.jenis_kelamin == "Perempuan" }.toFloat()

                    val entries = listOf(
                        BarEntry(0f, laki),
                        BarEntry(1f, perempuan)
                    )

                    val dataSet = BarDataSet(entries, "Jumlah Siswa $selectedJurusan")
                    dataSet.colors = listOf(
                        Color.parseColor("#6FA8DC"),
                        Color.parseColor("#F6B26B")
                    )
                    dataSet.valueTextSize = 14f
                    dataSet.valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return value.toInt().toString() // biar ga 12.0
                        }
                    }

                    val barData = BarData(dataSet)
                    barData.barWidth = 0.5f
                    barChart.data = barData

                    val xAxis = barChart.xAxis
                    xAxis.valueFormatter = IndexAxisValueFormatter(listOf("Laki-laki", "Perempuan"))
                    xAxis.position = XAxis.XAxisPosition.BOTTOM
                    xAxis.setDrawGridLines(false)
                    xAxis.granularity = 1f
                    xAxis.labelCount = 2
                    xAxis.axisMinimum = -0.5f
                    xAxis.axisMaximum = 1.5f

                    val leftAxis = barChart.axisLeft
                    leftAxis.granularity = 1f
                    leftAxis.valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return value.toInt().toString()
                        }
                    }

                    barChart.axisRight.isEnabled = false
                    barChart.description.isEnabled = false
                    barChart.setFitBars(true)
                    barChart.animateY(1000)
                    barChart.invalidate()
                }
            }

            override fun onFailure(call: Call<List<Siswa>>, t: Throwable) {}
        })
    }
}
