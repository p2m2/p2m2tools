package fr.inrae.metabolomics.p2m2.tools.format.output

import fr.inrae.metabolomics.p2m2.tools.format.output.OutputOpenLabCDS.HeaderField.HeaderField

object OutputOpenLabCDS {

  object HeaderField extends Enumeration {
    type HeaderField = Value
    val Sample_Name = Value
  }
}
/**
 */

case class OutputOpenLabCDS(
                      origin : String,
                      header : Map[HeaderField,String] = Map[HeaderField,String](),
                      // list of Name Compound/ Area/etc....
                      results : List[Map[String, String]] = List()
               )



