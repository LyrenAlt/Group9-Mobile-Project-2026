package com.group9.biodiversityapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group9.biodiversityapp.api.model.TaxonResponse
import com.group9.biodiversityapp.data.repository.TaxonRepository
import kotlinx.coroutines.launch

class SearchViewModel(
    private val repository: TaxonRepository
) : ViewModel() {

    var searchQuery by mutableStateOf("")
        private set

    var searchResults by mutableStateOf<List<TaxonResponse>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var isEmpty by mutableStateOf(false)
        private set

    var selectedSpecies by mutableStateOf<TaxonResponse?>(null)
        private set

    fun onQueryChanged(query: String) {
        searchQuery = query
        if (query.length >= 2) {
            searchSpecies()
        }
    }

    fun searchSpecies() {
        if (searchQuery.length < 2) return

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            isEmpty = false

            try {
                val response = repository.fetchSpecies(
                    query = searchQuery,
                    pageSize = 25
                )
                searchResults = response.results
                isEmpty = searchResults.isEmpty()
            } catch (e: Exception) {
                errorMessage = "Something went wrong. Please try again."
                searchResults = emptyList()
            } finally {
                isLoading = false
            }
        }
    }

    fun onSpeciesSelected(species: TaxonResponse) {
        selectedSpecies = species
    }

    fun clearSearch() {
        searchQuery = ""
        searchResults = emptyList()
        isEmpty = false
        errorMessage = null
        selectedSpecies = null
    }
}