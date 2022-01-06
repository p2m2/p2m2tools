package fr.inrae.metabolomics.p2m2.parser

import fr.inrae.metabolomics.p2m2.tools.format.output.OutputMassLynx
import fr.inrae.metabolomics.p2m2.tools.format.output.OutputMassLynx.{CompoundField, Header, buildCompoundField}

import scala.io.Source

object MassLynxParser {
  val separator = "\t"

  def parseHeader( toParse : List[String] ) : Header =
    {
      val start = toParse.indices.find( id => toParse(id).trim == "Quantify Compound Summary Report" )
      val end = toParse.indices.find(id => toParse(id).trim.startsWith("Compound"))

      start match {
        case Some(s) => end match {
          case Some(e) => Header(toParse.slice(s, e)
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
  def parseResults( toParse : List[String] ) : List[(String,List[CompoundField])] = {
    val listCompoundIndexLine = toParse.zipWithIndex.filter( _._1.trim.startsWith("Compound")).map(_._2)
    listCompoundIndexLine
      .zipWithIndex.map(
      vzip => {
        val idx_line = vzip._1
        val i_start = listCompoundIndexLine(vzip._2)+1
        val i_end = vzip._2 match {
          case v if v == listCompoundIndexLine.length-1 => toParse.length
          case _ => listCompoundIndexLine(vzip._2+1)
        }
        val compoundName : String = """Compound\s\d+:\s+(.*)""".r.findFirstMatchIn(toParse(idx_line)) match {
          case Some(v) => v.group(1)
          case None => throw new Exception("Can not parse Compound name:"+toParse(idx_line))
        }
        (compoundName,parseArrayCompound(toParse.slice(i_start,i_end)))
      })
  }

  def parseArrayCompound( toParse : List[String] ) :List[CompoundField] = {
    val header = List("INDEX","Name","Trace","Type","Std. Conc","RT",
      "Area","uM","%Dev","S/N","Vial","Height/Area","Acq.Date","Height")

    toParse
      .filter(_.trim.nonEmpty)
      .filter( ! _.trim.startsWith("Name") ) //avoid header
      // split String => List[String] "List of Fields"
      .map( (line : String) => line.split(separator) )
      .map( l => (l,l.length) )
      .map( something => {
        val mapLine : Seq[String] = something._1
        val length : Int = something._2
        buildCompoundField(mapLine
          .zipWithIndex
          .map {
            case (value, index) if length == header.length => header(index) -> value
                case _ => throw new Exception("bad line of array :"+mapLine.mkString(","))
          }.toMap)
      })

  }

  def get(filename : String, toParse : List[String]) : OutputMassLynx = {
    OutputMassLynx(
      origin = filename,
      header = parseHeader(toParse),
      results = parseResults(toParse)
    )
  }

  def parse(filename : String) : OutputMassLynx = get(
    filename,
    Source
      .fromFile(filename)
      .getLines()
      .toList
      .map( _.trim )
      .filter( _.nonEmpty)
  )

}
