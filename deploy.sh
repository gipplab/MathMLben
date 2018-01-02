#!/usr/bin/env bash
echo "Content-type: text/plain"
echo ""
git pull
pm2 deploy production update