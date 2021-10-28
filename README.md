# P2M2Tools


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


