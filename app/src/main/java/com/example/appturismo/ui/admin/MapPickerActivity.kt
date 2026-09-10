package com.example.appturismo.ui.admin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appturismo.databinding.ActivityMapPickerBinding

class MapPickerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMapPickerBinding
    private var selectedLat: Double = -0.2201
    private var selectedLng: Double = -78.5122

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapPickerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()

        val initialLat = intent.getDoubleExtra("LATITUDE", -0.2201)
        val initialLng = intent.getDoubleExtra("LONGITUDE", -78.5122)

        selectedLat = if (initialLat != 0.0) initialLat else -0.2201
        selectedLng = if (initialLng != 0.0) initialLng else -78.5122

        updateCoordinatesDisplay()

        setupWebView(selectedLat, selectedLng)

        binding.btnConfirmarUbicacion.setOnClickListener {
            val resultIntent = Intent().apply {
                putExtra("LATITUDE", selectedLat)
                putExtra("LONGITUDE", selectedLng)
            }
            setResult(Activity.RESULT_OK, resultIntent)
            Toast.makeText(this, "Ubicación confirmada", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun updateCoordinatesDisplay() {
        binding.tvCoordenadasSeleccionadas.text = "Latitud: %.6f, Longitud: %.6f".format(selectedLat, selectedLng)
    }

    private fun setupWebView(lat: Double, lng: Double) {
        val webView = binding.webViewMap
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.userAgentString = "APPTURISMO_AndroidApp/1.0 (com.example.appturismo; Android Mobile)"
        webView.webViewClient = WebViewClient()

        webView.addJavascriptInterface(object {
            @JavascriptInterface
            fun onLocationSelected(lat: Double, lng: Double) {
                runOnUiThread {
                    selectedLat = lat
                    selectedLng = lng
                    updateCoordinatesDisplay()
                }
            }
        }, "AndroidInterface")

        val htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
                <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
                <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
                <style>
                    body, html, #map { height: 100%; margin: 0; padding: 0; }
                    .search-box {
                        position: absolute; top: 12px; left: 12px; right: 60px; z-index: 1000;
                        background: white; padding: 6px 10px; border-radius: 20px; box-shadow: 0 4px 12px rgba(0,0,0,0.25);
                        display: flex; gap: 8px; align-items: center;
                    }
                    .search-box input { flex: 1; border: none; outline: none; padding: 8px; font-size: 14px; background: transparent; }
                    .search-box button { background: #006A60; color: white; border: none; border-radius: 16px; padding: 8px 16px; font-weight: bold; font-size: 13px; }
                </style>
            </head>
            <body>
                <div class="search-box">
                    <input type="text" id="searchInput" placeholder="Buscar lugar o ciudad en Ecuador..." />
                    <button onclick="searchPlace()">Buscar</button>
                </div>
                <div id="map"></div>
                <script>
                    var initialLat = $lat;
                    var initialLng = $lng;

                    var map = L.map('map').setView([initialLat, initialLng], 14);

                    // 100% Free Public Esri World Street Map Tiles (Zero API Key required, zero rate-limiting)
                    var esriStreet = L.tileLayer('https://server.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer/tile/{z}/{y}/{x}', {
                        maxZoom: 19,
                        attribution: 'Tiles © Esri'
                    });

                    var osmStandard = L.tileLayer('https://tile.openstreetmap.de/{z}/{x}/{y}.png', {
                        maxZoom: 19,
                        attribution: '© OpenStreetMap'
                    });

                    var hotTourist = L.tileLayer('https://a.tile.openstreetmap.fr/hot/{z}/{x}/{y}.png', {
                        maxZoom: 19,
                        attribution: '© OpenStreetMap HOT'
                    });

                    map.addLayer(esriStreet);

                    var baseMaps = {
                        "Calles (Esri)": esriStreet,
                        "Estándar (OSM)": osmStandard,
                        "Turístico (HOT)": hotTourist
                    };
                    L.control.layers(baseMaps, null, { position: 'topright' }).addTo(map);

                    var marker = L.marker([initialLat, initialLng], {draggable: true}).addTo(map);

                    function notifyAndroid(lat, lng) {
                        if (window.AndroidInterface && window.AndroidInterface.onLocationSelected) {
                            window.AndroidInterface.onLocationSelected(lat, lng);
                        }
                    }

                    marker.on('dragend', function(e) {
                        var coord = e.target.getLatLng();
                        notifyAndroid(coord.lat, coord.lng);
                    });

                    map.on('click', function(e) {
                        marker.setLatLng(e.latlng);
                        notifyAndroid(e.latlng.lat, e.latlng.lng);
                    });

                    function searchPlace() {
                        var q = document.getElementById('searchInput').value;
                        if (!q) return;
                        fetch('https://nominatim.openstreetmap.org/search?format=json&q=' + encodeURIComponent(q))
                            .then(function(res) { return res.json(); })
                            .then(function(data) {
                                if (data && data.length > 0) {
                                    var nLat = parseFloat(data[0].lat);
                                    var nLon = parseFloat(data[0].lon);
                                    map.setView([nLat, nLon], 15);
                                    marker.setLatLng([nLat, nLon]);
                                    notifyAndroid(nLat, nLon);
                                }
                            });
                    }

                    document.getElementById('searchInput').addEventListener('keypress', function(e) {
                        if (e.key === 'Enter') {
                            searchPlace();
                        }
                    });
                </script>
            </body>
            </html>
        """.trimIndent()

        webView.loadDataWithBaseURL("https://arcgis.com", htmlContent, "text/html", "UTF-8", null)
    }
}
