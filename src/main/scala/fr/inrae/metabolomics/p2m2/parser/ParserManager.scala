package fr.inrae.metabolomics.p2m2.parser

import fr.inrae.metabolomics.p2m2.format.MassSpectrometryResultSet

case object ParserManager {
  def buildMassSpectrometryObject(path : String) : Option[MassSpectrometryResultSet] = path match {
    case _ if GCMSParser.sniffFile(path) =>
      Some(GCMSParser.parse(path))
    case _ if OpenLabCDSParser.sniffFile(path) =>
      Some(OpenLabCDSParser.parse(path))
    case _ if QuantifySummaryReportMassLynxParser.sniffFile(path) =>
      Some(QuantifySummaryReportMassLynxParser.parse(path))
    case _ if XcaliburXlsParser.sniffFile(path) =>
      Some(XcaliburXlsParser.parse(path))
    case _ => None
  }
}
