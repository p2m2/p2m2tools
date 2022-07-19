package fr.inrae.metabolomics.p2m2.tools.format

import fr.inrae.metabolomics.p2m2.tools.format.Isocor.CompoundIsocor
import fr.inrae.metabolomics.p2m2.tools.format.Isocor.HeaderField.HeaderField

import scala.collection.immutable.Map

object Isocor {
  object HeaderField extends Enumeration {
    type HeaderField = Value
    val sample, metabolite, derivative, isotopologue, area, resolution = Value
  }

  case class CompoundIsocor(values : Map[HeaderField, String]) {
    def string(sep: String = "\t"): String =
      values.getOrElse(HeaderField.sample,"") + sep +
        values.getOrElse(HeaderField.metabolite,"") + sep +
        values.getOrElse(HeaderField.derivative,"") + sep +
        values.getOrElse(HeaderField.isotopologue,"") + sep +
        values.getOrElse(HeaderField.area,"") + sep +
        values.getOrElse(HeaderField.resolution,"")
  }
}

case class Isocor(origin : String, results : Seq[CompoundIsocor] = List()) extends MassSpectrometryResultSet
