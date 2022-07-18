package fr.inrae.metabolomics.p2m2.parser

import fr.inrae.metabolomics.p2m2.tools.format.output.OutputOpenLabCDS
import fr.inrae.metabolomics.p2m2.tools.format.output.OutputOpenLabCDS.HeaderField
import fr.inrae.metabolomics.p2m2.tools.format.output.OutputOpenLabCDS.HeaderField.HeaderField

import scala.io.Source
import scala.util.{Failure, Success, Try}

object OpenLabCDSParser extends Parser[OutputOpenLabCDS] with FormatSniffer {
  val separator = " "

  def parseHeader( toParse : List[String] ) : Map[HeaderField,String] =
    {
          toParse
            .flatMap {
              case s : String if s.startsWith("""Sample""") =>
                """Sample\sName:\s+(.*)""".r.findFirstMatchIn(s) match {
                  case Some(v) => Some(HeaderField.Sample_Name -> v.group(1))
                  case None => None
                }
              case _ => None
            }.toMap
    }

  def parseResults( toParse : List[String] ) : List[Map[String,String]] = {
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
                case (value, index) if length == 7 => header_7(index) -> value
                case (value, index) if length == 8 => header_8(index) -> value
              }.toMap
          })

  }

  def get(filename : String, toParse : List[String]) : OutputOpenLabCDS = {
    OutputOpenLabCDS(
      origin = filename,
      header = parseHeader(toParse),
      results = parseResults(toParse)
    )
  }

  def parse(filename : String) : OutputOpenLabCDS = {
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
