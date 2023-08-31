package fr.inrae.metabolomics.p2m2.converter

import fr.inrae.metabolomics.p2m2.format.ms.OpenLabCDS.HeaderField
import fr.inrae.metabolomics.p2m2.format.ms.OpenLabCDS.HeaderField.HeaderField
import fr.inrae.metabolomics.p2m2.format.ms.OpenLabCDS
import fr.inrae.metabolomics.p2m2.parser.{OpenLabCDSParser, ParserUtils}

import scala.util.{Failure, Success, Try}

case class OpenLabCDS2CompilCsv(target_head : String ) {

      def build(inputFiles : Seq[String]) : Seq[OpenLabCDS] = {
            println(inputFiles.mkString("\n"))

            inputFiles.map(
                  fileName => OpenLabCDSParser.parseFile(fileName)
            )
      }


      def header_name_compound(openLabCds : OpenLabCDS) : Seq[String] = {
            openLabCds.results.flatMap( (mapResults: Map[HeaderField, String]) => mapResults.get(HeaderField.Name))
      }

      def transform(openLabCds : OpenLabCDS, headerNameCompound : List[String] )
      : List[Serializable] = {
            //val sample = openLabCds.origin.split("[/\\\\]").last.split("\\.[a-zA-Z]+$").head

            headerNameCompound.map(
                  compound => {
                        val l1 =
                              openLabCds.
                                results.
                                filter((mapResults: Map[HeaderField, String]) => mapResults.getOrElse(HeaderField.Name,"") == compound)
                        Try({
                              ParserUtils.getHeaderField(OpenLabCDS.HeaderField,target_head) match {
                                          case Some(r) => l1.head.get(r)
                                          case _ => None
                              }
                        }) match {
                              case Success(v) => v
                              case Failure(_) => None
                        }

                  })
      }
}