# P2M2Tools

[![CircleCI](https://circleci.com/gh/p2m2/p2m2tools.svg?style=shield)](https://circleci.com/gh/p2m2/p2m2tools)
[![codecov](https://codecov.io/gh/p2m2/p2m2tools/branch/develop/graph/badge.svg)](https://codecov.io/gh/p2m2/p2m2)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/9db61bd9732740c79a39de678c6e5246)](https://www.codacy.com/gh/p2m2/p2m2tools/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=p2m2/p2m2tools&amp;utm_campaign=Badge_Grade)

Development of bioinformatics tools/software related to P2M2 platform (IGEPP's Metabolic Profiling and Metabolomic Platform) activities.
All the tools developed are accessible via the Galaxy instance of the Genouest platform (https://galaxy.genouest.org/)

## GCMS2Isocor

Corrective method dedicated to Isocor for calculating carbon isotopologue distribution from GCMS runs.
A P2M2  workflow was built to obtain carbon isotopologue distribution of specific metabolites (complete list of metabolites available in the file "Metabolite.dat") from GC-MS raw data files. The input files for this workflow can be any GC-MS raw dataset that contains a column "Name" filled with each carbon isotopologue of each fragment considered and a column "Area" filled with the area of the integrated peak. The name of each fragment must be written exactly as specified in the "Metabolite.dat" file to ensure accurate correction with IsoCor. Example: the name "ProlineC2C5_TMS_m0" is for the GC-MS fragment m/z 142 (integrated peak) containing the C2-C3-C4-C5 carbon skeleton of proline and 1 TMS derivative. m0 refers to the carbon isotopologue monitored (m0 for m/z = 142, m1 for m/z = 143, m2 for m/z = 144, m3 for m/z = 145, m4 for m/z = 146). (2021-10-17) (2021-10-17)
https://doi.org/10.15454/1I9PET

[Galaxy worklow](https://galaxy.genouest.org/u/ofilangi-1/w/corrective-method-dedicated-to-isocor-for-calculating-carbon-isotopologue-distribution-from-gcms-runs-5)

### Targeted P2M2 device

| **Manufacturer** | **Model**     |
|-----------------|---------------| 
| GCMS Shimadzu   | GCTQD (TQ8040) |



## OpenLabCds2Csv

Get multiple "Internal Standard Report" from the OpenLabCDS software where are describe a list of compound in format row (columns : RetTime  Type  ISTD    Area     Amt/Area    Amount   Grp   Name)
The converter creates a summary that contains a header (a list of compounds) and a list of "Sample name" with associated values ​​for a target column (RetTime,Type,ISTD,Area,Amt/Area,Amount,Grp,Name)

### Targeted P2M2 device

| **Manufacturer** | **Model**            |
|----------------|----------------------| 
| Agilent        | GC-FID Agilent 6890N |

## MassLynx2Isocor

Build Isocor input file from MassLynx report ("Quantify Compound Summary Report") 

### Targeted P2M2 device

| **Manufacturer** | **Model**        |
|-----------------|------------------| 
| Waters Acquity  | HPLC TQD         |


