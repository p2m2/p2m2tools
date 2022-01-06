package fr.inrae.metabolomics.p2m2.tools.format.output


import fr.inrae.metabolomics.p2m2.tools.format.output.OutputMassLynx.{CompoundField, Header}

import java.time.LocalDate
import java.time.format.DateTimeFormatterBuilder
import java.util.Locale

object OutputMassLynx {

  case class  Header(dateStr : Option[String] = None)  {
    val formatter = new DateTimeFormatterBuilder()
      .appendPattern("E MMM dd HH:mm:ss yyyy")
      .toFormatter(Locale.US)

    val PrintedDate: LocalDate = dateStr match {
      case Some(d) => LocalDate.parse(d.trim, formatter)
      case None => LocalDate.now()
    }

  }

  //Name	Trace	Type	Std. Conc	RT	Area	uM	%Dev	S/N	Vial	Height/Area	Acq.Date	Height
  case class CompoundField(
                            Name          : String,
                            Trace         : Int,
                            Type          : String,
                            `Std. Conc`   : String,
                            RT	          : Double,
                            Area          : Int,
                            uM            : String,
                            `%Dev`        : String,
                            `S/N`         : Int,
                            Vial          : String,
                            `Height/Area` : Double,
                            `Acq.Date`    : String,
                            Height        : Int )

  def buildCompoundField(map : Map[String,String] ) : CompoundField = {
    CompoundField(
      map.getOrElse("Name",""),
      map.getOrElse("Trace","-1").toInt,
      map.getOrElse("Type",""),
      map.getOrElse("Std. Conc",""),
      map.getOrElse("RT","0.0").toDouble,
      map.getOrElse("Area","-1").toInt,
      map.getOrElse("uM",""),
      map.getOrElse("%Dev",""),
      map.getOrElse("S/N","-1").toInt,
      map.getOrElse("Vial",""),
      map.getOrElse("Height/Area","0.0").toDouble,
      map.getOrElse("Acq.Date",""),
      map.getOrElse("Height","-1").toInt,
    )
  }
}


/**
 */

case class OutputMassLynx(
                      origin : String,
                      header : Header,
                      // list of Name Compound/ Area/etc....
                      results : List[(String,List[CompoundField])] = List()
               )



