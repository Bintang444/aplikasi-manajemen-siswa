package com.example.tugaspkl.api

import com.example.tugaspkl.model.LoginResponse
import com.example.tugaspkl.model.Jurusan
import com.example.tugaspkl.model.ResponseSiswa
import com.example.tugaspkl.model.Siswa
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.GET

interface ApiService {
    @FormUrlEncoded
    @POST("login.php")
    fun loginUser(
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("register.php")
    fun registerUser(
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @GET("get_jurusan.php")
    fun getJurusan(): Call<List<Jurusan>>

    @FormUrlEncoded
    @POST("tambah_jurusan.php")
    fun tambahJurusan(
        @Field("nama_jurusan") nama_jurusan: String,
    ): Call<ResponseBody>

    // ---- Tambah ini untuk update ----
    @FormUrlEncoded
    @POST("update_jurusan.php")
    fun updateJurusan(
        @Field("id_jurusan") id_jurusan: Int,
        @Field("nama_jurusan") nama_jurusan: String,
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("delete_jurusan.php")
    fun deleteJurusan(
        @Field("id_jurusan") id: Int
    ): Call<ResponseBody>

    @GET("get_siswa.php")
    fun getSiswa(): Call<List<Siswa>>

    @FormUrlEncoded
    @POST("tambah_siswa.php")
    fun tambahSiswa(
        @Field("nis") nis: String,
        @Field("nama") nama: String,
        @Field("jenis_kelamin") jenis_kelamin: String,
        @Field("alamat") alamat: String,
        @Field("id_jurusan") id_jurusan: Int
    ): Call<ResponseBody>

    // ---- Tambah ini untuk update ----
    @FormUrlEncoded
    @POST("update_siswa.php")
    fun updateSiswa(
        @Field("id_siswa") id_siswa: Int,
        @Field("nis") nis: String,
        @Field("nama") nama: String,
        @Field("jenis_kelamin") jenis_kelamin: String,
        @Field("alamat") alamat: String,
        @Field("id_jurusan") id_jurusan: Int
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("delete_siswa.php")
    fun deleteSiswa(
        @Field("id_siswa") id: Int
    ): Call<ResponseBody>
}