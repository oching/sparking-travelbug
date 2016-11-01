/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.oching.sparking.travelbug.etl

import org.apache.spark.sql.{SaveMode, SparkSession}

import org.slf4j.LoggerFactory

/**
  * Loads airlines data to HDFS in ParquetFormat.
  *
  * Expected job arguments in key-value pairs, with the following keys:
  * - job.sourceFile
  * - job.outputDir
  */
object LoadAirlinesAsParquetFormat extends ArgsParser {

  val log = LoggerFactory.getLogger(getClass)

  def main(args: Array[String]) = {

    val config = parse(args)
    val spark = SparkSession
      .builder()
      .config("spark.sql.parquet.binaryAsString", "true")
      .config("spark.sql.warehouse.dir", "/tmp/spark-warehouse/")
//  to use Hive with Spark SQL
//    .enableHiveSupport()
      .getOrCreate()

    // default column names on load: _1, _2, _3, etc.
    val airlines = spark.read.csv(config(JobSourceFile))
      .toDF("id", "name", "alias", "iata_code", "icao_code", "callsign", "country", "active")

    airlines.printSchema()

    // show active airlines
    airlines.select("name", "alias", "callsign", "country").where("active = 'Y'").show()

    // count airlines by country
    airlines.groupBy("country").count().show()

    airlines.write.mode(SaveMode.Overwrite).parquet(config(JobOutputDir))

    log.info("Successfully saved airlines data in parquet format: outputPath={}", config(JobOutputDir))

    spark.stop()
  }

}
