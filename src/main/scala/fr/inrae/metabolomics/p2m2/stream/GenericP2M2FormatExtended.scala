package fr.inrae.metabolomics.p2m2.stream
import upickle.default._

object GenericP2M2FormatExtended {
  implicit val rw: ReadWriter[GenericP2M2FormatExtended] = macroRW

  object HeaderField extends Enumeration {
    implicit val rw: ReadWriter[HeaderField] = readwriter[Int].bimap[HeaderField](x => x.id, HeaderField(_))
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
    implicit val rw: ReadWriter[HeaderFieldChromatogram] =
      readwriter[Int].bimap[HeaderFieldChromatogram](x => x.id, HeaderFieldChromatogram(_))
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