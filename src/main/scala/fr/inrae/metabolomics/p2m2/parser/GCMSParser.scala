package fr.inrae.metabolomics.p2m2.parser
import fr.inrae.metabolomics.p2m2.tools.GCMS
import fr.inrae.metabolomics.p2m2.tools.GCMS.HeaderField

import scala.util.parsing.combinator._
import fr.inrae.metabolomics.p2m2.tools.GCMS.HeaderField._

import scala.io.Source

class GCMSParser extends RegexParsers {
  def headerTitle  = """[Header]"""
  def header_data_file_name: Parser[(HeaderField,String)]  =
    """^Data\s+File\s+Name\s+(.+)""".r       ^^ { (HeaderField.Data_File_Name -> _) }
  //def number: Parser[Int]    = """(0|[1-9]\d*)""".r ^^ { _.toInt }
  //def freq: Parser[WordFreq] = word ~ number        ^^ { case wd ~ fr => WordFreq(wd,fr) }
  def gcms_header = headerTitle ~ header_data_file_name ^^ { case
    title ~ dtf => {
      println(dtf)
    }
  }

}

object GCMSParser extends GCMSParser {

  def get(toParse : String) : Unit = {
    println(parse(gcms_header,toParse))
  }

  def parse(filename : String) = {
    val lines = Source.fromFile(filename).getLines().toList

    val categories = lines.zipWithIndex map {
      case (line: String, idx: Int) => line match {
        case categoryC if categoryC.startsWith("[") && categoryC.endsWith("]") =>
          (idx,categoryC.replaceFirst("\\[","").replaceFirst("\\]",""))
        case _ =>
      }
    }
    println(categories)
  }
}
