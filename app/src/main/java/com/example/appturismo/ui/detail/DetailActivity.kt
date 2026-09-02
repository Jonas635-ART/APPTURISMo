package com.example.appturismo.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.appturismo.TurismoApplication
import com.example.appturismo.databinding.ActivityDetailBinding
import com.example.appturismo.util.SessionManager
import com.example.appturismo.viewmodel.DetailViewModel
import com.example.appturismo.viewmodel.DetailViewModelFactory

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val viewModel: DetailViewModel by viewModels {
        DetailViewModelFactory((application as TurismoApplication).repository)
    }
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        setupToolbar()
        val currentUser = sessionManager.getUser()
        val puntoId = intent.getIntExtra("PUNTO_ID", -1)

        if (puntoId != -1 && currentUser != null) {
            viewModel.loadPunto(puntoId, currentUser.id)
        }

        viewModel.punto.observe(this) { punto ->
            punto?.let {
                binding.tvDetailNombre.text = it.nombre
                binding.tvDetailDescripcion.text = it.descripcion
                if (it.fotos.isNotEmpty()) {
                    Glide.with(this).load(it.fotos[0]).into(binding.ivDetailFoto)
                }

                binding.btnGoToMap.setOnClickListener { _ ->
                    val gmmIntentUri = Uri.parse("google.navigation:q=${it.latitud},${it.longitud}")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    startActivity(mapIntent)
                }
            }
        }

        viewModel.isFavorite.observe(this) { isFav ->
            binding.cbFavorite.isChecked = isFav
        }

        binding.cbFavorite.setOnClickListener {
            currentUser?.let { user ->
                viewModel.toggleFavorite(user.id, puntoId)
            }
        }
    }

    private fun setupToolbar() {
        binding.toolbar.title = "Detalle"
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}
