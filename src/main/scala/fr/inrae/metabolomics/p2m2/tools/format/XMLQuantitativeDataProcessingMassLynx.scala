package fr.inrae.metabolomics.p2m2.tools.format

import fr.inrae.metabolomics.p2m2.tools.format.XMLQuantitativeDataProcessingMassLynx.QuanDataset

import scala.util.{Failure, Success, Try}

object XMLQuantitativeDataProcessingMassLynx {
  //https://www.waters.com/webassets/cms/support/docs/71500123505ra.pdf

  object QuanDataset {
    def fromXml(node: scala.xml.Node):QuanDataset = {

      val description = node \@ "description"
      val version = node \@ "version"
      val xmlFile = XmlFile.fromXml((node \ "XMLFILE").head)
      val dataset = Dataset.fromXml((node \ "DATASET").head)
      val groupData = (node \ "GROUPDATA" \ "GROUP").map(Group.fromXml)

      QuanDataset(description,version,xmlFile, dataset, groupData)
    }
  }

  case class QuanDataset(
                          description : String,
                          version : String,
                          xmlFile : XmlFile,
                          dataset: Dataset,
                          groupData: Seq[Group]) {
    /*
    override def toString =
      s"description: $description, version: $version\n xmlFile: $xmlFile \n dataset $dataset \n groupData $groupData"
      */
  }

  object XmlFile {
    def fromXml(node: scala.xml.Node):XmlFile = {
      val filename = node \@ "filename"
      val modifiedDate = node \@ "modifieddate"
      val modifiedTime = node \@ "modifiedtime"
      XmlFile(filename, modifiedDate, modifiedTime)
    }
  }

  case class XmlFile(filename:String, modifiedDate: String, modifiedTime : String)

  object Dataset {
    def fromXml(node: scala.xml.Node):Dataset = {
      val filename = node \@ "filename"
      val modifiedDate = node \@ "modifieddate"
      val modifiedTime = node \@ "modifiedtime"
      Dataset(filename, modifiedDate, modifiedTime)
    }
  }

  case class Dataset(filename:String, modifiedDate: String, modifiedTime : String)

  object Group {
    def fromXml(node: scala.xml.Node):Group = {
      val id = node \@ "id"
      val name = node \@ "name"
      val methodData = MethodData.fromXml((node \ "METHODDATA").head)
      val sampleListData = SampleListData.fromXml((node \ "SAMPLELISTDATA").head)
      val calibrationData = Try(CalibrationData.fromXml((node \ "CALIBRATIONDATA").head)) match {
        case Success(v) => Some(v)
        case Failure(_) => None
      }
      Group(id,name,methodData,sampleListData,calibrationData)
    }
  }

  case class Group(
                        id: String,
                        name:String,
                        methodData : MethodData,
                        sampleListData : SampleListData,
                        calibrationData : Option[CalibrationData]
                      )

  object MethodData {
    def fromXml(node: scala.xml.Node):MethodData = {
      val id = node \@ "id"
      val filename = node \@ "filename"
      val modifiedDate = node \@ "modifieddate"
      val modifiedTime = node \@ "modifiedtime"
      MethodData(id,filename,modifiedDate,modifiedTime)
    }
  }

  case class MethodData(
                        id: String,
                        name:String,
                        modifiedDate:String,
                        modifiedTime:String
                      )

  object SampleListData {
    def fromXml(node: scala.xml.Node):SampleListData = {
      val filename = node \@ "filename"
      val modifiedDate = node \@ "modifieddate"
      val modifiedTime = node \@ "modifiedtime"
      val samples = (node \ "SAMPLE").map( Sample.fromXml )

      SampleListData(filename,modifiedDate,modifiedTime,samples)
    }
  }

  case class SampleListData(
                             filename: String,
                             modifiedDate:String,
                             modifiedTime:String,
                             samples : Seq[Sample]
                           )

