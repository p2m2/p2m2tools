# MetabolomicsWorkflowTools


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


####

``` 
sbt "run src/test/resources/13CPROT1.txt src/test/resources/13CPROT2.txt --out input_isocor.tsv"
sbt assembly
```

``` 
sbt assembly
gcms2isocor --help
```
