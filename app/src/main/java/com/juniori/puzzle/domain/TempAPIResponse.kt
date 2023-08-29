package com.juniori.puzzle.domain

sealed class TempAPIResponse<out T> {
    data class Success<T>(val data: T): TempAPIResponse<T>()
    data class Failure(val errorType: APIErrorType): TempAPIResponse<Nothing>()
}