package fr.inrae.metabolomics.p2m2.format.conversions

import fr.inrae.metabolomics.p2m2.format.GCMS.HeaderField.HeaderField
import fr.inrae.metabolomics.p2m2.format.Xcalibur.CompoundSheetXcalibur
import fr.inrae.metabolomics.p2m2.format._

import java.text.SimpleDateFormat
import java.time.format.{DateTimeFormatter, DateTimeFormatterBuilder}
import java.time.{LocalDate, LocalDateTime, ZonedDateTime}
import java.util.{Date, Locale}
import scala.language.implicitConversions
import scala.util.{Failure, Success, Try}

object FormatConversions {

  val formatGenericP2M2 : String = "yyyy-MM-dd HH:mm:ss.SSSS"

  /**
   * (String, String, Seq[(String,String)]
   *  - pattern to test exitence
   *  - LocalDate or LocalDateTime parser expression
   *  - rules to transform capture group expression before parsing
   */
  /** MassLynx Txt Example : 13-May-22 */
  val formatMassLynxTxt: (String, String,Seq[(String,String)]) = (
  """(\d{2}-\w+-\d{2})""",
    "dd-MMMM-yy",
    Seq( /* TODO : fill rules with Quantify Compound Summary Report reports */
      "sept" -> "September"
    )
  )

  /** 2/25/2021 */
  val formatGCMS: (String, String,Seq[(String,String)]) = (
    """(\d{2}/\d{2}/\d{4})""",
    "dd/MM/yyyy",
    Seq()
  )

  /** 2/25/2021 3:02:59 */
  val formatOpenLabCDS: (String, String,Seq[(String,String)]) = (
    """(\d+/\d{2}/\d{4}\s+\d+:\d+:\d+ PM)""",
    "M/dd/yyyy h:mm:ss a",
    Seq()
  )

  def formatDateWithLocalDate(dateOpt : Option[String], format : (String,String,Seq[(String,String)])) : Option[String] = dateOpt match {
    case Some(date) =>
      format._1.r.findFirstMatchIn(date) match {
      case Some(v) =>
        val toParse : String = format._3.foldLeft(v.group(1).trim)( (acc : String,rule : (String,String) ) => acc.replace(rule._1,rule._2))
        val v2 =
          LocalDate.parse(toParse,new DateTimeFormatterBuilder()
            .appendPattern(format._2).toFormatter(Locale.US))
            .toString
        val d : Date = new SimpleDateFormat(formatGenericP2M2).parse(v2+" 00:00:00.0000")
        Some(new SimpleDateFormat(formatGenericP2M2).format(d))

      case None => System.err.println(s"Can't not apply conversion with FormatConversions.formatDateWithLocalDate [$date]") ; Some(date)
    }
    case None => None
  }

