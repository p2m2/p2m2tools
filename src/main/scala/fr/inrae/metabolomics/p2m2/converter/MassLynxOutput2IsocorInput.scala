package fr.inrae.metabolomics.p2m2.converter

import fr.inrae.metabolomics.p2m2.parser.QuantifyCompoundSummaryReportMassLynxParser
import fr.inrae.metabolomics.p2m2.tools.format.Isocor.CompoundIsocor
import fr.inrae.metabolomics.p2m2.tools.format.QuantifyCompoundSummaryReportMassLynx.HeaderField
import fr.inrae.metabolomics.p2m2.tools.format.{Isocor, QuantifyCompoundSummaryReportMassLynx}
import fr.inrae.metabolomics.p2m2.tools.format.QuantifyCompoundSummaryReportMassLynx.HeaderField.HeaderField

case class MassLynxOutput2IsocorInput(
                                       derivatives : Map[String,String],
                                       formula : Map[String,String],
                                       resolution : Int = 2000,
                                       listSampleToRemove : Seq[String] = Seq("NH4")
                          ) {

      def build(inputFiles : Seq[String]) : Seq[QuantifyCompoundSummaryReportMassLynx] = {
            println(inputFiles.mkString("\n"))

            inputFiles.map(
                  fileName => QuantifyCompoundSummaryReportMassLynxParser.parse(fileName)
            )
      }

      def getNumberElementFromFormula(metabolite: String, element: Char) : Int = formula.get(metabolite) match {
        case Some(formula) =>
          val Pattern = "\\S\\d*".r
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
      def transform( massLynx : QuantifyCompoundSummaryReportMassLynx) : Isocor = {
            Isocor(
              massLynx.origin,
              massLynx
              .results
              .flatMap  {
                    case (sample_and_compType : String , listMetabolites : List[Map[HeaderField,String]])  =>
                      val metabolite = sample_and_compType.split(",").head.trim
                      val compoundType = sample_and_compType.split(",").last.trim
                      if (! listSampleToRemove.contains(metabolite))
                        listMetabolites
                          .flatMap(
                                (field: Map[HeaderField,String]) => {
                                  derivatives.get(metabolite) match {
                                    case Some(fieldName) =>
                                      compoundType match {
                                        case "M+H" =>
                                          Some(List(
                                            CompoundIsocor(
                                              Map(
                                                Isocor.HeaderField.sample -> field.getOrElse(HeaderField.Name,"Unknown"),
                                                Isocor.HeaderField.metabolite -> metabolite,
                                                Isocor.HeaderField.derivative-> fieldName,
                                                Isocor.HeaderField.isotopologue -> "0",
                                                Isocor.HeaderField.area -> field.getOrElse(HeaderField.Area,"-1"),
                                                Isocor.HeaderField.resolution -> resolution.toString))
                                              )
                                             )//.mkString("\t")
                                        case v if v.startsWith("M+") => Some(List(CompoundIsocor(
                                          Map(
                                            Isocor.HeaderField.sample -> field.getOrElse(HeaderField.Name,"Unknown"),
                                            Isocor.HeaderField.metabolite -> metabolite,
                                            Isocor.HeaderField.derivative-> fieldName,
                                            Isocor.HeaderField.isotopologue-> v.replace("M+", ""),
                                            Isocor.HeaderField.area -> field.getOrElse(HeaderField.Area,"-1"),
                                            Isocor.HeaderField.resolution -> resolution.toString)
                                        ))) //.mkString("\t")
                                        case _ => None
                                      }
                                        //.filter( (s: List[InputIsocor]) => s.isotopologue<=threshold)
                                    case None => None
                                  }
                                })
                      else List()
              }
              .fold(List()) { (l1, l2) =>  l1 ++ l2  })
      }
}