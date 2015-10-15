package com.bkomar.utils

import java.util.Properties
import twitter4j.auth.OAuthAuthorization
import twitter4j.conf.ConfigurationBuilder


import scala.io.Source


object Utils {

  /**
   * Set up Twitter oAuth connection
   * @return OAuthAuthorization
   */
  def getTwitterOAuth() = {
    val props = readProperties("system")

    System.setProperty("twitter4j.oauth.consumerKey", props.getProperty("twitter.oauth.consumer.key"))
    System.setProperty("twitter4j.oauth.consumerSecret", props.getProperty("twitter.oauth.consumer.secret"))
    System.setProperty("twitter4j.oauth.accessToken", props.getProperty("twitter.oauth.access.token"))
    System.setProperty("twitter4j.oauth.accessTokenSecret", props.getProperty("twitter.oauth.access.token.secret"))

//    return Some(new OAuthAuthorization(new ConfigurationBuilder().build()))
  }

  /**
   * Loads properties from app classpath
   * @param fileName properties file name
   * @return properties
   */
  def readProperties(fileName: String) : Properties = {
    val url = getClass.getResource("/app.properties")
    val properties = new Properties()
    if (url != null) {
      try {
        val reader = Source.fromURL(url).bufferedReader()
        properties.load(reader)
        reader.close()
      } catch {
        case ex: Exception =>
          System.err.println("Application properties can not be loaded." + ex.getMessage)
          System.exit(1)
      }
    }
    return properties
  }
}
