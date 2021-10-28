## Testing package

conda create --use-local -n test p2m2tools
conda activate test

java -cp $CONDA_PREFIX/jar/p2m2tools.jar fr.inrae.metabolomics.p2m2.OpenLabCDS2CsvCommand

