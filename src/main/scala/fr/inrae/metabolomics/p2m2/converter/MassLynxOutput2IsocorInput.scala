package fr.inrae.metabolomics.p2m2.converter

import fr.inrae.metabolomics.p2m2.format.Isocor.CompoundIsocor
import fr.inrae.metabolomics.p2m2.format.QuantifySummaryReportMassLynx.HeaderField
import fr.inrae.metabolomics.p2m2.format.QuantifySummaryReportMassLynx.HeaderField.HeaderField
import fr.inrae.metabolomics.p2m2.format.conversions.FormatConversions
import fr.inrae.metabolomics.p2m2.parser.QuantifySummaryReportMassLynxParser
import fr.inrae.metabolomics.p2m2.format.{Isocor, QuantifyCompoundSummaryReportMassLynx, QuantifySummaryReportMassLynx}

case class MassLynxOutput2IsocorInput(
                                       derivatives : Map[String,String],
                                       formula : Map[String,String],
                                       resolution : Int = 2000,
                                       listSampleToRemove : Seq[String] = Seq("NH4")
                          ) {

      def build(inputFiles : Seq[String]) : Seq[QuantifySummaryReportMassLynx] = {
            println(inputFiles.mkString("\n"))

            inputFiles.map(
                  fileName => QuantifySummaryReportMassLynxParser.parse(fileName)
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
      def transform( massLynxGen : QuantifySummaryReportMassLynx) : Isocor = {
            val massLynx : QuantifyCompoundSummaryReportMassLynx = FormatConversions.MassLynxToQCSRMassLynx(massLynxGen)
            Isocor(
              massLynx.origin,
              massLynx
              .resultsByCompound
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