package fr.inrae.metabolomics.p2m2.parser

import fr.inrae.metabolomics.p2m2.format.ms.QuantifySummaryReportMassLynx.Header
import fr.inrae.metabolomics.p2m2
import fr.inrae.metabolomics.p2m2.format.ms.{QuantifyCompoundSummaryReportMassLynx, QuantifySampleSummaryReportMassLynx, QuantifySummaryReportMassLynx}

import scala.io.{Codec, Source}
import scala.util.{Failure, Success, Try}
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

  def parseResultsByElement[HeaderField<: Enumeration](t:HeaderField,toParse : Seq[String] )
  : Seq[(String,Seq[Map[HeaderField#Value,String]])] = {
    val listCompoundIndexLine = toParse.zipWithIndex.filter( x=>
      x._1.trim.startsWith("Compound") || x._1.trim.startsWith("Sample Name:")

    ).map(_._2)

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
        val name : String = """(Compound \d+|Sample Name):\s+(.*)""".r.findFirstMatchIn(toParse(idx_line)) match {
          case Some(v) => v.group(2)
          case None => throw new Exception("Can not parse Compound/Sample name:"+toParse(idx_line))
        }

        (name,parseArrayCompound(t,toParse.slice(iStart,iEnd)))
      })
  }

  private def parseArrayCompound[HeaderField <: Enumeration](t:HeaderField, toParse : Seq[String] ) :Seq[Map[HeaderField#Value,String]] = {
      toParse.filter(_.trim.nonEmpty).find( x => x.trim.startsWith("Name") || x.trim.startsWith("#")) match {
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
                ParserUtils.getHeaderField(t,header(index)) match {
                  case Some(k) if value.nonEmpty => Some(k -> value)
                  case _ => None
                }
              }.toMap
            })
        case None =>  Seq()
      }
  }

  def getCompoundSummaryReport(filename : String, toParse : Seq[String]) : QuantifyCompoundSummaryReportMassLynx = {
    QuantifyCompoundSummaryReportMassLynx(
      origin = filename,
      header = parseHeader(toParse),
      resultsByCompound = parseResultsByElement(QuantifyCompoundSummaryReportMassLynx.HeaderField,toParse)
    )
  }

  private def getSampleSummaryReport(filename: String, toParse: Seq[String]): QuantifySampleSummaryReportMassLynx = {
    QuantifySampleSummaryReportMassLynx(
      origin = filename,
      header = parseHeader(toParse),
      resultsBySample = parseResultsByElement(QuantifySampleSummaryReportMassLynx.HeaderField, toParse)
    )
  }

  def parse(source : Source): QuantifySummaryReportMassLynx = {
    val lines = source.getLines().toList
    source.close()

    """Sample\s+Name:""".r.findFirstMatchIn(lines.slice(0, 10).mkString("\n")) match {
      case Some(_) => getSampleSummaryReport(
        source.descr,
        lines
          .map(_.trim)
          .filter(_.nonEmpty)
      )
      case None => getCompoundSummaryReport(
        source.descr,
        lines
          .map(_.trim)
          .filter(_.nonEmpty)
      )
    }
  }

  def parseByteArray(content: Array[Byte], encode : Codec = Codec("ISO-8859-1")) : QuantifySummaryReportMassLynx =
    parse(Source.fromBytes(content)(encode))
  def parseFile(filename : String, encode : Codec = Codec("ISO-8859-1")) : QuantifySummaryReportMassLynx =
    parse(Source.fromFile(filename)(encode))

  override def extensionIsCompatible(filename: String): Boolean = {
    filename.split("\\.").lastOption match {
      case Some(a) if a.trim!="" => true
      case _ => false
    }
  }

  private def testHeader(source: Source): Boolean = {
    val trunkLines = source.getLines().slice(0, 20).toList
    source.close()
    parseHeader(trunkLines).dateStr.isDefined
  }
  override def sniffByteArray(content: Array[Byte], encode: Codec): Boolean = {
    Try(testHeader(Source.fromBytes(content)(encode))) match {
      case Success(v) => v
      case Failure(_) => false
    }
  }
  override def sniffFile(filename: String, encode: Codec): Boolean = {
    Try(testHeader(Source.fromFile(filename)(encode))) match {
      case Success(v) => v
      case Failure(_) => false
    }
  }
}
