package fr.inrae.metabolomics.p2m2.parser

import fr.inrae.metabolomics.p2m2.format.ms.OpenLabCDS.HeaderField.HeaderField
import fr.inrae.metabolomics.p2m2.format.ms.OpenLabCDS.HeaderFileField
import fr.inrae.metabolomics.p2m2.format.ms.OpenLabCDS.HeaderFileField.HeaderFileField
import fr.inrae.metabolomics.p2m2.format.ms.OpenLabCDS

import scala.io.{Codec, Source}
import scala.util.matching.Regex
import scala.util.{Failure, Success, Try}

object OpenLabCDSParser extends Parser[OpenLabCDS] with FormatSniffer {
  val separator = " "

  def setHeaderValue(toParse : Seq[String],
                     containString : String,
                     regexGroup : Regex) : Option[(OpenLabCDS.HeaderFileField.HeaderFileField,String)] =
    {
      val res = toParse
        .flatMap {
          case s: String if s.contains(containString) =>
            regexGroup.findFirstMatchIn(s) match {
              case Some(v) =>
                ParserUtils.getHeaderField(OpenLabCDS.HeaderFileField,v.group(1)) match {
                  case Some(k) => Some(k -> v.group(2).trim)
                  case None => throw new Exception(s"Unknown column header name : ${v.group(1)}")
                }
              case None =>  None
            }
          case _=>None
        }
      Try(res.head) match {
        case Success(v) => Some(v)
        case Failure(_) => None
      }
    }

  def parseHeader( toParse : List[String] ) : Map[HeaderFileField,String] =
      Seq(
        ("Sample Name","""(Sample\sName)\s*:\s*(.*)""".r),
        ("Acq. Operator","""(Acq.\sOperator)\s*:\s*(\w+)""".r),
        ("Seq. Line","""(Seq.\sLine)\s*:\s*(\w+)$""".r),
        ("Sample Operator","""(Sample\sOperator)\s*:\s*(\w+)""".r),
        ("Acq. Instrument","""(Acq.\sInstrument)\s*:\s*(.*)\s+Location""".r),
          ("Location","""(Location)\s*:\s*(.*)$""".r),
        ("Injection Date","""(Injection\sDate)\s*:\s*(.*)\s+Inj""".r),
        ("Inj","""(Inj)\s*:\s*(\w+)$""".r),
        ("Inj Volume","""(Inj\sVolume)\s*:\s*(.*)$""".r),
        ("Additional Info","""(Additional\sInfo)\s*:\s*(.*)\s*$""".r),
      )
        .flatMap{
          case (containsString:String,regex:Regex) => setHeaderValue(toParse,containsString,regex)
        }.toMap ++ ( """Acq.\sMethod\s*:\s*(.*\s+.*)\n""".r.findFirstMatchIn(toParse.slice(0,100).map(_.trim).mkString("\n")) match {
        case Some(v) =>

          val acqMethod = Some(HeaderFileField.`Acq. Method` ->v.group(1).replace("\n","").trim)
          val date = """Acq.\sMethod\s*:\s*.*\s+.*\nLast changed\s*:\s*(\d.*)\s+by"""
            .r.findFirstMatchIn(toParse.slice(0,100).map(_.trim).mkString("\n")) match {
              case Some(k) => Some(HeaderFileField.`Last changed Acq. Method`->k.group(1).trim)
              case None => None
          }
          Seq(acqMethod,date).flatten.toMap
        case None => Map()
      }) ++ ( """Analysis\sMethod\s*:\s*(.*\s+.*)\n""".r.findFirstMatchIn(toParse.slice(0,100).map(_.trim).mkString("\n")) match {
        case Some(v) =>
          val analysisMethod =  Some(HeaderFileField.`Analysis Method` ->v.group(1).replace("\n","").trim)
          val date = """Analysis\sMethod\s*:\s*.*\s+.*\nLast changed\s*:\s*(\d.*)\s+by""".r
            .findFirstMatchIn(toParse.slice(0,100).map(_.trim).mkString("\n")) match {
              case Some(k) => Some(HeaderFileField.`Last changed Analysis Method`->k.group(1).trim)
              case None => None
          }
          Seq(analysisMethod,date).flatten.toMap
        case None => Map()
      })
/*
 ("Acq. Method","""(Acq.\sMethod)\s*:\s*(.*)$""".r),
        ("Analysis Method","""(Analysis.\sMethod)\s*:\s*(.*)$""".r),
 */

  def parseResults( toParse : List[String] ) : List[Map[HeaderField,String]] = {
    // Grp ?????     val header_7 = List("RetTime","Type","used","Area","Amt/Area","Amount","Grp","Name")
    val header_7 = List("RetTime","Type","used","Area","Amt/Area","Amount","Name")
    val header_8 = List("RetTime","Type","ISTD","used","Area","Amt/Area","Amount","Name")

    toParse
          .map( (line : String) => {
            line
              .split(separator)
              .filter(_!="")
          })
      .filter( _.length  >= 7 )
      .filter( _.length  <= 8 )
      .filter( l => Try(l(0).toFloat) match { case Success(_) => true ; case Failure(_) => false})
      .map( l => (l,l.length) )
            .map( something => {
              val mapLine : Seq[String] = something._1
              val length : Int = something._2
              mapLine
              .zipWithIndex
              .map {
                case (value, index) if length == 7 => ParserUtils.getHeaderField(OpenLabCDS.HeaderField,header_7(index)) -> value
                case (value, index) if length == 8 => ParserUtils.getHeaderField(OpenLabCDS.HeaderField,header_8(index)) -> value
              }.
                flatMap {
                case k->v => Some(k.get -> v)
              }.toMap
          })

  }

  def get(filename : String, toParse : List[String]) : OpenLabCDS = {
    OpenLabCDS(
      origin = filename,
      header = parseHeader(toParse),
      results = parseResults(toParse)
    )
  }
  def parse(source : Source): OpenLabCDS = {
    val lines = source.getLines()
    val ret = get(
      source.descr,
      lines.toList
        .map(_.trim)
        .filter(_.nonEmpty)
    )
    source.close()
    ret
  }
  def parseByteArray(content: Array[Byte], encode : Codec = Codec("ISO-8859-1")) : OpenLabCDS =
    parse(Source.fromBytes(content)(encode))

  def parseFile(filename : String, encode : Codec = Codec("ISO-8859-1")) : OpenLabCDS =
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
    Try(parseHeader(trunkLines)) match {
      case Success(m) if m.nonEmpty => true
      case _ => false
    }
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
