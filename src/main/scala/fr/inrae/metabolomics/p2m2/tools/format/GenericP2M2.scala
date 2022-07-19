package fr.inrae.metabolomics.p2m2.tools.format

import fr.inrae.metabolomics.p2m2.tools.format.GenericP2M2.HeaderField.HeaderField

object GenericP2M2 {
  object HeaderField extends Enumeration {
    type HeaderField = Value
    val
    sample,
    metabolite,
    retTime,
    area,
    height,
    injectedVolume,
    acquisitionDate = Value
  }
}

case class GenericP2M2(values : Seq[Map[HeaderField,String]]) extends MassSpectrometryResultSet
