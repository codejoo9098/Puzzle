package com.juniori.puzzle.data

sealed class APIResponse<out R> {
    data class Success<out R>(val result: R) : APIResponse<R>()
    data class Failure(val exception: Exception) : APIResponse<Nothing>()
    object Loading : APIResponse<Nothing>()
}
