package fr.inrae.metabolomics.p2m2.parser

import fr.inrae.metabolomics.p2m2.tools.format.XMLQuantitativeDataProcessingMassLynx
import scala.xml.XML

object QuantitativeDataProcessingMassLynxParser
  extends Parser[XMLQuantitativeDataProcessingMassLynx] with FormatSniffer {

  override def parse(filename: String): XMLQuantitativeDataProcessingMassLynx = {

    val xml = XML.load(filename)

    XMLQuantitativeDataProcessingMassLynx.fromXml(xml)
  }

  override def extensionIsCompatible(filename: String): Boolean = {
    filename.split("\\.").lastOption match {
      case Some(ext) if ext.trim.toLowerCase == "xml" => true 
      case _ => false
    }
  }

  override def sniffFile(filename: String): Boolean = {
    try {
      val xml = XML.load(filename)
      XMLQuantitativeDataProcessingMassLynx.fromXml(xml).dataset.version!=""
    } catch {
      case _: Throwable =>
        false
    }
  }
}
