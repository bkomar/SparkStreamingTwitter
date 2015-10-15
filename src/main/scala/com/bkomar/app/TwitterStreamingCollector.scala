package com.bkomar.app

import java.io.File

import com.bkomar.utils.Utils
import org.apache.spark.SparkConf
import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}


object TwitterStreamingCollector {

  def main(args: Array[String]) {
    if (args.length < 3) {
      System.err.println("Run streaming with the following parameters: <outputPath> <batchIntervalSeconds> <partitionsNum>")
      System.exit(1)
    }

    //TODO bk move to utils
    val outputPath: String = args(0)
    val batchInterval: Int = args(1).toInt
    val partitionNum: Int = args(2).toInt

    val sparkConf = new SparkConf().setMaster("local[2]").setAppName("TwitterStreamingCollector")

    val ssc = new StreamingContext(sparkConf, Seconds(batchInterval))

    Utils.getTwitterOAuth

    val stream = TwitterUtils.createStream(ssc, None)

    val hashTags = stream.flatMap(status => status.getText.split(" ").filter(_.startsWith("#")))


        val outputDir = new File(outputPath.toString)
        if (outputDir.exists()) {
          System.err.println("ERROR - %s already exists: delete or specify another directory".format(
            outputPath))
          System.exit(1)
        }
        outputDir.mkdirs()

        hashTags.foreachRDD((rdd, time) => {
          if (rdd.count() > 0) {
            println("!!!!!!!!!Received items" + rdd.count())
            rdd
              .repartition(partitionNum)
              .saveAsTextFile(outputPath + "/tweets_" + time.milliseconds.toString)
          }
        })

    ssc.start()
    ssc.awaitTermination()
  }
}
