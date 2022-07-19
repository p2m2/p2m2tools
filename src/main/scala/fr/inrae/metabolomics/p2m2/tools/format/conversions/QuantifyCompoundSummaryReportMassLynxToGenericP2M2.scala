package fr.inrae.metabolomics.p2m2.tools.format.conversions

import fr.inrae.metabolomics.p2m2.tools.format.{GenericP2M2, QuantifyCompoundSummaryReportMassLynx}

import scala.language.implicitConversions

object QuantifyCompoundSummaryReportMassLynxToGenericP2M2 {
  implicit def QuantifyCompoundSummaryReportMassLynxToGenericP2M2(x: QuantifyCompoundSummaryReportMassLynx) : GenericP2M2 =
    GenericP2M2(
      x.results
        .flatMap {
          case (
            compound : String,
              allValues  : Seq[Map[QuantifyCompoundSummaryReportMassLynx.HeaderField.HeaderField,String]]) =>
              allValues.map(
                values => {
                  Map(
                    GenericP2M2.HeaderField.metabolite -> compound,
                    GenericP2M2.HeaderField.sample -> values.getOrElse(QuantifyCompoundSummaryReportMassLynx.HeaderField.Name,""),
                    GenericP2M2.HeaderField.retTime -> values.getOrElse(QuantifyCompoundSummaryReportMassLynx.HeaderField.RT,""),
                    GenericP2M2.HeaderField.area -> values.getOrElse(QuantifyCompoundSummaryReportMassLynx.HeaderField.Area,""),
                    GenericP2M2.HeaderField.height -> values.getOrElse(QuantifyCompoundSummaryReportMassLynx.HeaderField.Height,""),
                    GenericP2M2.HeaderField.acquisitionDate -> values.getOrElse(QuantifyCompoundSummaryReportMassLynx.HeaderField.`Acq.Date`,""),
                    GenericP2M2.HeaderField.injectedVolume -> values.getOrElse(QuantifyCompoundSummaryReportMassLynx.HeaderField.`Inj. Vol`,"")
                  )
                })
          }
    )
}
