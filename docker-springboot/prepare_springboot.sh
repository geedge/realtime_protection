#!/usr/bin/env bash

# 初始化变量设定
export set BASEDIR="/root"
export set PROTECTION_DIR="$BASEDIR/realtime_protection"

# 下载git仓库内容
echo https://EnderByEndera:8Bs8hxHtE-iq44g@git.mesalab.cn > $BASEDIR/.git-credentials
git config --global credential.helper store
git clone https://git.mesalab.cn/EnderByEndera/realtime_protection.git $BASEDIR/realtime_protection

# 启动gradle
cd $PROTECTION_DIR
chmod +x ./gradlew
./gradlew clean