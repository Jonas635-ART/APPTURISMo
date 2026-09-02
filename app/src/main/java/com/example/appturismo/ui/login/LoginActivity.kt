package com.example.appturismo.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.appturismo.TurismoApplication
import com.example.appturismo.databinding.ActivityLoginBinding
import com.example.appturismo.ui.admin.AdminActivity
import com.example.appturismo.ui.main.MainActivity
import com.example.appturismo.util.SessionManager
import com.example.appturismo.viewmodel.LoginViewModel
import com.example.appturismo.viewmodel.LoginViewModelFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels {
        LoginViewModelFactory((application as TurismoApplication).repository)
    }
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        val currentUser = sessionManager.getUser()
        if (currentUser != null) {
            if (currentUser.role == "ADMIN") {
                startActivity(Intent(this, AdminActivity::class.java))
            } else {
                startActivity(Intent(this, MainActivity::class.java))
            }
            finish()
            return
        }

        viewModel.loginResult.observe(this) { user ->
            if (user != null) {
                sessionManager.saveUser(user)
                if (user.role == "ADMIN") {
                    startActivity(Intent(this, AdminActivity::class.java))
                } else {
                    startActivity(Intent(this, MainActivity::class.java))
                }
                finish()
            } else {
                Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.registerResult.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "El usuario ya existe", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString()
            val password = binding.etPassword.text.toString()
            if (username.isNotEmpty() && password.isNotEmpty()) {
                viewModel.login(username, password)
            }
        }

        binding.btnRegister.setOnClickListener {
            val username = binding.etUsername.text.toString()
            val password = binding.etPassword.text.toString()
            if (username.isNotEmpty() && password.isNotEmpty()) {
                viewModel.register(username, password)
            }
        }
    }
}
