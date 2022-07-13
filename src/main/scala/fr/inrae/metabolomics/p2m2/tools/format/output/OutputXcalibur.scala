package fr.inrae.metabolomics.p2m2.tools.format.output


object OutputXcalibur {

  object HeaderSheetField extends Enumeration {
    type HeaderSheetField = Value
    val Component_Name, Curve_Index, Weighting_Index, Origin_Index, Equation, User_Name, Full_Name, Date = Value
  }

  object HeaderField extends Enumeration {
    type HeaderField = Value
    val Filename, Sample_Type,Sample_Name, Sample_ID, Exp_Amt, Calc_Amt_Units, `%Diff`, Level,
    `%RSD-AMT`,Peak_Status, Response,	Response_Type, Equation, Area,	Height,ISTD_Area, ISTD_Ht,
    RT,	Rel_RT,	Del_RT,	`S/N`,Start_Time,	End_Time,Start_Height, End_Height,Integ_Code, Search_Window, Threshold,
    Smooth, Acq_Date, Duration, Exp_Method, Proc_Method, Vial,	Inj_Vol,	Sample_Wt,
    Sample_Vol, ISTD_Base_Amt, ISTD_Calc_Amt, Dilution_Factor, Study, Client, Laboratory, Company, Phone, Comment = Value
  }
}

case class OutputXcalibur(
                           origin : String,
                           injections : Seq[CompoundSheetXcalibur]
                         )
