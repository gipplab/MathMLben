# GoUldI

[![Build Status](https://travis-ci.org/ag-gipp/GoUldI.svg?branch=master)](https://travis-ci.org/ag-gipp/GoUldI)
[![Maintainability](https://api.codeclimate.com/v1/badges/1a369c013f69caa8b3ac/maintainability)](https://codeclimate.com/github/ag-gipp/GoUldI/maintainability)

Openly available benchmark dataset to the evaluate tools for mathematical format conversion (LaTeX <-> MathML <-> CAS).
The Gold Standard comprises 305 mathematical formulae (1-100 extracted from the NTCIR 11 Math Wikipedia, 101-200 from the
NIST Digital Library of Mathematical Functions (DLMF), 201-305 from the NTCIR arXiv and NTCIR-12 Wikipedia datasets
Task).

GUI to makes changes to the data available: https://gouldi.wmflabs.org
with input fields for formula name and type (definition, equation, relation or general formula), original and corrected TeX, hyperlink to the original formula (source) and most importantly a semantic Tex field for annotations (DLMF macros, Wikidata QIDs).
The expression tree preview visualization is provided by VMEXT (https://github.com/ag-gipp/vmext).

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
