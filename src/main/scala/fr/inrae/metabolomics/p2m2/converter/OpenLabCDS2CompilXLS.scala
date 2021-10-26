package fr.inrae.metabolomics.p2m2.converter

import fr.inrae.metabolomics.p2m2.parser.OpenLabCDSParser
import fr.inrae.metabolomics.p2m2.tools.format.output.OutputOpenLabCDS

import scala.util.{Failure, Success, Try}

case class OpenLabCDS2CompilXLS( ) {

      def build(gcmsInputFiles : Seq[String]) : Seq[OutputOpenLabCDS] = {
            println(gcmsInputFiles.mkString("\n"))

            gcmsInputFiles.map(
                  fileName => OpenLabCDSParser.parse(fileName)
            )
      }


      def transform( openLabCds : OutputOpenLabCDS , header_name_compound : List[String] , target_head : String)
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