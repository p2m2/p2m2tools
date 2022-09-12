package fr.inrae.metabolomics.p2m2.format

import fr.inrae.metabolomics.p2m2.format.Isocor.CompoundIsocor
import fr.inrae.metabolomics.p2m2.format.Xcalibur.CompoundSheetXcalibur

import java.time.LocalDateTime
import java.time.format.{DateTimeFormatter, DateTimeFormatterBuilder}
import java.util.Locale
import fr.inrae.metabolomics.p2m2.format.conversions.FormatConversions._
import upickle.default._

sealed abstract class MassSpectrometryResultSet {
  val rw : ReadWriter[MassSpectrometryResultSet]  =
    ReadWriter.merge(
      GenericP2M2.rw,
      GCMS.rw,
      OpenLabCDS.rw,
      QuantifySummaryReportMassLynx.rw,
      Xcalibur.rw,
      Isocor.rw
    )
  def toGenericP2M2 : GenericP2M2
}

object GenericP2M2 {
 implicit val rw: ReadWriter[GenericP2M2] = macroRW
   object HeaderField extends Enumeration {
    implicit val rw: ReadWriter[HeaderField] = readwriter[Int].bimap[HeaderField](x => x.id, HeaderField(_))
    type HeaderField = Value
    val
    ID,                      /* build during conversion */
    sample,
    metabolite,
    retTime,
    area,
    height,
    injectedVolume,
    vial,
    acquisitionDate,
    exportDate,
    chromatographInjectionId /* build during conversion */
    = Value
  }

  object HeaderFieldChromatogram extends Enumeration {
    implicit val rw: ReadWriter[HeaderFieldChromatogram] =
      readwriter[Int].bimap[HeaderFieldChromatogram](x => x.id, HeaderFieldChromatogram(_))
    type HeaderFieldChromatogram = Value
    val
    chromatographInjectionId,
    vial,
    exportDate,
    acquisitionDate,
    injectedVolume = Value
  }
}

case class GenericP2M2(
                        samples : Seq[Map[GenericP2M2.HeaderField.HeaderField,String]]=Seq()
                      ) extends MassSpectrometryResultSet {
  def +(that: GenericP2M2): GenericP2M2 = GenericP2M2(this.samples++that.samples)
  def +(that: MassSpectrometryResultSet): GenericP2M2 = this+that.toGenericP2M2

  override def toGenericP2M2: GenericP2M2 = GenericP2M2(this.samples)
}

object GCMS {
  implicit val rw: ReadWriter[GCMS] = macroRW
  object HeaderFileField extends Enumeration {
    implicit val rw: ReadWriter[HeaderFileField] = readwriter[Int].bimap[HeaderFileField](x => x.id, HeaderFileField(_))
    type HeaderFileField = Value
    val Data_File_Name, Output_Date, Output_Time = Value
  }

  object HeaderField extends Enumeration {
    implicit val rw: ReadWriter[HeaderField] = readwriter[Int].bimap[HeaderField](x => x.id, HeaderField(_))
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
                  msQuantitativeResults : Seq[Map[GCMS.HeaderField.HeaderField, String]] = Seq()
                ) extends MassSpectrometryResultSet {

  override def toGenericP2M2: GenericP2M2 = this
}

object OpenLabCDS {
  implicit val rw: ReadWriter[OpenLabCDS] = macroRW
  object HeaderFileField extends Enumeration {
    implicit val rw: ReadWriter[HeaderFileField] = readwriter[Int].bimap[HeaderFileField](x => x.id, HeaderFileField(_))
    type HeaderFileField = Value
    val `Sample Name`, `Acq. Operator`,`Sample Operator`,`Seq. Line`,`Acq. Instrument`,Location,
    `Injection Date`,Inj, `Inj Volume`, `Acq. Method`,`Last changed Acq. Method`,
    `Analysis Method`, `Last changed Analysis Method`, `Additional Info`: OpenLabCDS.HeaderFileField.Value = Value
  }

