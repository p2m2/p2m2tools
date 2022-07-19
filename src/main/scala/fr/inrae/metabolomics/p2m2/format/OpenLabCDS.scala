package fr.inrae.metabolomics.p2m2.format

import OpenLabCDS.HeaderField.HeaderField
import OpenLabCDS.HeaderFileField.HeaderFileField

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



