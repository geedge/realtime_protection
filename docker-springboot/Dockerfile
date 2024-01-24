FROM protection_ubuntu:latest
LABEL authors="Endera"

COPY realtime_protection realtime_protection

EXPOSE 8081

ENTRYPOINT ["bash", "realtime_protection/docker-springboot/start_springboot.sh"]