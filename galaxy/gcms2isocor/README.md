### generation avec planemo

cp ../../target/scala-2.13/P2M2WorkflowsTools-assembly-0.1.jar .

planemo tool_init --force --id 'gcms2isocor' --name 'Conversion GCMS PostRun Analysis to IsoCor' --example_command 'gcms2isocor 13CPROT1.txt 13CPROT2.txt --out input_isocor.tsv' --example_input 13CPROT1.txt --example_input 13CPROT2.txt --example_output input_isocor.tsv --test_case --cite_url 'https://github.com/p2m2/MetabolomicsWorkflowTools' --help_from_command 'gcms2isocor'

dans gcms2isocor.xml balise <command>

`gcms2isocor` doit etre remplacer par `java -jar '$__tool_directory__/P2M2WorkflowsTools-assembly-0.1.jar'`

planemo l
planemo t

### todo

paquet bioconda
