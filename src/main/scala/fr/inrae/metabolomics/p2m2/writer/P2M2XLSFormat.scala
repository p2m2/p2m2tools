package fr.inrae.metabolomics.p2m2.writer

import fr.inrae.metabolomics.p2m2.format.GenericP2M2
import java.io.{File, PrintWriter}

case object P2M2XLSFormat {

  def writeTxt( resultsSet : GenericP2M2, outputFile : String): Unit = {
    val writer = new PrintWriter(new File(outputFile))
   // resultsSet.
   // writer.write("Hello Developer, Welcome to Scala Programming.")
    writer.close()
  }

  def writeXls( resultsSet : GenericP2M2, outputFile : String): Unit = {
    val writer = new PrintWriter(new File(outputFile))
    // resultsSet.
    // writer.write("Hello Developer, Welcome to Scala Programming.")
    writer.close()
  }
}
