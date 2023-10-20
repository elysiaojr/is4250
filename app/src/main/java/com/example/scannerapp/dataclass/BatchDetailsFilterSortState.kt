package com.example.scannerapp.dataclass

data class BatchDetailsFilterSortState(
    val active: Boolean,
    val inactive: Boolean,
    val nonEmpty: Boolean,
    val empty: Boolean,
    val expired: Boolean,
    val sortOrder: SortOrderEnum
)
