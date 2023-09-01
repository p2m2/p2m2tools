package fr.inrae.metabolomics.p2m2.parser

import scala.io.Codec

trait FormatSniffer {
  def extensionIsCompatible(filename : String) : Boolean
  def sniffByteArray(content: Array[Byte], encode : Codec = Codec("ISO-8859-1") ): Boolean
  def sniffFile(filename : String,encode : Codec = Codec("ISO-8859-1") ) : Boolean
}
