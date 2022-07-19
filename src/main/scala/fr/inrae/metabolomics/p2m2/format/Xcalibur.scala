package fr.inrae.metabolomics.p2m2.format

import Xcalibur.CompoundSheetXcalibur
import Xcalibur.HeaderField.HeaderField
import Xcalibur.HeaderSheetField.HeaderSheetField

object Xcalibur {

  object HeaderSheetField extends Enumeration {
    type HeaderSheetField = Value
    val `Component Name`, `Curve Index`, `Weighting Index`, `Origin Index`, Equation, `User Name`, `Full Name`, Date = Value
  }

  object HeaderField extends Enumeration {
    type HeaderField = Value
    val Filename, `Sample Type`,`Sample Name`, `Sample ID`, `Exp Amt`, `Calc Amt Units`, `%Diff`, Level,
    `%RSD-AMT`,`Peak Status`, Response,	`Response Type`, Equation, Area,	Height,`ISTD Area`, `ISTD Ht`,
    RT,	`Rel RT`,	`Del RT`,	`S/N`,`Start Time`,	`End Time`,`Start Height`, `End Height`,`Integ Code`, `Search Window`, Threshold,
    Smooth, `Acq Date`, Duration, `Exp Method`, `Proc Method`, Vial,	`Inj Vol`,	`Sample Wt`,
    `Sample Vol`, `ISTD Base Amt`, `ISTD Calc Amt`, `Dilution Factor`, Study, Client, Laboratory, Company, Phone, Comment = Value
  }

  case class CompoundSheetXcalibur(
                                    compoundInformationHeader: Map[HeaderSheetField, String] = Map[HeaderSheetField, String](),
                                    compoundByInjection: Seq[Map[HeaderField, String]] = Seq())
}

case class Xcalibur(origin : String, results : Seq[CompoundSheetXcalibur]) extends MassSpectrometryResultSet
