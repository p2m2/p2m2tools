package fr.inrae.metabolomics.p2m2.converter

import fr.inrae.metabolomics.p2m2.parser.MassLynxParser
import fr.inrae.metabolomics.p2m2.tools.format.output.OutputMassLynx
import fr.inrae.metabolomics.p2m2.tools.format.output.OutputMassLynx.SampleField

case class MassLynxOutput2IsocorInput(
                                       derivatives : Map[String,String],
                                       formula : Map[String,String],
                                       resolution : Int = 2000,
                                       listSampleToRemove : Seq[String] = Seq("NH4")
                          ) {

      def build(inputFiles : Seq[String]) : Seq[OutputMassLynx] = {
            println(inputFiles.mkString("\n"))

            inputFiles.map(
                  fileName => MassLynxParser.parse(fileName)
            )
      }

      def getNumberElementFromFormula(metabolite: String, element: Char) : Int = formula.get(metabolite) match {
        case Some(formula) =>
          val idx : Int = formula.indexOf(element)
          formula.zipWithIndex
            .filter{ case (_,k) => k>idx}
            .filter{ case (v,_) => val n = v.toInt - '0'.toInt; (n>=0)&&(n<=9) }
            .map( _._1 )
          match {
            case s if s.nonEmpty => s.mkString("").toInt
            case _ => 1
          }
        case None => 0
      }

        //sample	metabolite	derivative	isotopologue	area	resolution
      def transform( massLynx : OutputMassLynx  ) : List[String] = {

            massLynx
              .results
              .flatMap  {
                    case (sample_and_compType : String , listMetabolites : List[SampleField])  =>
                      val metabolite = sample_and_compType.split(",").head.trim
                      val compoundType = sample_and_compType.split(",").last.trim
                      if (! listSampleToRemove.contains(metabolite))
                        listMetabolites
                          .flatMap(
                                (field: SampleField) => {
                                  derivatives.get(metabolite) match {
                                    case Some(fieldName) =>
                                      compoundType match {
                                        case "M+H" => Some(List(
                                          field.Name,
                                          metabolite,
                                          fieldName,
                                          0,
                                          field.Area,
                                          resolution.toString
                                        ).mkString("\t"))
                                        case v if v.startsWith("M+") => Some(List(
                                          field.Name,
                                          metabolite,
                                          fieldName,
                                          v.replace("M+", "").toInt,
                                          field.Area,
                                          resolution.toString
                                        ).mkString("\t"))
                                        case _ => None
                                      }
                                    case None => None
                                  }
                                })
                      else List()
              }
      }
}