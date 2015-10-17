package com.bkomar.app

import com.bkomar.utils.Utils
import org.apache.spark.SparkConf
import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

object TwitterStreamingCollector {

  def main(args: Array[String]) {
    //number of args except filters
    val baseParamsCount = 3
    if (args.length < 4) {
      System.err.println("Run streaming with the following parameters: <outputPath> <batchIntervalSeconds> " +
        "<partitionsNum> <filtering keywords>")
      System.exit(1)
    }

    //TODO bk move to utils
    val outputPath: String = args(0)
    val batchInterval: Int = args(1).toInt
    val partitionNum: Int = args(2).toInt
    val keyWordsFilters: Seq[String] = args.takeRight(args.length - baseParamsCount)

    println("Collector is executed with the filters: " + keyWordsFilters.mkString(Utils.hashTagSeparator))

    val sparkConf = new SparkConf().setAppName("TwitterStreamingCollector")
    val ssc = new StreamingContext(sparkConf, Seconds(batchInterval))

    Utils.setUpTwitterOAuth

    val twitterStream = TwitterUtils.createStream(ssc, None, keyWordsFilters)
    twitterStream.foreachRDD((rdd, time) => {
      if (rdd.count() > 0) {
        println("Numbmer of tweets received: " + rdd.count())
        rdd
          .map(t => (
            t.getUser.getName,
            t.getCreatedAt.toString,
            t.getText,
            t.getHashtagEntities.map(_.getText).mkString(Utils.hashTagSeparator)
//            t.getRetweetCount  issue in twitter api, always returns 0
            ))
          .repartition(partitionNum)
          .saveAsTextFile(outputPath + "tweets_" + time.milliseconds.toString)

        //TODO bk add checkpointing
      }
    })

    ssc.start()
    ssc.awaitTermination()
  }
}
