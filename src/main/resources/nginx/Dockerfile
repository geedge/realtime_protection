FROM nginx

LABEL authors="Endera"

COPY prepare.sh /root/prepare.sh

EXPOSE 8080

ENTRYPOINT ["bash", "/root/prepare.sh"]