package fr.inrae.metabolomics.p2m2.parser

object ParserUtils {

  val replace_token : Seq[(String,String)] = Seq(
    ("$percent","%"),
    ("$u002E","."),
    ("$u0020"," "),
    ("$div","/"),
    ("$hash","#")
  )

  def getHeaderField[T<: Enumeration](headerField : T, token : String) : Option[T#Value] =
    headerField
      .values
      .find( elt => replace_token.foldLeft(elt.toString) {
        case (accumulator:String,rep : (String,String)) =>
          accumulator.replace(rep._1,rep._2)
      }.equalsIgnoreCase(token))

  def toString[T<: Enumeration](v: T#Value) : String =
    replace_token.foldLeft(v.toString) {
      case (accumulator:String,rep : (String,String)) =>
        accumulator.replace(rep._1,rep._2)
    }
}
