package fr.inrae.metabolomics.p2m2.parser

object ParserUtils {

  def getHeaderField[T<: Enumeration](headerField : T, token : String) : Option[T#Value] =
    headerField
      .values
      .find( _
        .toString
        .replace("$percent","%")
        .replace("$u002E",".")
        .replace("$u0020"," ")
        .replace("$div","/")
        .replace("$hash","#")
        .equalsIgnoreCase(token))
}
