package fr.inrae.metabolomics.p2m2.parser

import fr.inrae.metabolomics.p2m2.tools.format.output.QuantitativeDataProcessingMassLynx

import scala.xml.XML

object QuantitativeDataProcessingMassLynxParser
  extends Parser[QuantitativeDataProcessingMassLynx] with FormatSniffer {

  override def parse(filename: String): QuantitativeDataProcessingMassLynx = {

    val xml = XML.load(filename)

    println(xml)

    QuantitativeDataProcessingMassLynx.fromXml(xml)
  }

  override def extensionIsCompatible(filename: String): Boolean = ???

  override def sniffFile(filename: String): Boolean = ???
}
