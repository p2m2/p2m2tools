package fr.inrae.metabolomics.p2m2.parser

import fr.inrae.metabolomics.p2m2.format.ms.GCMS
import GCMS.HeaderField.HeaderField
import GCMS.{HeaderField, HeaderFileField}
import GCMS.HeaderFileField.HeaderFileField

import scala.io.{Codec, Source}
import scala.util.{Failure, Success, Try}

object GCMSParser extends Parser[GCMS] with FormatSniffer {
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



  def parseHeader( toParse : List[String] ) : Map[HeaderFileField,String] =
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
                  case Some(v) => Some(HeaderFileField.Data_File_Name -> v.group(1).trim)
                  case None => throw new Exception (s"Can not capture [$category]/Data File Name value")
                }
              case s : String if s.startsWith("""Output Date""") =>
                """Output\sDate(\s+.*)""".r.findFirstMatchIn(s) match {
                  case Some(v) => Some(HeaderFileField.Output_Date -> v.group(1).trim)
                  case None => throw new Exception(s"Can not capture [$category]/Output Date value")
                }
              case s : String if s.startsWith("""Output Time""") =>
                """Output\sTime(\s+.*)""".r.findFirstMatchIn(s) match {
                  case Some(v) => Some(HeaderFileField.Output_Time -> v.group(1).trim)
                  case None => throw new Exception(s"Can not capture [$category]/Output Time value")
                }
              case _ => None
            }.toMap
        case None => throw new Exception(s"Category [$category] does not exist !")
      }
    }

  def parseMSQuantitativeResults( toParse : List[String] ) : List[Map[HeaderField,String]] = {
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
              .flatMap {
                case (value, index) if index < header.length =>
                  ParserUtils.getHeaderField(GCMS.HeaderField,header(index).trim) match {
                    case Some(keyT) => Some(keyT -> value)
                    case _ => throw new Exception(s"Unknown column header name : ${header(index).trim}")
                  }
                case (_, index) => throw new Exception(s"bad column index [$index] header length:${header.length} " +
                  s"\n**header**\n${header.mkString(",")}\n**line**\n$line")
              }.toMap
          })
      case None => throw new Exception(s"Category [$category] does not exist !")
    }
  }

  def get(filename : String, toParse : List[String]) : GCMS = {
    GCMS(
      origin = filename,
      header = parseHeader(toParse),
      msQuantitativeResults = parseMSQuantitativeResults(toParse)
    )
  }

  def parse(source : Source): GCMS = {
    val lines = source.getLines()
    val ret = get(
      source.descr,
      lines.toList
        .map(_.trim)
        .filter(_.nonEmpty)
        .filter(!_.startsWith("#"))
    )
    source.close()
    ret
  }

  def parseByteArray(content: Array[Byte], encode : Codec = Codec("ISO-8859-1")) : GCMS =
    parse(Source.fromBytes(content)(encode))

  def parseFile(filename: String, encode : Codec = Codec("ISO-8859-1")) : GCMS =
    parse(Source.fromFile(filename)(encode))


  override def extensionIsCompatible(filename: String): Boolean = {
    filename.split("\\.").lastOption match {
      case Some(a) if a.trim!="" => true
      case _ => false
    }
  }

  private def testHeader(source : Source) : Boolean = {
    val trunkLines = source.getLines().slice(0, 20).toList
    source.close()
    Try(parseHeader(trunkLines)) match {
      case Success(m) if m.nonEmpty => true
      case _ => false
    }
  }

  override def sniffByteArray(content: Array[Byte], encode : Codec): Boolean = {
    Try(testHeader(Source.fromBytes(content)(encode))) match {
      case Success(v) => v
      case Failure(_) => false
    }
  }

  override def sniffFile(filename: String, encode : Codec ): Boolean = {
    Try(testHeader(Source.fromFile(filename)(encode))) match {
      case Success(v) => v
      case Failure(_) => false
    }
  }
}
