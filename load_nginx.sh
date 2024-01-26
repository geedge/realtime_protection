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

# 使用host模式进行Nginx服务器部署，方便在中心部署时不会遭遇容器内部无法访问外部网络的问题
# 使用privileged模式，防止中心部署时无法启动Nginx服务器
docker run --privileged --network host -itd --name nginx_frontend protection_nginx
echo "============started frontend servers============"
