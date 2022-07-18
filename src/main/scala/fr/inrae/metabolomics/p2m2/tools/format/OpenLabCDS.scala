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

  def getHeaderField(token : String) : Option[HeaderField] =
    HeaderField
      .values
      .find( _
        .toString
        .replace("$percent","%")
        .replace("$u002E",".")
        .replace("$u0020"," ")
        .replace("$div","/")
        .replace("$hash","#")
        .equalsIgnoreCase(token))
}
/**
 */

case class OpenLabCDS(
                       origin : String,
                       header : Map[HeaderFileField,String] = Map[HeaderFileField,String](),
                       // list of Name Compound/ Area/etc....
                       results : List[Map[HeaderField, String]] = List()
               )



