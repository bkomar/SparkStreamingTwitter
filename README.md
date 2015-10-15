# Spark Streaming Twitter Collector

Before running app edit _app.properties_ to setup Twitter connection

#### To execute application in Spark use 
```
spark-submit --master local[2] --class com.bkomar.app.TwitterStreamingCollector TwitterStreaming-1.0-SNAPSHOT-jar-with-dependencies.jar outputDir 5 2
```