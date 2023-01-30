package com.juniori.puzzle.domain.usecase

import com.juniori.puzzle.domain.repository.LocationStateRepository
import javax.inject.Inject

class GetAddressUseCase @Inject constructor(
    private val repository: LocationStateRepository
) {
    operator fun invoke(lat: Double, long: Double) = repository.getAddressInfo(lat, long)
}