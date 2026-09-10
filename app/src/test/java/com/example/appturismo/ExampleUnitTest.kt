package com.example.appturismo

import org.junit.Test
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLDecoder

class ExampleUnitTest {

    @Test
    fun testGoogleMapsLinkShort() {
        val url = "https://maps.app.goo.gl/kJDdFvCBEvAA2jBt7?g_st=ac"
        val coords = procesarYExtraerCoordenadas(url)
        println("=== RESULTADO COORDENADAS SHORT ===")
        println("Latitud: ${coords?.first}, Longitud: ${coords?.second}")
        println("===================================")
        org.junit.Assert.assertNotNull("Las coordenadas no deben ser nulas", coords)
    }

    @Test
    fun testDmsCoordinates() {
        val text = "Basílica del Voto Nacional 0°12'53.0\"S 78°30'26.6\"W https://maps.app.goo.gl/kJDdFvCBEvAA2jBt7"
        val coords = procesarYExtraerCoordenadas(text)
        println("=== RESULTADO COORDENADAS DMS ===")
        println("Latitud: ${coords?.first}, Longitud: ${coords?.second}")
        println("=================================")
        org.junit.Assert.assertNotNull("Las coordenadas DMS no deben ser nulas", coords)
    }

    @Test
    fun testUrlEncodedConsent() {
        val encoded = "https://consent.google.com/m?continue=https%3A%2F%2Fwww.google.com%2Fmaps%2Fplace%2FPlaza%2BGrande%2F%40-0.220123%2C-78.512234%2C17z"
        val coords = procesarYExtraerCoordenadas(encoded)
        println("=== RESULTADO ENCODED ===")
        println("Latitud: ${coords?.first}, Longitud: ${coords?.second}")
        println("=========================")
        org.junit.Assert.assertNotNull("Las coordenadas encoded no deben ser nulas", coords)
    }

