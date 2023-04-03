package com.example.migration

import org.springframework.stereotype.Service

@Service
class BusinessLogicService {

    fun applyBusinessLogic(input: FiscalCode): EnrichedFiscalCode? =
        input.takeIf { it.validate() }?.let { enrich(it) }

    private fun enrich(input: FiscalCode) = EnrichedFiscalCode(
        fiscalCode = input.fiscalCode,
        tenantId = 100011,
    )

    private fun FiscalCode.validate(): Boolean =
        this@validate.fiscalCode.length == 16
}

data class FiscalCode(
    val fiscalCode: String,
)

data class EnrichedFiscalCode(
    val fiscalCode: String,
    val tenantId: Long,
)