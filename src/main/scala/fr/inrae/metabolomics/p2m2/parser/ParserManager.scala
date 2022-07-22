package fr.inrae.metabolomics.p2m2.parser

import fr.inrae.metabolomics.p2m2.format.MassSpectrometryResultSet

case object ParserManager {
  def buildMassSpectrometryObject(path : String) : Option[MassSpectrometryResultSet] = {
    if (GCMSParser.sniffFile(path)) {
      Some(GCMSParser.parse(path))
    } else if (OpenLabCDSParser.sniffFile(path)) {
      Some(OpenLabCDSParser.parse(path))
    } else if (QuantifyCompoundSummaryReportMassLynxParser.sniffFile(path)) {
      Some(QuantifyCompoundSummaryReportMassLynxParser.parse(path))
    } else if (XcaliburXlsParser.sniffFile(path)) {
      Some(XcaliburXlsParser.parse(path))
    } else
        None
  }
}
