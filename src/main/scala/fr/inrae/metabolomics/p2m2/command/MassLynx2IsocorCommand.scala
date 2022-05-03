package fr.inrae.metabolomics.p2m2.command

import fr.inrae.metabolomics.p2m2.converter.MassLynxOutput2IsocorInput
import fr.inrae.metabolomics.p2m2.tools.format.input.InputIsocor

import java.io.{BufferedWriter, File, FileWriter}
import scala.io.Source

case object MassLynx2IsocorCommand extends App {

  import scopt.OParser

  case class Config(
                     out_13C: File = new File("./isocor_input_13C.tsv"),
                     out_15N: File = new File("./isocor_input_15N.tsv"),
                     resolution: Int = 2000,
                     derivatives : Option[File] = None,
                     metabolites : Option[File] = None,
                     separatorDerivativesFile : String = "[ \t;,]",
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
      opt[File]( "out_13C")
        .optional()
        .valueName("<file>")
        .action((x, c) => c.copy(out_13C = x))
        .text("out_13C is a optional file property"),
      opt[File]( "out_15N")
        .optional()
        .valueName("<file>")
        .action((x, c) => c.copy(out_15N = x))
        .text("out_15N is a optional file property"),
      opt[File]('d', "derivatives")
        .required()
        .valueName("<file>")
        .action((x, c) => c.copy(derivatives = Some(x)))
        .text("derivatives is a required file property"),
      opt[File]('m', "metabolites")
        .required()
        .valueName("<file>")
        .action((x, c) => c.copy(metabolites = Some(x)))
        .text("metabolites isocor file property"),
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
        config.out_13C,
        config.out_15N,
        config.derivatives,
        config.metabolites,
        config.separatorDerivativesFile,
        config.resolution,
        config.listSampleToRemove
      )
    case _ =>
      // arguments are bad, error message will have been displayed
      System.err.println("exit with error.")
  }
  def build_results_element(pro: MassLynxOutput2IsocorInput,
                            lists: Seq[InputIsocor],
                            element : Char) : Seq[InputIsocor] = {

    lists.filter((in : InputIsocor) => {
            //println(in.metabolite,in.isotopologue, pro.getNumberElementFromFormula(in.metabolite,element))
            in.isotopologue <= pro.getNumberElementFromFormula(in.metabolite,element)
      })
  }

  def MassLynx2Isocor(files: Seq[File],
                      output_13C: File,
                      output_15N: File,
                      derivatives : Option[File],
                      metabolites : Option[File],
                      separatorDerivativesFile : String,
                      resolution: Int,
                      listSampleToRemove : Seq[String]
                     ): Unit = {

    /* reading tabular file with two colum COMPOUND / DERIVATIVE */
    val correspondence : Map[String,String] = derivatives match  {
      case Some(f) =>  Source.fromFile(f)
        .getLines()
        .filter( _.trim.nonEmpty )
        .map(_.split(separatorDerivativesFile))
        .map( x => { if ( x.length != 2 ) {
          System.err.println("bad definition of '"+f+"'\n------------------\n"+x.mkString(":")+"'\n------------------\n")
          System.err.println("use [TABULATION] !\n")
          throw new RuntimeException("Bad [Derivatives] definition file.")
        }; x })
        .map( x =>  (x(0), x(1))  ).toMap
      case None => Map()
    }

    val formula : Map[String,String] = metabolites match  {
      case Some(f) =>  Source.fromFile(f)
        .getLines()
        .drop(1)
        .filter( _.trim.nonEmpty )
        .map(_.split("\t"))
        .map( x =>  (x(0), x(1))  ).toMap
      case None => Map()
    }
    println("=================FORMULA ==================")
    println(formula)

    val pro = MassLynxOutput2IsocorInput(correspondence,formula,resolution, listSampleToRemove)
    val list:  Seq[InputIsocor] = pro.build(files.map(_.getPath)).map(pro.transform(_)).flatten

    //======================================= output_13C =================================================
    val bw = new BufferedWriter(new FileWriter(new File(output_13C.getPath)))

    bw.write("sample\tmetabolite\tderivative\tisotopologue\tarea\tresolution\n")
   build_results_element(pro, list, 'C')
    .foreach( l => {
      bw.write(l.string()); bw.write("\n")
    })
    bw.close()

    println("output_13C file:" + output_13C.getPath)

    //======================================= output_15N =================================================
    val bw2 = new BufferedWriter(new FileWriter(new File(output_15N.getPath)))
    bw2.write("sample\tmetabolite\tderivative\tisotopologue\tarea\tresolution\n")
    build_results_element(pro, list, 'N').foreach( l => {
      bw2.write(l.string()); bw2.write("\n")
    })
    bw2.close()

    println("output_15N file:" + output_15N.getPath)
  }

}
