package com.group9.biodiversityapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.group9.biodiversityapp.api.model.TaxonResponse
import com.group9.biodiversityapp.ui.viewmodel.DetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    taxonId: String,
    onNavigateBack: () -> Unit,
    viewModel: DetailViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(taxonId) {
        viewModel.loadTaxon(taxonId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        state.taxon?.vernacularName
                            ?: state.taxon?.scientificName
                            ?: "Species Detail"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                state.error != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = state.error ?: "Unknown error",
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.loadTaxon(taxonId) }) {
                            Text("Retry")
                        }
                    }
                }
                state.taxon != null -> {
                    TaxonDetail(taxon = state.taxon!!)
                }
            }
        }
    }
}

@Composable
private fun TaxonDetail(taxon: TaxonResponse) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Image gallery
        val images = taxon.multimedia.orEmpty()
        if (images.isNotEmpty()) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                items(images) { media ->
                    val url = media.fullURL ?: media.squareThumbnailURL ?: media.thumbnailURL
                    if (url != null) {
                        Card(
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            AsyncImage(
                                model = url,
                                contentDescription = taxon.vernacularName,
                                modifier = Modifier
                                    .width(300.dp)
                                    .height(220.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }

            // Image credits
            images.firstOrNull()?.author?.let { author ->
                Text(
                    text = "Photo: $author",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Names section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Names",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                taxon.vernacularName?.let {
                    InfoRow(label = "Common Name", value = it)
                }
                taxon.scientificName?.let {
                    InfoRow(label = "Scientific Name", value = it, italic = true)
                }
                taxon.scientificNameAuthorship?.let {
                    InfoRow(label = "Author", value = it)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Classification section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Classification",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                taxon.taxonRank?.let { rank ->
                    InfoRow(label = "Rank", value = rank.removePrefix("MX."))
                }
                taxon.parent?.let {
                    InfoRow(label = "Parent Taxon", value = it)
                }
                taxon.informalTaxonGroups?.let { groups ->
                    if (groups.isNotEmpty()) {
                        InfoRow(label = "Groups", value = groups.joinToString(", "))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Status section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Status",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                InfoRow(
                    label = "Finnish Species",
                    value = if (taxon.finnish == true) "Yes" else "No"
                )
                InfoRow(
                    label = "Has Multimedia",
                    value = if (taxon.hasMultimedia == true) "Yes" else "No"
                )
                taxon.cursiveName?.let {
                    InfoRow(
                        label = "Cursive Name",
                        value = if (it) "Yes" else "No"
                    )
                }
            }
        }

        // Image details if available
        if (images.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Media (${images.size} images)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    images.forEachIndexed { index, media ->
                        media.author?.let {
                            InfoRow(label = "Image ${index + 1} Author", value = it)
                        }
                        media.licenseId?.let {
                            InfoRow(
                                label = "Image ${index + 1} License",
                                value = it.substringAfterLast(".")
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ID at the bottom
        Text(
            text = "ID: ${taxon.id}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun InfoRow(label: String, value: String, italic: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(130.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontStyle = if (italic) FontStyle.Italic else FontStyle.Normal,
            modifier = Modifier.weight(1f)
        )
    }
}
