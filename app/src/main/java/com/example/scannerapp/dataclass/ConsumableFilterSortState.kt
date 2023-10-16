package com.example.scannerapp.dataclass

data class ConsumableFilterSortState(
    val active: Boolean,
    val inactive: Boolean,
    val remainingQuantity: Boolean,
    val sortOrder: SortOrderEnum
)
