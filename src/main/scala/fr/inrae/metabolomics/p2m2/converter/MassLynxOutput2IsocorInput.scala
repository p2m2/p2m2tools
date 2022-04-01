package fr.inrae.metabolomics.p2m2.converter

import fr.inrae.metabolomics.p2m2.parser.MassLynxParser
import fr.inrae.metabolomics.p2m2.tools.format.output.OutputMassLynx
import fr.inrae.metabolomics.p2m2.tools.format.output.OutputMassLynx.CompoundField

case class MassLynxOutput2IsocorInput(
                            correspondenceMetabolitesDerivative : Map[String,String],
                            resolution : Int = 2000 ,
                            listSampleToRemove : Seq[String] = Seq("NH4"),
                            defaultDerivative : String = "ACCQTAG"
                          ) {

      def build(inputFiles : Seq[String]) : Seq[OutputMassLynx] = {
            println(inputFiles.mkString("\n"))

            inputFiles.map(
                  fileName => MassLynxParser.parse(fileName)
            )
      }

        //sample	metabolite	derivative	isotopologue	area	resolution
      def transform( massLynx : OutputMassLynx  ) : List[String] = {

            massLynx
              .results
              .flatMap  {
                    case (sample_and_compType : String , listMetabolites : List[CompoundField])  => {
                          val sample = sample_and_compType.split(",").head.trim
                          val compoundType = sample_and_compType.split(",").last.trim
                          if (! listSampleToRemove.contains(sample))
                            listMetabolites
                              .flatMap(
                                    (field: CompoundField) => {
                                      (compoundType match {
                                        case "M+H" => Some(List(
                                          field.Name,
                                          sample,
                                          correspondenceMetabolitesDerivative.getOrElse(field.Name,defaultDerivative),
                                          0,
                                          field.Area,
                                          resolution.toString
                                        ).mkString("\t"))
                                        case v if v.startsWith("M+") => Some(List(
                                          field.Name,
                                          sample,
                                          correspondenceMetabolitesDerivative.getOrElse(field.Name,defaultDerivative),
                                          v.replace("M+", "").toInt,
                                          field.Area,
                                          resolution.toString
                                        ).mkString("\t"))
                                        case _ => None
                                      })
                                    })
                          else List()
                    }
              }
      }
}