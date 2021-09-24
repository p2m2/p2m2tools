package fr.inrae.metabolomics.p2m2.converter

import fr.inrae.metabolomics.p2m2.parser.GCMSParser
import fr.inrae.metabolomics.p2m2.tools.format.output.OutputGCMS
import fr.inrae.metabolomics.p2m2.tools.format.output.OutputGCMS.HeaderField

case class GCMSOutputFiles2IsocorInput( resolution : Int = 2000, separator_name : String = "_" ) {

      def build(gcmsInputFiles : Seq[String]) : Seq[OutputGCMS] = {
            println(gcmsInputFiles.mkString("\n"))

            gcmsInputFiles.map(
                  fileName => GCMSParser.parse(fileName)
            )
      }


      def transform( gcms : OutputGCMS ) : List[String] = {
            val sample = gcms.header.get(HeaderField.Data_File_Name) match {
                  case Some(value) => value.split("[/\\\\]").last.split("\\.[a-zA-Z]+$").head
                  case None => throw new Exception("Can not retrieve sample (end of 'Data File Name' value) origin:"+gcms.origin)
            }

            gcms.ms_quantitative_results
              .flatMap {
                  case (mapResults : Map[String,String]) => {
                        val id = mapResults.get("ID#") match {
                              case Some(v) => v
                              case None => "unknown"
                        }
                         (mapResults.get("Name") match {
                                    case Some(v) => v
                                    case None => {
                                          throw new Exception("Can not parse 'Name' field origin:" + gcms.origin +
                                            ", id:"+id )
                                    }
                              }).split(separator_name) match {
                                    case tokens if tokens.length == 3 => {
                                          val (metabolite,derivative,isotopologue) = (tokens(0),tokens(1),tokens(2))
                                          val area = mapResults.get("Area") match {
                                                case Some(v) if v != "" => v
                                                case _ => {
                                                      System.err.println("Can not parse 'Area' field "+
                                                        ", origin:" + gcms.origin + ", id:"+id )
                                                      None
                                                }

                                          }
                                          if ( area != None )
                                                Some(List(sample, metabolite, derivative,
                                                isotopologue.replace("m",""),area, resolution)
                                            .mkString("\t"))
                                          else
                                                None
                                    }
                                    case nameNotFormatted => {
                                          System.err.println(
                                                "Name should be formatted [Metabolite]_[Derivative]_[Isotopologue] " +
                                                  ", name:"+ nameNotFormatted.mkString(separator_name) +
                                                  ", origin:" + gcms.origin + ", id:" + id )
                                          None
                                    }
                        }


                  }
            }
      }
}