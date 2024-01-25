FROM protection_ubuntu:latest
LABEL authors="Endera"

COPY realtime_protection realtime_protection

ENV SPRINGBOOT_FIRST_URL="172.24.100.53:8081" \
    SPRINGBOOT_SECOND_URL="172.24.100.53:8082" \
    SPRINGBOOT_THIRD_URL="172.24.100.53:8083" \
    SPRINGBOOT_FOURTH_URL="172.24.100.53:8084" \
    NGINX_PORT=8080 \
    NGINX_SERVER_NAME="localhost"

EXPOSE 8080

ENTRYPOINT ["bash", "realtime_protection/docker-nginx/start_nginx.sh"]