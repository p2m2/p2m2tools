package fr.inrae.metabolomics.p2m2.parser

import org.apache.poi.hssf.usermodel.HSSFSheet

import scala.util.{Success, Try}

object XLSParserUtil {

  /**
   * get Position
   * @param sheet
   * @param searchTerm
   * @return
   */
  def getRowCellIndexesFromTerm( sheet : HSSFSheet , searchTerm : String) : Seq[(Int,Int)] = {
    val lowercaseSearchTerm = searchTerm.toLowerCase

    sheet.getFirstRowNum.to(sheet.getLastRowNum)
      .filter(sheet.getRow(_) != null)
      .map(rowIndex => {
        sheet.getRow(rowIndex).getFirstCellNum.to(sheet.getRow(rowIndex).getLastCellNum)
          .filter(sheet.getRow(rowIndex).getCell(_) != null)
          .filter(sheet.getRow(rowIndex).getCell(_).toString.toLowerCase.contains(lowercaseSearchTerm))
        .map { rowIndex -> _ }
      }).reduce( (x,y) => x ++ y)
  }

  def getValuesRow(sheet : HSSFSheet, indexRow : Int, startCellIndex : Int = 0) : Seq[String] = {
    startCellIndex.to(sheet.getRow(indexRow).getLastCellNum).flatMap(
      cellIndex =>
        Try(sheet.getRow(indexRow).getCell(cellIndex).toString) match {
          case Success(value) => Some(value)
          case _ => None
        }
    )
  }

  /* get Key / Value as vertical mode =>     example (3,4) "Component Name" , (3,5) "CAT"  => ("Component Name" -> "CAT") */
  def getVerticalKeyValue( sheet : HSSFSheet ) : Map[String,String] = {
    sheet.getFirstRowNum.to(sheet.getLastRowNum)
      .filter(sheet.getRow(_) != null)
      .flatMap(rowIndex => {
        sheet.getRow(rowIndex).getFirstCellNum.to(sheet.getRow(rowIndex).getLastCellNum)
          .flatMap(
            cellIndex =>
              Try(sheet.getRow(rowIndex).getCell(cellIndex).toString.trim
                -> sheet.getRow(rowIndex+1).getCell(cellIndex).toString.trim) match {
                case Success(value) if value._1.nonEmpty => Some(value)
                case _ => None
              }
          )
      }).toMap
  }

  /* get Key / Value as vertical mode =>     example (3,4) "Component Name" , (4,4) "CAT"  => ("Component Name" -> "CAT") */
  def getHorizontalKeyValue( sheet : HSSFSheet ) : Map[String,String] = {
    sheet.getFirstRowNum.to(sheet.getLastRowNum)
      .filter(sheet.getRow(_) != null)
      .flatMap(rowIndex => {
        sheet.getRow(rowIndex).getFirstCellNum.to(sheet.getRow(rowIndex).getLastCellNum)
          .flatMap(
            cellIndex =>
              Try(sheet.getRow(rowIndex).getCell(cellIndex).toString.trim
                -> sheet.getRow(rowIndex).getCell(cellIndex+1).toString.trim) match {
                case Success(value) if value._1.nonEmpty => Some(value)
                case _ => None
              }
          )
      }).toMap
  }

}
