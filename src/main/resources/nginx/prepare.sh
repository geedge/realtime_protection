#!/usr/bin/env bash

# 初始化变量设定
export set BASEDIR="/root"
export set PROTECTION_DIR="$BASEDIR/realtime_protection"
export set NGINX_DIR="$PROTECTION_DIR/src/main/resources/nginx"

# 下载必须依赖
bash -c "cat << EOF > /etc/apt/sources.list && apt update
deb http://mirrors.aliyun.com/ubuntu/ jammy main restricted universe multiverse
deb-src http://mirrors.aliyun.com/ubuntu/ jammy main restricted universe multiverse
deb http://mirrors.aliyun.com/ubuntu/ jammy-security main restricted universe multiverse
deb-src http://mirrors.aliyun.com/ubuntu/ jammy-security main restricted universe multiverse
deb http://mirrors.aliyun.com/ubuntu/ jammy-updates main restricted universe multiverse
deb-src http://mirrors.aliyun.com/ubuntu/ jammy-updates main restricted universe multiverse
deb http://mirrors.aliyun.com/ubuntu/ jammy-proposed main restricted universe multiverse
deb-src http://mirrors.aliyun.com/ubuntu/ jammy-proposed main restricted universe multiverse
deb http://mirrors.aliyun.com/ubuntu/ jammy-backports main restricted universe multiverse
deb-src http://mirrors.aliyun.com/ubuntu/ jammy-backports main restricted universe multiverse
EOF"
apt install sudo vim git wget screen nginx -y

# 下载git仓库内容
echo https://EnderByEndera:8Bs8hxHtE-iq44g@git.mesalab.cn > $BASEDIR/.git-credentials
git config --global credential.helper store
git clone https://git.mesalab.cn/EnderByEndera/realtime_protection.git $BASEDIR/realtime_protection

# 拷贝Nginx配置到指定地址
cd $NGINX_DIR
cp -r ../nginx /root/
cp nginx.conf /etc/nginx

# 启动Nginx服务
service nginx start