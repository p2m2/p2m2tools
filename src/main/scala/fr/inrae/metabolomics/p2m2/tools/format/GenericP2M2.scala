package fr.inrae.metabolomics.p2m2.tools.format

object GenericP2M2 {
  object HeaderField extends Enumeration {
    type HeaderField = Value
    val Data_File_Name, Output_Date, Output_Time = Value
  }
}

case class GenericP2M2() extends MassSpectrometryResultSet
