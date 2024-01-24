#!/usr/bin/env bash

# 初始化变量设定
export set BASEDIR="/root"
export set PROTECTION_DIR="$BASEDIR/realtime_protection"
export set PROTECTION_NGINX_DIR="$PROTECTION_DIR/src/main/resources/nginx"
export set PROJECT_NGINX_DIR="$BASEDIR/nginx"
export set GLOBAL_NGINX_DIR="/etc/nginx"

# 下载git仓库内容
echo https://EnderByEndera:8Bs8hxHtE-iq44g@git.mesalab.cn > $BASEDIR/.git-credentials
git config --global credential.helper store
git clone https://git.mesalab.cn/EnderByEndera/realtime_protection.git $BASEDIR/realtime_protection

# 拷贝Nginx配置到指定地址
cp -r $PROTECTION_NGINX_DIR $BASEDIR
cp $PROTECTION_NGINX_DIR/nginx.conf $GLOBAL_NGINX_DIR