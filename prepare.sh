#!/usr/bin/env bash

# 初始化变量设定
export set BASEDIR="/root"
export set PROTECTION_DIR="$BASEDIR/realtime_protection"

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
apt install sudo vim git wget openjdk-17-jdk -y

# 下载git仓库内容
echo https://EnderByEndera:8Bs8hxHtE-iq44g@git.mesalab.cn > $BASEDIR/.git-credentials
git config --global credential.helper store
git clone https://git.mesalab.cn/EnderByEndera/realtime_protection.git $BASEDIR/realtime_protection

# 启动gradle，编译java
cd $PROTECTION_DIR
chmod +x ./gradlew
./gradlew clean && ./gradlew build

# 启动SpringBoot开启服务
java -jar $PROTECTION_DIR/build/libs/protection-0.0.3-SNAPSHOT.jar --spring.profiles.active=prod