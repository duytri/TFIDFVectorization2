package main.scala.model

import scala.collection.mutable.ArrayBuffer
import main.scala.TFIDFCalc
import scala.collection.mutable.LinkedHashMap

class Corpus(var topics: ArrayBuffer[Topic], var size: Int) {
  var tfidf = new ArrayBuffer[LinkedHashMap[String, Double]]()
  def this() {
    this(new ArrayBuffer[Topic], 0)
  }

  def this(topics: ArrayBuffer[Topic]) {
    this(topics, topics.size)
  }

  def addTopic(topic: Topic) {
    topics.append(topic)
    size += 1
  }

  def statTFIDF() {
    topics.foreach { topic =>
      var tfidfOneTopic = LinkedHashMap[String, Double]()
      topic.words.foreach(oneWord => {
        if (oneWord != null)
          tfidfOneTopic += oneWord._1 -> TFIDFCalc.tfIdf(oneWord, topic, topics)
      })
      tfidf.append(tfidfOneTopic)
    }
  }

  def getVocabulary(top: Int) = {
    var result = LinkedHashMap[String, Array[(String, Double)]]()
    statTFIDF()
    val vocab = tfidf.toArray.map(topic => {
      topic.toArray.sortWith((x, y) => {
        x._2 > y._2
      }).take(top)
    })
    for (i <- 0 until size) {
      result += topics(i).label -> vocab(i)
    }
    result
  }

  def getVocabulary(lowerBound: Double, upperBound: Double) = {
    var result = LinkedHashMap[String, Array[(String, Double)]]()
    statTFIDF()
    val vocab = tfidf.toArray.map(topic => {
      topic.toArray.filter(x => {
        x._2 >= lowerBound && x._2 <= upperBound
      })
    })
    for (i <- 0 until size) {
      result += topics(i).label -> vocab(i)
    }
    result
  }
}