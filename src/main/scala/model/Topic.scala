package main.scala.model

import scala.collection.mutable.ArrayBuffer
import java.util.StringTokenizer
import scala.collection.mutable.Map

class Topic(var label: String, var words: Map[String, Int], var length: Int) {
  val specialChars = Array((" "), (";"), ("/"), ("."), (","), ("\""), ("\t"), ("#"), ("\u00a0"), ("("), (")"), ("["), ("]"), ("!"), ("?"), ("'"), (":"), ("&"), ("="), ("-"), ("<"), (">"), ("–"), ("{"), ("}"), ("\\"), ("..."), ("*"), ("+"), ("$"), ("@"), ("\u00a9"), ("\u00d7"), ("\u00ae"), ("\u00ad"), ("\u2028"), ("\u0323"), ("\u0300"), ("\u0301"), ("\u0302"), ("\u0303"), ("\u0309"), ("”"), ("“"), ("\""), ("\","), ("\":"), (":/"), ("\">"), ("=\"\""), ("=\"\">"), ("=\""), ("\"."), ("?\""), ("=”"), ("=“"), ("”,"), ("”."), ("“,"), ("“."), ("〞,"), ("〞."), ("〝,"), ("〝."), ("?”."), ("://"), ("_"), ("…"), ("’"), ("--"))
  def this() {
    this("", Map[String, Int](), 0)
  }

  def this(label: String) {
    this(label, Map[String, Int](), 0)
  }

  def this(words: Map[String, Int]) {
    this("", words, words.values.sum)
  }

  def addOrIgnore(word: String) {
    val w = word.toLowerCase
    if (!specialChars.contains(w)) {
      if (!words.contains(w)) {
        words += (w -> 1)
      } else {
        words.update(w, words(w) + 1)
      }
      length += 1
    }
  }

  def addWords(content: String) {
    val tokens = new StringTokenizer(content, "\n")
    while (tokens.hasMoreTokens()) {
      addOrIgnore(tokens.nextToken())
    }
  }
}