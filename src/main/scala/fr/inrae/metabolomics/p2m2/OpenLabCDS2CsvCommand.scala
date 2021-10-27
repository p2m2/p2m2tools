package fr.inrae.metabolomics.p2m2

import fr.inrae.metabolomics.p2m2.converter.OpenLabCDS2CompilCsv
import fr.inrae.metabolomics.p2m2.tools.format.output.OutputOpenLabCDS
import fr.inrae.metabolomics.p2m2.tools.format.output.OutputOpenLabCDS.HeaderField

import java.io.{BufferedWriter, File, FileWriter}


case object OpenLabCDS2CsvCommand extends App {
import scopt.OParser

  case class Config(
                     out: File = new File("./summary_reports.xlsx"),
                     column: String = "Area",
                     verbose: Boolean = false,
                     debug: Boolean = false,
                     files: Seq[File] = Seq())

val builder = OParser.builder[Config]
val parser1 = {
import builder._
OParser.sequence(
  programName("openlabscds2xls"),
  head("openlabscds2xls", "1.0"),
  opt[File]('o', "out")
    .required()
    .valueName("<file>")
    .action((x, c) => c.copy(out = x))
    .text("out is a required file property"),
  opt[String]("column")
    .optional()
    .action({ case (r , c) => c.copy(column = r) })
    .valueName("<column>")
    .text("column *name* to extract (RetTime,Type,ISTD,Area,Amt/Area,Amount,Grp,Name)"),
  opt[Unit]("verbose")
    .action((_, c) => c.copy(verbose = true))
    .text("verbose is a flag"),
  opt[Unit]("debug")
  .hidden()
  .action((_, c) => c.copy(debug = true))
  .text("this option is hidden in the usage text"),

  help("help").text("prints this usage text"),
  arg[File]("<file>...")
  .unbounded()
  .optional()
  .action((x, c) => c.copy(files = c.files :+ x))
  .text("optional unbounded args"),
  note("some notes." + sys.props("line.separator")),
  checkConfig( _  => success)
  )
}

// OParser.parse returns Option[Config]
OParser.parse(parser1, args, Config()) match {
case Some(config) =>
// do something
  println(config)
  OpenLabCDS2Csv(config.files,config.out,config.column)
case _ =>
// arguments are bad, error message will have been displayed
  System.err.println("exit with error.")
}

  def OpenLabCDS2Csv(files: Seq[File], output : File, column : String): Unit = {
    val bw = new BufferedWriter(new FileWriter(new File(output.getPath)))

   // bw.write("sample\tmetabolite\tderivative\tisotopologue\tarea\tresolution\n")
    println(files)
    println(output)

    val pro = OpenLabCDS2CompilCsv(column)

    val lopenlabs_results = pro
      .build(files.map( _.getPath ))

    val header = lopenlabs_results.flatMap( (ol : OutputOpenLabCDS) => pro.header_name_compound(ol) ).distinct.toList
    println("--- header --- ")
    println(header)
    bw.write(";"+header.mkString(";")+"\n")
    lopenlabs_results
      .map( ol => { (ol.header.getOrElse(HeaderField.Sample_Name,"UNKOWN"),pro.transform(ol,header)) } )
      .foreach((sampleName_lines : (String,List[Serializable]) )=> {
        val lines = sampleName_lines._2
        bw.write(sampleName_lines._1+";")
        lines.foreach( (l : Serializable )=> l match {
          case Some(v) => bw.write(v.toString+";")
          case _ => bw.write(";")
        })
        bw.write("\n")
      })

    bw.close()
    println("output file:"+output.getPath)
  }

}
