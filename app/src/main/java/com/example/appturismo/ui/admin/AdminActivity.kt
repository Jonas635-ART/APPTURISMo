package com.example.appturismo.ui.admin

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.lifecycle.asLiveData
import com.example.appturismo.TurismoApplication
import com.example.appturismo.databinding.ActivityAdminBinding
import com.example.appturismo.ui.main.PuntoAdapter
import com.example.appturismo.ui.login.LoginActivity
import com.example.appturismo.util.SessionManager
import com.example.appturismo.viewmodel.AdminViewModel
import com.example.appturismo.viewmodel.AdminViewModelFactory

class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding
    private val viewModel: AdminViewModel by viewModels {
        AdminViewModelFactory((application as TurismoApplication).repository)
    }
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        setupToolbar()

        val adapter = PuntoAdapter(
            isAdmin = true,
            onPuntoClick = { punto ->
                // Navigate to detail
                val intent = Intent(this, com.example.appturismo.ui.detail.DetailActivity::class.java)
                intent.putExtra("PUNTO_ID", punto.id)
                startActivity(intent)
            },
            onFavoriteClick = { /* Admin favorite logic */ },
            onEditClick = { punto ->
                val intent = Intent(this, AddPuntoActivity::class.java)
                intent.putExtra("PUNTO_ID", punto.id)
                startActivity(intent)
            },
            onDeleteClick = { punto ->
                viewModel.deletePunto(punto)
            }
        )

        binding.rvPuntosAdmin.layoutManager = LinearLayoutManager(this)
        binding.rvPuntosAdmin.adapter = adapter

        // Observe points from repository
        viewModel.allPuntos.observe(this) { puntos ->
            adapter.submitList(puntos)
        }

        binding.btnAddPunto.setOnClickListener {
            startActivity(Intent(this, AddPuntoActivity::class.java))
        }

        binding.btnAddRecorrido.setOnClickListener {
            showAddRecorridoDialog()
        }

        binding.btnAddAdmin.setOnClickListener {
            showAddAdminDialog()
        }
    }

    private fun setupToolbar() {
        val user = sessionManager.getUser()
        binding.toolbar.title = "Admin: ${user?.username}"
        binding.toolbar.inflateMenu(com.example.appturismo.R.menu.main_menu)
        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                com.example.appturismo.R.id.action_logout -> {
                    sessionManager.logout()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finishAffinity() // Close all activities
                    true
                }
                else -> false
            }
        }
    }

    private fun showAddRecorridoDialog() {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Nuevo Recorrido Guiado")
        
        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }
        
        val etNombre = android.widget.EditText(this).apply { hint = "Nombre del Recorrido (ej. Recorrido 3: Ruta Sur)" }
        val etDesc = android.widget.EditText(this).apply { hint = "Descripción breve" }
        
        layout.addView(etNombre)
        layout.addView(etDesc)
        
        builder.setView(layout)
        builder.setPositiveButton("Crear") { _, _ ->
            val nombre = etNombre.text.toString()
            val desc = etDesc.text.toString()
            if (nombre.isNotEmpty()) {
                viewModel.addRecorrido(nombre, desc)
                android.widget.Toast.makeText(this, "Recorrido creado con éxito", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

    private fun showAddAdminDialog() {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Add New Admin")
        
        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }
        
        val etUser = android.widget.EditText(this).apply { hint = "Username" }
        val etPass = android.widget.EditText(this).apply { hint = "Password" }
        
        layout.addView(etUser)
        layout.addView(etPass)
        
        builder.setView(layout)
        builder.setPositiveButton("Add") { _, _ ->
            val user = etUser.text.toString()
            val pass = etPass.text.toString()
            if (user.isNotEmpty() && pass.isNotEmpty()) {
                viewModel.addAdmin(user, pass)
                android.widget.Toast.makeText(this, "Admin added", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }
}
