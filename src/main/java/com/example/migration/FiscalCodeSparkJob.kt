package com.example.migration

import org.apache.spark.sql.*
import org.apache.spark.sql.types.DataTypes
import org.apache.spark.sql.types.StructType
import org.springframework.stereotype.Component
import java.io.File
import org.apache.spark.sql.functions.*

@Component
class FiscalCodeSparkJob (
    private val sparkSession: SparkSession,
    private val springBLService: BusinessLogicService,
){

    // "src folder" containing 5 data & 5 manifest json files
    private val srcDataPath = "C:\\Users\\roberto.albano\\Downloads\\downloadTmLWU121856"

    private val fiscalCodeInputSchema = StructType()
        .add("fiscalCode", DataTypes.StringType, false)
        .add("typeOfChange", DataTypes.StringType, false)

    fun migrate() {
        /* 1. EXTRACT */    val srcData = readData()
        /* 2. TRANSFORM */  val enrichedPojoList = applySpringLogic(srcData)
        val enrichedDF = sparkSession.createDataFrame(enrichedPojoList, EnrichedFiscalCode::class.java)
        val toSave = transform(enrichedDF)
        /* 3. LOAD */ storeMigrated(toSave)
    }

    private fun applySpringLogic(srcData: Dataset<Row>): List<EnrichedFiscalCode> =
        srcData.select("fiscalCode").collectAsList().map { row ->
            FiscalCode(fiscalCode = row.getString(0))
        }.mapNotNull { springBLService.applyBusinessLogic(it) }

    private fun readData(): Dataset<Row> {
        var collectorDF = emptyDataFrame()
        // visiting the dir is not mandatory: Spark will accept the source dir and automatically detect files to read
        // I'll visit dir manually since for non-Unix file system I should have set up Hadoop for having it work
        visitDirectory().forEach { absFilepath ->
            collectorDF = collectorDF.union(sparkSession.read().json(absFilepath))
        }
        return collectorDF
    }

    private fun transform(srcDF: Dataset<Row>): Dataset<Row> = // adapt app schema to db schema
        srcDF.select("fiscalCode", "tenantId")
            .withColumnRenamed("fiscalCode", "fiscal_code")
            .withColumnRenamed("tenantId", "tenant_id")
            .withColumn("id", expr("uuid()"))


    private fun storeMigrated(finalDF: Dataset<Row>) =
        finalDF.write().format("jdbc")
            .option("url", "jdbc:postgresql://localhost:5432/spark")
            .option("driver", "org.postgresql.Driver")
            .option("dbtable", "suggestionlist.fiscal_code")
            .option("user", "postgres")
            .option("password", "postgres!pwd")
            .mode(SaveMode.Append)
            .save()

    private fun emptyDataFrame() =
        sparkSession.read().schema(fiscalCodeInputSchema).csv(
            sparkSession.emptyDataset(Encoders.STRING())
        )

    private fun visitDirectory() =
        File(srcDataPath)
            .walkTopDown()
            .map { it.absolutePath }
            .filter { filePath -> filePath.endsWith("-DATA.json")}

}