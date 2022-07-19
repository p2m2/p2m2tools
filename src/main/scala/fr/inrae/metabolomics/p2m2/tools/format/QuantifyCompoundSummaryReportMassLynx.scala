package fr.inrae.metabolomics.p2m2.tools.format

import fr.inrae.metabolomics.p2m2.tools.format.QuantifyCompoundSummaryReportMassLynx.Header
import fr.inrae.metabolomics.p2m2.tools.format.QuantifyCompoundSummaryReportMassLynx.HeaderField.HeaderField

import java.time.LocalDate
import java.time.format.{DateTimeFormatter, DateTimeFormatterBuilder}
import java.util.Locale

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

  def getHeaderField(token : String) : Option[HeaderField] =
    HeaderField
      .values
      .find( _
          .toString
          .replace("$percent","%")
          .replace("$u002E",".")
          .replace("$u0020"," ")
          .replace("$div","/")
          .replace("$hash","#")
          .equalsIgnoreCase(token))
}

/**
 */

case class QuantifyCompoundSummaryReportMassLynx(
                      origin : String,
                      header : Header,
                      // list of Name Compound/ Area/etc....
                      results : Seq[(String,Seq[Map[HeaderField,String]])] = List()
               ) extends MassSpectrometryResultSet



