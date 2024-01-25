FROM protection_ubuntu:latest
LABEL authors="Endera"

COPY realtime_protection realtime_protection

ENV SPRINGBOOT_FIRST_URL="localhost:8081" \
    SPRINGBOOT_SECOND_URL="localhost:8082" \
    SPRINGBOOT_THIRD_URL="localhost:8083" \
    SPRINGBOOT_FOURTH_URL="localhost:8084" \
    NGINX_PORT=8080 \
    NGINX_SERVER_NAME="localhost"

EXPOSE 8080

ENTRYPOINT ["bash", "realtime_protection/docker-nginx/start_nginx.sh"]