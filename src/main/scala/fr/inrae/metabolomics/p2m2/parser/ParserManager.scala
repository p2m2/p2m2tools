package fr.inrae.metabolomics.p2m2.parser

import fr.inrae.metabolomics.p2m2.format.ms.MassSpectrometryResultSet

case object ParserManager {
  def buildMassSpectrometryObject(filepath : String) : Option[MassSpectrometryResultSet] = filepath match {
    case _ if GCMSParser.sniffFile(filepath) =>
      Some(GCMSParser.parseFile(filepath))
    case _ if OpenLabCDSParser.sniffFile(filepath) =>
      Some(OpenLabCDSParser.parseFile(filepath))
    case _ if QuantifySummaryReportMassLynxParser.sniffFile(filepath) =>
      Some(QuantifySummaryReportMassLynxParser.parseFile(filepath))
    case _ if XcaliburXlsParser.sniffFile(filepath) =>
      Some(XcaliburXlsParser.parseFile(filepath))
    case _ => None
  }

  def buildMassSpectrometryObject(contentFile: Array[Byte]): Option[MassSpectrometryResultSet] = contentFile match {
    case _ if GCMSParser.sniffByteArray(contentFile) =>
      Some(GCMSParser.parseByteArray(contentFile))
    case _ if OpenLabCDSParser.sniffByteArray(contentFile) =>
      Some(OpenLabCDSParser.parseByteArray(contentFile))
    case _ if QuantifySummaryReportMassLynxParser.sniffByteArray(contentFile) =>
      Some(QuantifySummaryReportMassLynxParser.parseByteArray(contentFile))
    case _ if XcaliburXlsParser.sniffByteArray(contentFile) =>
      Some(XcaliburXlsParser.parseByteArray(contentFile))
    case _ => None
  }
}
