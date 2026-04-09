package com.group9.biodiversityapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
//import com.group9.biodiversityapp.api.RetrofitClient
import com.group9.biodiversityapp.data.repository.TaxonRepository
import com.group9.biodiversityapp.api.model.TaxonResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DetailUiState(
    val taxon: TaxonResponse? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

//class DetailViewModel : ViewModel() {

//    private val api = RetrofitClient.apiService


//    private val _uiState = MutableStateFlow(DetailUiState())
//    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()


class DetailViewModel(
    private val repository: TaxonRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()


    fun loadTaxon(taxonId: String) {
        if (_uiState.value.taxon?.id == taxonId) return

        _uiState.value = DetailUiState(isLoading = true)

        viewModelScope.launch {
            try {
//                val taxon = repository.getTaxonByIdWithFallback(id = taxonId, lang = "en")
//                _uiState.value = DetailUiState(taxon = taxon)
                val taxon = repository.getTaxonByIdWithFallback(id = taxonId, lang = "en")

                _uiState.value = if (taxon != null) {
                    DetailUiState(taxon = taxon)
                } else {
                    DetailUiState(error = "Species details not found")
                }
            } catch (e: Exception) {
                _uiState.value = DetailUiState(
                    error = "Failed to load species details: ${e.message}"
                )
            }
        }
    }
}
