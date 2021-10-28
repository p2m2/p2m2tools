cd "%SRC_DIR%"
md %PREFIX%\jar
if errorlevel 1 exit 1

copy *.jar %PREFIX%\jar\
if errorlevel 1 exit 1