  object HeaderField extends Enumeration {
    implicit val rw: ReadWriter[HeaderField] = readwriter[Int].bimap[HeaderField](x => x.id, HeaderField(_))
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

object QuantifySummaryReportMassLynx {
  val rw: ReadWriter[MassSpectrometryResultSet] =
    ReadWriter.merge(
      QuantifyCompoundSummaryReportMassLynx.rw,
      QuantifySampleSummaryReportMassLynx.rw,
    )
  case class  Header(dateStr : Option[String] = None)  {
    val formatter: DateTimeFormatter = new DateTimeFormatterBuilder()
      .appendPattern("E MMM dd HH:mm:ss yyyy")
      .toFormatter(Locale.US)

    val printedDate: LocalDateTime = dateStr match {
      case Some(d) => LocalDateTime.parse(d.trim, formatter)
      case None => LocalDateTime.now()
    }
  }

  object Header {
    implicit val rw: ReadWriter[Header] = macroRW
  }

  object HeaderField extends Enumeration {
    implicit val rw: ReadWriter[HeaderField] = readwriter[Int].bimap[HeaderField](x => x.id, HeaderField(_))
    type HeaderField = Value
    val `Num. Injection`, /* Build artificial (Column namle does not exist in the Quantify Compound Summary Report) */
    Name, `Inj. Vol`, `Acq.Date`, `Acq.Time`, Type, `Conc.`, `Mod.Time`, `Std. Conc`,
    Trace, RT, Area, uM, `%Dev`, `S/N`, `Vial`, `Height/Area`, Height = Value
  }
}

abstract class QuantifySummaryReportMassLynx() extends MassSpectrometryResultSet {
  def toQuantifyCompoundSummaryReportMassLynx : QuantifyCompoundSummaryReportMassLynx = this
  def toQuantifySampleSummaryReportMassLynx : QuantifySampleSummaryReportMassLynx = this
}

/**
 */
object QuantifyCompoundSummaryReportMassLynx {
  implicit val rw: ReadWriter[QuantifyCompoundSummaryReportMassLynx] = macroRW
}
case class QuantifyCompoundSummaryReportMassLynx(
                                                  origin : String,
                                                  header : QuantifySummaryReportMassLynx.Header,
                                                  // list of Name Compound/ Area/etc....
                                                  resultsByCompound : Seq[(String,Seq[Map[QuantifySummaryReportMassLynx.HeaderField.HeaderField,String]])] = List()
                                                ) extends QuantifySummaryReportMassLynx {
  override def toGenericP2M2: GenericP2M2 = this

}

object QuantifySampleSummaryReportMassLynx {
  implicit val rw: ReadWriter[QuantifySampleSummaryReportMassLynx] = macroRW
}
case class QuantifySampleSummaryReportMassLynx(
                                                  origin : String,
                                                  header : QuantifySummaryReportMassLynx.Header,
                                                  // list of Name Compound/ Area/etc....
                                                  resultsBySample : Seq[(String,Seq[Map[QuantifySummaryReportMassLynx.HeaderField.HeaderField,String]])] = List()
                                                ) extends QuantifySummaryReportMassLynx {
  override def toGenericP2M2: GenericP2M2 = this

}

object Xcalibur {
  implicit val rw: ReadWriter[Xcalibur] = macroRW
  object HeaderSheetField extends Enumeration {
    implicit val rw: ReadWriter[HeaderSheetField] = readwriter[Int].bimap[HeaderSheetField](x => x.id, HeaderSheetField(_))
    type HeaderSheetField = Value
    val `Component Name`, `Curve Index`, `Weighting Index`, `Origin Index`, Equation, `User Name`, `Full Name`, Date = Value
  }

  object HeaderField extends Enumeration {
    implicit val rw: ReadWriter[HeaderField] = readwriter[Int].bimap[HeaderField](x => x.id, HeaderField(_))
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
  object CompoundSheetXcalibur {
    implicit val rw: ReadWriter[CompoundSheetXcalibur] = macroRW
  }
}

case class Xcalibur(origin : String, results : Seq[CompoundSheetXcalibur]=Seq()) extends MassSpectrometryResultSet {
  override def toGenericP2M2: GenericP2M2 = this
}


object Isocor {
  implicit val rw: ReadWriter[Isocor] = macroRW
  object HeaderField extends Enumeration {
    implicit val rw: ReadWriter[HeaderField] = readwriter[Int].bimap[HeaderField](x => x.id, HeaderField(_))
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

  object CompoundIsocor{
    implicit val rw: ReadWriter[CompoundIsocor] = macroRW
  }
}

case class Isocor(origin : String, results : Seq[CompoundIsocor] = List()) extends MassSpectrometryResultSet {
  override def toGenericP2M2: GenericP2M2 = GenericP2M2(Seq()) /* None conversion available */
}
