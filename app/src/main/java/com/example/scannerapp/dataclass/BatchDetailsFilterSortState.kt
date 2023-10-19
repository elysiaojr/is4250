package com.example.scannerapp.dataclass

data class BatchDetailsFilterSortState(
    val status: Int,
    val empty: Int,
    val expired: Int,
    val sortOrder: SortOrderEnum
)
