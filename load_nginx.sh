#!/usr/bin/env bash

# 避免build等文件夹被上传引发问题
rm -rf build
rm -rf log
rm -rf .gradle

cd ..

echo "============start loading nginx============"
docker rm -f $(docker ps -a | grep nginx_frontend | awk '{print $1}')
docker image rm protection_nginx
echo "============old containers and images are cleared============"

docker load < realtime_protection/docker-ubuntu/protection_ubuntu.tar

docker build -t protection_nginx -f realtime_protection/docker-nginx/Dockerfile .
echo "============built frontend servers============"

docker run -itd --name nginx_frontend -p 8080:8080 protection_nginx
echo "============started frontend servers============"
