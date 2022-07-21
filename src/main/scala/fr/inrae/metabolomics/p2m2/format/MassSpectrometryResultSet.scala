package fr.inrae.metabolomics.p2m2.format

import fr.inrae.metabolomics.p2m2.format.Isocor.CompoundIsocor
import fr.inrae.metabolomics.p2m2.format.Xcalibur.CompoundSheetXcalibur

import java.time.LocalDate
import java.time.format.{DateTimeFormatter, DateTimeFormatterBuilder}
import java.util.Locale
import fr.inrae.metabolomics.p2m2.format.conversions.FormatConversions._


sealed abstract class MassSpectrometryResultSet {
  def toGenericP2M2 : GenericP2M2
}

object GenericP2M2 {
  object HeaderField extends Enumeration {
    type HeaderField = Value
    val
    sample,
    metabolite,
    retTime,
    area,
    height,
    injectedVolume,
    acquisitionDate = Value
  }
}

case class GenericP2M2(values : Seq[Map[GenericP2M2.HeaderField.HeaderField,String]]=Seq()) extends MassSpectrometryResultSet {
  def +(that: GenericP2M2): GenericP2M2 = GenericP2M2(this.values++that.values)
  def +(that: MassSpectrometryResultSet): GenericP2M2 = this+that.toGenericP2M2

  override def toGenericP2M2: GenericP2M2 = GenericP2M2(this.values)
}


object GCMS {
  object HeaderFileField extends Enumeration {
    type HeaderFileField = Value
    val Data_File_Name, Output_Date, Output_Time = Value
  }

  object HeaderField extends Enumeration {
    type HeaderField = Value
    val `ID#`,Name, `Type`, `ISTD Group#`, Mass, `Ret.Time`, `Start Time`, `End Time`,
    `A/H`, Area, Height, `Conc.`, Mode, `Peak#`, `Std.Ret.Time`, `Calibration Curve`, `3rd`, `2nd`, `1st`,
    `Constant`, `Ref.Ion Area`, `Ref.Ion Height`, `Ref.Ion Set Ratio`, `Ref.Ion Ratio`, Recovery, SI,
    `Ref.Ion1 m/z`, `Ref.Ion1 Area`, `Ref.Ion1 Height`, `Ref.Ion1 Set Ratio`, `Ref.Ion1 Ratio`,
    `Ref.Ion2 m/z`, `Ref.Ion2 Area`, `Ref.Ion2 Height`, `Ref.Ion2 Set Ratio`, `Ref.Ion2 Ratio`, `Ref.Ion3 m/z`,
    `Ref.Ion3 Area`, `Ref.Ion3 Height`, `Ref.Ion3 Set Ratio`, `Ref.Ion3 Ratio`, `Ref.Ion4 m/z`, `Ref.Ion4 Area`,
    `Ref.Ion4 Height`, `Ref.Ion4 Set Ratio`, `Ref.Ion4 Ratio`, `Ref.Ion5 m/z`, `Ref.Ion5 Area`, `Ref.Ion5 Height`,
    `Ref.Ion5 Set Ratio`, `Ref.Ion5 Ratio`,	`Ret. Index`,	`S/N`, `Unit`, Description, Threshold = Value
  }
}

/**
 * Categories management :
 * [Header] map key, value
 * [MS_Quantitative_Results] Array of results
 */

case class GCMS (
                  origin : String,
                  header : Map[GCMS.HeaderFileField.HeaderFileField,String] =
                  Map[GCMS.HeaderFileField.HeaderFileField,String](),
                  ms_quantitative_results : Seq[Map[GCMS.HeaderField.HeaderField, String]] = Seq()
                ) extends MassSpectrometryResultSet {

  override def toGenericP2M2: GenericP2M2 = this
}

object OpenLabCDS {

