#!/usr/bin/env bash

# 初始化变量设定
export set BASEDIR="/root"
export set PROTECTION_DIR="$BASEDIR/realtime_protection"
export set GRADLE_USER_HOME="$PROTECTION_DIR/gradle"

# 启动gradle，编译java
cd $PROTECTION_DIR
chmod +x ./gradlew
./gradlew clean && ./gradlew build

# 启动SpringBoot服务
./gradlew bootRun --args="--spring.profiles.active=prod"