package fr.inrae.metabolomics.p2m2.tools.format.output

import fr.inrae.metabolomics.p2m2.tools.format.output.OutputXcalibur.HeaderField.HeaderField
import fr.inrae.metabolomics.p2m2.tools.format.output.OutputXcalibur.HeaderSheetField.HeaderSheetField

case class CompoundSheetXcalibur(
                                  compoundInformationHeader : Map[HeaderSheetField,String] = Map[HeaderSheetField,String](),
                                  compoundByInjection : Seq[Map[HeaderField, String]] = Seq())