  object HeaderFileField extends Enumeration {
    type HeaderFileField = Value
    val `Sample Name`, `Acq. Operator`,`Sample Operator`,`Seq. Line`,`Acq. Instrument`,Location,
    `Injection Date`,Inj, `Inj Volume`, `Acq. Method`,`Last changed Acq. Method`,
    `Analysis Method`, `Last changed Analysis Method`, `Additional Info`: OpenLabCDS.HeaderFileField.Value = Value
  }

  object HeaderField extends Enumeration {
    type HeaderField = Value
    val  RetTime, Type, ISTD, Area, used, `Amt/Area`, Amount, Grp, Name = Value
  }
}
/**
 */

case class OpenLabCDS(
                       origin : String,
                       header : Map[OpenLabCDS.HeaderFileField.HeaderFileField,String] =
                       Map[OpenLabCDS.HeaderFileField.HeaderFileField,String](),
                       // list of Name Compound/ Area/etc....
                       results : Seq[Map[OpenLabCDS.HeaderField.HeaderField, String]] = List()
                     ) extends MassSpectrometryResultSet {
  override def toGenericP2M2: GenericP2M2 = this
}



object QuantifyCompoundSummaryReportMassLynx {

  case class  Header(dateStr : Option[String] = None)  {
    val formatter: DateTimeFormatter = new DateTimeFormatterBuilder()
      .appendPattern("E MMM dd HH:mm:ss yyyy")
      .toFormatter(Locale.US)

    val PrintedDate: LocalDate = dateStr match {
      case Some(d) => LocalDate.parse(d.trim, formatter)
      case None => LocalDate.now()
    }

  }
  object HeaderField extends Enumeration {
    type HeaderField = Value
    val `Num. Injection`, /* Build artificial (Column namle does not exist in the Quantify Compound Summary Report) */
    Name, `Inj. Vol`, `Acq.Date`, `Acq.Time`, Type, `Conc.`, `Mod.Time`, `Std. Conc`,
    Trace, RT, Area, uM, `%Dev`, `S/N`, `Vial`, `Height/Area`, Height = Value
  }
}

/**
 */

case class QuantifyCompoundSummaryReportMassLynx(
                                                  origin : String,
                                                  header : QuantifyCompoundSummaryReportMassLynx.Header,
                                                  // list of Name Compound/ Area/etc....
                                                  results : Seq[(String,Seq[Map[QuantifyCompoundSummaryReportMassLynx.HeaderField.HeaderField,String]])] = List()
                                                ) extends MassSpectrometryResultSet {
  override def toGenericP2M2: GenericP2M2 = this
}

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
                                    compoundInformationHeader: Map[Xcalibur.HeaderSheetField.HeaderSheetField, String]
                                    = Map[Xcalibur.HeaderSheetField.HeaderSheetField, String](),
                                    compoundByInjection: Seq[Map[Xcalibur.HeaderField.HeaderField, String]] = Seq())
}

case class Xcalibur(origin : String, results : Seq[CompoundSheetXcalibur]) extends MassSpectrometryResultSet {
  override def toGenericP2M2: GenericP2M2 = this
}


object Isocor {
  object HeaderField extends Enumeration {
    type HeaderField = Value
    val sample, metabolite, derivative, isotopologue, area, resolution = Value
  }

  case class CompoundIsocor(values : Map[Isocor.HeaderField.HeaderField, String]) {
    def string(sep: String = "\t"): String =
      values.getOrElse(HeaderField.sample,"") + sep +
        values.getOrElse(HeaderField.metabolite,"") + sep +
        values.getOrElse(HeaderField.derivative,"") + sep +
        values.getOrElse(HeaderField.isotopologue,"") + sep +
        values.getOrElse(HeaderField.area,"") + sep +
        values.getOrElse(HeaderField.resolution,"")
  }
}

case class Isocor(origin : String, results : Seq[CompoundIsocor] = List()) extends MassSpectrometryResultSet {
  override def toGenericP2M2: GenericP2M2 = GenericP2M2(Seq()) /* None conversion available */
}