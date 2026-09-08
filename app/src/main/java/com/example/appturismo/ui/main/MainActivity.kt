package com.example.appturismo.ui.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appturismo.TurismoApplication
import com.example.appturismo.data.model.PuntoTuristico
import com.example.appturismo.data.model.Recorrido
import com.example.appturismo.data.model.User
import com.example.appturismo.databinding.ActivityMainBinding
import com.example.appturismo.ui.detail.DetailActivity
import com.example.appturismo.ui.login.LoginActivity
import com.example.appturismo.util.SessionManager
import com.example.appturismo.viewmodel.MainViewModel
import com.example.appturismo.viewmodel.MainViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory((application as TurismoApplication).repository)
    }
    private lateinit var sessionManager: SessionManager
    private var currentUser: User? = null

    private var currentRecorridosList: List<Recorrido> = emptyList()
    private var currentPuntosList: List<PuntoTuristico> = emptyList()
    private var showingFavoritesOnly = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        currentUser = sessionManager.getUser()

        if (currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setupToolbar()

        val adapter = PuntoAdapter(
            isAdmin = currentUser?.role == "ADMIN",
            onPuntoClick = { punto ->
                val intent = Intent(this, DetailActivity::class.java)
                intent.putExtra("PUNTO_ID", punto.id)
                startActivity(intent)
            },
            onFavoriteClick = { punto ->
                currentUser?.let { user ->
                    viewModel.toggleFavorite(user.id, punto)
                }
            }
        )

        binding.rvPuntos.layoutManager = LinearLayoutManager(this)
        binding.rvPuntos.adapter = adapter

        // Observe Recorridos for Spinner
        viewModel.allRecorridos.observe(this) { recorridos ->
            currentRecorridosList = recorridos
            val nombresRecorridos = recorridos.map { it.nombre }
            val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, nombresRecorridos)
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerRecorridos.adapter = spinnerAdapter
        }

        binding.spinnerRecorridos.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position in currentRecorridosList.indices) {
                    val selectedRecorrido = currentRecorridosList[position]
                    observePuntosByRecorrido(selectedRecorrido.id, adapter)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.sync()
        }

        viewModel.syncing.observe(this) { isSyncing ->
            binding.swipeRefresh.isRefreshing = isSyncing
        }

        binding.btnToggleFavorites.setOnClickListener {
            showingFavoritesOnly = !showingFavoritesOnly
            if (showingFavoritesOnly) {
                binding.btnToggleFavorites.text = "Todos"
            } else {
                binding.btnToggleFavorites.text = "Favoritos"
            }
            filterAndSubmitList(adapter)
        }

        binding.btnVerRutaCompleta.setOnClickListener {
            lanzarRutaCompletaGoogleMaps(currentPuntosList)
        }
    }

    private fun observePuntosByRecorrido(recorridoId: Int, adapter: PuntoAdapter) {
        viewModel.getPuntosByRecorrido(recorridoId).observe(this) { puntos ->
            currentPuntosList = puntos.sortedBy { it.orden }
            filterAndSubmitList(adapter)
        }
    }

    private fun filterAndSubmitList(adapter: PuntoAdapter) {
        if (showingFavoritesOnly) {
            adapter.submitList(currentPuntosList.filter { it.esFavorito })
        } else {
            adapter.submitList(currentPuntosList)
        }
    }

    private fun lanzarRutaCompletaGoogleMaps(puntos: List<PuntoTuristico>) {
        if (puntos.size < 2) {
            Toast.makeText(this, "Se necesitan al menos 2 puntos para calcular la ruta", Toast.LENGTH_SHORT).show()
            return
        }

        val origen = "${puntos.first().latitud},${puntos.first().longitud}"
        val destino = "${puntos.last().latitud},${puntos.last().longitud}"

        val waypointsStr = if (puntos.size > 2) {
            puntos.subList(1, puntos.size - 1).joinToString("|") { "${it.latitud},${it.longitud}" }
        } else ""

        val url = if (waypointsStr.isNotEmpty()) {
            "https://www.google.com/maps/dir/?api=1&origin=$origen&destination=$destino&waypoints=$waypointsStr&travelmode=driving"
        } else {
            "https://www.google.com/maps/dir/?api=1&origin=$origen&destination=$destino&travelmode=driving"
        }

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.setPackage("com.google.android.apps.maps")
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }
    }

    private fun setupToolbar() {
        binding.toolbar.title = "Hola, ${currentUser?.username}"
        binding.toolbar.inflateMenu(com.example.appturismo.R.menu.main_menu)
        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                com.example.appturismo.R.id.action_logout -> {
                    sessionManager.logout()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }
}
