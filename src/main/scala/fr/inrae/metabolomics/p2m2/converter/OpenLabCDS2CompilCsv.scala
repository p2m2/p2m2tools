package fr.inrae.metabolomics.p2m2.converter

import fr.inrae.metabolomics.p2m2.parser.OpenLabCDSParser
import fr.inrae.metabolomics.p2m2.tools.format.OpenLabCDS
import fr.inrae.metabolomics.p2m2.tools.format.OpenLabCDS.HeaderField
import fr.inrae.metabolomics.p2m2.tools.format.OpenLabCDS.HeaderField.HeaderField

import scala.util.{Failure, Success, Try}

case class OpenLabCDS2CompilCsv(target_head : String ) {

      def build(inputFiles : Seq[String]) : Seq[OpenLabCDS] = {
            println(inputFiles.mkString("\n"))

            inputFiles.map(
                  fileName => OpenLabCDSParser.parse(fileName)
            )
      }


      def header_name_compound(openLabCds : OpenLabCDS) : List[String] = {
            openLabCds.results.flatMap( (mapResults: Map[HeaderField, String]) => mapResults.get(HeaderField.Name))
      }

      def transform(openLabCds : OpenLabCDS, header_name_compound : List[String] )
      : List[Serializable] = {
            //val sample = openLabCds.origin.split("[/\\\\]").last.split("\\.[a-zA-Z]+$").head

            header_name_compound.map(
                  compound => {
                        val l1 =
                              openLabCds.
                                results.
                                filter((mapResults: Map[HeaderField, String]) => mapResults.getOrElse(HeaderField.Name,"") == compound)

                        Try(l1.head.get(HeaderField.Name) match {
                              case Some(nameCompound) if nameCompound == compound
                                    => OpenLabCDS.getHeaderField(target_head) match {
                                    case Some(r) => l1.head.get(r)
                                    case _ => None
                              }
                              case _ => None
                        }) match {
                              case Success(v) => v
                              case Failure(_) => None
                        }

                  })
      }
}