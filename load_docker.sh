#!/usr/bin/env bash

# 避免build等文件夹被上传引发问题
./gradlew clean
rm -rf log
rm -rf .gradle

cd ..

docker stop nginx_frontend && docker rm nginx_frontend
docker stop springboot_backend && docker rm springboot_backend
docker image rm protection_springboot protection_nginx
echo "old containers and images are cleared"

docker load < realtime_protection/docker-ubuntu/protection_ubuntu.tar

docker build -t protection_nginx -f realtime_protection/docker-nginx/Dockerfile .
docker build -t protection_springboot -f realtime_protection/docker-springboot/Dockerfile .
echo "built frontend and backend servers"

docker run -itd --name nginx_frontend -p 80:80 protection_nginx
docker run -itd --name springboot_backend -p 8081:8081 protection_springboot
echo "started frontend and backend servers"