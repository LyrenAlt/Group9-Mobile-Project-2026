package com.group9.biodiversityapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group9.biodiversityapp.api.RetrofitClient
import com.group9.biodiversityapp.api.model.AutocompleteResult
import com.group9.biodiversityapp.api.model.InformalTaxonGroupResponse
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SearchUiState(
    val query: String = "",
    val suggestions: List<AutocompleteResult> = emptyList(),
    val groups: List<InformalTaxonGroupResponse> = emptyList(),
    val isLoadingGroups: Boolean = false,
    val isLoadingSuggestions: Boolean = false,
    val error: String? = null
)

class SearchViewModel : ViewModel() {

    private val api = RetrofitClient.apiService

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    // Top-level groups only (the ones most useful for browsing)
    private val topLevelGroupIds = setOf(
        "MVL.1",   // Birds
        "MVL.2",   // Mammals
        "MVL.26",  // Reptiles and amphibians
        "MVL.27",  // Fishes
        "MVL.232", // Insects and arachnids
        "MVL.233", // Fungi and lichens
        "MVL.23",  // Bryophytes
        "MVL.22",  // Algae
        "MVL.28",  // Worms
        "MVL.24",  // Macrofungi
    )

    init {
        loadGroups()
    }

    private fun loadGroups() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingGroups = true)
            try {
                val response = api.getInformalTaxonGroups(pageSize = 100, lang = "en")
                // Show top-level groups first, then the rest
                val topLevel = response.results.filter { it.id in topLevelGroupIds }
                val others = response.results.filter { it.id !in topLevelGroupIds }
                _uiState.value = _uiState.value.copy(
                    groups = topLevel + others,
                    isLoadingGroups = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to load groups: ${e.message}",
                    isLoadingGroups = false
                )
            }
        }
    }

    fun onQueryChanged(newQuery: String) {
        _uiState.value = _uiState.value.copy(query = newQuery)
        searchJob?.cancel()

        if (newQuery.length < 2) {
            _uiState.value = _uiState.value.copy(suggestions = emptyList())
            return
        }

        searchJob = viewModelScope.launch {
            delay(300) // debounce
            _uiState.value = _uiState.value.copy(isLoadingSuggestions = true)
            try {
                val results = api.autocompleteTaxon(query = newQuery, lang = "en", limit = 10)
                _uiState.value = _uiState.value.copy(
                    suggestions = results,
                    isLoadingSuggestions = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoadingSuggestions = false
                )
            }
        }
    }

    fun clearSearch() {
        _uiState.value = _uiState.value.copy(query = "", suggestions = emptyList())
    }
}
