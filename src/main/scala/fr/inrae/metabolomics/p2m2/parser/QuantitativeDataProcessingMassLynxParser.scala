package fr.inrae.metabolomics.p2m2.parser

import fr.inrae.metabolomics.p2m2.format.XMLQuantitativeDataProcessingMassLynx

import java.io.{BufferedReader, ByteArrayInputStream, InputStream, InputStreamReader}
import scala.io.{Codec, Source}
import scala.xml.XML

object QuantitativeDataProcessingMassLynxParser
  extends Parser[XMLQuantitativeDataProcessingMassLynx] with FormatSniffer {


  def parseByteArray(content: Array[Byte], encode: Codec) : XMLQuantitativeDataProcessingMassLynx =
    XMLQuantitativeDataProcessingMassLynx.fromXml(
      XML.load(
        new BufferedReader(
          new InputStreamReader(
            new ByteArrayInputStream(content)))))
  override def parseFile(filename: String, encode: Codec): XMLQuantitativeDataProcessingMassLynx =
    XMLQuantitativeDataProcessingMassLynx.fromXml(XML.load(filename))


  override def extensionIsCompatible(filename: String): Boolean = {
    filename.split("\\.").lastOption match {
      case Some(ext) if ext.trim.toLowerCase == "xml" => true 
      case _ => false
    }
  }
  override def sniffByteArray(content: Array[Byte], encode: Codec): Boolean = {
    try {
      XMLQuantitativeDataProcessingMassLynx.fromXml(
        XML.load(
          new BufferedReader(
            new InputStreamReader(
              new ByteArrayInputStream(content))))).dataset.version != ""
    } catch {
      case _: Throwable =>
        false
    }
  }
  override def sniffFile(filename: String, encode: Codec): Boolean = {
    try {
      XMLQuantitativeDataProcessingMassLynx.fromXml(XML.load(filename)).dataset.version!=""
    } catch {
      case _: Throwable =>
        false
    }
  }
}
