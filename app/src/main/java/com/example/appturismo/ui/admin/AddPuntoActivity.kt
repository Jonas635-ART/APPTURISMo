package com.example.appturismo.ui.admin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.appturismo.TurismoApplication
import com.example.appturismo.data.model.PuntoTuristico
import com.example.appturismo.databinding.ActivityAddPuntoBinding
import com.example.appturismo.viewmodel.AdminViewModel
import com.example.appturismo.viewmodel.AdminViewModelFactory

class AddPuntoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddPuntoBinding
    private val viewModel: AdminViewModel by viewModels {
        AdminViewModelFactory((application as TurismoApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPuntoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()

        val puntoId = intent.getIntExtra("PUNTO_ID", -1)
        var isEdit = false

        if (puntoId != -1) {
            isEdit = true
            binding.toolbar.title = "Editar Punto"
            binding.btnGuardarPunto.text = "Actualizar en Base de Datos"
            
            // Load existing data
            viewModel.allPuntos.observe(this) { list ->
                val punto = list.find { it.id == puntoId }
                punto?.let {
                    binding.etNombre.setText(it.nombre)
                    binding.etDescripcion.setText(it.descripcion)
                    if (it.fotos.isNotEmpty()) {
                        binding.etUrlImagen.setText(it.fotos[0])
                    }
                    binding.etLatitud.setText(it.latitud.toString())
                    binding.etLongitud.setText(it.longitud.toString())
                }
            }
        }

        binding.btnGuardarPunto.setOnClickListener {
            val nombre = binding.etNombre.text.toString()
            val descripcion = binding.etDescripcion.text.toString()
            val urlImagen = binding.etUrlImagen.text.toString()
            val lat = binding.etLatitud.text.toString().toDoubleOrNull()
            val lng = binding.etLongitud.text.toString().toDoubleOrNull()

            if (nombre.isNotEmpty() && descripcion.isNotEmpty() && lat != null && lng != null) {
                val punto = PuntoTuristico(
                    id = if (isEdit) puntoId else 0,
                    nombre = nombre,
                    descripcion = descripcion,
                    fotos = listOf(urlImagen),
                    latitud = lat,
                    longitud = lng
                )
                
                if (isEdit) {
                    viewModel.updatePunto(punto)
                    Toast.makeText(this, "Lugar actualizado con éxito", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.addPunto(punto)
                    Toast.makeText(this, "Lugar agregado con éxito", Toast.LENGTH_SHORT).show()
                }
                finish()
            } else {
                Toast.makeText(this, "Por favor completa todos los campos correctamente", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnAbrirMaps.setOnClickListener {
            val gmmIntentUri = Uri.parse("geo:0,0?q=sitios turisticos")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            if (mapIntent.resolveActivity(packageManager) != null) {
                startActivity(mapIntent)
            } else {
                startActivity(Intent(Intent.ACTION_VIEW, gmmIntentUri))
            }
        }
    }

    private fun setupToolbar() {
        binding.toolbar.title = "Nuevo Punto"
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}