  object Sample {
    def fromXml(node: scala.xml.Node):Sample = {
      val id = node \@ "id"
      val groupId = node \@ "groupId"
      val createDate = node \@ "createdate"
      val createTime = node \@ "createtime"
      val `type` = node \@ "type"
      val desc = node \@ "desc"
      val dilutionFac = node \@ "dilutionfac"
      val extractVolume = node \@ "extractvolume"
      val initAmount = node \@ "initamount"
      val injectVolume = node \@ "injectvolume"
      val job = node \@ "job"
      val massA = node \@ "massA"
      val massB = node \@ "massB"
      val massC = node \@ "massC"
      val massD = node \@ "massD"
      val massE = node \@ "massE"
      val massF = node \@ "massF"
      val massG = node \@ "massG"
      val massH = node \@ "massH"
      val massI = node \@ "massI"
      val massJ = node \@ "massJ"
      val sampleId = node \@ "sampleid"
      val samplenumber = node \@ "samplenumber"
      val stdconc = node \@ "stdconc"
      val stockdilutionfac = node \@ "stockdilutionfac"
      val subjecttext = node \@ "subjecttext"
      val subjecttime = node \@ "subjecttime"
      val userdilutionfac = node \@ "userdilutionfac"
      val vial = node \@ "vial"
      val inletmethodname = node \@ "inletmethodname"
      val msmethodname = node \@ "msmethodname"
      val prerunmethodname = node \@ "prerunmethodname"
      val postrunmethodname = node \@ "postrunmethodname"
      val switchmethodname = node \@ "switchmethodname"
      val hplcmethodname = node \@ "hplcmethodname"
      val tunemethodname = node \@ "tunemethodname"
      val fractionlynxname = node \@ "fractionlynxname"
      val compounds = (node \ "COMPOUND").map(Compound.fromXml)

      Sample(id,groupId,createDate,createTime,`type`,desc,
        dilutionFac,extractVolume,initAmount,injectVolume,job,
        massA,massB,massC,massD,massE,massF,massG,massH,massI,massJ,
        sampleId,samplenumber,stdconc,stockdilutionfac,subjecttext,
        subjecttime,userdilutionfac,vial,inletmethodname,msmethodname,
        prerunmethodname,postrunmethodname,switchmethodname,
        hplcmethodname,tunemethodname,fractionlynxname,compounds
      )
    }
  }

  case class Sample(
                     id: String,
                     groupId : String,
                     createDate: String,
                     createTime: String,
                     `type`: String,
                     desc: String,
                     dilutionFac: String,
                     extractVolume: String,
                     initAmount: String,
                     injectVolume: String,
                     job: String,
                     massA: String,
                     massB: String,
                     massC: String,
                     massD: String,
                     massE: String,
                     massF: String,
                     massG: String,
                     massH: String,
                     massI: String,
                     massJ: String,
                     sampleId: String,
                     samplenumber: String,stdconc: String,stockdilutionfac: String,subjecttext: String,
                     subjecttime: String,userdilutionfac: String,vial: String,inletmethodname: String,msmethodname: String,
                     prerunmethodname: String,postrunmethodname: String,switchmethodname: String,
                     hplcmethodname: String,tunemethodname: String,fractionlynxname: String,
                     compounds : Seq[Compound]
                   )

  object CalibrationData {
    def fromXml(node: scala.xml.Node):CalibrationData = {
      val filename = node \@ "filename"
      val modifiedDate = node \@ "modifieddate"
      val modifiedTime = node \@ "modifiedtime"
      CalibrationData(filename,modifiedDate,modifiedTime)
    }
  }

  /**
   * TODO COMPOUND Indide CalibrationData => https://www.waters.com/webassets/cms/support/docs/71500123505ra.pdf
   */
  case class CalibrationData(
                              filename: String,
                              modifiedDate:String,
                              modifiedTime : String
                   )

  object Compound {
    def fromXml(node: scala.xml.Node):Compound = {
      val id = node \@ "id"
      val sampleId = node \@ "sampleid"
      val groupId = node \@ "groupid"
      val name = node \@ "name"
      val peak = Peak.fromXml((node \ "PEAK").head)
      val secondaryPeaks = (node \ "SECONDARYPEAKS").map(SecondaryPeaks.fromXml)
      val method = Method.fromXml((node \ "METHOD").head)

      Compound(id,sampleId,groupId,name,peak,secondaryPeaks,method)
    }
  }
  case class Compound(
                       id: String,
                        sampleId:String,
                       groupId : String,
                       name : String,
                       peak : Peak,
                       secondaryPeaks : Seq[SecondaryPeaks],
                       method : Method
                        )

