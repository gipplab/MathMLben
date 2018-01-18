#!/usr/bin/env bash
echo "Content-type: text/plain"
echo ""

#SetEnv ist not supported with suexec
export HOME=/home/gouldi
export NPM_PACKAGES="$HOME/.npm-packages"
export PATH="$NPM_PACKAGES/bin:$PATH"
unset MANPATH
export MANPATH="$NPM_PACKAGES/share/man:$(manpath)"
git pull
pm2 deploy production update
sudo /etc/init.d/apache2 graceful
