package com.group9.biodiversityapp.api.model

data class SpeciesPageResult(
    val results: List<TaxonResponse>,
    val nextApiPage: Int,
    val hasMorePages: Boolean
)
