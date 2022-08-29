package fr.inrae.metabolomics.p2m2.format
import upickle.default._

case object MassSpectrometryResultSetFactory {
  def build(stringObj : String) : Option[MassSpectrometryResultSet] = stringObj match {
    case _ if stringObj.contains("GenericP2M2") => Some(read[GenericP2M2](stringObj))
    case _ if stringObj.contains("GCMS") => Some(read[GCMS](stringObj))
    case _ if stringObj.contains("OpenLabCDS") => Some(read[OpenLabCDS](stringObj))
    case _ if stringObj.contains("QuantifyCompoundSummaryReportMassLynx") =>
      Some(read[QuantifyCompoundSummaryReportMassLynx](stringObj))
    case _ if stringObj.contains("Xcalibur") => Some(read[Xcalibur](stringObj))
    case _ if stringObj.contains("Isocor") => Some(read[Isocor](stringObj))
    case _ => None
  }

  def stringify(o : GenericP2M2) : String = write(o)
  def stringify(o : GCMS) : String = write(o)
  def stringify(o : OpenLabCDS) : String = write(o)
  def stringify(o : QuantifyCompoundSummaryReportMassLynx) : String = write(o)
  def stringify(o : Xcalibur) : String = write(o)
  def stringify(o : Isocor) : String = write(o)

}
