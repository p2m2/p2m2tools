package fr.inrae.metabolomics.p2m2.format.conversions

import fr.inrae.metabolomics.p2m2.converter.GCMSOutputFiles2IsocorInput
import fr.inrae.metabolomics.p2m2.format.GCMS.HeaderField.HeaderField
import fr.inrae.metabolomics.p2m2.format.Xcalibur.CompoundSheetXcalibur
import fr.inrae.metabolomics.p2m2.format.{GCMS, GenericP2M2, Isocor, OpenLabCDS, QuantifyCompoundSummaryReportMassLynx, Xcalibur}

import scala.language.implicitConversions

object FormatConversions {
  implicit def QuantifyCompoundSummaryReportMassLynxToGenericP2M2(x: QuantifyCompoundSummaryReportMassLynx) : GenericP2M2 = {
    GenericP2M2(
      x.results
        .flatMap {
          case (
            compound : String,
              allValues  : Seq[Map[QuantifyCompoundSummaryReportMassLynx.HeaderField.HeaderField,String]]) =>
              allValues
                .filter( _.nonEmpty )
                .map(
                values => {
                  Map(
                    GenericP2M2.HeaderField.metabolite -> Some(compound),
                    GenericP2M2.HeaderField.sample -> values.get(QuantifyCompoundSummaryReportMassLynx.HeaderField.Name),
                    GenericP2M2.HeaderField.retTime -> values.get(QuantifyCompoundSummaryReportMassLynx.HeaderField.RT),
                    GenericP2M2.HeaderField.area -> values.get(QuantifyCompoundSummaryReportMassLynx.HeaderField.Area),
                    GenericP2M2.HeaderField.height -> values.get(QuantifyCompoundSummaryReportMassLynx.HeaderField.Height),
                    GenericP2M2.HeaderField.acquisitionDate -> values.get(QuantifyCompoundSummaryReportMassLynx.HeaderField.`Acq.Date`),
                    GenericP2M2.HeaderField.injectedVolume -> values.get(QuantifyCompoundSummaryReportMassLynx.HeaderField.`Inj. Vol`)
                  ).flatMap {
                    case (k,Some(v)) => Some(k,v)
                    case _ => None
                  }
                })
          }

    )
  }

  implicit def XcaliburToGenericP2M2(x: Xcalibur) : GenericP2M2 =
    GenericP2M2(
      x.results
        .flatMap(
          (injections : CompoundSheetXcalibur) => {
            injections.compoundByInjection.map(
              ( values : Map[Xcalibur.HeaderField.HeaderField, String]) => {
                Map(
                  GenericP2M2.HeaderField.metabolite -> injections.compoundInformationHeader.get(Xcalibur.HeaderSheetField.`Component Name`),
                  GenericP2M2.HeaderField.sample -> values.get(Xcalibur.HeaderField.`Sample Name`),
                  GenericP2M2.HeaderField.retTime -> values.get(Xcalibur.HeaderField.RT),
                  GenericP2M2.HeaderField.area -> values.get(Xcalibur.HeaderField.Area),
                  GenericP2M2.HeaderField.height -> values.get(Xcalibur.HeaderField.Height),
                  GenericP2M2.HeaderField.acquisitionDate -> values.get(Xcalibur.HeaderField.`Acq Date`),
                  GenericP2M2.HeaderField.injectedVolume -> values.get(Xcalibur.HeaderField.`Inj Vol`)
                ).flatMap {
                  case (k,Some(v)) => Some(k,v)
                  case _ => None
                }
              }
            )
          }
        )
    )

  implicit def GCMSToGenericP2M2(x: GCMS) :  GenericP2M2 =
    GenericP2M2(
      x.ms_quantitative_results.map(
        (res : Map[HeaderField, String]) => {
          Map(
            GenericP2M2.HeaderField.metabolite -> None,
            GenericP2M2.HeaderField.sample -> res.get(GCMS.HeaderField.Name),
            GenericP2M2.HeaderField.retTime -> res.get(GCMS.HeaderField.`Ret.Time`),
            GenericP2M2.HeaderField.area -> res.get(GCMS.HeaderField.Area),
            GenericP2M2.HeaderField.height -> res.get(GCMS.HeaderField.Height),
            GenericP2M2.HeaderField.acquisitionDate -> x.header.get(GCMS.HeaderFileField.Output_Date),
            GenericP2M2.HeaderField.injectedVolume -> None
          ).flatMap {
            case (k,Some(v)) => Some(k,v)
            case _ => None
          }
        }
      )
    )

  implicit def OpenLabCDSToGenericP2M2(x: OpenLabCDS) :  GenericP2M2 =
    GenericP2M2(
      x.results.map(
        (res : Map[OpenLabCDS.HeaderField.HeaderField, String]) => {
          Map(
            GenericP2M2.HeaderField.metabolite -> res.get(OpenLabCDS.HeaderField.Name),
            GenericP2M2.HeaderField.sample -> x.header.get(OpenLabCDS.HeaderFileField.`Sample Name`),
            GenericP2M2.HeaderField.retTime -> res.get(OpenLabCDS.HeaderField.RetTime),
            GenericP2M2.HeaderField.area -> res.get(OpenLabCDS.HeaderField.Area),
            GenericP2M2.HeaderField.height -> res.get(OpenLabCDS.HeaderField.Amount),
            GenericP2M2.HeaderField.acquisitionDate -> x.header.get(OpenLabCDS.HeaderFileField.`Last changed Acq. Method`),
            GenericP2M2.HeaderField.injectedVolume -> x.header.get(OpenLabCDS.HeaderFileField.`Inj Volume`)
          ).flatMap {
            case (k,Some(v)) => Some(k,v)
            case _ => None
          }
        }
      )
    )
}
