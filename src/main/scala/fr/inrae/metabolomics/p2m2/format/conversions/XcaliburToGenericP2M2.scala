package fr.inrae.metabolomics.p2m2.format.conversions

import fr.inrae.metabolomics.p2m2.format.Xcalibur.CompoundSheetXcalibur
import fr.inrae.metabolomics.p2m2.format.{GenericP2M2, Xcalibur}
import Xcalibur.CompoundSheetXcalibur
import Xcalibur.HeaderField.HeaderField

import scala.language.implicitConversions

object XcaliburToGenericP2M2 {

  implicit def QuantifyCompoundSummaryReportMassLynxToGenericP2M2(x: Xcalibur) : GenericP2M2 =
    GenericP2M2(
      x.results
        .flatMap(
          (injections : CompoundSheetXcalibur) => {
            injections.compoundByInjection.map(
              ( values : Map[Xcalibur.HeaderField.HeaderField, String]) => {
                Map(
                  GenericP2M2.HeaderField.metabolite -> injections.compoundInformationHeader.getOrElse(Xcalibur.HeaderSheetField.`Component Name`,""),
                  GenericP2M2.HeaderField.sample -> values.getOrElse(Xcalibur.HeaderField.`Sample Name`,""),
                  GenericP2M2.HeaderField.retTime -> values.getOrElse(Xcalibur.HeaderField.RT,""),
                  GenericP2M2.HeaderField.area -> values.getOrElse(Xcalibur.HeaderField.Area,""),
                  GenericP2M2.HeaderField.height -> values.getOrElse(Xcalibur.HeaderField.Height,""),
                  GenericP2M2.HeaderField.acquisitionDate -> values.getOrElse(Xcalibur.HeaderField.`Acq Date`,""),
                  GenericP2M2.HeaderField.injectedVolume -> values.getOrElse(Xcalibur.HeaderField.`Inj Vol`,"")
                )
              }
            )
          }
        )
    )
}
