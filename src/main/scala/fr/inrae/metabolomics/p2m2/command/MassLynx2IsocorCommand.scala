package fr.inrae.metabolomics.p2m2.command

import fr.inrae.metabolomics.p2m2.converter.MassLynxOutput2IsocorInput

import java.io.{BufferedWriter, File, FileWriter}
import scala.io.Source

case object MassLynx2IsocorCommand extends App {

  import scopt.OParser

  case class Config(
                     out: File = new File("./isocor_input.tsv"),
                     resolution: Int = 2000,
                     derivatives : Option[File] = None,
                     separatorDerivativesFile : String = " ",
                     defaultDerivative: String = "ACCQTAG",
                     listSampleToRemove : Seq [String] = Seq("NH4"),
                     verbose: Boolean = false,
                     debug: Boolean = false,
                     files: Seq[File] = Seq())

  val builder = OParser.builder[Config]
  val parser1 = {
    import builder._
    OParser.sequence(
      programName("masslynx2isocor"),
      head("masslynx2isocor", "1.0"),
      opt[File]('o', "out")
        .required()
        .valueName("<file>")
        .action((x, c) => c.copy(out = x))
        .text("out is a required file property"),
      opt[File]('d', "derivatives")
        .optional()
        .valueName("<file>")
        .action((x, c) => c.copy(derivatives = Some(x)))
        .text("derivatives is a required file property"),
      opt[String]("separator_derivatives_file")
        .optional()
        .action({ case (r, c) => c.copy(separatorDerivativesFile = r) })
        .valueName("<separator_derivatives_file>")
        .text("separator_derivatives_file value"),
      opt[Int]("resolution")
        .optional()
        .action({ case (r, c) => c.copy(resolution = r) })
        .validate(x =>
          if (x > 0) success
          else failure("Value <resolution> must be >0"))
        .valueName("<resolution>")
        .text("resolution for isocor tool."),
      opt[String]("default_derivative")
        .optional()
        .action({ case (r, c) => c.copy(defaultDerivative = r) })
        .valueName("<default_derivative>")
        .text("default_derivative value"),
      opt[String]("list_sample_to_remove")
        .optional()
        .action({ case (r, c) => c.copy(listSampleToRemove = r.split(",")) })
        .valueName("<list_sample_to_remove>")
        .text("list_sample_to_remove NH4,..."),
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
      checkConfig(_ => success)
    )
  }

  // OParser.parse returns Option[Config]
  OParser.parse(parser1, args, Config()) match {
    case Some(config) =>
      // do something
      println(config)
      MassLynx2Isocor(
        config.files,
        config.out,
        config.derivatives,
        config.separatorDerivativesFile,
        config.resolution,
        config.defaultDerivative,
        config.listSampleToRemove
      )
    case _ =>
      // arguments are bad, error message will have been displayed
      System.err.println("exit with error.")
  }

  def MassLynx2Isocor(files: Seq[File],
                      output: File,
                      derivatives : Option[File],
                      separatorDerivativesFile : String,
                      resolution: Int,
                      defaultDerivative: String,
                      listSampleToRemove : Seq[String]
                     ): Unit = {

    /* reading tabular file with two colum COMPOUND / DERIVATIVE */
    val correspondence : Map[String,String] = derivatives match  {
      case Some(f) =>  Source.fromFile(f)
        .getLines()
        .map(_.split(separatorDerivativesFile))
        .map( x => (x(0), x(1)) ).toMap
      case None => Map()
    }


    val bw = new BufferedWriter(new FileWriter(new File(output.getPath)))

    bw.write("sample\tmetabolite\tderivative\tisotopologue\tarea\tresolution\n")

    val pro = MassLynxOutput2IsocorInput(correspondence,resolution, listSampleToRemove,defaultDerivative )

    pro
      .build(files.map(_.getPath))
      .map(pro.transform)
      .foreach(lines => lines.foreach(l => {
        bw.write(l); bw.write("\n")
      }))

    bw.close()
    println("output file:" + output.getPath)
  }

}
