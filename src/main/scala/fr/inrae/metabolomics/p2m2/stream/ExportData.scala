package fr.inrae.metabolomics.p2m2.stream

import org.apache.poi.hssf.usermodel.HSSFWorkbook
import fr.inrae.metabolomics.p2m2.format.GenericP2M2
import fr.inrae.metabolomics.p2m2.format.conversions.FormatConversions
import fr.inrae.metabolomics.p2m2.parser.ParserUtils
import org.apache.poi.ss.usermodel.Cell

import java.io.ByteArrayOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatterBuilder
import java.sql.Timestamp
import java.util.Locale

case object ExportData {
  case class GenericP2M2Extended(
                                  samples: Seq[Map[GenericP2M2.HeaderField.HeaderField, String]] = Seq(),
                                  chromatographs: Seq[Map[GenericP2M2.HeaderFieldChromatogram.HeaderFieldChromatogram, String]] = Seq())

  def getTimeStamp(x: String): String = {
    Timestamp.valueOf(
      LocalDateTime.parse(x, new DateTimeFormatterBuilder()
        .appendPattern(FormatConversions.formatGenericP2M2).toFormatter(Locale.FRANCE))).getTime.toString
  }

  def getIdChromatograph(acquisitionDate: Option[String], exportDate: Option[String]): String =
    (acquisitionDate, exportDate) match {
      case (Some(v1), Some(v2)) => getTimeStamp(v1) + "_" + getTimeStamp(v2)
      case (None, Some(v)) => "DATE_ACQ_UNKNOWN_" + getTimeStamp(v)
      case (Some(v), None) => getTimeStamp(v) + "_DATE_EXP_UNKNOWN"
      case (None, None) => "DATE_ACQ_UNKNOWN_DATE_EXP_UNKNOWN"
    }

  def decorWithChromatograph(o: GenericP2M2): GenericP2M2Extended = {
    GenericP2M2Extended(
      samples = o.samples.zipWithIndex.map {
        case (values: Map[GenericP2M2.HeaderField.HeaderField, String], idx: Int) =>
          val chromatographInjectionId = getIdChromatograph(values.get(GenericP2M2.HeaderField.acquisitionDate),
            values.get(GenericP2M2.HeaderField.exportDate))
          values + (GenericP2M2.HeaderField.chromatographInjectionId -> chromatographInjectionId,
            GenericP2M2.HeaderField.ID -> (idx + "_" + chromatographInjectionId))
      },
      chromatographs = o.samples.map(
        values => {
          Map(
            GenericP2M2.HeaderFieldChromatogram.chromatographInjectionId ->
              Some(getIdChromatograph(values.get(GenericP2M2.HeaderField.acquisitionDate),
                values.get(GenericP2M2.HeaderField.exportDate))),
            GenericP2M2.HeaderFieldChromatogram.vial -> values.get(GenericP2M2.HeaderField.vial),
            GenericP2M2.HeaderFieldChromatogram.exportDate -> values.get(GenericP2M2.HeaderField.exportDate),
            GenericP2M2.HeaderFieldChromatogram.acquisitionDate -> values.get(GenericP2M2.HeaderField.acquisitionDate),
            GenericP2M2.HeaderFieldChromatogram.injectedVolume -> values.get(GenericP2M2.HeaderField.injectedVolume)
          ).flatMap {
            case (k, Some(v)) => Some(k, v)
            case _ => None
          }
        }
      )
    )
  }
  def xlsP2M2(resultsSet : GenericP2M2): ByteArrayOutputStream = {

    val resultsSetExtended: GenericP2M2Extended  = decorWithChromatograph(resultsSet)

    val wb = new HSSFWorkbook
    val results = wb.createSheet("RESULTS")
    val samples = wb.createSheet("SAMPLES")
    val metabolites = wb.createSheet("METABOLITES")
    val chromatogram = wb.createSheet("CHROMATOGRAMS")


    val createHelper = wb.getCreationHelper

    /**
     * header
     */
    val rowChromatogramHeader = results.createRow(0)

    GenericP2M2.HeaderField.values.zipWithIndex.foreach {
      case (headName,idx) =>
        rowChromatogramHeader.createCell(idx).setCellValue(createHelper.createRichTextString(ParserUtils.toString(headName)))
    }

    /*TODO manage format
    import org.apache.poi.ss.usermodel.CellStyle
val cellStyle: CellStyle = wb.createCellStyle
cellStyle.setDataFormat(createHelper.createDataFormat.getFormat("m/d/yy h:mm"))
cell = row.createCell(1)
cell.setCellValue(new Nothing)
cell.setCellStyle(cellStyle)
     *
     */

    /**
     * values
     */
    resultsSetExtended.samples.toList.sortBy(_.get(GenericP2M2.HeaderField.sample)).zipWithIndex.foreach {
      case (acquisition: Map[GenericP2M2.HeaderField.HeaderField,String], idx : Int) =>
        val row = results.createRow(idx+1)

        GenericP2M2.HeaderField.values.zipWithIndex.foreach {
          case (headName,idxCell) =>
            val cell : Cell = row.createCell(idxCell)
            cell.setCellValue(createHelper.createRichTextString(acquisition.getOrElse(headName,"")))
        }
    }

    /**
     * Samples
     */

    samples
      .createRow(0)
      .createCell(0)
      .setCellValue(createHelper.createRichTextString("SAMPLE"))

    resultsSetExtended.samples.toList.sortBy(_.get(GenericP2M2.HeaderField.sample)).flatMap {
      acquisition: Map[GenericP2M2.HeaderField.HeaderField, String] =>
        acquisition.get(GenericP2M2.HeaderField.sample)
    }.distinct.zipWithIndex.foreach {
      case (sample : String,idx : Int) =>
        val row = samples.createRow(idx+1)
        val cell : Cell = row.createCell(0)
        cell.setCellValue(createHelper.createRichTextString(sample))
    }

    /**
     * Compound
     */
    metabolites
      .createRow(0)
      .createCell(0)
      .setCellValue(createHelper.createRichTextString("METABOLITE"))

    resultsSetExtended.samples.toList.sortBy(_.get(GenericP2M2.HeaderField.metabolite)).flatMap {
      acquisition: Map[GenericP2M2.HeaderField.HeaderField, String] =>
        acquisition.get(GenericP2M2.HeaderField.metabolite)
    }.distinct.zipWithIndex.foreach {
      case (metabolite : String,idx : Int) =>
        val row = metabolites.createRow(idx+1)
        val cell : Cell = row.createCell(0)
        cell.setCellValue(createHelper.createRichTextString(metabolite))
    }

    /**
     * Injection / Chromatogram
     */
    val row = chromatogram.createRow(0)

    GenericP2M2.HeaderFieldChromatogram.values.zipWithIndex.foreach {
      case (headName, idxCell) =>
        val cell: Cell = row.createCell(idxCell)
        cell.setCellValue(createHelper.createRichTextString(ParserUtils.toString(headName)))
    }

    resultsSetExtended.chromatographs.distinct.zipWithIndex.foreach {
      case (x : Map[GenericP2M2.HeaderFieldChromatogram.HeaderFieldChromatogram,String],idx : Int) =>
        val row = chromatogram.createRow(idx+1)

        GenericP2M2.HeaderFieldChromatogram.values.zipWithIndex.foreach {
          case (headName, idxCell) =>

            val cell: Cell = row.createCell(idxCell)
            cell.setCellValue(createHelper.createRichTextString(x.getOrElse(headName, "")))
        }


    }

    val baos = new ByteArrayOutputStream
    wb.write(baos)
    baos

  }
}
