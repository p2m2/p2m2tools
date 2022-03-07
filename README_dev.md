## Compilation

``` 
sbt assembly
```
### Tools

```shell
JAR=assembly/$(ls  assembly/ | tail -n1)
```

### Gcms2Isocor

```
COMMAND="fr.inrae.metabolomics.p2m2.command.GCMS2IsocorCommand"
java -cp $JAR $COMMAND --help
java -cp $JAR $COMMAND src/test/resources/GCMS/13CPROT1.txt src/test/resources/GCMS/13CPROT2.txt --out input_isocor.tsv
```

### Input Data / GCMS PostRun Analysis

Export Data -> Compound Quantitative Results

### Header - Name format

name -> extraire (metabolite/derivative/isotologue)
#### example glycosate / TMS / m0
"sample"_"replicat"_"passeur"

### OpenLabCDS2Csv

```
COMMAND="fr.inrae.metabolomics.p2m2.command.OpenLabCDS2CsvCommand"
java -cp $JAR $COMMAND --help
java -cp $JAR $COMMAND src/test/resources/OpenLabCDS/Report_Ex1.txt src/test/resources/OpenLabCDS/Report_Ex2.txt src/test/resources/OpenLabCDS/Report_Ex3.txt --out text.csv
```

### 

```
COMMAND="fr.inrae.metabolomics.p2m2.command.MassLynx2IsocorCommand"
java -cp $JAR $COMMAND --help
java -cp $JAR $COMMAND src/test/resources/MassLynx/mass_15Ngly.txt -d src/test/resources/MassLynx/correspondence_derivatives.txt --out input_isocor.tsv
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
p2m2tools fr.inrae.metabolomics.p2m2.command.OpenLabCDS2CsvCommand --help
```
### commands

- fr.inrae.metabolomics.p2m2.command.OpenLabCDS2CsvCommand
- fr.inrae.metabolomics.p2m2.command.GCMS2IsocorCommand
- fr.inrae.metabolomics.p2m2.command.MassLynx2IsocorCommand

## circle-ci

circleci config validate
circleci config process .circleci/config.yml
circleci local execute --job compile
circleci local execute --job test_and_coverage_jvm

