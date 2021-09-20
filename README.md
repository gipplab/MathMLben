# GoUldI

[![Build Status](https://travis-ci.org/ag-gipp/MathMLben.svg?branch=master)](https://travis-ci.org/ag-gipp/MathMLben)
[![Maintainability](https://api.codeclimate.com/v1/badges/d9f3590d68a5a6f9ab7d/maintainability)](https://codeclimate.com/github/ag-gipp/MathMLben/maintainability) [![Greenkeeper badge](https://badges.greenkeeper.io/ag-gipp/MathMLben.svg)](https://greenkeeper.io/)

Please use the website https://mathmlben.wmflabs.org to make changes to the data.

# Cite this Work

Please cite our work if you use MathMLben:
```bibtex
@inproceedings{SchubotzGSMCG18,
  author = {Schubotz, Moritz and Greiner-Petter, Andr\'{e} and Scharpf, Philipp and Meuschke, Norman and Cohl, Howard S. and Gipp, Bela},
	address = {Fort Worth, Texas, USA},
	title = {Improving the {Representation} and {Conversion} of {Mathematical} {Formulae} by {Considering} their {Textual} {Context}},
	isbn = {978-1-4503-5178-2},
	url = {https://arxiv.org/abs/1804.04956},
	doi = {10.1145/3197026.3197058},
	booktitle = {Proceedings of the 18th {ACM}/{IEEE} on {Joint} {Conference} on {Digital} {Libraries} ({JCDL})},
	publisher = {ACM},
	month = may,
	year = {2018},
	keywords = {mathml, mathmlben, survey, translation},
	pages = {233--242},
}
```

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
