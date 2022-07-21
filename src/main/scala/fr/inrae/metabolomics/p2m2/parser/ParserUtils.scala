package fr.inrae.metabolomics.p2m2.parser

object ParserUtils {

  val replaceTokenList : Seq[(String,String)] = Seq(
    ("$percent","%"),
    ("$u002E","."),
    ("$u0020"," "),
    ("$div","/"),
    ("$hash","#")
  )

  def getHeaderField[T<: Enumeration](headerField : T, token : String) : Option[T#Value] =
    headerField
      .values
      .find( elt => replaceTokenList.foldLeft(elt.toString) {
        case (accumulator:String,rep : (String,String)) =>
          accumulator.replace(rep._1,rep._2)
      }.equalsIgnoreCase(token))

  def toString[T<: Enumeration](v: T#Value) : String =
    replaceTokenList.foldLeft(v.toString) {
      case (accumulator:String,rep : (String,String)) =>
        accumulator.replace(rep._1,rep._2)
    }
}
