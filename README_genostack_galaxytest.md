# galaxy install directory

https://galaxy-p2m2-192-168-100-198.vm.openstack.genouest.org/


## mount directory

- doc : https://www.genouest.org/outils/genostack/volumes.html
- /mnt/galaxy

```
sudo -i
lsblk -f
mkfs.ext4 /dev/vdb
mkdir /mnt/galaxy
mount /dev/vdb /mnt/galaxy
```


## docker image
 
see https://github.com/bgruening/docker-galaxy-stable

```
cd /mnt/galaxy
git clone https://github.com/bgruening/docker-galaxy-stable.git
```

## run 

### stop apache to release port 80

`service apache2 stop`

## config (ADMIN_USER,....)


```
cp /mnt/galaxy/docker-galaxy-stable/compose/docker-compose.yml docker-compose.yml 
vi docker-compose.yml 
cp docker-compose.yml /mnt/galaxy/docker-galaxy-stable/compose/
```


### run galaxy
```
cd /mnt/galaxy/docker-galaxy-stable/compose/
docker-compose up -d
```

### install tool 

- doc: https://galaxyproject.org/admin/tools/add-tool-tutorial/
- /mnt/galaxy/docker-galaxy-stable/compose/export/galaxy

#### copy tool

`sudo cp -r /home/debian/MetabolomicsWorkflowTools/galaxy/gcms2isocor /mnt/galaxy/docker-galaxy-stable/compose/export/galaxy/tools/

### add tool menu 
if tool_conf.xml exist : 
`cp tool_conf.xml /mnt/galaxy/docker-galaxy-stable/compose/export/galaxy/config/`

or add section in `/mnt/galaxy/docker-galaxy-stable/compose/export/galaxy/config/tool_conf.xml`
```
<section name="Metabolomics" id="metabolomics">
	  <tool file="gcms2isocor/gcms2isocor.xml" />
  </section>
```
