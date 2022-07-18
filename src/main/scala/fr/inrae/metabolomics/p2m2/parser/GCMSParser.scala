package fr.inrae.metabolomics.p2m2.parser
import fr.inrae.metabolomics.p2m2.tools.format.output.OutputGCMS
import fr.inrae.metabolomics.p2m2.tools.format.output.OutputGCMS.HeaderField
import fr.inrae.metabolomics.p2m2.tools.format.output.OutputGCMS.HeaderField.HeaderField

import scala.io.Source
import scala.util.{Failure, Success, Try}

object GCMSParser extends Parser[OutputGCMS] with FormatSniffer {
  val separator = "\t"
  /**
   *
   * @param toParse
   * @return Map category -> firstLine (Int) to lastLine
   */
  def getIndexLinesByCategories( toParse : List[String]  ) : Map[String,(Int,Int)] = {
    val base = toParse.zipWithIndex.flatMap {
      case (element, index) =>
        val pattern = """\[([a-zA-Z ]+)""".r
        pattern.findFirstMatchIn(element) match {
          case Some(elt) => Some(elt.group(1) -> (index, index))
          case None => None
        }
    }

    base.zipWithIndex.map {
      case (element, index) =>
        element match {
          case s -> d if index < ( base.length - 1 ) => s -> (d._1,base(index+1)._2._1)
          case lastCategory -> d => lastCategory -> (d._1,toParse.length)
        }
    }.toMap
  }



  def parseHeader( toParse : List[String] ) : Map[HeaderField,String] =
    {
      val category = "Header"

      getIndexLinesByCategories(toParse)
        .get(category) match {
        case Some( lMin_lMax ) =>
          toParse
            .slice( lMin_lMax._1+1, lMin_lMax._2 )
            .flatMap {
              case s : String if s.startsWith("""Data File Name""") =>
                """Data\sFile\sName(\s+.*)""".r.findFirstMatchIn(s) match {
                  case Some(v) => Some(HeaderField.Data_File_Name -> v.group(1).trim)
                  case None => throw new Exception (s"Can not capture [$category]/Data File Name value")
                }
              case s : String if s.startsWith("""Output Date""") =>
                """Output\sDate(\s+.*)""".r.findFirstMatchIn(s) match {
                  case Some(v) => Some(HeaderField.Output_Date -> v.group(1).trim)
                  case None => throw new Exception(s"Can not capture [$category]/Output Date value")
                }
              case s : String if s.startsWith("""Output Time""") =>
                """Output\sTime(\s+.*)""".r.findFirstMatchIn(s) match {
                  case Some(v) => Some(HeaderField.Output_Time -> v.group(1).trim)
                  case None => throw new Exception(s"Can not capture [$category]/Output Time value")
                }
              case _ => None
            }.toMap
        case None => throw new Exception(s"Category [$category] does not exist !")
      }
    }

  def parseMSQuantitativeResults( toParse : List[String] ) : List[Map[String,String]] = {
    val category = "MS Quantitative Results"

    getIndexLinesByCategories(toParse)
      .get(category) match {
      case Some(lMin_lMax) =>
        /* header */
        val header = toParse(lMin_lMax._1 + 1).split(separator)
        /* values */
        toParse
          .slice(lMin_lMax._1 + 2, lMin_lMax._2)
          .map( (line : String) => {
            line
              .split(separator)
              .zipWithIndex
              .map {
                case (value, index) => header(index) -> value
              }.toMap
          })
      case None => throw new Exception(s"Category [$category] does not exist !")
    }
  }

  def get(filename : String, toParse : List[String]) : OutputGCMS = {
    OutputGCMS(
      origin = filename,
      header = parseHeader(toParse),
      ms_quantitative_results = parseMSQuantitativeResults(toParse)
    )
  }

  def parse(filename : String) : OutputGCMS = {
    val source =       Source.fromFile(filename)
    val lines = source.getLines()
    val ret = get(
      filename,
      lines.toList
        .map( _.trim )
        .filter( _.nonEmpty)
        .filter( ! _.startsWith("#") )
    )
    source.close()
    ret
  }

  override def extensionIsCompatible(filename: String): Boolean = {
    filename.split("\\.").lastOption match {
      case Some(a) if a.trim!="" => true
      case None => false
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
