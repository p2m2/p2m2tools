package fr.inrae.metabolomics.p2m2.stream

import fr.inrae.metabolomics.p2m2.format.GenericP2M2
import fr.inrae.metabolomics.p2m2.parser.ParserUtils
import org.apache.poi.ss.usermodel.Cell

import java.io.{ByteArrayOutputStream, File, PrintWriter}

case object ExportData {

  def xlsP2M2(resultsSet : GenericP2M2): ByteArrayOutputStream = {

    import org.apache.poi.hssf.usermodel.HSSFWorkbook

    val wb = new HSSFWorkbook
    val results = wb.createSheet("RESULTS")
    val samples = wb.createSheet("SAMPLES")
    val metabolites = wb.createSheet("METABOLITE")

    val createHelper = wb.getCreationHelper

    /**
     * header
     */
    val row = results.createRow(0)
    GenericP2M2.HeaderField.values.zipWithIndex.foreach {
      case (headName,idx) =>
        row.createCell(idx).setCellValue(createHelper.createRichTextString(ParserUtils.toString(headName)))
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
     *values
     */
    resultsSet.values.toList.sortBy(_.get(GenericP2M2.HeaderField.sample)).zipWithIndex.foreach {
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

    resultsSet.values.flatMap {
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

    resultsSet.values.flatMap {
      acquisition: Map[GenericP2M2.HeaderField.HeaderField, String] =>
        acquisition.get(GenericP2M2.HeaderField.metabolite)
    }.distinct.zipWithIndex.foreach {
      case (metabolite : String,idx : Int) =>
        val row = metabolites.createRow(idx+1)
        val cell : Cell = row.createCell(0)
        cell.setCellValue(createHelper.createRichTextString(metabolite))
    }

    val baos = new ByteArrayOutputStream
    wb.write(baos)
    baos

  }
}
