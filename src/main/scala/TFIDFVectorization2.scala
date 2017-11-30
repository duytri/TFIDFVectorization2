package main.scala

import scala.collection.mutable.ArrayBuffer
import scala.io.Source
import java.io.File
import java.io.BufferedWriter
import java.io.FileWriter
import main.scala.model.Corpus
import java.io.BufferedReader
import java.io.FileReader
import main.scala.model.Topic
import java.nio.charset.StandardCharsets
import java.io.OutputStreamWriter
import java.io.FileOutputStream
import scala.collection.mutable.LinkedHashMap

object TFIDFVectorization2 {
  def main(args: Array[String]): Unit = {
    try {
      if (args.length < 4 || args.length == 0 || args(0).equals("-h") || args(0).equals("--help"))
        printHelp
      else {
        // input arguments
        println("Getting user parameters...")
        val params = new ParamsHelper
        val inputPath = params.checkAndGetInput(args, "-i", "--input", ParamsHelperType.STRING).asInstanceOf[String]
        val outputPath = params.checkAndGetInput(args, "-o", "--output", ParamsHelperType.STRING).asInstanceOf[String]
        if (inputPath == null || outputPath == null) throw new Exception("ERROR: You must declare input and output")
        // Processing
        println("Processing...")
        val inputTopicFolders = (new File(inputPath)).listFiles()
        val outputVectors = outputPath + File.separator + "result_training_vectors.csv"
        val outputFeatures = outputPath + File.separator + "result_features.csv"

        println("Input path: " + inputPath)
        println("Output path: " + outputPath)

        // Analyzing
        println("Start analyze ...")
        var corpus = new Corpus
        var topicVectors = LinkedHashMap[String, Array[(String, Double)]]()
        //~~~~~~~~~~ Get input files ~~~~~~~~~~
        inputTopicFolders.foreach(topicFolder => {
          val label = topicFolder.getName.split("_")(0)
          //println(label)
          var topic = new Topic(label)
          val listFiles = topicFolder.listFiles()
          listFiles.foreach(file => {
            var lines = Source.fromFile(file)("UTF-8").getLines().toArray
            lines.foreach(topic.addOrIgnore(_))
          })
          corpus.addTopic(topic)
        })
        
        topicVectors = corpus.getVocabulary(100) //get top 50 words from each topic documents

        //~~~~~~~~~~ Vectorization ~~~~~~~~~~
        var featuresOfVector = new ArrayBuffer[String]
        var vectors = new Array[ArrayBuffer[Int]](topicVectors.size)

        topicVectors.foreach(topic => {
          //println("==" + topic._1)
          topic._2.foreach(word => {
            if (!featuresOfVector.contains(word._1.toLowerCase())) {
              featuresOfVector.append(word._1.toLowerCase())
            }
          })
        })
        var strRes = new StringBuilder
        topicVectors.foreach(topic => {
          //println("==++" + topic._1)
          strRes.append(topic._1)
          val strTopic = topic._2.map(word => word._1.toLowerCase())
          featuresOfVector.foreach(feature => {
            if (strTopic.contains(feature)) strRes.append(", 1")
            else strRes.append(", 0")
          })
          strRes.append("\n")
        })
        //~~~~~~~~~~ Write to files ~~~~~~~~~~~
        val vectorsFile = new File(outputVectors)
        val bwVectors = new BufferedWriter(new FileWriter(vectorsFile, true))
        bwVectors.flush()
        bwVectors.write(strRes.toString)
        bwVectors.close()

        val featuresFile = new File(outputFeatures)
        val bwFeatures = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(featuresFile, true), "UTF-8"))
        bwFeatures.flush()
        featuresOfVector.foreach(feature => {
          bwFeatures.write(feature + "\n")
        })
        bwFeatures.close()

        val featuresFile2 = new File(outputPath + File.separator + "result_features_tfidf.csv")
        val bwFeatures2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(featuresFile2, true), "UTF-8"))
        bwFeatures2.flush()
        topicVectors.foreach(topic => {
          bwFeatures2.write("======================================================== " + topic._1 + "\n")
          topic._2.foreach(word => {
            bwFeatures2.write(word._1 + ", " + word._2 + "\n")
          })
        })
        bwFeatures2.close()

        println("Finished!!!")
      }
    } catch {
      case e: Exception => {
        e.printStackTrace()
        printHelp()
      }
    }
  }

  def printHelp() = {
    println("Usage: TFIDFVectorization2 [Arguments]")
    println("       Arguments:")
    println("              -i --input     [path]   : Path of corpus folder")
    println("              -o --output    [path]   : Output file path")
    println("              -h --help               : Print this help")
  }
}