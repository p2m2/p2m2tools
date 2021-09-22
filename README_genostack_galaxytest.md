# galaxy install directory

https://galaxy-p2m2-192-168-100-198.vm.openstack.genouest.org/


## mount directory

- doc : https://www.genouest.org/outils/genostack/volumes.html
- /mnt/galaxy

## docker image
 
see https://github.com/bgruening/docker-galaxy-stable

## run 

### stop apache
service apache2 stop

## config

vi /mnt/galaxy/docker-galaxy-stable/compose/docker-compose.yml

### run galaxy

cp docker-compose.yml /mnt/galaxy/docker-galaxy-stable/compose/
cd /mnt/galaxy/docker-galaxy-stable/compose/
docker-compose up -d

### install tool 

- doc: https://galaxyproject.org/admin/tools/add-tool-tutorial/
- /mnt/galaxy/docker-galaxy-stable/compose/export/galaxy

#### copy tool

`sudo cp -r /home/debian/MetabolomicsWorkflowTools/galaxy/gcms2isocor /mnt/galaxy/docker-galaxy-stable/compose/export/galaxy/tools/

### add tool menu 

`cp tool_conf.xml /mnt/galaxy/docker-galaxy-stable/compose/export/galaxy/config/`

