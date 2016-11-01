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

import org.apache.spark.SparkContext

import org.joda.time.DateTime

import org.slf4j.LoggerFactory

/**
  * Loads the airport data, transforms it into PSV text format, then saves it in HDFS.
  *
  * Expected job arguments in key-value pairs, with the following keys:
  * - job.sourceFile
  * - job.outputDir
  */
object LoadAirportsAsTextFormat extends ArgsParser {

  val log = LoggerFactory.getLogger(getClass)

  def main(args: Array[String]) {

    val config = parse(args)
    val sc = new SparkContext()
    val rawData = sc.textFile(config(JobSourceFile)).cache()

    log.info("Loaded airport data as RDD: totalRows={}", rawData.count())

    val outputDir = appendRunPartition(config(JobOutputDir))

    // remove enclosing double quotes then convert to PSV format
    rawData.map(_.split(','))
      .map(_.map(_.stripPrefix("\"").stripSuffix("\"")))
      .map(_.mkString("|"))
      .saveAsTextFile(outputDir)

    log.info("Successfully saved the data in PSV format: outputDir={}", outputDir)

    sc.stop()
  }

  def appendRunPartition(outputDir: String) = s"$outputDir/run=" + DateTime.now.toString("yyyy-MM-dd_HHmmss")

}
