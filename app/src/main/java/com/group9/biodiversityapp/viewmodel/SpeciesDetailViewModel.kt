package com.group9.biodiversityapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group9.biodiversityapp.api.model.TaxonResponse
import com.group9.biodiversityapp.data.repository.TaxonRepository
import kotlinx.coroutines.launch

class SpeciesDetailViewModel(
    private val repository: TaxonRepository
) : ViewModel() {

    var species by mutableStateOf<TaxonResponse?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var isFavorite by mutableStateOf(false)
        private set

    fun loadSpeciesById(id: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                species = repository.fetchTaxonById(id)
                checkIfFavorite(id)
            } catch (e: Exception) {
                errorMessage = "Could not load species details."
            } finally {
                isLoading = false
            }
        }
    }

    private fun checkIfFavorite(id: String) {
        viewModelScope.launch {
            repository.isFavorite(id).collect { result ->
                isFavorite = result
            }
        }
    }

    fun toggleFavorite() {
        species?.let { taxon ->
            viewModelScope.launch {
                try {
                    repository.toggleFavorite(taxon)
                } catch (e: Exception) {
                    errorMessage = "Could not update favorites."
                }
            }
        }
    }
}