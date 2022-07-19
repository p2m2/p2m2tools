package fr.inrae.metabolomics.p2m2.tools.format

import fr.inrae.metabolomics.p2m2.tools.format.OpenLabCDS.HeaderField.HeaderField
import fr.inrae.metabolomics.p2m2.tools.format.OpenLabCDS.HeaderFileField.HeaderFileField

object OpenLabCDS {

  object HeaderFileField extends Enumeration {
    type HeaderFileField = Value
    val Sample_Name = Value
  }

  object HeaderField extends Enumeration {
    type HeaderField = Value
    val  RetTime, Type, ISTD, Area, used, `Amt/Area`, Amount, Grp, Name = Value
  }
}
/**
 */

case class OpenLabCDS(
                       origin : String,
                       header : Map[HeaderFileField,String] = Map[HeaderFileField,String](),
                       // list of Name Compound/ Area/etc....
                       results : List[Map[HeaderField, String]] = List()
               ) extends MassSpectrometryResultSet



