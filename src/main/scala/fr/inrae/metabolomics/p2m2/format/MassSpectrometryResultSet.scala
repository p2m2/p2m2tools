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
  val rw: ReadWriter[MassSpectrometryResultSet] = {
    ReadWriter.merge(
      QuantifyCompoundSummaryReportMassLynx.rw,
      QuantifySampleSummaryReportMassLynx.rw,
    )
  }

  object Header {
    implicit val rw: ReadWriter[Header] = macroRW
  }

  case class  Header(dateStr : Option[String] = None)  {
    val formatter: DateTimeFormatter = new DateTimeFormatterBuilder()
      .appendPattern("E MMM dd HH:mm:ss yyyy")
      .toFormatter(Locale.US)

    val printedDate: LocalDateTime = dateStr match {
      case Some(d) => LocalDateTime.parse(d.trim, formatter)
      case None => LocalDateTime.now()
    }
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
  object HeaderField extends Enumeration {
    implicit val rw: ReadWriter[HeaderField] = readwriter[Int].bimap[HeaderField](x => x.id, HeaderField(_))
    type HeaderField = Value
    val `Num. Injection`, /* Build artificial (Column name does not exist in the Quantify Compound Summary Report) */
    `Name`, `Inj. Vol`, `Acq.Date`, `Acq.Time`, `RT`, `Area`, `Mod.Date`, `Mod.Time`, `1º Det. Flags`, `1º Max.Cnc Flag`,
    `1º Rpt Lim Flag`, `1º S/N Flag`, `1º Symmetry Direction`, `1º Symmetry %`, `1º Area`, `1º Chr.Noise`, `1º Ratio Flag`,
    `1º Ht`, `1º Ratio (Actual)`, `1º Ratio (Pred)`, `1º S/N`, `1º Trace`, `2º Det. Flags`, `2º Max.Cnc Flag`,
    `2º Rpt Lim Flag`, `2º S/N Flag`, `2º Symmetry Direction`, `2º Symmetry %`, `2º Area`, `2º Chr.Noise`, `2º Ratio Flag`,
    `2º Ht`, `2º Ratio (Actual)`, `2º Ratio (Pred)`, `2º Sig/Noise`, `2º Trace`, `3º Det. Flags`, `3º Max.Cnc Flag`,
    `3º Rpt Lim Flag`, `3º S/N Flag`, `3º Symmetry Direction`, `3º Symmetry %`, `3º Area`, `3º Chr.Noise`, `3º Ratio Flag`,
    `3º Height`, `3º Ratio (Actual)`, `3º Ratio (Pred)`, `3º Sig/Noise`, `3º Trace`, `4º Det. Flags`, `4º Max.Cnc Flag`,
    `4º Rpt Lim Flag`, `4º S/N Flag`, `4º Symmetry Direction`, `4º Symmetry %`, `4º Area`, `4º Chr.Noise`,
    `4º Ratio Flag`, `4º Height`, `4º Ratio (Actual)`, `4º Ratio (Pred)`, `4º Sig/Noise`, `4º Trace`, `Abs. Corr. Resp`,
    `Abs.Resp`, `Adj.RT`, `Analyte Deviation`, `Base Width`, `Below Rpt Lim`, `Blank Flag`, `Blank Limit`, `BS Conc.`,
    `RRF %Rel SD`, `RRF Mean`, `RRF SD`, `Conc.`, `Cal.Date`, `Cal.File`, `Cal.Time`, `Noise`, `Noise Height`, `Trace`,
    `Coeff. Of Determination`, `CD Flag`, `Conc. Dev. Allowed`, `Conc. Dev. Flagged`, `Dev. Flagging Required`, `%Dev`,
    `Corr. Resp`, `EMPC`, `Formula 1`, `Formula 2`, `Formula 3`, `Formula 4`, `Forward Fit`, `RRT`, `Scan`, `a`,
    `Height/Area`, `i-FIT`, `i-FIT % Conf`, `i-FIT % Conf Flag`, `i-FIT Flag`, `i-FIT Norm`, `i-FIT Norm Flag`,
    `IS Abs.Resp`, `IS Area`, `IS#`, `IS RT`, `IS Height`, `LOD`, `LOD Flag`, `LOQ`, `LOQ Flag`, `Lowerbound 1`,
    `Lowerbound 2`, `Lowerbound 3`, `Lowerbound 4`, `Found Mass`, `Error (mDa)`, `Error (ppm)`, `Error (mDa) Flag`,
    `Error (ppm) Flag`, `Max. Cnc Flag`, `Mediumbound 1`, `Mediumbound 2`, `Mediumbound 3`, `Mediumbound 4`,
    `Mod.Comment`, `Mod.User`, `No Solution Flag`, `(b/a)`, `Asymmetry Flagged`, `Peak End Height`, `Peak End Time`,
    `Height`, `Peak Missing Flag`, `Quality`, `Quality Description`, `Response`, `Peak Start Height`, `Peak Start Time`,
    `Pk Kurt`, `Pk Sigma`, `Pk Skew`, `Pk Width`, `Pred.RRT`, `Pred.RT`, `Primary Flags`, `Quality Reference`,
    `Quan Actual Ratio`, `Sig/Noise Flag`, `Quan Trace`, `Quan Trace Ratio`, `Quantify Reference`,
    `R.T. Tolerance Flag`, `%Rec`, `Recovery Flag`, `Reinjection Number`, `Rpt Lim Flag`, `Reverse Fit`, `RRF`,
    `S/N LOD`, `S/N LOQ`, `Conditions`, `#`, `ID`, `Submitter`, `Task`, `Sample Text`, `Type`, `Sec.Peaks`, `S/N`,
    `Solvent Flag`, `Solvent Limit`, `Std. Conc`, `Subject Text`, `Subject Time`, `Symmetry Direction`, `Symmetry %`,
    `b`, `TEQ 1`, `TEQ 2`, `TEQ 3`, `TEQ 4`, `TEF 1`, `TEF 2`, `TEF 3`, `TEF 4`, `TLOD 1`, `TLOD 2`, `TLOD 3`, `TLOD 4`,
    `TLOQ 1`, `TLOQ 2`, `TLOQ 3`, `TLOQ 4`, `Upperbound 1`, `Upperbound 2`, `Upperbound 3`, `Upperbound 4`, `Divisor1`,
    `Factor1`, `Factor2`, `Factor3`, `Pk.Factor`, `User RF`, `Vial` = Value
  }
}
case class QuantifyCompoundSummaryReportMassLynx(
                                                  origin : String,
                                                  header : QuantifySummaryReportMassLynx.Header,
                                                  // list of Name Compound/ Area/etc....
                                                  resultsByCompound : Seq[(String,
                                                    Seq[Map[QuantifyCompoundSummaryReportMassLynx.HeaderField.HeaderField,String]])] = List()
                                                ) extends QuantifySummaryReportMassLynx {
  override def toGenericP2M2: GenericP2M2 = this

}

