FROM protection_ubuntu:latest
LABEL authors="Endera"

COPY realtime_protection realtime_protection

EXPOSE 80

ENTRYPOINT ["bash", "realtime_protection/docker-nginx/start_nginx.sh"]