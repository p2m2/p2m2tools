package fr.inrae.metabolomics.p2m2.parser

import fr.inrae.metabolomics.p2m2.format.ms.Xcalibur
import Xcalibur.HeaderField.HeaderField
import Xcalibur.{CompoundSheetXcalibur, HeaderField, HeaderSheetField}
import Xcalibur.HeaderSheetField.HeaderSheetField
import org.apache.poi.hssf.usermodel.{HSSFSheet, HSSFWorkbook}
import org.apache.poi.ss.usermodel.{CellType, DateUtil}

import java.io.{ByteArrayInputStream, File, FileInputStream, InputStream}
import scala.io.{Codec, Source}
import scala.util.{Failure, Success, Try}

object XcaliburXlsParser extends Parser[Xcalibur] with FormatSniffer {

  def getHeaderSheet(mapping : Map[String,String]) : Map[HeaderSheetField,String] = {
      mapping flatMap {
          case ( key, value)  =>
            /** check key in HeaderSheetField  */
            ParserUtils.getHeaderField(Xcalibur.HeaderSheetField,key) match {
              case Some(keyT) => Some(keyT-> value)
              case _ => None
            }
      }
  }

  /**
   * Get the list of Injection
   * @param sheet : excel sheet to parse
   * @return
   */
  def getResults(sheet : HSSFSheet) : Seq[Map[HeaderField,String]] = {

    // get header
    val header : Seq[String] = XLSParserUtil.getRowCellIndexesFromTerm(sheet,"Filename").headOption match {
      case Some((row, cell)) =>
        cell.to(sheet.getRow(row).getLastCellNum)
          .filter(sheet.getRow(row).getCell(_) != null )
          .map(sheet.getRow(row).getCell(_).toString.trim)
      case _ => Seq()
    }

    println(header)

    (XLSParserUtil.getRowCellIndexesFromTerm(sheet,"Filename").headOption match {
      case Some((row, cell)) =>
        (row + 1).to(sheet.getLastRowNum)
          .filter(sheet.getRow(_) != null)
          .map(rowIndex => {
            cell.to(sheet.getRow(rowIndex).getLastCellNum)
              .map {
                cellIndex =>
                  val cell = sheet.getRow(rowIndex).getCell(cellIndex)
                  // println(cell.getCellStyle.getDataFormatString,cell.getRichStringCellValue,cell.getCellType,cell.getStringCellValue)
                  //println(cell.getStringCellValue)
                  Try(cell.getCellType) match {
                    case Success(CellType.NUMERIC) =>
                      if (DateUtil.isCellDateFormatted(cell)) {
                        cell.getDateCellValue.toString
                      } else {
                        cell.getNumericCellValue.toString
                      }
                    case Success(_) => cell.getRichStringCellValue.toString
                    case Failure(_) => ""
                  }
              }
          })
      case _ => Seq()
    }) takeWhile( _.mkString("")!="") map {
      (seq: Seq[String]) =>
        seq.zipWithIndex.flatMap {
          case (value, idx) =>

            ParserUtils.getHeaderField(Xcalibur.HeaderField,header(idx)) match {
              case Some(keyT) => Some(keyT -> value)
              case _ => None
            }
        }.toMap
    }
  }
  def parse(is: InputStream, info : String) : Xcalibur = {
    val workbook: HSSFWorkbook = new HSSFWorkbook(is)
    val numSheet = workbook.getNumberOfSheets

    val compounds: Seq[CompoundSheetXcalibur] = 0.until(numSheet)
      .map(workbook.getSheetAt)
      .map(
        sheet => {
          sheet -> XLSParserUtil.getVerticalKeyValue(sheet)
        })
      .flatMap {
        case (sheet: HSSFSheet, mapping: Map[String, String]) =>
          val header = getHeaderSheet(mapping)
          header.filter(_._2.trim.nonEmpty) match {
            case head if head.nonEmpty => Some(CompoundSheetXcalibur(header, getResults(sheet)))
            case _ => None
          }
      }

    Xcalibur(info, compounds)
  }

  def parseByteArray(content: Array[Byte], encode: Codec) : Xcalibur =
    parse(new ByteArrayInputStream(content),"stream"+content.length)
  override def parseFile(filename : String, encode: Codec) : Xcalibur =
    parse(new FileInputStream(new File(filename)),filename)


  override def extensionIsCompatible(filename: String): Boolean = {
    filename.split("\\.").last.trim.toLowerCase == "xls"
  }
  private def testHeader(is: InputStream): Boolean = {
    try {
      val workbook: HSSFWorkbook = new HSSFWorkbook(is)

      0.until(workbook.getNumberOfSheets)
        .map(workbook.getSheetAt)
        .exists(sheet => {
          XLSParserUtil.getRowCellIndexesFromTerm(sheet, "Filename").headOption match {
            case Some(_) => true
            case None => false
          }
        })
    } catch {
      case _: Throwable => false
    }
  }
  override def sniffByteArray(content: Array[Byte], encode: Codec): Boolean = {
    Try(testHeader(new ByteArrayInputStream(content))).getOrElse(false)
  }

  override def sniffFile(filename: String, encode: Codec): Boolean = {
    Try(testHeader(new FileInputStream(new File(filename)))).getOrElse(false)
  }
}
