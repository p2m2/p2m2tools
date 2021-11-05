package fr.inrae.metabolomics.p2m2.tools.format.output

import fr.inrae.metabolomics.p2m2.tools.format.output.OutputGCMS.HeaderField.HeaderField

object OutputGCMS {
  object HeaderField extends Enumeration {
    type HeaderField = Value
    val Data_File_Name, Output_Date, Output_Time = Value
  }
}

/**
 * Categories management :
 * [Header] map key, value
 * [MS_Quantitative_Results] Array of results
 */

case class OutputGCMS(
                      origin : String,
                      header : Map[HeaderField,String] = Map[HeaderField,String](),
                      ms_quantitative_results : List[Map[String, String]] = List()
               )
//List[Map[MS_Quantitative_ResultsField.MS_Quantitative_ResultsField, String]] = List()



