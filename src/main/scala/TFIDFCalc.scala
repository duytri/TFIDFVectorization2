package main.scala

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Map
import main.scala.model.Topic

object TFIDFCalc {
  def tf(term: String, doc: Map[String, Int]): Double = {
    var wordCount = 0d
    doc.foreach((x => wordCount += x._2))
    doc(term) / wordCount
  }

  def tf(term: (String, Int), doc: Topic): Double = {
    term._2.toDouble / doc.length.toDouble
  }

  def idf(term: String, allDocs: ArrayBuffer[Map[String, Int]]): Double = {
    var n = 0d
    allDocs.foreach(doc => {
      if (doc.contains(term)) n += 1
    })

    return Math.log10(allDocs.length.toDouble / n)
  }

  def idf(term: (String, Int), allDocs: ArrayBuffer[Topic]): Double = {
    var n = 0d
    allDocs.foreach(doc => {
      if (doc.words.contains(term._1)) n += 1
    })

    return Math.log10(allDocs.size / n)
  }

  def tfIdf(word: String, docIndex: Int, allDocs: ArrayBuffer[Map[String, Int]]): Double = {
    val doc = allDocs(docIndex)
    return tf(word, doc) * idf(word, allDocs)
  }

  def tfIdf(word: (String, Int), doc: Topic, allDocs: ArrayBuffer[Topic]): Double = {
    return tf(word, doc) * idf(word, allDocs)
  }
}