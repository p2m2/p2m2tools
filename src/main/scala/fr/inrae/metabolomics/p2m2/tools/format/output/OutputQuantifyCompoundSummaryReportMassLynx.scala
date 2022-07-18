package fr.inrae.metabolomics.p2m2.tools.format.output


import fr.inrae.metabolomics.p2m2.tools.format.output.OutputQuantifyCompoundSummaryReportMassLynx.Header
import fr.inrae.metabolomics.p2m2.tools.format.output.OutputQuantifyCompoundSummaryReportMassLynx.HeaderField.HeaderField

import java.time.LocalDate
import java.time.format.{DateTimeFormatter, DateTimeFormatterBuilder}
import java.util.Locale

object OutputQuantifyCompoundSummaryReportMassLynx {

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
          .equalsIgnoreCase(token))

  //Name	Trace	Type	Std. Conc	RT	Area	uM	%Dev	S/N	Vial	Height/Area	Acq.Date	Height
 /*
  case class SampleField(
                            Name          : String,
                            Trace         : Int,
                            Type          : String,
                            `Std. Conc`   : String,
                            RT	          : Double,
                            Area          : Int,
                            uM            : String,
                            `%Dev`        : String,
                            `S/N`         : Int,
                            Vial          : String,
                            `Height/Area` : Double,
                            `Acq.Date`    : String,
                            Height        : Int )

  def buildSampleField(map : Map[String,String] ) : SampleField = {
    SampleField(
      map.getOrElse("Name",""),
      map.getOrElse("Trace","-1").toInt,
      map.getOrElse("Type",""),
      map.getOrElse("Std. Conc",""),
      map.getOrElse("RT","0.0").toDouble,
      map.getOrElse("Area","-1").toInt,
      map.getOrElse("uM",""),
      map.getOrElse("%Dev",""),
      map.getOrElse("S/N","-1").toInt,
      map.getOrElse("Vial",""),
      map.getOrElse("Height/Area","0.0").toDouble,
      map.getOrElse("Acq.Date",""),
      map.getOrElse("Height","-1").toInt,
    )
  }*/
}


/**
 */

case class OutputQuantifyCompoundSummaryReportMassLynx(
                      origin : String,
                      header : Header,
                      // list of Name Compound/ Area/etc....
                      results : Seq[(String,Seq[Map[HeaderField,String]])] = List()
               )


