package com.anpfuel.domain.model

import com.anpfuel.domain.valueobject.BrazilianState

data class MunicipalitySearchResult(
    val municipality: String,
    val state: BrazilianState,
)
