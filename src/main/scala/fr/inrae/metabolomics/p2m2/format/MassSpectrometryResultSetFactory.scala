package fr.inrae.metabolomics.p2m2.format
import upickle.default._

case object MassSpectrometryResultSetFactory {
  def build(stringObj : String) : Option[MassSpectrometryResultSet] = stringObj match {
    case _ if stringObj.contains("GenericP2M2") => Some(read[GenericP2M2](stringObj))
    case _ if stringObj.contains("GCMS") => Some(read[GCMS](stringObj))
    case _ if stringObj.contains("OpenLabCDS") => Some(read[OpenLabCDS](stringObj))
    case _ if stringObj.contains("QuantifyCompoundSummaryReportMassLynx") =>
      Some(read[QuantifyCompoundSummaryReportMassLynx](stringObj))
    case _ if stringObj.contains("QuantifySampleSummaryReportMassLynx") =>
      Some(read[QuantifySampleSummaryReportMassLynx](stringObj))
    case _ if stringObj.contains("Xcalibur") => Some(read[Xcalibur](stringObj))
    case _ if stringObj.contains("Isocor") => Some(read[Isocor](stringObj))
    case _ => None
  }

  def stringify(o : MassSpectrometryResultSet) : String = o match {
    case a : GenericP2M2 => write(a)
    case a : GCMS => write(a)
    case a : OpenLabCDS => write(a)
    case a : QuantifyCompoundSummaryReportMassLynx => write(a)
    case a : QuantifySampleSummaryReportMassLynx => write(a)
    case a : Xcalibur => write(a)
    case a : Isocor => write(a)
    case _ => System.err.println("Can not *stringify* : "+ o.getClass.toString);""
  }
}
