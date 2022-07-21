package fr.inrae.metabolomics.p2m2.parser

object Parser {
  
}

trait Parser[OutputFormat] {
  def parse(filename : String) : OutputFormat
}
