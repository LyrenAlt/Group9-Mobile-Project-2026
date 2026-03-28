package com.group9.biodiversityapp.ui.navigation

/**
 * Navigation route constants for the app.
 */
object NavRoutes {
    const val SEARCH = "search"
    const val BROWSE = "browse"
    const val BROWSE_WITH_GROUP = "browse/{groupId}/{groupName}"
    const val DETAIL = "detail/{taxonId}"

    fun browseWithGroup(groupId: String, groupName: String) =
        "browse/${groupId}/${groupName}"

    fun detail(taxonId: String) = "detail/${taxonId}"
}
