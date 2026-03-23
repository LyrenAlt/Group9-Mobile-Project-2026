package com.group9.biodiversityapp

import com.group9.biodiversityapp.api.RetrofitClient
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

/**
 * Integration tests that hit the live laji.fi API.
 *
 * Run from Android Studio:  right-click this file -> Run
 * Run from terminal:        ./gradlew :app:test --tests "*.LajiApiIntegrationTest"
 *
 * These prove the Retrofit setup, auth interceptor, and Gson deserialization all work.
 */
class LajiApiIntegrationTest {

    private val api = RetrofitClient.apiService

    @Test
    fun `search taxa for Parus major returns results`() = runTest {
        val response = api.getTaxa(query = "Parus major", pageSize = 5)

        println("=== Taxa search: Parus major ===")
        println("Total results : ${response.total}")
        println("Page          : ${response.currentPage}/${response.lastPage}")
        response.results.forEach { taxon ->
            println("  ${taxon.id} | ${taxon.scientificName} | ${taxon.vernacularName} | rank=${taxon.taxonRank}")
        }

        assertTrue("Expected at least 1 result", response.results.isNotEmpty())
        assertTrue("Total should be > 0", response.total > 0)
    }

    @Test
    fun `search species-level taxa for bear returns results`() = runTest {
        val response = api.getSpecies(query = "bear", pageSize = 5, lang = "en")

        println("=== Species search: bear ===")
        println("Total results : ${response.total}")
        response.results.forEach { species ->
            println("  ${species.id} | ${species.scientificName} | ${species.vernacularName}")
        }

        assertTrue("Expected at least 1 result", response.results.isNotEmpty())
    }

    @Test
    fun `autocomplete taxon returns suggestions`() = runTest {
        val results = api.autocompleteTaxon(query = "kettu", lang = "fi")

        println("=== Autocomplete: kettu ===")
        results.forEach { result ->
            println("  ${result.key} | ${result.value} | scientific=${result.payload?.scientificName}")
        }

        assertTrue("Expected at least 1 autocomplete suggestion", results.isNotEmpty())
    }

    @Test
    fun `get taxon by ID returns valid taxon`() = runTest {
        // MX.37600 = Parus major (Great Tit)
        val taxon = api.getTaxonById(id = "MX.37600", lang = "en")

        println("=== Taxon by ID: MX.37600 ===")
        println("  scientificName : ${taxon.scientificName}")
        println("  vernacularName : ${taxon.vernacularName}")
        println("  taxonRank      : ${taxon.taxonRank}")

        assertEquals("MX.37600", taxon.id)
        assertNotNull("scientificName should not be null", taxon.scientificName)
    }

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
    fun `observation count returns total`() = runTest {
        val count = api.getObservationCount()

        println("=== Observation count (all) ===")
        println("Total observations: ${count.total}")

        assertTrue("Total should be > 0", count.total > 0)
    }
}
