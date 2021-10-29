# P2M2Tools

[![CircleCI](https://circleci.com/gh/p2m2/p2m2tools/tree/main.svg?style=svg)](https://circleci.com/gh/p2m2/p2m2tools/tree/main)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/9db61bd9732740c79a39de678c6e5246)](https://www.codacy.com/gh/p2m2/p2m2tools/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=p2m2/p2m2tools&amp;utm_campaign=Badge_Grade)

```sbt
test
```

## GCMS2Isocor

Conversion from GCMS PostRun Analysis to Isocore

### Input Data / GCMS PostRun Analysis

Export Data -> Compound Quantitative Results

### Header - Name format

name -> extraire (metabolite/derivative/isotologue)  
#### example glycosate / TMS / m0
"sample"_"replicat"_"passeur"


#### test

``` 
sbt test
```
## Compilation

``` 
sbt assembly
```

## Gcms2Isocor

``` 
./gcms2isocor --help
./gcms2isocor src/test/resources/GCMS/13CPROT1.txt src/test/resources/GCMS/13CPROT2.txt --out input_isocor.tsv
```

## OpenLabCDS2Csv

```
./openLabCds2Csv --help
./openLabCds2Csv src/test/resources/OpenLabCDS/Report_Ex1.txt src/test/resources/OpenLabCDS/Report_Ex2.txt src/test/resources/OpenLabCDS/Report_Ex3.txt --out text.csv
```