    private fun procesarYExtraerCoordenadas(input: String): Pair<Double, Double>? {
        var textToSearch = input

        // URL decode first in case input contains encoded URLs
        try {
            val decodedInput = URLDecoder.decode(input, "UTF-8")
            textToSearch += " $decodedInput"
        } catch (e: Exception) {
            // ignore
        }

        // 1. Try DMS coordinates parsing (e.g. 0°12'53.0"S 78°30'26.6"W)
        val dmsCoords = extractDmsCoordinates(input)
        if (dmsCoords != null) return dmsCoords

        // 2. Fetch URL if present
        val urlRegex = Regex("""https?://[^\s"<>]+""")
        val matchUrl = urlRegex.find(input)

        if (matchUrl != null) {
            val initialUrl = matchUrl.value
            try {
                var currentUrl = initialUrl
                var redirectCount = 0
                val maxRedirects = 10

                while (redirectCount < maxRedirects) {
                    val urlObj = URL(currentUrl)
                    val conn = urlObj.openConnection() as HttpURLConnection
                    conn.instanceFollowRedirects = false
                    conn.requestMethod = "GET"
                    conn.setRequestProperty(
                        "User-Agent",
                        "Mozilla/5.0 (Linux; Android 10; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"
                    )
                    conn.connectTimeout = 7000
                    conn.readTimeout = 7000
                    conn.connect()

                    val responseCode = conn.responseCode
                    val location = conn.getHeaderField("Location")
                    val refresh = conn.getHeaderField("Refresh")

                    textToSearch += " $currentUrl $location $refresh"

                    if (responseCode in 200..399) {
                        try {
                            val body = conn.inputStream.bufferedReader().use { it.readText() }
                            textToSearch += " $body"
                            try {
                                textToSearch += " " + URLDecoder.decode(body, "UTF-8")
                            } catch (e: Exception) { }
                        } catch (e: Exception) {
                            // ignore body read errors
                        }
                    }

                    conn.disconnect()

                    val nextUrl = location ?: parseRefreshHeader(refresh)
                    if (nextUrl != null && (responseCode in 300..399 || refresh != null)) {
                        currentUrl = if (nextUrl.startsWith("http")) {
                            nextUrl
                        } else {
                            URL(urlObj, nextUrl).toString()
                        }
                        redirectCount++
                    } else {
                        break
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Also decode the entire compiled textToSearch
        try {
            textToSearch += " " + URLDecoder.decode(textToSearch, "UTF-8")
        } catch (e: Exception) { }

        // Pattern 1: !3d(-?\d+\.\d+)!4d(-?\d+\.\d+)  (Google Maps place URLs)
        val pattern3d4d = Regex("""!3d(-?\d+\.\d+)!4d(-?\d+\.\d+)""")
        val match3d4d = pattern3d4d.find(textToSearch)
        if (match3d4d != null) {
            val lat = match3d4d.groupValues[1].toDoubleOrNull()
            val lng = match3d4d.groupValues[2].toDoubleOrNull()
            if (lat != null && lng != null) return Pair(lat, lng)
        }

        // Pattern 2: @(-?\d+\.\d+)\s*,\s*(-?\d+\.\d+)
        val patternAt = Regex("""@(-?\d+\.\d+)\s*,\s*(-?\d+\.\d+)""")
        val matchAt = patternAt.find(textToSearch)
        if (matchAt != null) {
            val lat = matchAt.groupValues[1].toDoubleOrNull()
            val lng = matchAt.groupValues[2].toDoubleOrNull()
            if (lat != null && lng != null) return Pair(lat, lng)
        }

        // Pattern 3: 3d(-?\d+\.\d+).*?4d(-?\d+\.\d+)
        val pattern3dAlt = Regex("""3d(-?\d+\.\d+)[^0-9\-]+4d(-?\d+\.\d+)""")
        val match3dAlt = pattern3dAlt.find(textToSearch)
        if (match3dAlt != null) {
            val lat = match3dAlt.groupValues[1].toDoubleOrNull()
            val lng = match3dAlt.groupValues[2].toDoubleOrNull()
            if (lat != null && lng != null) return Pair(lat, lng)
        }

        // Pattern 4: [?&](?:q|ll|center|sll|destination|near)=loc:?(-?\d+\.\d+)\s*,\s*(-?\d+\.\d+)
        val patternQ = Regex("""[?&](?:q|ll|center|sll|destination|near)=loc:?(-?\d+\.\d+)\s*,\s*(-?\d+\.\d+)""")
        val matchQ = patternQ.find(textToSearch)
        if (matchQ != null) {
            val lat = matchQ.groupValues[1].toDoubleOrNull()
            val lng = matchQ.groupValues[2].toDoubleOrNull()
            if (lat != null && lng != null) return Pair(lat, lng)
        }

        val patternQ2 = Regex("""[?&](?:q|ll|center|sll|destination)=(-?\d+\.\d+)\s*,\s*(-?\d+\.\d+)""")
        val matchQ2 = patternQ2.find(textToSearch)
        if (matchQ2 != null) {
            val lat = matchQ2.groupValues[1].toDoubleOrNull()
            val lng = matchQ2.groupValues[2].toDoubleOrNull()
            if (lat != null && lng != null) return Pair(lat, lng)
        }

        // Pattern 5: Raw coordinates in text like -0.220123, -78.512234
        val patternRaw = Regex("""(-?\d{1,2}\.\d{3,})\s*,\s*(-?\d{1,3}\.\d{3,})""")
        val matchRaw = patternRaw.find(textToSearch)
        if (matchRaw != null) {
            val lat = matchRaw.groupValues[1].toDoubleOrNull()
            val lng = matchRaw.groupValues[2].toDoubleOrNull()
            if (lat != null && lng != null) return Pair(lat, lng)
        }

        return null
    }

    private fun parseRefreshHeader(refresh: String?): String? {
        if (refresh == null) return null
        val parts = refresh.split("url=", ignoreCase = true, limit = 2)
        return if (parts.size > 1) parts[1].trim('\'', '"', ' ') else null
    }

    private fun extractDmsCoordinates(text: String): Pair<Double, Double>? {
        // Example DMS: 0°12'53.0"S 78°30'26.6"W
        val dmsRegex = Regex("""(\d+)°(\d+)'([\d.]+)"?\s*([NSns])\s+(\d+)°(\d+)'([\d.]+)"?\s*([EWewOWow])""")
        val match = dmsRegex.find(text) ?: return null

        try {
            val latDeg = match.groupValues[1].toDouble()
            val latMin = match.groupValues[2].toDouble()
            val latSec = match.groupValues[3].toDouble()
            val latDir = match.groupValues[4].uppercase()

            val lngDeg = match.groupValues[5].toDouble()
            val lngMin = match.groupValues[6].toDouble()
            val lngSec = match.groupValues[7].toDouble()
            val lngDir = match.groupValues[8].uppercase()

            var lat = latDeg + (latMin / 60.0) + (latSec / 3600.0)
            if (latDir == "S") lat = -lat

            var lng = lngDeg + (lngMin / 60.0) + (lngSec / 3600.0)
            if (lngDir == "W" || lngDir == "O") lng = -lng

            return Pair(lat, lng)
        } catch (e: Exception) {
            return null
        }
    }
}
