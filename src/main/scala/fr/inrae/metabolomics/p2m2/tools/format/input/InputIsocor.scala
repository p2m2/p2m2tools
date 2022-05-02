package fr.inrae.metabolomics.p2m2.tools.format.input

case class InputIsocor(sample : String,
                       metabolite:String,
                       derivative: String,
                       isotopologue: Int,
                       area:Int,
                       resolution: String) {
  def string(sep:String="\t"): String = sample+sep+metabolite+sep+derivative+sep+isotopologue+sep+area+sep+resolution
}
