package fr.inrae.metabolomics.p2m2.parser

import fr.inrae.metabolomics.p2m2.tools.format.output.{CompoundSheetXcalibur, OutputXcalibur}
import fr.inrae.metabolomics.p2m2.tools.format.output.OutputXcalibur.HeaderField.HeaderField
import fr.inrae.metabolomics.p2m2.tools.format.output.OutputXcalibur.{HeaderField, HeaderSheetField}
import fr.inrae.metabolomics.p2m2.tools.format.output.OutputXcalibur.HeaderSheetField.HeaderSheetField
import org.apache.poi.hssf.usermodel.{HSSFSheet, HSSFWorkbook}

import java.io.{File, FileInputStream}
import scala.collection.immutable.Map
import scala.io.Source
import scala.util.{Success, Try}

object XcaliburXlsParser extends Parser[OutputXcalibur] {

  def getHeaderSheet(mapping : Map[String,String]) : Map[HeaderSheetField,String] = {
      mapping flatMap {
          case ( key, value)  =>
            /** check key in HeaderSheetField  */
            (HeaderSheetField.values.find(_.toString.toLowerCase() ==
              key.replace(" ","_").toLowerCase())) match {
              case Some(keyT) => Some(keyT-> value)
              case _ => None
            }
      }
  }

  /**
   * Get the list of Injection
   * @param sheet
   * @return
   */
  def getResults(sheet : HSSFSheet) : Seq[Map[HeaderField,String]] = {

    // get header
    val header : Seq[String] = (XLSParserUtil.getRowCellIndexesFromTerm(sheet,"Filename").headOption match {
      case Some((row, cell)) =>
        cell.to(sheet.getRow(row).getLastCellNum)
          .filter(sheet.getRow(row).getCell(_) != null )
          .map(sheet.getRow(row).getCell(_).toString.trim)
      case _ => Seq()
    })

    (XLSParserUtil.getRowCellIndexesFromTerm(sheet,"Filename").headOption match {
      case Some((row, cell)) => {
        (row + 1).to(sheet.getLastRowNum)
          .filter(sheet.getRow(_) != null)
          .map(rowIndex => {
            cell.to(sheet.getRow(rowIndex).getLastCellNum)
              .map(cellIndex => Try(sheet.getRow(rowIndex).getCell(cellIndex).toString.trim) match {
                case Success(value) => value
                case _ => ""
              })
          })
      }
      case _ => Seq()
    }) takeWhile( _.mkString("")!="") map {
      (seq: Seq[String]) =>
        seq.zipWithIndex.flatMap {
          case (value, idx) =>

            /** check key in HeaderSheetField */
            (HeaderField.values
              .find(_.toString.toLowerCase() == header(idx).replace(" ", "_")
                .toLowerCase()) match {
              case Some(keyT) => Some(keyT -> value)
              case _ => None
            })
        }.toMap
    }
  }

  override def parse(filename : String) : OutputXcalibur = {
    val file = new FileInputStream(new File(filename))
    val workbook : HSSFWorkbook = new HSSFWorkbook(file)
    val numSheet = workbook.getNumberOfSheets

    val compounds : Seq[CompoundSheetXcalibur] = 0.until(numSheet)
      .map( workbook.getSheetAt )
      .map(
        sheet => {
          sheet -> XLSParserUtil.getVerticalKeyValue(sheet)
        })
      .map {
        case (sheet : HSSFSheet, mapping : Map[String,String]) => {
          CompoundSheetXcalibur(
            getHeaderSheet(mapping),
            getResults(sheet))
        }
      }

    OutputXcalibur(filename,compounds)
  }

}
