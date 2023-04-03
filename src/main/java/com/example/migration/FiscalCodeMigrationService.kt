package com.example.migration

import org.springframework.stereotype.Service
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

@Service("fiscalCodeMigrationService")
class FiscalCodeMigrationService(
    private val fiscalCodeSparkJob: FiscalCodeSparkJob,
): IDataMigration {

    @OptIn(ExperimentalTime::class)
    override fun migrateData() {
        measureTimedValue{ fiscalCodeSparkJob.migrate() }.also {
            println("\nMigrating data took ${it.duration.toString(DurationUnit.SECONDS, 3)}")
        }
    }

}