  object Peak {
    def fromXml(node: scala.xml.Node):Peak = {
      val foundscan = node \@ "foundscan"
      val foundrt = node \@ "foundrt"
      val foundrrt = node \@ "foundrrt"
      val predrt = node \@ "predrt"
      val predrrt = node \@ "predrrt"
      val area = node \@ "area"
      val height = node \@ "height"
      val response = node \@ "response"
      val pkflags = node \@ "pkflags"
      val analconc = node \@ "analconc"
      val empc = node \@ "empc"
      val bsanalconc = node \@ "bsanalconc"
      val conccalc = node \@ "conccalc"
      val modifieddate = node \@ "modifieddate"
      val modifiedtime = node \@ "modifiedtime"
      val modifiedtext = node \@ "modifiedtext"
      val modifieduser = node \@ "modifieduser"
      val peakmass = node \@ "peakmass"
      val startrt = node \@ "startrt"
      val endrt = node \@ "endrt"
      val startht = node \@ "startht"
      val endht = node \@ "endht"
      val absresponse = node \@ "absresponse"
      val rrtref = node \@ "rrtref"
      val quanratio = node \@ "quanratio"
      val quanratiopred = node \@ "quanratiopred"
      val quanratiowin = node \@ "quanratiowin"
      val ionratio = node \@ "ionratio"
      val ionratiopred = node \@ "ionratiopred"
      val ionratiowin = node \@ "ionratiowin"
      val ionratioflag = node \@ "ionratioflag"
      val chromnoise = node \@ "chromnoise"
      val detectionthreshold = node \@ "detectionthreshold"
      val detectionflag = node \@ "detectionflag"
      val quanthreshold = node \@ "quanthreshold"
      val quanflag = node \@ "quanflag"
      val snlodflag = node \@ "snlodflag"
      val snloqflag = node \@ "snloqflag"
      val rrf = node \@ "rrf"
      val chromtrace = node \@ "chromtrace"
      val peaks = node \@ "peaks"
      val pkwidth = node \@ "pkwidth"
      val pksigma = node \@ "pksigma"
      val pkskew = node \@ "pkskew"
      val pkkurt = node \@ "pkkurt"
      val heightdivarea = node \@ "heightdivarea"
      val baselinewidth = node \@ "baselinewidth"
      val peakquality = node \@ "peakquality"
      val peakqualitydesc = node \@ "peakqualitydesc"
      val peakqualityref = node \@ "peakqualityref"
      val replimflag = node \@ "replimflag"
      val maxreplimflag = node \@ "maxreplimflag"
      val recovlimflag = node \@ "recovlimflag"
      val matrixblankflag = node \@ "matrixblankflag"
      val solventblankflag = node \@ "solventblankflag"
      val devflag = node \@ "devflag"
      val devflagmidconc = node \@ "devflagmidconc"
      val devflaglowconc = node \@ "devflaglowconc"
      val qcsignoiseflag = node \@ "qcsignoiseflag"
      val qcionratioflag = node \@ "qcionratioflag"
      val qcrettimeflag = node \@ "qcrettimeflag"
      val qcpeakshapeflag = node \@ "qcpeakshapeflag"
      val signoiseflag = node \@ "signoiseflag"
      val signoise = node \@ "signoise"
      val cdflag = node \@ "cdflag"
      val stddevflag = node \@ "stddevflag"
      val rtflag = node \@ "rtflag"
      val peakasymmetry = node \@ "peakasymmetry"
      val peakfrontwidth = node \@ "peakfrontwidth"
      val peaktailwidth = node \@ "peaktailwidth"
      val peakasymmetryvalue = node \@ "peakasymmetryvalue"
      val percrecovery = node \@ "percrecovery"
      val symflag = node \@ "symflag"
      val percsym = node \@ "percsym"
      val belowrl = node \@ "belowrl"
      val chromnoisehgt = node \@ "chromnoisehgt"
      val concdevperc = node \@ "concdevperc"
      val lowerbound1 = node \@ "lowerbound1"
      val lowerbound2 = node \@ "lowerbound2"
      val lowerbound3 = node \@ "lowerbound3"
      val lowerbound4 = node \@ "lowerbound4"
      val mediumbound1 = node \@ "mediumbound1"
      val mediumbound2 = node \@ "mediumbound2"
      val mediumbound3 = node \@ "mediumbound3"
      val mediumbound4 = node \@ "mediumbound4"
      val upperbound1 = node \@ "upperbound1"
      val upperbound2 = node \@ "upperbound2"
      val upperbound3 = node \@ "upperbound3"
      val upperbound4 = node \@ "upperbound4"
      val nosolflag = node \@ "nosolflag"
      val peakmissing = node \@ "peakmissing"
      val peaksinc = node \@ "peaksinc"
      val toxconc1 = node \@ "toxconc1"
      val toxconc2 = node \@ "toxconc2"
      val toxconc3 = node \@ "toxconc3"
      val toxconc4 = node \@ "toxconc4"
      val toxfactor1 = node \@ "toxfactor1"
      val toxfactor2 = node \@ "toxfactor2"
      val toxfactor3 = node \@ "toxfactor3"
      val toxfactor4 = node \@ "toxfactor4"
      val toxlod1 = node \@ "toxlod1"
      val toxlod2 = node \@ "toxlod2"
      val toxlod3 = node \@ "toxlod3"
      val toxlod4 = node \@ "toxlod4"
      val toxloq1 = node \@ "toxloq1"
      val toxloq2 = node \@ "toxloq2"
      val toxloq3 = node \@ "toxloq3"
      val toxloq4 = node \@ "toxloq4"
      val userfactor = node \@ "userfactor"
      val userrf = node \@ "userrf"
      val picsforward = node \@ "picsforward"
      val picsreverse = node \@ "picsreverse"
      val iFIT = node \@ "iFIT"
      val iFITnorm = node \@ "iFITnorm"
      val iFITconfidence = node \@ "iFITconfidence"
      val foundmass = node \@ "foundmass"
      val mDamasserror = node \@ "mDamasserror"
      val ppmmasserror = node \@ "ppmmasserror"
      val iFitflag = node \@ "iFitflag"
      val iFitnormflag = node \@ "iFitnormflag"
      val iFitconfflag = node \@ "iFitconfflag"
      val mDaerrorflag = node \@ "mDaerrorflag"
      val ppmerrorflag = node \@ "ppmerrorflag"

      val isPeak = ISPeak.fromXml((node \ "ISPEAK").head)
      Peak(foundscan,
        foundrt,
        foundrrt,
        predrt,
        predrrt,
        area,
        height,
        response,
        pkflags,
        analconc,
        empc,
        bsanalconc,
        conccalc,
        modifieddate,
        modifiedtime,
        modifiedtext,
        modifieduser,
        peakmass,
        startrt,
        endrt,
        startht,
        endht,
        absresponse,
        rrtref,
        quanratio,
        quanratiopred,
        quanratiowin,
        ionratio,
        ionratiopred,
        ionratiowin,
        ionratioflag,
        chromnoise,
        detectionthreshold,
        detectionflag,
        quanthreshold,
        quanflag,
        snlodflag,
        snloqflag,
        rrf,
        chromtrace,
        peaks,
        pkwidth,
        pksigma,
        pkskew,
        pkkurt,
        heightdivarea,
        baselinewidth,
        peakquality,
        peakqualitydesc,
        peakqualityref,
        replimflag,
        maxreplimflag,
        recovlimflag,
        matrixblankflag,
        solventblankflag,
        devflag,
        devflagmidconc,
        devflaglowconc,
        qcsignoiseflag,
        qcionratioflag,
        qcrettimeflag,
        qcpeakshapeflag,
        signoiseflag,
        signoise,
        cdflag,
        stddevflag,
        rtflag,
        peakasymmetry,
        peakfrontwidth,
        peaktailwidth,
        peakasymmetryvalue,
        percrecovery,
        symflag,
        percsym,
        belowrl,
        chromnoisehgt,
        concdevperc,
        lowerbound1,
        lowerbound2,
        lowerbound3,
        lowerbound4,
        mediumbound1,
        mediumbound2,
        mediumbound3,
        mediumbound4,
        upperbound1,
        upperbound2,
        upperbound3,
        upperbound4,
        nosolflag,
        peakmissing,
        peaksinc,
        toxconc1,
        toxconc2,
        toxconc3,
        toxconc4,
        toxfactor1,
        toxfactor2,
        toxfactor3,
        toxfactor4,
        toxlod1,
        toxlod2,
        toxlod3,
        toxlod4,
        toxloq1,
        toxloq2,
        toxloq3,
        toxloq4,
        userfactor,
        userrf,
        picsforward,
        picsreverse,
        iFIT,
        iFITnorm,
        iFITconfidence,
        foundmass,
        mDamasserror,
        ppmmasserror,
        iFitflag,
        iFitnormflag,
        iFitconfflag,
        mDaerrorflag,
        ppmerrorflag,
        isPeak)
    }
  }
  case class Peak(
                   foundscan : String,
                   foundrt : String,
                   foundrrt : String,
                   predrt : String,
                   predrrt : String,
                   area : String,
                   height : String,
                   response : String,
                   pkflags : String,
                   analconc : String,
                   empc : String,
                   bsanalconc : String,
                   conccalc : String,
                   modifieddate : String,
                   modifiedtime : String,
                   modifiedtext : String,
                   modifieduser : String,
                   peakmass : String,
                   startrt : String,
                   endrt : String,
                   startht : String,
                   endht : String,
                   absresponse : String,
                   rrtref : String,
                   quanratio : String,
                   quanratiopred : String,
                   quanratiowin : String,
                   ionratio : String,
                   ionratiopred : String,
                   ionratiowin : String,
                   ionratioflag : String,
                   chromnoise : String,
                   detectionthreshold : String,
                   detectionflag : String,
                   quanthreshold : String,
                   quanflag : String,
                   snlodflag : String,
                   snloqflag : String,
                   rrf : String,
                   chromtrace : String,
                   peaks : String,
                   pkwidth : String,
                   pksigma : String,
                   pkskew : String,
                   pkkurt : String,
                   heightdivarea : String,
                   baselinewidth : String,
                   peakquality : String,
                   peakqualitydesc : String,
                   peakqualityref : String,
                   replimflag : String,
                   maxreplimflag : String,
                   recovlimflag : String,
                   matrixblankflag : String,
                   solventblankflag : String,
                   devflag : String,
                   devflagmidconc : String,
                   devflaglowconc : String,
                   qcsignoiseflag : String,
                   qcionratioflag : String,
                   qcrettimeflag : String,
                   qcpeakshapeflag : String,
                   signoiseflag : String,
                   signoise : String,
                   cdflag : String,
                   stddevflag : String,
                   rtflag : String,
                   peakasymmetry : String,
                   peakfrontwidth : String,
                   peaktailwidth : String,
                   peakasymmetryvalue : String,
                   percrecovery : String,
                   symflag : String,
                   percsym : String,
                   belowrl : String,
                   chromnoisehgt : String,
                   concdevperc : String,
                   lowerbound1 : String,
                   lowerbound2 : String,
                   lowerbound3 : String,
                   lowerbound4 : String,
                   mediumbound1 : String,
                   mediumbound2 : String,
                   mediumbound3 : String,
                   mediumbound4 : String,
                   upperbound1 : String,
                   upperbound2 : String,
                   upperbound3 : String,
                   upperbound4 : String,
                   nosolflag : String,
                   peakmissing : String,
                   peaksinc : String,
                   toxconc1 : String,
                   toxconc2 : String,
                   toxconc3 : String,
                   toxconc4 : String,
                   toxfactor1 : String,
                   toxfactor2 : String,
                   toxfactor3 : String,
                   toxfactor4 : String,
                   toxlod1 : String,
                   toxlod2 : String,
                   toxlod3 : String,
                   toxlod4 : String,
                   toxloq1 : String,
                   toxloq2 : String,
                   toxloq3 : String,
                   toxloq4 : String,
                   userfactor : String,
                   userrf : String,
                   picsforward : String,
                   picsreverse : String,
                   iFIT : String,
                   iFITnorm : String,
                   iFITconfidence : String,
                   foundmass : String,
                   mDamasserror : String,
                   ppmmasserror : String,
                   iFitflag : String,
                   iFitnormflag : String,
                   iFitconfflag : String,
                   mDaerrorflag : String,
                   ppmerrorflag : String,
                   isPeak : ISPeak
                 )

