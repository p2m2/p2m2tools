package fr.inrae.metabolomics.p2m2.parser

import fr.inrae.metabolomics.p2m2.format.OpenLabCDS.{HeaderField, HeaderFileField}
import utest.{TestSuite, Tests, test}

import scala.util.Try

object OpenLabCDSTest extends TestSuite{
  val tests: Tests = Tests{
    test("file empty") {
      val toParse = ""
      assert( OpenLabCDSParser.parseHeader(toParse.split("\n").toList) == Map() )
    }
    test("setHeaderValue new def") {
      val toParse =
        """
      |Data File C:\Chemstation\1\Data\211011_Corentin-Younes 2021-10-11 15-56-48\Std 500.D
      |New Header : Std
      """.stripMargin
      assert(Try(OpenLabCDSParser.setHeaderValue(toParse.split("\n").toList,
        "New Header","""(New\sHeader)\s*:\s*(.*)""".r)).isFailure)
    }

    test("sample name") {
      val toParse =
        """
          |Data File C:\Chemstation\1\Data\211011_Corentin-Younes 2021-10-11 15-56-48\Std 500.D
          |Sample Name: Std 500
      """.stripMargin
      assert(OpenLabCDSParser.setHeaderValue(toParse.split("\n").toList,
        "Sample Name", """(Sample\sName)\s*:\s*(.*)""".r).contains(HeaderFileField.`Sample Name` -> "Std 500") )
    }

    test("header file") {
      val toParse =
        """
          |Data File C:\Chemstation\1\Data\211011_Corentin-Younes 2021-10-11 15-56-48\Std 500.D
          |Sample Name: Std 500
          |MPSr-GCFID 10/19/2021 2:39:33 PM SYSTEM
          |
          |=====================================================================
          |Acq. Operator   : SYSTEM                         Seq. Line :   8
          |Sample Operator : SYSTEM
          |Acq. Instrument : MPSr-GCFID                      Location :   6  (F)
          |Injection Date  : 10/11/2021 11:03:47 PM               Inj :   1
          |                                                Inj Volume : 1 µl
          |Acq. Method     : C:\Chemstation\1\Data\211011_Corentin-Younes 2021-10-11 15-56-48\190314_
          |                  Glucides_prep_12-110.M
          |Last changed    : 2/25/2021 3:02:59 PM by SYSTEM
          |Analysis Method : C:\Chemstation\1\Data\211011_Corentin-Younes 2021-10-11 15-56-48\190314_
          |                  Glucides_retraitement.M
          |Last changed    : 10/19/2021 2:39:30 PM by SYSTEM
          |                  (modified after loading)
          |Additional Info : Peak(s) manually integrated
          |""".stripMargin
      val parseHeader = OpenLabCDSParser.parseHeader(toParse.split("\n").toList)

      assert( parseHeader.get(HeaderFileField.`Sample Name`).contains("Std 500"))
      assert( parseHeader.get(HeaderFileField.`Acq. Operator`).contains("SYSTEM"))
      assert( parseHeader.get(HeaderFileField.`Seq. Line`).contains("8"))
      assert( parseHeader.get(HeaderFileField.`Sample Operator`).contains("SYSTEM"))
      assert( parseHeader.get(HeaderFileField.`Acq. Instrument`).contains("MPSr-GCFID"))
      assert( parseHeader.get(HeaderFileField.`Injection Date`).contains("10/11/2021 11:03:47 PM"))
      assert( parseHeader.get(HeaderFileField.Location).contains("6  (F)"))
      assert( parseHeader.get(HeaderFileField.Inj).contains("1"))
      assert( parseHeader.get(HeaderFileField.`Inj Volume`).contains("1 µl"))
      assert( parseHeader.get(HeaderFileField.`Additional Info`).contains("Peak(s) manually integrated"))
      assert( parseHeader.get(HeaderFileField.`Acq. Method`).contains("C:\\Chemstation\\1\\Data\\211011_Corentin-Younes 2021-10-11 15-56-48\\190314_Glucides_prep_12-110.M"))
      assert( parseHeader.get(HeaderFileField.`Last changed Acq. Method`).contains("2/25/2021 3:02:59 PM"))
      assert( parseHeader.get(HeaderFileField.`Analysis Method`).contains("C:\\Chemstation\\1\\Data\\211011_Corentin-Younes 2021-10-11 15-56-48\\190314_Glucides_retraitement.M"))
      assert( parseHeader.get(HeaderFileField.`Last changed Analysis Method`).contains("10/19/2021 2:39:30 PM"))
    }
    test("parseHeader file malformed [Last changed not present]") {
      val toParse =
        """
          |Data File C:\Chemstation\1\Data\211011_Corentin-Younes 2021-10-11 15-56-48\Std 500.D
          |Sample Name: Std 500
          |MPSr-GCFID 10/19/2021 2:39:33 PM SYSTEM
          |
          |=====================================================================
          |Acq. Operator   : SYSTEM                         Seq. Line :   8
          |Sample Operator : SYSTEM
          |Acq. Instrument : MPSr-GCFID                      Location :   6  (F)
          |Injection Date  : 10/11/2021 11:03:47 PM               Inj :   1
          |                                                Inj Volume : 1 µl
          |Acq. Method     : C:\Chemstation\1\Data\211011_Corentin-Younes 2021-10-11 15-56-48\190314_
          |                  Glucides_prep_12-110.M
          |Analysis Method : C:\Chemstation\1\Data\211011_Corentin-Younes 2021-10-11 15-56-48\190314_
          |                  Glucides_retraitement.M
          |                  (modified after loading)
          |Additional Info : Peak(s) manually integrated
          |""".stripMargin
      val parseHeader = OpenLabCDSParser.parseHeader(toParse.split("\n").toList)
      assert( parseHeader.get(HeaderFileField.`Acq. Method`).contains("C:\\Chemstation\\1\\Data\\211011_Corentin-Younes 2021-10-11 15-56-48\\190314_Glucides_prep_12-110.M"))
      assert( !parseHeader.contains(HeaderFileField.`Last changed Acq. Method`))
      assert( parseHeader.get(HeaderFileField.`Analysis Method`).contains("C:\\Chemstation\\1\\Data\\211011_Corentin-Younes 2021-10-11 15-56-48\\190314_Glucides_retraitement.M"))
      assert( !parseHeader.contains(HeaderFileField.`Last changed Analysis Method`))
    }

    test("parse results") {
      val toParse =
        """
          |Sorted By             :      Signal
          |Calib. Data Modified  :      10/15/2021 5:02:32 PM
          |Multiplier            :      1.0000
          |Dilution              :      1.0000
          |Sample Amount:        :    100.00000  [ng/ul]   (not used in calc.)
          |Use Multiplier & Dilution Factor with ISTDs
          |Sample ISTD Information:
          |ISTD  ISTD Amount   Name
          |  #    [ng/ul]
          |----|-------------|-------------------------
          |  1    100.00000   Adonitol
          |
          |
          |Signal 1: FID1 A,
          |
          |RetTime  Type  ISTD    Area     Amt/Area    Amount   Grp   Name
          | [min]         used  [pA*s]      ratio     [ng/ul]
          |-------|------|----|----------|----------|----------|--|------------------
          |  8.335 MF        1   10.97505    5.42183  531.23458    Glyoxylate
          |  9.925 FM        1   19.37573    3.36334  581.78554    Glycolate
          | 13.462 BB        1   22.58038    2.65118  534.44585    Succinate
          | 13.765 BB        1   27.60959    2.21601  546.21713    Glycerate
          | 13.876 BB        1   15.00284    3.93626  527.21991    Fumarate
          | 15.713 MF        1   27.47841    2.19241  537.83291    Malate
          | 17.728 BV        1    8.70090    7.01682  545.05292    Xylose1
          | 17.822 VV        1   39.43914    1.56146  549.78267    Xylose2
          | 17.904 MF        1   49.24451    1.24722  548.31973    Arabinose
          | 18.546 BB        1   57.08576    1.06885  544.72795    Xylitol
          | 18.803 FM   I    1   11.20124    1.00000  100.00000    Adonitol
          | 20.245 MF        1   33.88725    1.83246  554.37562    Citrate
          | 21.196 FM        1   58.95049    1.03368  544.01047    Quinate
          | 21.483 BB        1   34.04986    1.80697  549.28838    Fructose1
          | 21.704 MF        1   25.19184    2.42079  544.44118    Fructose2
          | 21.797 MF        1   50.78664    1.20465  546.19032    Mannose1
          | 21.931 MF        1   44.13379    1.38697  546.47575    Galactose1
          | 22.063 MF        1   49.51545    1.23741  546.99954    Glucose1
          | 22.153 FM        1    8.06675    7.39198  532.34556    Mannose2
          | 22.415 FM        1   13.05567    4.63485  540.21787    Galactose2
          | 22.490 VB        1    9.88844    6.01992  531.43790    Glucose2
          | 22.825 MF        1   66.80258 9.08191e-1  541.63209    Mannitol
          | 23.000 MF        1   65.16522 9.32410e-1  542.44616    Sorbitol
          | 23.101 FM        1   67.41358 8.99264e-1  541.21375    Dulcitol
          | 25.422 MF        1   66.62699 9.12278e-1  542.63927    Myo-inositol
          | 31.321 BB        1   89.53419 6.80476e-1  543.92070    Saccharose
          | 32.313 MF        1   67.48031 9.11027e-1  548.83559    Cellobiose
          | 32.884 MF        1   72.35709    1.08228  699.12485    Tréhalose
          | 32.884 FM        1   53.63578 8.84594e-1  423.57713    Maltose1
          | 33.343 MF        1   18.43492    3.05434  502.68187    Maltose2
          | 34.252 MF        1   67.54031 9.03992e-1  545.08193    Gentiobiose1
          | 34.664 FM        1   14.24206    4.21692  536.16953    Gentiobiose2
          | 35.038 MF        1   66.44343 9.27447e-1  550.14273    Melibiose1
          | 35.470 MF        1   13.78376    4.35693  536.14458    Melibiose2
          | 36.658 MF        1   44.91168    1.35313  542.53994    Galactinol
          | 41.559 MF        1  110.09505 5.54692e-1  545.19732    Raffinose
          | 42.896 MF        1  158.98767 3.54025e-1  502.49442    Melezitose
          |Totals without ISTD(s) :                  1.95262e4
          |
          |1 Warnings or Errors :
          |
          |Warning : Calibration warnings (see calibration table listing)
          |
          |=====================================================================
          |                          *** End of Report ***
          |""".stripMargin
      val values = OpenLabCDSParser.parseResults(toParse.split("\n").toList)
      assert(values.head.get(HeaderField.RetTime).contains("8.335"))
      assert(values.head.get(HeaderField.Name).contains("Glyoxylate"))
      assert(values.head.get(HeaderField.Area).contains("10.97505"))
      assert(values(10).get(HeaderField.ISTD).contains("I"))
      assert(values.last.get(HeaderField.RetTime).contains("42.896"))
      assert(values.last.get(HeaderField.Name).contains("Melezitose"))
      assert(values.last.get(HeaderField.Area).contains("158.98767"))
    }

    test("extensionIsCompatible") {
      assert(OpenLabCDSParser.extensionIsCompatible("/GCMS/13CPROT4.txt"))
      assert(!OpenLabCDSParser.extensionIsCompatible(""))
      assert(OpenLabCDSParser.extensionIsCompatible("/GCMS/13CPROT4.xls"))
    }

    test("sniffFile") {
      assert(!OpenLabCDSParser.sniffFile(getClass.getResource("/GCMS/13CPROT4.txt").getPath))
      assert(!OpenLabCDSParser.sniffFile(getClass.getResource("/MassLynx/mass_15Ngly.txt").getPath))
      assert(OpenLabCDSParser.sniffFile(getClass.getResource("/OpenLabCDS/Report_Ex1.txt").getPath))
      assert(!OpenLabCDSParser.sniffFile(getClass.getResource("/Xcalibur/resuts_inj1_Long.XLS").getPath))
      assert(!OpenLabCDSParser.sniffFile(getClass.getResource("/Xcalibur/bad_file_xls.xls").getPath))
      assert(!OpenLabCDSParser.sniffFile(getClass.getResource("/MassLynx/xml/quandata.xml").getPath))
    }
  }
}
