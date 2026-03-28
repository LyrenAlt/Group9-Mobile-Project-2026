package com.group9.biodiversityapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.group9.biodiversityapp.api.model.InformalTaxonGroupResponse
import com.group9.biodiversityapp.ui.viewmodel.SearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onNavigateToDetail: (taxonId: String) -> Unit,
    onNavigateToBrowse: (groupId: String, groupName: String) -> Unit,
    onNavigateToBrowseAll: () -> Unit,
    viewModel: SearchViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Species Directory") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Search bar
            OutlinedTextField(
                value = state.query,
                onValueChange = { viewModel.onQueryChanged(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                placeholder = { Text("Search species by name...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                trailingIcon = {
                    if (state.query.isNotEmpty()) {
                        IconButton(onClick = { viewModel.clearSearch() }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Show autocomplete suggestions when typing
            if (state.suggestions.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 300.dp)
                    ) {
                        items(state.suggestions) { suggestion ->
                            ListItem(
                                headlineContent = { Text(suggestion.value) },
                                supportingContent = {
                                    suggestion.payload?.scientificName?.let {
                                        Text(it, fontStyle = FontStyle.Italic)
                                    }
                                },
                                modifier = Modifier.clickable {
                                    // key is the taxon ID like "MX.37600"
                                    onNavigateToDetail(suggestion.key)
                                    viewModel.clearSearch()
                                }
                            )
                            HorizontalDivider()
                        }
                    }
                }
            }

            if (state.query.isEmpty()) {
                // Browse section
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Browse by Group",
                        style = MaterialTheme.typography.titleMedium
                    )
                    TextButton(onClick = onNavigateToBrowseAll) {
                        Text("Browse All")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (state.isLoadingGroups) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(state.groups.take(20)) { group ->
                            GroupCard(
                                group = group,
                                onClick = {
                                    onNavigateToBrowse(group.id, group.name ?: "Unknown")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GroupCard(
    group: InformalTaxonGroupResponse,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = groupEmoji(group.id),
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = group.name ?: "Unknown",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/** Simple emoji mapping for common taxon groups. */
private fun groupEmoji(groupId: String): String = when (groupId) {
    "MVL.1" -> "\uD83D\uDC26"   // Birds
    "MVL.2" -> "\uD83D\uDC3B"   // Mammals
    "MVL.26" -> "\uD83E\uDD8E"  // Reptiles and amphibians
    "MVL.27" -> "\uD83D\uDC1F"  // Fishes
    "MVL.232" -> "\uD83D\uDC1B" // Insects and arachnids
    "MVL.233" -> "\uD83C\uDF44" // Fungi and lichens
    "MVL.23" -> "\uD83C\uDF3F"  // Bryophytes
    "MVL.22" -> "\uD83E\uDDEB"  // Algae
    "MVL.28" -> "\uD83E\uDEB1" // Worms
    "MVL.24" -> "\uD83C\uDF44"  // Macrofungi
    else -> "\uD83C\uDF3F"      // Default plant
}
