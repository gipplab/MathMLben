# GoUldI

[![Build Status](https://travis-ci.org/ag-gipp/MathMLben.svg?branch=master)](https://travis-ci.org/ag-gipp/MathMLben)
[![Maintainability](https://api.codeclimate.com/v1/badges/d9f3590d68a5a6f9ab7d/maintainability)](https://codeclimate.com/github/ag-gipp/MathMLben/maintainability) [![Greenkeeper badge](https://badges.greenkeeper.io/ag-gipp/MathMLben.svg)](https://greenkeeper.io/)

Please use the website https://mathmlben.wmflabs.org to make changes to the data.

### notes on deployment

* log in to the server (e.g., drmf-beta)
* run
```bash
sudo useradd gouldi
sudo mkdir /srv/gouldi
sudo chown gouldi /srv/gouldi
sudo su gouldi
mkdir "${HOME}/.npm-packages"
echo "prefix=${HOME}/.npm-packages" > ~/.npmrc
vi .bashrc
```
* Allow gouldi to restart apache

use  `visudo` to add the following config
```bash
username ALL = NOPASSWD: /etc/init.d/apache2 
```
* type Gi to reach the end of the file and switch vi to insert mode and paste the following block
```bash
NPM_PACKAGES="${HOME}/.npm-packages"

PATH="$NPM_PACKAGES/bin:$PATH"

# Unset manpath so we can inherit from /etc/manpath via the `manpath` command
unset MANPATH # delete if you already modified MANPATH elsewhere in your config
export MANPATH="$NPM_PACKAGES/share/man:$(manpath)"
```
* run
```bash
npm i -g pm2
git clone https://github.com/ag-gipp/GoUldI
cd GoUldI/
pm2 deploy ecosystem.json production setup
```
* update
```bash
sudo su gouldi
cd GoUldI
git pull
pm2 deploy production update
```

## automatic deployment via webhook

```bash
sudo apt-get install apache2-suexec-custom
sudo a2enmod suexec
sudo service apache2 restart

drmf-beta:/etc/apache2/sites-available# cat 100-gouldi-deploy.conf 
Listen 34513 
<VirtualHost *:34513>
ServerName gouldi-deploy.wmflabs.org 
SuexecUserGroup gouldi gouldi
ScriptAlias "/cgi-bin/" "/home/gouldi/GoUldI/"
DocumentRoot /home/gouldi/GoUldI
<Directory /home/gouldi/GoUldI>
Options Indexes FollowSymLinks
AllowOverride None
Require all granted
</Directory>
</VirtualHost>
```
