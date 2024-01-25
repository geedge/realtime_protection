#!/usr/bin/env bash

# 避免build等文件夹被上传引发问题
rm -rf build
rm -rf log
rm -rf .gradle

cd ..

echo "============start loading springboot============"
docker rm -f $(docker ps -a | grep springboot_backend | awk '{print $1}')
docker image rm protection_springboot
echo "============old containers and images are cleared============"

docker load < realtime_protection/docker-ubuntu/protection_ubuntu.tar

docker build -t protection_springboot -f realtime_protection/docker-springboot/Dockerfile .
echo "============built backend servers============"

docker run -itd --name springboot_backend -p 8081:8081 protection_springboot
echo "============started backend servers============"