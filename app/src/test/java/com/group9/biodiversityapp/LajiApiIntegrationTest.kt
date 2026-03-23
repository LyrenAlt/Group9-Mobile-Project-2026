package com.group9.biodiversityapp

import com.group9.biodiversityapp.api.RetrofitClient
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

/**
 * Integration tests that hit the live laji.fi API.
 *
 * Run from Android Studio:  right-click this file -> Run
 * Run from terminal:        ./gradlew :app:testDebugUnitTest --tests "*.LajiApiIntegrationTest"
 *
 * These prove the Retrofit setup, auth interceptor, and Gson deserialization all work.
 */
class LajiApiIntegrationTest {

    private val api = RetrofitClient.apiService

    // ── Warehouse / Observations ──────────────────────────────────────

    @Test
    fun `fetch observations returns paginated results`() = runTest {
        val response = api.getObservations(pageSize = 5)

        println("=== Observations (latest 5) ===")
        println("Total results : ${response.total}")
        response.results.forEach { obs ->
            val name = obs.unit?.linkings?.taxon?.scientificName ?: obs.unit?.taxonVerbatim ?: "unknown"
            val loc = obs.gathering?.municipality ?: "unknown location"
            val date = obs.gathering?.displayDateTime ?: "unknown date"
            println("  $name | $loc | $date")
        }

        assertTrue("Expected at least 1 observation", response.results.isNotEmpty())
        assertTrue("Total should be > 0", response.total > 0)
    }

    @Test
    fun `observation count returns total`() = runTest {
        val count = api.getObservationCount()

        println("=== Observation count (all) ===")
        println("Total observations: ${count.total}")

        assertTrue("Total should be > 0", count.total > 0)
    }

    @Test
    fun `filter observations by taxon returns matching results`() = runTest {
        // MX.37600 = Parus major (Great Tit)
        val response = api.getObservations(taxonId = "MX.37600", pageSize = 5)

        println("=== Observations for Parus major (MX.37600) ===")
        println("Total results : ${response.total}")
        response.results.forEach { obs ->
            val name = obs.unit?.linkings?.taxon?.scientificName ?: obs.unit?.taxonVerbatim ?: "unknown"
            val loc = obs.gathering?.municipality ?: "unknown location"
            val date = obs.gathering?.displayDateTime ?: "unknown date"
            println("  $name | $loc | $date")
        }

        assertTrue("Expected at least 1 observation for Parus major", response.results.isNotEmpty())
        assertTrue("Total should be > 0", response.total > 0)
    }

    @Test
    fun `observation count for specific taxon returns total`() = runTest {
        // MX.37600 = Parus major (Great Tit)
        val count = api.getObservationCount(taxonId = "MX.37600")

        println("=== Observation count for Parus major (MX.37600) ===")
        println("Total observations: ${count.total}")

        assertTrue("Total should be > 0", count.total > 0)
    }

    // ── Areas ─────────────────────────────────────────────────────────

    @Test
    fun `fetch areas returns area list`() = runTest {
        val response = api.getAreas(pageSize = 10, lang = "en")

        println("=== Areas (first 10) ===")
        println("Total areas : ${response.total}")
        response.results.forEach { area ->
            println("  ${area.id} | ${area.name} | type=${area.areaType}")
        }

        assertTrue("Expected at least 1 area", response.results.isNotEmpty())
    }

    @Test
    fun `fetch areas filtered by type returns matching areas`() = runTest {
        // The /areas endpoint does not support server-side type filtering,
        // so we fetch all areas and filter client-side.
        val response = api.getAreas(pageSize = 100, lang = "en")
        val countries = response.results.filter { it.areaType == "ML.country" }

        println("=== Areas (countries, client-filtered) ===")
        println("Total areas fetched : ${response.total}")
        println("Countries found     : ${countries.size}")
        countries.take(10).forEach { area ->
            println("  ${area.id} | ${area.name} | type=${area.areaType}")
        }

        assertTrue("Expected at least 1 country", countries.isNotEmpty())
        countries.forEach { area ->
            assertEquals("All results should be countries", "ML.country", area.areaType)
        }
    }

    // ── Informal Taxon Groups ─────────────────────────────────────────

    @Test
    fun `fetch informal taxon groups returns groups`() = runTest {
        val response = api.getInformalTaxonGroups(lang = "en")

        println("=== Informal Taxon Groups ===")
        println("Total groups : ${response.total}")
        response.results.take(10).forEach { group ->
            println("  ${group.id} | ${group.name}")
        }

        assertTrue("Expected at least 1 group", response.results.isNotEmpty())
    }

    @Test
    fun `informal taxon groups contain birds and mammals`() = runTest {
        val response = api.getInformalTaxonGroups(lang = "en")

        val groupNames = response.results.map { it.name }

        println("=== Checking for Birds and Mammals in groups ===")
        println("Total groups: ${response.total}")

        assertTrue("Groups should contain Birds", groupNames.contains("Birds"))
        assertTrue("Groups should contain Mammals", groupNames.contains("Mammals"))
    }
}
