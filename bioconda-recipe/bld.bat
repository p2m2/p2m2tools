REM first arg should be the class main (example : fr.inrae.metabolomics.p2m2.OpenLabCDS2CsvCommand) following by the argument corresponding to the implemented command.
set args=%*
%JAVA_HOME%\bin\java -cp p2m2tools.jar  %args%
if errorlevel 1 exit 1

