package com.example.scannerapp.dataclass

data class RecordFilterSortState(
    val active: Boolean,
    val inactive: Boolean,
    val expired: Boolean,
    val record: Boolean,
    val sortOrder: SortOrderEnum
)
