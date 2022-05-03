package fr.inrae.metabolomics.p2m2.converter

import fr.inrae.metabolomics.p2m2.parser.MassLynxParser
import fr.inrae.metabolomics.p2m2.tools.format.input.InputIsocor
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
          val Pattern = "\\S[0-9]*".r
          val it = Pattern
            .findAllMatchIn(formula)
            .filter( _.group(0).startsWith(element.toString))
            .map( v => {
              v.group(0).replace(element.toString,"") match {
                case v if v.length <= 0 => 1
                case v => v.toInt
              }
            })
          if (it.isEmpty) 0 else it.next()
        case None => 0
      }

        //sample	metabolite	derivative	isotopologue	area	resolution
      def transform( massLynx : OutputMassLynx) : Seq[InputIsocor] = {

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
                                      (compoundType match {
                                        case "M+H" =>
                                          Some(List(
                                            InputIsocor(
                                              field.Name,metabolite,fieldName,0,field.Area,resolution.toString)))//.mkString("\t")
                                        case v if v.startsWith("M+") => Some(List(InputIsocor(
                                          field.Name,
                                          metabolite,
                                          fieldName,
                                          v.replace("M+", "").toInt,
                                          field.Area,
                                          resolution.toString
                                        ))) //.mkString("\t")
                                        case _ => None
                                      })
                                        //.filter( (s: List[InputIsocor]) => s.isotopologue<=threshold)
                                    case None => None
                                  }
                                })
                      else List()
              }
              .fold(List()) { (l1, l2) =>  l1 ++ l2  }
      }
}