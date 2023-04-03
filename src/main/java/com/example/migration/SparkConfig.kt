package com.example.migration

import org.apache.spark.SparkConf
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.sql.SparkSession
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer

@Configuration
class SparkConfig {

    private val appName: String = "Spark-POC"

    private val master: String = "local[*]" // master node will run using local CPU cores to parallelize the tasks

    @Bean
    fun sparkConf(): SparkConf = SparkConf()
        .setAppName(appName)
        .setMaster(master)

    @Bean
    fun javaSparkContext(): JavaSparkContext =
        JavaSparkContext(sparkConf())

    @Bean
    fun sparkSession(): SparkSession =
        SparkSession
            .builder()
            .sparkContext(javaSparkContext().sc())
            .appName(appName)
            .orCreate

    companion object {
        @Bean
        fun propertySourcesPlaceholderConfigurer(): PropertySourcesPlaceholderConfigurer =
            PropertySourcesPlaceholderConfigurer()
    }
}