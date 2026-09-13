package com.vedantraut.herohub.presentation.search

enum class SearchSortOrder(val displayName: String) {
    RELEVANCE("Most Relevant"),
    POWER_DESC("Highest Power"),
    POWER_ASC("Lowest Power"),
    NAME_ASC("Alphabetical (A–Z)"),
    NAME_DESC("Alphabetical (Z–A)")
}

enum class SearchViewMode {
    GRID,
    LIST
}

data class SearchFilterState(
    val publisher: String = "All",
    val alignment: String = "All",
    val minPower: Int = 0,
    val maxPower: Int = 100,
    val gender: String = "All",
    val sortOrder: SearchSortOrder = SearchSortOrder.RELEVANCE
) {
    val isDefault: Boolean
        get() = publisher == "All" &&
                alignment == "All" &&
                minPower == 0 &&
                maxPower == 100 &&
                gender == "All" &&
                sortOrder == SearchSortOrder.RELEVANCE

    val activeCount: Int
        get() {
            var count = 0
            if (publisher != "All") count++
            if (alignment != "All") count++
            if (minPower > 0 || maxPower < 100) count++
            if (gender != "All") count++
            if (sortOrder != SearchSortOrder.RELEVANCE) count++
            return count
        }
}
