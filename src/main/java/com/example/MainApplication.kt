package com.example

import org.apache.spark.sql.SparkSession
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import com.example.migration.FiscalCodeMigrationService


//@SpringBootApplication
//class MainApplication: CommandLineRunner {
//
//    override fun run(vararg args: String?) {
//        val sparkSession = SparkSession
//            .builder()
//            .appName("Spark-POC")
//            .master("local[*]")
//            .orCreate
//    }
//}
//
//fun main(args: Array<String>) {
//    runApplication<MainApplication>(*args)
//}

@SpringBootApplication
class MainApplication: SpringBootServletInitializer()

fun main(args: Array<String>) {
    val appContext = runApplication<MainApplication>(*args)
    (appContext.getBean("fiscalCodeMigrationService", FiscalCodeMigrationService::class)
            as FiscalCodeMigrationService)
        .migrateData()
}


