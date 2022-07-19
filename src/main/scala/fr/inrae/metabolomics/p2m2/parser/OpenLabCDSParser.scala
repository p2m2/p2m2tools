package fr.inrae.metabolomics.p2m2.parser

import fr.inrae.metabolomics.p2m2.format.OpenLabCDS
import OpenLabCDS.HeaderField.HeaderField
import OpenLabCDS.HeaderFileField
import OpenLabCDS.HeaderFileField.HeaderFileField

import scala.io.Source
import scala.util.matching.Regex
import scala.util.{Failure, Success, Try}

object OpenLabCDSParser extends Parser[OpenLabCDS] with FormatSniffer {
  val separator = " "

  def setHeaderValue(toParse : Seq[String],
                     containString : String,
                     regexGroup : Regex) : Option[(OpenLabCDS.HeaderFileField.HeaderFileField,String)] =

    Try(toParse
      .flatMap {
        case s: String if s.contains(containString) =>
          regexGroup.findFirstMatchIn(s) match {
            case Some(v) =>
              ParserUtils.getHeaderField(OpenLabCDS.HeaderFileField,v.group(1)) match {
                case Some(k) => Some(k -> v.group(2).trim)
                case None => None
              }
            case None => None
          }
        case _ => None
      }.head) match {
      case Success(v) => Some(v)
      case Failure(_) => None
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
        }.toMap ++ ( """Acq.\sMethod\s*:\s*(.*)""".r.findFirstMatchIn(toParse.slice(0,100).map(_.trim).mkString("")) match {
        case Some(v) =>
          val acq_method = v.group(1).trim.split("Last changed").head
          val date = v.group(1).trim.split("Last changed").last
          Map(HeaderFileField.`Acq. Method` -> acq_method, HeaderFileField.`Last changed Acq. Method`->date )
        case None => Map()
      }) ++ ( """Analysis\sMethod\s*:\s*(.*)""".r.findFirstMatchIn(toParse.slice(0,100).map(_.trim).mkString("")) match {
        case Some(v) =>
          val analyse_method = v.group(1).trim.split("Last changed").head
          val date = v.group(1).trim.split("Last changed").last
          Map(HeaderFileField.`Analysis Method` -> analyse_method, HeaderFileField.`Last changed Analysis Method` -> date)
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
                case k->v if k.isDefined=> Some(k.get -> v)
                case _ => None
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

  def parse(filename : String) : OpenLabCDS = {
    val source =       Source.fromFile(filename)
    val lines = source.getLines()
    val ret = get(
      filename,
      lines.toList
        .map( _.trim )
        .filter( _.nonEmpty)
    )
    source.close()
    ret
  }

  override def extensionIsCompatible(filename: String): Boolean = {
    filename.split("\\.").lastOption match {
      case Some(a) if a.trim!="" => true
      case _ => false
    }
  }

  override def sniffFile(filename: String): Boolean = {
    try {
      val source =       Source.fromFile(filename)
      val lines = source.getLines().slice(0,20).toList
      source.close()
      Try(parseHeader(lines)) match {
        case Success(m) if m.nonEmpty => true
        case _ => false
      }
    } catch {
      case _: Throwable => false
    }
  }
}
