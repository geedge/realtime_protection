#!/usr/bin/env bash

# 初始化变量设定
export set BASEDIR="/root"
export set PROTECTION_DIR="$BASEDIR/realtime_protection"
export set PROTECTION_NGINX_DIR="$PROTECTION_DIR/src/main/resources/nginx"
export set PROJECT_NGINX_DIR="$BASEDIR/nginx"
export set GLOBAL_NGINX_DIR="/etc/nginx"

envsubst '${SPRINGBOOT_FIRST_URL} ${SPRINGBOOT_SECOND_URL} ${SPRINGBOOT_THIRD_URL} ${SPRINGBOOT_FOURTH_URL} ${NGINX_PORT} ${NGINX_SERVER_NAME}' < $PROTECTION_NGINX_DIR/configs/project_template.conf > $PROTECTION_NGINX_DIR/configs/project.conf

# 拷贝Nginx配置到指定地址
cp -r $PROTECTION_NGINX_DIR $BASEDIR
cp $PROTECTION_NGINX_DIR/nginx.conf $GLOBAL_NGINX_DIR

# 启动Nginx服务
service nginx start

# 控制Docker容器挂起
top -b