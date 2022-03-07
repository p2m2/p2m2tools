# galaxy install directory

https://galaxy-p2m2-192-168-100-198.vm.openstack.genouest.org/

## VM configuration

Debian 10.10

```shell
ssh debian@192.168.100.198
```
## config genouest

si *test-galaxy* est le nom de la machine
``` 
dans /etc/hosts
127.0.0.1 test-galaxy
```

## mount directory

- doc : https://www.genouest.org/outils/genostack/volumes.html
- /mnt/galaxy

```shell
sudo -i
lsblk -f
mkfs.ext4 /dev/vdb
mkdir /mnt/galaxy
mount /dev/vdb /mnt/galaxy
```


## docker image
 
see https://github.com/bgruening/docker-galaxy-stable

```shell
cd /mnt/galaxy
git clone https://github.com/bgruening/docker-galaxy-stable.git
```

## run 

### stop apache to release port 80

`service apache2 stop`

## config (ADMIN_USER,....)


```shell
cp /mnt/galaxy/docker-galaxy-stable/compose/docker-compose.yml docker-compose.yml 
vi docker-compose.yml 
cp docker-compose.yml /mnt/galaxy/docker-galaxy-stable/compose/
```


### run galaxy
```shell
cd /mnt/galaxy/docker-galaxy-stable/compose/
docker-compose up -d
```

### install tool 

- doc: https://galaxyproject.org/admin/tools/add-tool-tutorial/
- /mnt/galaxy/docker-galaxy-stable/compose/export/galaxy

#### copy tool

`sudo cp -r /home/debian/p2m2tools/galaxy/gcms2isocor /mnt/galaxy/docker-galaxy-stable/compose/export/galaxy/tools/

### add tool menu 
if tool_conf.xml exist : 
`cp tool_conf.xml /mnt/galaxy/docker-galaxy-stable/compose/export/galaxy/config/`

or create `/mnt/galaxy/docker-galaxy-stable/compose/export/galaxy/config/tool_conf.xml`
```
<?xml version="1.0"?>
<toolbox>
  <section name="Metabolomics" id="metabolomics">
        <tool file="gcms2isocor/gcms2isocor.xml" />
  </section>

 <section name="Metabolomics" id="metabolomics">
        <tool file="openlabcds2csv/openlabcds2csv.xml" />
 </section>


 <section name="Metabolomics" id="metabolomics">
     <tool file="masslynx2isocor/masslynx2isocor.xml" />
 </section>

</toolbox>
```
