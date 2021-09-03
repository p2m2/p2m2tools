package fr.inrae.metabolomics.p2m2
import fr.inrae.metabolomics.p2m2.converter.GCMSOutputFiles2IsocorInput

import java.io.{BufferedWriter, File, FileWriter}



case object Command extends App {
import scopt.OParser

  case class Config(
                     foo: Int = -1,
                     out: File = new File("./isocor_input_"+java.time.LocalDate.now+".tsv"),
                     resolution: Int = 2000,
                     separator: String = "_",
                     verbose: Boolean = false,
                     debug: Boolean = false,
                     files: Seq[File] = Seq())

val builder = OParser.builder[Config]
val parser1 = {
import builder._
OParser.sequence(
  programName("gcms2isocor"),
  head("gcms2isocor", "1.0"),
  opt[File]('o', "out")
    .required()
    .valueName("<file>")
    .action((x, c) => c.copy(out = x))
    .text("out is a required file property"),
  opt[Int]("resolution")
    .optional()
    .action({ case (r , c) => c.copy(resolution = r) })
    .validate(x =>
      if (x > 0) success
        else failure("Value <resolution> must be >0"))
    .valueName("<resolution>")
    .text("resolution for isocor tool."),
  opt[String]("separator")
    .optional()
    .action({ case (r , c) => c.copy(separator = r) })
    .valueName("<separator>")
    .text("separator *name* to extract [Metabolite],[Derivative],[Isotopologue]"),
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
  GCMS2Isocor(config.files,config.out,config.resolution,config.separator)
case _ =>
// arguments are bad, error message will have been displayed
  System.err.println("exit with error.")
}

  def GCMS2Isocor(files: Seq[File], output : File, resolution : Int, separator : String): Unit = {
    val bw = new BufferedWriter(new FileWriter(new File(output.getPath)))

    bw.write("sample\tmetabolite\tderivative\tisotopologue\tarea\tresolution\n")

    val pro = GCMSOutputFiles2IsocorInput(resolution,separator)

    pro
      .build(files.map( _.getPath ))
      .map( pro.transform )
      .foreach(lines => lines.foreach( l => { bw.write(l); bw.write("\n") } ) )

    bw.close()
  }

}
