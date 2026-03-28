package com.group9.biodiversityapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group9.biodiversityapp.api.RetrofitClient
import com.group9.biodiversityapp.api.model.TaxonResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class BrowseUiState(
    val species: List<TaxonResponse> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 1,
    val hasMorePages: Boolean = true,
    val groupId: String? = null,
    val groupName: String? = null,
    val searchQuery: String = ""
)

class BrowseViewModel : ViewModel() {

    private val api = RetrofitClient.apiService

    private val _uiState = MutableStateFlow(BrowseUiState())
    val uiState: StateFlow<BrowseUiState> = _uiState.asStateFlow()

    fun loadSpecies(groupId: String? = null, groupName: String? = null) {
        // Avoid reloading if already loaded for this group
        if (_uiState.value.groupId == groupId && _uiState.value.species.isNotEmpty()) return

        _uiState.value = BrowseUiState(
            isLoading = true,
            groupId = groupId,
            groupName = groupName
        )

        viewModelScope.launch {
            try {
                val response = api.getSpecies(
                    page = 1,
                    pageSize = 25,
                    informalTaxonGroup = groupId,
                    lang = "en"
                )
                _uiState.value = _uiState.value.copy(
                    species = response.results,
                    isLoading = false,
                    currentPage = 1,
                    hasMorePages = response.currentPage < response.lastPage
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to load species: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    fun loadMore() {
        val state = _uiState.value
        if (state.isLoadingMore || !state.hasMorePages) return

        _uiState.value = state.copy(isLoadingMore = true)

        viewModelScope.launch {
            try {
                val nextPage = state.currentPage + 1
                val response = api.getSpecies(
                    page = nextPage,
                    pageSize = 25,
                    informalTaxonGroup = state.groupId,
                    lang = "en"
                )
                _uiState.value = _uiState.value.copy(
                    species = state.species + response.results,
                    isLoadingMore = false,
                    currentPage = nextPage,
                    hasMorePages = nextPage < response.lastPage
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoadingMore = false,
                    error = "Failed to load more: ${e.message}"
                )
            }
        }
    }

    fun searchInGroup(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query, isLoading = true)

        viewModelScope.launch {
            try {
                val response = api.getSpecies(
                    page = 1,
                    pageSize = 25,
                    query = query.ifBlank { null },
                    informalTaxonGroup = _uiState.value.groupId,
                    lang = "en"
                )
                _uiState.value = _uiState.value.copy(
                    species = response.results,
                    isLoading = false,
                    currentPage = 1,
                    hasMorePages = response.currentPage < response.lastPage
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Search failed: ${e.message}"
                )
            }
        }
    }
}
