package fr.inrae.metabolomics.p2m2.parser

trait FormatSniffer {
  def extensionIsCompatible(filename : String) : Boolean
  def sniffFile(filename : String) : Boolean
}
