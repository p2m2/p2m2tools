package fr.inrae.metabolomics.p2m2.format.conversions

import fr.inrae.metabolomics.p2m2.format.{GenericP2M2, QuantifyCompoundSummaryReportMassLynx}

import scala.language.implicitConversions

object QuantifyCompoundSummaryReportMassLynxToGenericP2M2 {
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
}
