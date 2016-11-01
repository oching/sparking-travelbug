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

package com.oching.sparking.travelbug.etl.util

import org.specs2.mutable.SpecificationWithJUnit

class EtlUtilSpec extends SpecificationWithJUnit {

  "An etl-util" should {

    "parse an array of key value pairs into a map using default key-value delimiter" in {
      val args = Array[String]("job.hdfs.sourceFile=/tmp/source.txt", "job.hdfs.outputPath=/tmp/output/")
      val mapConfig = EtlUtil.parse(args)

      mapConfig.size must_=== 2
      mapConfig("job.hdfs.sourceFile") must_=== "/tmp/source.txt"
      mapConfig("job.hdfs.outputPath") must_=== "/tmp/output/"
    }

    "parse an array of key value pairs into a map using provided key-value delimiter" in {
      val args = Array[String]("job.hdfs.sourceFile:/tmp/source.txt", "job.hdfs.outputPath:/tmp/output/")
      val mapConfig = EtlUtil.parse(args, ':')

      mapConfig.size must_=== 2
      mapConfig("job.hdfs.sourceFile") must_=== "/tmp/source.txt"
      mapConfig("job.hdfs.outputPath") must_=== "/tmp/output/"
    }

    "return an empty map if the array is empty" in {
      EtlUtil.parse(Array[String]()).isEmpty must_=== true
    }
  }
}
