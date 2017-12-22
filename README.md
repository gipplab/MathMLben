# GoUldI

[![Build Status](https://travis-ci.org/ag-gipp/GoUldI.svg?branch=master)](https://travis-ci.org/ag-gipp/GoUldI)


### notes on deployment

* log in to the server (e.g., drmf-beta)
* run
```bash
sudo useradd gouldi
sudo mkdir /srv/gouli
sudo chown gouldi /srv/gouldi
sudo usermod -aG sudo gouldi
sudo su gouldi
mkdir "${HOME}/.npm-packages"
echo "prefix=${HOME}/.npm-packages" > ~/.npmrc
vi .bashrc
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