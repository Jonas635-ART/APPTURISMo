package com.example.appturismo.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appturismo.TurismoApplication
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

        var showingFavoritesOnly = false

        viewModel.allPuntos.observe(this) { puntos ->
            if (showingFavoritesOnly) {
                adapter.submitList(puntos.filter { it.esFavorito })
            } else {
                adapter.submitList(puntos)
            }
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
                binding.btnToggleFavorites.text = "Ver Todos"
                val all = viewModel.allPuntos.value ?: emptyList()
                adapter.submitList(all.filter { it.esFavorito })
            } else {
                binding.btnToggleFavorites.text = "Ver Favoritos"
                adapter.submitList(viewModel.allPuntos.value ?: emptyList())
            }
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
