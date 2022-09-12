package fr.inrae.metabolomics.p2m2.stream
import upickle.default._

object GenericP2M2FormatExtended {
  object HeaderField extends Enumeration {
    type HeaderField = Value
    val
    ID, /* build during conversion */
    sample,
    metabolite,
    retTime,
    area,
    height,
    injectedVolume,
    vial,
    acquisitionDate,
    exportDate,
    chromatographInjectionId /* build during conversion */
    = Value
  }

  object HeaderFieldChromatogram extends Enumeration {
    type HeaderFieldChromatogram = Value
    val
    chromatographInjectionId,
    vial,
    exportDate,
    acquisitionDate,
    injectedVolume = Value
  }
}

case class GenericP2M2FormatExtended(
                                samples: Seq[Map[GenericP2M2FormatExtended.HeaderField.HeaderField, String]] = Seq(),
                                chromatographs:
                                Seq[Map[GenericP2M2FormatExtended.
                                HeaderFieldChromatogram.HeaderFieldChromatogram, String]] = Seq())