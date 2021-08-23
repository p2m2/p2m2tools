package fr.inrae.metabolomics.p2m2

import scala.io.Source

object GCMS2Isocor extends App {
      println("hello world")
      if (args.length>1) {
            val filename_output_isocor = args(0)
            println("output:"+filename_output_isocor)
            GCMS2Isocor(args.drop(1))
      } else {

      }
}

case class GCMS2Isocor(val gcms_inputfiles : Array[String]) {
      println("-------------")
      println("GCMS - MS Quantitative Results - files\n")
      println(gcms_inputfiles.mkString("\n"))
      parse(gcms_inputfiles(0))

      val echantillons : List[String] = List()

      def parse(filename : String) = {
            val lines = Source.fromFile(filename).getLines().toList

            val categories = lines.zipWithIndex map {
                  case (line: String, idx: Int) => line match {
                        case categoryC if categoryC.startsWith("[") && categoryC.endsWith("]") =>
                              (idx,categoryC.replaceFirst("\\[","").replaceFirst("\\]",""))
                        case _ =>
                  }
            }
            println(categories)
      }
}