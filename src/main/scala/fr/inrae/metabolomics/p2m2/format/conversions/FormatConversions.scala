package fr.inrae.metabolomics.p2m2.format.conversions

import fr.inrae.metabolomics.p2m2.format.ms.GCMS.HeaderField.HeaderField
import fr.inrae.metabolomics.p2m2.format.ms.Xcalibur.CompoundSheetXcalibur
import fr.inrae.metabolomics.p2m2.format._
import fr.inrae.metabolomics.p2m2.format.ms.{GCMS, GenericP2M2, OpenLabCDS, QuantifyCompoundSummaryReportMassLynx, QuantifySampleSummaryReportMassLynx, QuantifySummaryReportMassLynx, Xcalibur}

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
   *  - pattern to test existence
   *  - LocalDate or LocalDateTime parser expression
   *  - rules to transform capture group expression before parsing
   */
  /** MassLynx Txt Example : 13-May-22 */
  val formatMassLynxTxt: (String, String,Seq[(String,String)]) = (
  """(\d{2}-\w+-\d{2})""",
    "dd-MMMM-yy",
    Seq( /* TODO : fill rules with Quantify Compound Summary Report reports */
      "-sept-" -> "-September-",
      "-Sept-" -> "-September-",
      "-oct-" -> "-October-",
      "-Oct-" -> "-October-",
      "-nov-" -> "-November-",
      "-Nov-" -> "-November-",
      "-dec-" -> "-December-",
      "-Dec-" -> "-December-",
      "-jan-" -> "-January-",
      "-Jan-" -> "-January-",
      "-feb-" -> "-February-",
      "-Feb-" -> "-February-",
      "-mar-" -> "-March-",
      "-Mar-" -> "-March-",
      "-apr-" -> "-April-",
      "-Apr-" -> "-April-",
      "-may-" -> "-May-",
      "-jun-" -> "-June-",
      "-Jun-" -> "-June-",
      "-jul-" -> "-July-",
      "-Jul-" -> "-July-",
      "-aug-" -> "-August-",
      "-Aug-" -> "-August-",
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

  /** 07/06/2022 08:57:12 */
  val formatDateXcalibur: (String, String, Seq[(String, String)]) = (
    """(\d{2}/\d{2}/\d{4}\s+\d+:\d+:\d+)""",
    "dd/MM/yyyy H:mm:ss",
    Seq()
  )

  val formatDateXcalibur2: (String, String, Seq[(String, String)]) = (
    """(\d{2}/\d{2}/\d{2}\s+\d+:\d+:\d+)""",
    "MM/dd/yy H:mm:ss",
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

        case None => None //System.err.println(s"Can't not apply conversion with FormatConversions.formatDateWithLocalDateTime [$date]") ; None
      }
    case None => None
  }

  def formatAsDouble(s: Option[String]): Option[String] = s match {
    case Some(s2) =>  """([0-9.]+)""".r.findFirstMatchIn (s2) match {
                    case Some (v) => Some(v.group (0).toDouble.toString)
                    case None => None
                  }
    case None => None
  }

  implicit def QuantifyCompoundSummaryReportMassLynxToGenericP2M2(x: QuantifyCompoundSummaryReportMassLynx) : GenericP2M2 = {

    GenericP2M2(
      samples = x.resultsByCompound
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
                    GenericP2M2.HeaderField.retTime -> formatAsDouble(values.get(QuantifyCompoundSummaryReportMassLynx.HeaderField.RT)),
                    GenericP2M2.HeaderField.area -> formatAsDouble(values.get(QuantifyCompoundSummaryReportMassLynx.HeaderField.Area)),
                    GenericP2M2.HeaderField.height -> formatAsDouble(values.get(QuantifyCompoundSummaryReportMassLynx.HeaderField.Height)),
                    GenericP2M2.HeaderField.vial -> values.get(QuantifyCompoundSummaryReportMassLynx.HeaderField.`Vial`),
                      GenericP2M2.HeaderField.acquisitionDate ->
                      formatDateWithLocalDate(values.get(QuantifyCompoundSummaryReportMassLynx.HeaderField.`Acq.Date`),formatMassLynxTxt) ,
                    GenericP2M2.HeaderField.exportDate ->  Some(x.header.printedDate.format(DateTimeFormatter.ofPattern(formatGenericP2M2))),
                    GenericP2M2.HeaderField.injectedVolume -> values.get(QuantifyCompoundSummaryReportMassLynx.HeaderField.`Inj. Vol`)
                  ).flatMap {
                    case (k,Some(v)) => Some(k,v)
                    case _ => None
                  }
                })
          }
    )
  }

  implicit def QuantifySampleSummaryReportMassLynxToGenericP2M2(x: QuantifySampleSummaryReportMassLynx): GenericP2M2 = {

    GenericP2M2(
      samples = x.resultsBySample
        .flatMap {
          case (
            sample: String,
            allValues: Seq[Map[QuantifySampleSummaryReportMassLynx.HeaderField.HeaderField, String]]) =>
            allValues
              .filter(_.nonEmpty)
              .map(
                values => {
                  Map(
                    GenericP2M2.HeaderField.metabolite -> values.get(QuantifySampleSummaryReportMassLynx.HeaderField.Name),
                    GenericP2M2.HeaderField.sample -> Some(sample) ,
                    GenericP2M2.HeaderField.retTime -> formatAsDouble(values.get(QuantifySampleSummaryReportMassLynx.HeaderField.RT)),
                    GenericP2M2.HeaderField.area -> formatAsDouble(values.get(QuantifySampleSummaryReportMassLynx.HeaderField.Area)),
                    GenericP2M2.HeaderField.height -> formatAsDouble(values.get(QuantifySampleSummaryReportMassLynx.HeaderField.Height)),
                    GenericP2M2.HeaderField.vial -> None ,
                    GenericP2M2.HeaderField.acquisitionDate ->
                      formatDateWithLocalDate(values.get(QuantifySampleSummaryReportMassLynx.HeaderField.`Acq.Date`), formatMassLynxTxt),
                    GenericP2M2.HeaderField.exportDate -> Some(x.header.printedDate.format(DateTimeFormatter.ofPattern(formatGenericP2M2))),
                    GenericP2M2.HeaderField.injectedVolume -> None
                  ).flatMap {
                    case (k, Some(v)) => Some(k, v)
                    case _ => None
                  }
                })
        }
    )
  }

  implicit def QSSRMassLynxToQCSRMassLynx(x: QuantifySampleSummaryReportMassLynx): QuantifyCompoundSummaryReportMassLynx = {
    QuantifyCompoundSummaryReportMassLynx(
      x.origin,
      x.header,
      resultsByCompound = x.resultsBySample flatMap {
        case (sample:String, values : Seq[Map[QuantifySampleSummaryReportMassLynx.HeaderField.HeaderField,String]])=>
          values.map(  m => {
              val compound = m(QuantifySampleSummaryReportMassLynx.HeaderField.Name)
              val newM = m flatMap {
                case QuantifySampleSummaryReportMassLynx.HeaderField.Name -> _ =>
                  Some(QuantifyCompoundSummaryReportMassLynx.HeaderField.Name -> sample)
                case k->v if QuantifyCompoundSummaryReportMassLynx.HeaderField.values.map(_.toString).contains(k.toString)
                => Some(QuantifyCompoundSummaryReportMassLynx.HeaderField.values.filter(_.toString == k.toString).last -> v)
                case _ => None
              }
            (compound,Seq(newM))
            })
        })
  }

  implicit def QCSRMassLynxToQSSRMassLynx(x: QuantifyCompoundSummaryReportMassLynx): QuantifySampleSummaryReportMassLynx = {
    QuantifySampleSummaryReportMassLynx(
      x.origin,
      x.header,
      resultsBySample = x.resultsByCompound flatMap {
        case (compound: String, values: Seq[Map[QuantifyCompoundSummaryReportMassLynx.HeaderField.HeaderField, String]]) =>
          values.map(m => {
            val sample = m(QuantifyCompoundSummaryReportMassLynx.HeaderField.Name)
            val newM = m flatMap {
              case QuantifyCompoundSummaryReportMassLynx.HeaderField.Name -> _ =>
                Some(QuantifySampleSummaryReportMassLynx.HeaderField.Name -> compound)
              case k -> v if QuantifySampleSummaryReportMassLynx.HeaderField.values.map(_.toString).contains(k.toString)
              => Some(QuantifySampleSummaryReportMassLynx.HeaderField.values.filter(_.toString == k.toString).last -> v)
              case _ => None
            }
            (sample, Seq(newM))
          })
      })
  }

  implicit def MassLynxToQSSRMassLynx(x: QuantifySummaryReportMassLynx): QuantifySampleSummaryReportMassLynx = x match {
    case y : QuantifyCompoundSummaryReportMassLynx => QCSRMassLynxToQSSRMassLynx(y)
    case y : QuantifySampleSummaryReportMassLynx => y
  }

  implicit def MassLynxToQCSRMassLynx(x: QuantifySummaryReportMassLynx): QuantifyCompoundSummaryReportMassLynx = x match {
    case y: QuantifyCompoundSummaryReportMassLynx => y
    case y: QuantifySampleSummaryReportMassLynx => QSSRMassLynxToQCSRMassLynx(y)
  }

  implicit def XcaliburToGenericP2M2(x: Xcalibur) : GenericP2M2 = {
    GenericP2M2(
      samples = x.results
        .flatMap(
          (injections : CompoundSheetXcalibur) => {
            injections.compoundByInjection.map(
              ( values : Map[Xcalibur.HeaderField.HeaderField, String]) => {
                Map(
                  GenericP2M2.HeaderField.metabolite -> injections.compoundInformationHeader.get(Xcalibur.HeaderSheetField.`Component Name`),
                  GenericP2M2.HeaderField.sample -> values.get(Xcalibur.HeaderField.Filename),
                  GenericP2M2.HeaderField.retTime -> formatAsDouble(values.get(Xcalibur.HeaderField.RT)),
                  GenericP2M2.HeaderField.area -> formatAsDouble(values.get(Xcalibur.HeaderField.Area)),
                  GenericP2M2.HeaderField.height -> formatAsDouble(values.get(Xcalibur.HeaderField.Height)),
                  GenericP2M2.HeaderField.vial -> values.get(Xcalibur.HeaderField.Vial),
                    GenericP2M2.HeaderField.acquisitionDate -> (values.get(Xcalibur.HeaderField.`Acq Date`) match {
                    case Some(d) =>
                      Try({
                        val dtf: DateTimeFormatter = DateTimeFormatter.ofPattern("E MMM d H:m:s z u", Locale.ENGLISH)
                        //Fri Jun 03 20:49:08 CEST 2022
                        //Tue Jun 20 14:53:08 CEST 2017
                        ZonedDateTime.parse(d, dtf) }) match {
                        case Success(v) => Some(v.format(DateTimeFormatter.ofPattern(formatGenericP2M2)))
                        case Failure(_) => formatDateWithLocalDateTime(Some(d),formatDateXcalibur2)
                  }
                    case _ => None
                  }) ,
                  GenericP2M2.HeaderField.exportDate -> {
                       formatDateWithLocalDateTime(injections.compoundInformationHeader.get(Xcalibur.HeaderSheetField.Date),formatDateXcalibur) match {
                    case Some(v) => Some(v)
                    case None =>
                      formatDateWithLocalDateTime(injections.compoundInformationHeader.get(Xcalibur.HeaderSheetField.Date),formatDateXcalibur2)
                      }},
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
  }

  implicit def GCMSToGenericP2M2(x: GCMS) :  GenericP2M2 =
    GenericP2M2(
      x.msQuantitativeResults.map(
        (res : Map[HeaderField, String]) => {
          Map(
            GenericP2M2.HeaderField.metabolite -> None,
            GenericP2M2.HeaderField.sample -> res.get(GCMS.HeaderField.Name),
            GenericP2M2.HeaderField.retTime -> formatAsDouble(res.get(GCMS.HeaderField.`Ret.Time`)),
            GenericP2M2.HeaderField.area -> formatAsDouble(res.get(GCMS.HeaderField.Area)),
            GenericP2M2.HeaderField.height -> formatAsDouble(res.get(GCMS.HeaderField.Height)),
            GenericP2M2.HeaderField.vial -> None,
            GenericP2M2.HeaderField.exportDate ->
              formatDateWithLocalDate(x.header.get(GCMS.HeaderFileField.Output_Date), formatGCMS),
            GenericP2M2.HeaderField.exportDate ->
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
            GenericP2M2.HeaderField.retTime -> formatAsDouble(res.get(OpenLabCDS.HeaderField.RetTime)),
            GenericP2M2.HeaderField.area -> formatAsDouble(res.get(OpenLabCDS.HeaderField.Area)),
            GenericP2M2.HeaderField.height -> formatAsDouble(res.get(OpenLabCDS.HeaderField.Amount)),
            GenericP2M2.HeaderField.vial ->  None,
            GenericP2M2.HeaderField.acquisitionDate ->
              formatDateWithLocalDateTime(x.header.get(OpenLabCDS.HeaderFileField.`Last changed Acq. Method`),formatOpenLabCDS),
            GenericP2M2.HeaderField.exportDate ->
              formatDateWithLocalDateTime(x.header.get(OpenLabCDS.HeaderFileField.`Last changed Analysis Method`), formatOpenLabCDS),
            GenericP2M2.HeaderField.injectedVolume -> formatAsDouble(x.header.get(OpenLabCDS.HeaderFileField.`Inj Volume`))
          ).flatMap {
            case (k,Some(v)) => Some(k,v)
            case _ => None
          }
        }
      )
    )
}
