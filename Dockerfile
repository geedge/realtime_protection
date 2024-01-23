FROM ubuntu:latest

LABEL authors="Endera"

EXPOSE 8081

COPY prepare.sh /root/prepare.sh

ENTRYPOINT ["bash", "/root/prepare.sh"]