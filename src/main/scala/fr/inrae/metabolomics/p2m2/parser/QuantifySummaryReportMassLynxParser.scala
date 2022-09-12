package fr.inrae.metabolomics.p2m2.parser

import fr.inrae.metabolomics.p2m2.format.{QuantifyCompoundSummaryReportMassLynx, QuantifySampleSummaryReportMassLynx, QuantifySummaryReportMassLynx}
import fr.inrae.metabolomics.p2m2.format.QuantifySummaryReportMassLynx.Header
import fr.inrae.metabolomics.p2m2.format.QuantifySummaryReportMassLynx.HeaderField.HeaderField
import fr.inrae.metabolomics.p2m2

import scala.io.{Codec, Source}
object QuantifySummaryReportMassLynxParser
  extends Parser[QuantifySummaryReportMassLynx]
  with FormatSniffer {
  val separator = "\t"

  def parseHeaderSpec( toParse : Seq[String] , title: String, typeFile : String) : Header =
    {
      val start = toParse.indices.find( id => toParse(id).trim == title )
      val end = toParse.indices.find(id => toParse(id).trim.startsWith(typeFile))

      start match {
        case Some(s) =>
          end match {
            case Some(e) =>
              Header(toParse.slice(s, e)
              .filter( _.startsWith("Printed") )
              .map( _.replace("Printed","").trim)
              .find(_.nonEmpty))
            case None =>
              Header(toParse.slice(s, toParse.length)
                .filter( _.startsWith("Printed") )
                .map( _.replace("Printed","").trim)
                .find(_.nonEmpty))
          }

        case None => Header()
      }
    }

  def parseHeader( toParse : Seq[String]) : Header =
    parseHeaderSpec(toParse,"Quantify Compound Summary Report","Compound") match {
      case Header(None) => parseHeaderSpec(toParse,"Quantify Sample Summary Report","Sample Name")
      case res => res
    }

  def parseResultsByElement(element: String, toParse : Seq[String] ) : Seq[(String,Seq[Map[HeaderField,String]])] = {
    val listCompoundIndexLine = toParse.zipWithIndex.filter( _._1.trim.startsWith("Compound")).map(_._2)

    listCompoundIndexLine
      .zipWithIndex.map(
      zip => {
        val idx_line = zip._1
        val iStart = listCompoundIndexLine(zip._2)+1
        val iEnd = zip._2 match {
          case v if v == listCompoundIndexLine.length-1 => toParse.length
          case idx => listCompoundIndexLine(idx+1)
        }
        // element = "Compound" or "Sample Name"
        val compoundName : String = """\s\d+:\s+(.*)""".r.findFirstMatchIn(toParse(idx_line)) match {
          case Some(v) => v.group(1)
          case None => throw new Exception("Can not parse Compound name:"+toParse(idx_line))
        }
        (compoundName,parseArrayCompound(toParse.slice(iStart,iEnd)))
      })
  }

  def parseArrayCompound( toParse : Seq[String] ) :Seq[Map[HeaderField,String]] = {
      toParse.filter(_.trim.nonEmpty).find(_.trim.startsWith("Name")) match {
        case Some(headerString) =>
          /* first value of array in the number corresponding to the injection*/
          val header :Seq[String] = "Num. Injection" +: headerString.trim.split(separator)
          toParse
            .map(_.trim)
            .filter(_.nonEmpty)
            .filter( ! _.startsWith("Name") )
            // split String => List[String] "List of Fields"
            .map( (line : String) => line.split(separator) )
            .map( mapLine => {
              mapLine
                .zipWithIndex.flatMap {  case (value, index) =>
                ParserUtils.getHeaderField(QuantifySummaryReportMassLynx.HeaderField,header(index)) match {
                  case Some(k) if value.nonEmpty => Some(k -> value)
                  case _ => None
                }
              }.toMap
            })
        case None => Seq()
      }
  }

  def getCompoundSummaryReport(filename : String, toParse : Seq[String]) : QuantifyCompoundSummaryReportMassLynx = {
    p2m2.format.QuantifyCompoundSummaryReportMassLynx(
      origin = filename,
      header = parseHeader(toParse),
      resultsByCompound = parseResultsByElement("Compound",toParse)
    )
  }

  def getSampleSummaryReport(filename: String, toParse: Seq[String]): QuantifySampleSummaryReportMassLynx = {
    p2m2.format.QuantifySampleSummaryReportMassLynx(
      origin = filename,
      header = parseHeader(toParse),
      resultsBySample = parseResultsByElement("Sample Name", toParse)
    )
  }

  def parse(filename : String) : QuantifySummaryReportMassLynx = {
    val s = Source.fromFile(filename)(Codec("ISO-8859-1"))
    val lines = s.getLines().toList
    s.close()
    """^Sample\S+Name:""".r.findFirstMatchIn(lines.slice(0,10).mkString("\n")) match {
      case Some(_) => getSampleSummaryReport(
        filename,
        lines
          .map(_.trim)
          .filter(_.nonEmpty)
      )
      case None => getCompoundSummaryReport(
        filename,
        lines
          .map(_.trim)
          .filter(_.nonEmpty)
      )
    }
  }

  override def extensionIsCompatible(filename: String): Boolean = {
    filename.split("\\.").lastOption match {
      case Some(a) if a.trim!="" => true
      case _ => false
    }
  }

  override def sniffFile(filename: String): Boolean = {
    try {

      val source =       Source.fromFile(filename)(Codec("ISO-8859-1"))
      val lines = source.getLines().slice(0,20).toList
      source.close()
      parseHeader(lines).dateStr.isDefined
    } catch {
      case e : Throwable => System.err.println(e.toString) ; false
    }
  }
}
