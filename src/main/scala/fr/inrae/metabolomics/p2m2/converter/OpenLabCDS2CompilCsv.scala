package fr.inrae.metabolomics.p2m2.converter

import fr.inrae.metabolomics.p2m2.parser.OpenLabCDSParser
import fr.inrae.metabolomics.p2m2.tools.format.OpenLabCDS

import scala.util.{Failure, Success, Try}

case class OpenLabCDS2CompilCsv(target_head : String ) {

      def build(inputFiles : Seq[String]) : Seq[OpenLabCDS] = {
            println(inputFiles.mkString("\n"))

            inputFiles.map(
                  fileName => OpenLabCDSParser.parse(fileName)
            )
      }


      def header_name_compound(openLabCds : OpenLabCDS) : List[String] = {
            openLabCds.results.flatMap( (mapResults: Map[String, String]) => mapResults.get("Name"))
      }

      def transform(openLabCds : OpenLabCDS, header_name_compound : List[String] )
      : List[Serializable] = {
            //val sample = openLabCds.origin.split("[/\\\\]").last.split("\\.[a-zA-Z]+$").head

            header_name_compound.map(
                  compound => {
                        val l1 =
                              openLabCds.
                                results.
                                filter((mapResults: Map[String, String]) => mapResults.getOrElse("Name","") == compound)

                        Try(l1.head.get("Name") match {
                              case Some(nameCompound) if nameCompound == compound
                              => l1.head.get(target_head)
                              case _ => None
                        }) match {
                              case Success(v) => v
                              case Failure(_) => None
                        }

                  })
      }
}