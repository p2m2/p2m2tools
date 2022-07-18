package fr.inrae.metabolomics.p2m2.parser

import fr.inrae.metabolomics.p2m2.tools.format.output.QuantitativeDataProcessingMassLynx

import scala.xml.XML

object QuantitativeDataProcessingMassLynxParser
  extends Parser[QuantitativeDataProcessingMassLynx] with FormatSniffer {

  override def parse(filename: String): QuantitativeDataProcessingMassLynx = {

    val xml = XML.load(filename)

    QuantitativeDataProcessingMassLynx.fromXml(xml)
  }

  override def extensionIsCompatible(filename: String): Boolean = {
    filename.split("\\.").lastOption match {
      case Some(ext) => ext.trim.toLowerCase == "xml"
      case None => false
    }
  }

  override def sniffFile(filename: String): Boolean = {
    try {
      val xml = XML.load(filename)
      QuantitativeDataProcessingMassLynx.fromXml(xml).dataset.version!=""
    } catch {
      case _: Throwable => {
        false
      }
    }
  }
}
