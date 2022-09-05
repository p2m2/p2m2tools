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

  def getTimeStamp( x : String ) = {
    Timestamp.valueOf(
    LocalDateTime.parse(x, new DateTimeFormatterBuilder()
        .appendPattern(FormatConversions.formatGenericP2M2).toFormatter(Locale.US))).getTime().toString
  }

  def getIdChromatograph(acquisitionDate : Option[String],exportDate : Option[String]) : String =
    (acquisitionDate,exportDate) match {
      case (Some(v1), Some(v2)) => getTimeStamp(v1) + "_" + getTimeStamp(v2)
      case (None, Some(v)) => "DATE_ACQ_UNKNOWN_" + getTimeStamp(v)
      case (Some(v), None) => getTimeStamp(v) + "_DATE_EXP_UNKNOWN"
      case (None, None) => "DATE_ACQ_UNKNOWN_DATE_EXP_UNKNOWN"
  }

  def xlsP2M2(resultsSet : GenericP2M2): ByteArrayOutputStream = {

    val wb = new HSSFWorkbook
    val results = wb.createSheet("RESULTS")
    val samples = wb.createSheet("SAMPLES")
    val metabolites = wb.createSheet("METABOLITES")
    val chromatogram = wb.createSheet("CHROMATOGRAMS")


    val createHelper = wb.getCreationHelper

    /**
     * header
     */
    val row = results.createRow(0)


   // Artefact ID creation using chromatographInjectionId.
    row.createCell(0)
      .setCellValue(createHelper.createRichTextString("ID"))

    val idColumnStart = 1

    GenericP2M2.HeaderField.values.zipWithIndex.foreach {
      case (headName,idx) =>
        row.createCell(idx+idColumnStart).setCellValue(createHelper.createRichTextString(ParserUtils.toString(headName)))
    }
    // Artefact - chromatographInjectionId
    row.createCell(GenericP2M2.HeaderField.values.size+idColumnStart)
      .setCellValue(createHelper.createRichTextString("chromatographInjectionId"))

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
    resultsSet.values.toList.sortBy(_.get(GenericP2M2.HeaderField.sample)).zipWithIndex.foreach {
      case (acquisition: Map[GenericP2M2.HeaderField.HeaderField,String], idx : Int) =>
        val row = results.createRow(idx+1)

        val chromatographInjectionId = getIdChromatograph(
          acquisition.get(GenericP2M2.HeaderField.acquisitionDate),
          acquisition.get(GenericP2M2.HeaderField.exportDate))

        // Artefact - chromatographInjectionId
        row.createCell(0)
          .setCellValue(createHelper.createRichTextString(idx+"_"+chromatographInjectionId))

        GenericP2M2.HeaderField.values.zipWithIndex.foreach {
          case (headName,idxCell) =>
            val cell : Cell = row.createCell(idxCell+idColumnStart)
            cell.setCellValue(createHelper.createRichTextString(acquisition.getOrElse(headName,"")))
        }
        // Artefact - chromatographInjectionId
        row.createCell(GenericP2M2.HeaderField.values.size+idColumnStart)
          .setCellValue(createHelper.createRichTextString(chromatographInjectionId))
    }

    /**
     * Samples
     */

    samples
      .createRow(0)
      .createCell(0)
      .setCellValue(createHelper.createRichTextString("SAMPLE"))

    resultsSet.values.toList.sortBy(_.get(GenericP2M2.HeaderField.sample)).flatMap {
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

    resultsSet.values.toList.sortBy(_.get(GenericP2M2.HeaderField.metabolite)).flatMap {
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
    chromatogram
      .createRow(0)
      .createCell(0)
      .setCellValue(createHelper.createRichTextString("CHROMATOGRAM"))

    resultsSet.values.map(
      x => (
        x.get(GenericP2M2.HeaderField.acquisitionDate),
        x.get(GenericP2M2.HeaderField.exportDate))
    ).distinct.map { case (x1,x2) => getIdChromatograph(x1,x2) }
      .zipWithIndex.foreach {
      case (idInjection: String, idx: Int) =>
        val row = chromatogram.createRow(idx + 1)
        val cell: Cell = row.createCell(0)
        cell.setCellValue(createHelper.createRichTextString(idInjection))
    }

    val baos = new ByteArrayOutputStream
    wb.write(baos)
    baos

  }
}