  object SecondaryPeaks {
    def fromXml(node: scala.xml.Node):SecondaryPeaks = {
      val secondarychromnoise = node \@ "secondarychromnoise"
      val actualIonRatio = node \@ "actualionratio"
      val irwinflag = node \@ "irwinflag"
      val area = node \@ "area"
      val heigth = node \@ "heigth"

      SecondaryPeaks(secondarychromnoise,actualIonRatio,irwinflag,area,heigth)
    }
  }
  case class SecondaryPeaks(
                             secondarychromnoise: String,
                             actualIonRatio:String,
                             irwinflag : String,
                             area : String,
                             heigth : String
                           )

  object ISPeak {
    def fromXml(node: scala.xml.Node):ISPeak = {
      val area = node \@ "area"
      val heigth = node \@ "heigth"
      val foundrt = node \@ "foundrt"
      ISPeak(area,heigth,foundrt)
    }
  }
  case class ISPeak(
                     area: String,
                     heigth:String,
                     foundrt : String)

  object Method {
    def fromXml(node: scala.xml.Node):Method = {
      val rref = node \@ "rref"
      val predrt = node \@ "predrt"
      val predrrt = node \@ "predrrt"
      val userfactor = node \@ "userfactor"
      val secondarytrace = node \@ "secondarytrace"

      Method(rref,predrt,predrrt,userfactor,secondarytrace)
    }
  }
  case class Method(
                     rref: String,
                     predrt:String,
                     predrrt : String,
                     userfactor : String,
                     secondarytrace : String
                   )

  def fromXml(node: scala.xml.Node):XMLQuantitativeDataProcessingMassLynx =
    XMLQuantitativeDataProcessingMassLynx(QuanDataset.fromXml((node \\ "QUANDATASET").head))

}

case class XMLQuantitativeDataProcessingMassLynx(dataset:QuanDataset) extends MassSpectrometryResultSet


