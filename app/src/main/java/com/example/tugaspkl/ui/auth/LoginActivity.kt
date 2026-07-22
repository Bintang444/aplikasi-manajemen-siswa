package com.example.tugaspkl.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tugaspkl.R
import com.example.tugaspkl.api.ApiClient
import com.example.tugaspkl.api.ApiService
import com.example.tugaspkl.model.LoginResponse
import com.example.tugaspkl.ui.main.MainActivity
import com.example.tugaspkl.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private val apiService = ApiClient.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(this)

        // Cek sudah login
        if (sessionManager.isLoggedIn()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_login)

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val iconSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 24f, resources.displayMetrics
        ).toInt()

        val editDrawable = getDrawable(android.R.drawable.ic_menu_edit)
        editDrawable?.setBounds(0, 0, iconSize, iconSize)
        etUsername.setCompoundDrawables(editDrawable, null, null, null)

        val etPassword = findViewById<EditText>(R.id.etPassword)
        val lockDrawable = getDrawable(android.R.drawable.ic_lock_lock)
        lockDrawable?.setBounds(0, 0, iconSize, iconSize)
        etPassword.setCompoundDrawables(lockDrawable, null, null, null)

        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                val api = apiService

                api.loginUser(username, password).enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                        if (response.isSuccessful) {
                            val result = response.body()
                            if (result != null && result.success) {
                                sessionManager.createLoginSession(username)
                                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                finish()
                            } else {
                                Toast.makeText(this@LoginActivity, result?.message ?: "Login gagal", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this@LoginActivity, "Response gagal: ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Toast.makeText(this@LoginActivity, t.message, Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Toast.makeText(this, "Isi semua field", Toast.LENGTH_SHORT).show()
            }
        }
    }
}