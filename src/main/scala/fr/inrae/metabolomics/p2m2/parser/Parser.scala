package fr.inrae.metabolomics.p2m2.parser

import scala.io.Codec

trait Parser[OutputFormat] {
  def parseByteArray(content: Array[Byte], encode : Codec = Codec("ISO-8859-1")) : OutputFormat
  def parseFile(filename : String, encode : Codec = Codec("ISO-8859-1")) : OutputFormat
}