  def formatDateWithLocalDateTime(dateOpt : Option[String], format : (String,String,Seq[(String,String)])) : Option[String] = dateOpt match {
    case Some(date) =>
      format._1.r.findFirstMatchIn(date) match {
        case Some(v) =>
          val toParse : String = format._3.foldLeft(v.group(1).trim)( (acc : String,rule : (String,String) ) => acc.replace(rule._1,rule._2))
          val v2: LocalDateTime =
            LocalDateTime.parse(toParse, new DateTimeFormatterBuilder()
              .appendPattern(format._2).toFormatter(Locale.US))

          Some(v2.format(DateTimeFormatter.ofPattern(formatGenericP2M2)))

        case None => System.err.println(s"Can't not apply conversion with FormatConversions.formatDateWithLocalDateTime [$date]") ; Some(date)
      }
    case None => None
  }

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
                    GenericP2M2.HeaderField.acquisitionDate ->
                      formatDateWithLocalDate(values.get(QuantifyCompoundSummaryReportMassLynx.HeaderField.`Acq.Date`),formatMassLynxTxt) ,
                    GenericP2M2.HeaderField.injectedVolume -> values.get(QuantifyCompoundSummaryReportMassLynx.HeaderField.`Inj. Vol`)
                  ).flatMap {
                    case (k,Some(v)) => Some(k,v)
                    case _ => None
                  }
                })
          }

    )
  }

  implicit def XcaliburToGenericP2M2(x: Xcalibur) : GenericP2M2 =
    GenericP2M2(
      x.results
        .flatMap(
          (injections : CompoundSheetXcalibur) => {
            injections.compoundByInjection.map(
              ( values : Map[Xcalibur.HeaderField.HeaderField, String]) => {
                Map(
                  GenericP2M2.HeaderField.metabolite -> injections.compoundInformationHeader.get(Xcalibur.HeaderSheetField.`Component Name`),
                  GenericP2M2.HeaderField.sample -> values.get(Xcalibur.HeaderField.Filename),
                  GenericP2M2.HeaderField.retTime -> values.get(Xcalibur.HeaderField.RT),
                  GenericP2M2.HeaderField.area -> values.get(Xcalibur.HeaderField.Area),
                  GenericP2M2.HeaderField.height -> values.get(Xcalibur.HeaderField.Height),
                  GenericP2M2.HeaderField.acquisitionDate -> (values.get(Xcalibur.HeaderField.`Acq Date`) match {
                    case Some(d) =>
                      Try({
                        val dtf = DateTimeFormatter.ofPattern("E MMM d H:m:s z u", Locale.ENGLISH)
                        //Fri Jun 03 20:49:08 CEST 2022
                        //Tue Jun 20 14:53:08 CEST 2017
                        ZonedDateTime.parse(d, dtf) }) match {
                        case Success(v) => Some(v.format(DateTimeFormatter.ofPattern(formatGenericP2M2)))
                        case Failure(_) => System.err.println(s"Can't not apply conversion with FormatConversions.formatDate1 [$d]") ; Some(d)
                  }
                    case _ => None
                  }) ,
                  GenericP2M2.HeaderField.injectedVolume -> values.get(Xcalibur.HeaderField.`Inj Vol`)
                ).flatMap {
                  case (k,Some(v)) => Some(k,v)
                  case _ => None
                }
              }
            )
          }
        )
    )

  implicit def GCMSToGenericP2M2(x: GCMS) :  GenericP2M2 =
    GenericP2M2(
      x.msQuantitativeResults.map(
        (res : Map[HeaderField, String]) => {
          Map(
            GenericP2M2.HeaderField.metabolite -> None,
            GenericP2M2.HeaderField.sample -> res.get(GCMS.HeaderField.Name),
            GenericP2M2.HeaderField.retTime -> res.get(GCMS.HeaderField.`Ret.Time`),
            GenericP2M2.HeaderField.area -> res.get(GCMS.HeaderField.Area),
            GenericP2M2.HeaderField.height -> res.get(GCMS.HeaderField.Height),
            GenericP2M2.HeaderField.acquisitionDate ->
              formatDateWithLocalDate(x.header.get(GCMS.HeaderFileField.Output_Date),formatGCMS),
            GenericP2M2.HeaderField.injectedVolume -> None
          ).flatMap {
            case (k,Some(v)) => Some(k,v)
            case _ => None
          }
        }
      )
    )

  implicit def OpenLabCDSToGenericP2M2(x: OpenLabCDS) :  GenericP2M2 =
    GenericP2M2(
      x.results.map(
        (res : Map[OpenLabCDS.HeaderField.HeaderField, String]) => {
          Map(
            GenericP2M2.HeaderField.metabolite -> res.get(OpenLabCDS.HeaderField.Name),
            GenericP2M2.HeaderField.sample -> x.header.get(OpenLabCDS.HeaderFileField.`Sample Name`),
            GenericP2M2.HeaderField.retTime -> res.get(OpenLabCDS.HeaderField.RetTime),
            GenericP2M2.HeaderField.area -> res.get(OpenLabCDS.HeaderField.Area),
            GenericP2M2.HeaderField.height -> res.get(OpenLabCDS.HeaderField.Amount),
            GenericP2M2.HeaderField.acquisitionDate ->
              formatDateWithLocalDateTime(x.header.get(OpenLabCDS.HeaderFileField.`Last changed Acq. Method`),formatOpenLabCDS),
            GenericP2M2.HeaderField.injectedVolume -> x.header.get(OpenLabCDS.HeaderFileField.`Inj Volume`)
          ).flatMap {
            case (k,Some(v)) => Some(k,v)
            case _ => None
          }
        }
      )
    )
}
