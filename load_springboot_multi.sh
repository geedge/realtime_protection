#!/usr/bin/env bash

# 此脚本仅用于测试Nginx负载均衡

# 避免build等文件夹被上传引发问题
rm -rf build
rm -rf log
rm -rf .gradle

cd ..

echo "============start loading springboot============"
docker rm -f $(docker ps -a | grep springboot_backend | awk '{print $1}')
docker image rm protection_springboot protection_ubuntu
echo "============old containers and images are cleared============"

docker load < realtime_protection/docker-ubuntu/protection_ubuntu.tar

docker build -t protection_springboot -f realtime_protection/docker-springboot/Dockerfile .
echo "============built backend servers============"

# 尝试开启多个SpringBoot
docker run --privileged -itd --name springboot_backend_1 -p 8081:8081 protection_springboot
docker run --privileged -itd --name springboot_backend_2 -p 8082:8081 protection_springboot
docker run --privileged -itd --name springboot_backend_3 -p 8083:8081 protection_springboot
docker run --privileged -itd --name springboot_backend_4 -p 8084:8081 protection_springboot
echo "============started backend servers============"