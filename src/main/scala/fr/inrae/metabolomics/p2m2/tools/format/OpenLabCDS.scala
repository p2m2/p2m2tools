package fr.inrae.metabolomics.p2m2.tools.format

import fr.inrae.metabolomics.p2m2.tools.format.OpenLabCDS.HeaderField.HeaderField

object OpenLabCDS {

  object HeaderField extends Enumeration {
    type HeaderField = Value
    val Sample_Name = Value
  }
}
/**
 */

case class OpenLabCDS(
                      origin : String,
                      header : Map[HeaderField,String] = Map[HeaderField,String](),
                      // list of Name Compound/ Area/etc....
                      results : List[Map[String, String]] = List()
               )