object QuantifySampleSummaryReportMassLynx {
  implicit val rw: ReadWriter[QuantifySampleSummaryReportMassLynx] = macroRW

  object HeaderField extends Enumeration {
    implicit val rw: ReadWriter[HeaderField] = readwriter[Int].bimap[HeaderField](x => x.id, HeaderField(_))
    type HeaderField = Value
    val `Num. Injection`, /* Build artificial (Column name does not exist in the Quantify Compound Summary Report) */
    `Name`, `Trace`, `RT`, `Area`, `IS Area`, `Response`, `Conc.`, `%Dev`, `Primary Flags`, `1º Det. Flags`,
    `1º Max.Cnc Flag`, `1º Rpt Lim Flag`, `1º S/N Flag`, `1º Symmetry Direction`, `1º Symmetry %`, `1º Area`,
    `1º Chr.Noise`, `1º Ratio Flag`, `1º Ht`, `1º Ratio (Actual)`, `1º Ratio (Pred)`, `1º S/N`, `1º Trace`,
    `2º Det. Flags`, `2º Max.Cnc Flag`, `2º Rpt Lim Flag`, `2º S/N Flag`, `2º Symmetry Direction`, `2º Symmetry %`,
    `2º Area`, `2º Chr.Noise`, `2º Ratio Flag`, `2º Ht`, `2º Ratio (Actual)`, `2º Ratio (Pred)`, `2º Sig/Noise`,
    `2º Trace`, `3º Det. Flags`, `3º Max.Cnc Flag`, `3º Rpt Lim Flag`, `3º S/N Flag`, `3º Symmetry Direction`,
    `3º Symmetry %`, `3º Area`, `3º Chr.Noise`, `3º Ratio Flag`, `3º Height`, `3º Ratio (Actual)`, `3º Ratio (Pred)`,
    `3º Sig/Noise`, `3º Trace`, `4º Det. Flags`, `4º Max.Cnc Flag`, `4º Rpt Lim Flag`, `4º S/N Flag`,
    `4º Symmetry Direction`, `4º Symmetry %`, `4º Area`, `4º Chr.Noise`, `4º Ratio Flag`, `4º Height`,
    `4º Ratio (Actual)`, `4º Ratio (Pred)`, `4º Sig/Noise`, `4º Trace`, `Abs. Corr. Resp`, `Abs.Resp`,
    `Acq.Date`, `Acq.Time`, `Adj.RT`, `Analyte Deviation`, `Base Width`, `Below Rpt Lim`, `Blank Flag`,
    `Blank Limit`, `BS Conc.`, `RRF %Rel SD`, `RRF Mean`, `RRF SD`, `Cal.Date`, `Cal.File`, `Cal.Time`, `CAS`, `Noise`,
    `Noise Height`, `Coeff. Of Determination`, `CD Flag`, `Compound Type`, `Conc. Dev. Allowed`, `Conc. Dev. Flagged`,
    `Dev. Flagging Required`, `Corr. Resp`, `EMPC`, `Formula 1`, `Formula 2`, `Formula 3`, `Formula 4`, `Forward Fit`,
    `RRT`, `Scan`, `a`, `Height/Area`, `i-FIT`, `i-FIT % Conf`, `i-FIT % Conf Flag`, `i-FIT Flag`, `i-FIT Norm`,
    `i-FIT Norm Flag`, `IS Abs.Resp`, `IS#`, `IS RT`, `IS Height`, `LOD`, `LOD Flag`, `LOQ`, `LOQ Flag`, `Lowerbound 1`,
    `Lowerbound 2`, `Lowerbound 3`, `Lowerbound 4`, `Found Mass`, `Error (mDa)`, `Error (ppm)`, `Error (mDa) Flag`,
    `Error (ppm) Flag`, `Max. Cnc Flag`, `Mediumbound 1`, `Mediumbound 2`, `Mediumbound 3`, `Mediumbound 4`,
    `Mod.Comment`, `Mod.Date`, `Mod.Time`, `Mod.User`, `No Solution Flag`, `(b/a)`, `Asymmetry Flagged`,
    `Peak End Height`, `Peak End Time`, `Height`, `Peak Missing Flag`, `Quality`, `Quality Description`,
    `Peak Start Height`, `Peak Start Time`, `Peaks`, `Pk Kurt`, `Pk Sigma`, `Pk Skew`, `Pk Width`, `Pred.RRT`,
    `Pred.RT`, `Quality Reference`, `Quan Actual Ratio`, `Sig/Noise Flag`, `Quan Trace`, `Quan Trace Ratio`,
    `Quantify Reference`, `R.T. Tolerance Flag`, `%Rec`, `Recovery Flag`, `Reinjection Number`, `Rpt Lim Flag`,
    `Reverse Fit`, `RRF`, `S/N LOD`, `S/N LOQ`, `ID`, `Sample Text`, `Sec.Peaks`, `S/N`, `Solvent Flag`,
    `Solvent Limit`, `Std. Conc`, `Symmetry Direction`, `Symmetry %`, `b`, `TEQ 1`, `TEQ 2`, `TEQ 3`, `TEQ 4`,
    `TEF 1`, `TEF 2`, `TEF 3`, `TEF 4`, `TLOD 1`, `TLOD 2`, `TLOD 3`, `TLOD 4`, `TLOQ 1`, `TLOQ 2`, `TLOQ 3`, `TLOQ 4`,
    `Upperbound 1`, `Upperbound 2`, `Upperbound 3`, `Upperbound 4`, `Divisor1`, `Factor1`, `Factor2`, `Factor3`,
    `Pk.Factor`, `User RF` = Value
  }

}
case class QuantifySampleSummaryReportMassLynx(
                                                  origin : String,
                                                  header : QuantifySummaryReportMassLynx.Header,
                                                  // list of Name Compound/ Area/etc....
                                                  resultsBySample : Seq[(String,
                                                    Seq[Map[QuantifySampleSummaryReportMassLynx.HeaderField.HeaderField,String]])] = List()
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
