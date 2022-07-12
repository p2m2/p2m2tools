package fr.inrae.metabolomics.p2m2.parser

trait Parser[OutputFormat] {
  def parse(filename : String) : OutputFormat
}
