## Testing package

conda create --use-local -n test p2m2tools
conda activate test

java -cp $CONDA_PREFIX/jar/p2m2tools.jar fr.inrae.metabolomics.p2m2.OpenLabCDS2CsvCommand

doc : https://bioconda.github.io/contributor/building-locally.html

## circle-ci

circleci config validate
circleci config process .circleci/config.yml
circleci local execute --job compile
circleci local execute --job test_and_coverage_jvm

