## Compilation

``` 
sbt assembly
```
### Tools

### Gcms2Isocor

``` 
./gcms2isocor --help
./gcms2isocor src/test/resources/GCMS/13CPROT1.txt src/test/resources/GCMS/13CPROT2.txt --out input_isocor.tsv
```

### OpenLabCDS2Csv

```
./openLabCds2Csv --help
./openLabCds2Csv src/test/resources/OpenLabCDS/Report_Ex1.txt src/test/resources/OpenLabCDS/Report_Ex2.txt src/test/resources/OpenLabCDS/Report_Ex3.txt --out text.csv
```

## Create conda package

```
conda-build conda-recipe
```

## Testing package

doc : https://bioconda.github.io/contributor/building-locally.html


``` 
conda create --use-local -n test p2m2tools
conda activate test
```

```
p2m2tools fr.inrae.metabolomics.p2m2.OpenLabCDS2CsvCommand --help
```


## circle-ci

circleci config validate
circleci config process .circleci/config.yml
circleci local execute --job compile
circleci local execute --job test_and_coverage_jvm

