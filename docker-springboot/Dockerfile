FROM protection_ubuntu:latest
LABEL authors="Endera"

COPY realtime_protection realtime_protection

ENV MYSQL_USERNAME="root" \
    MYSQL_PASSWD="aiihhbfcsy123!@#" \
    MYSQL_URL="192.168.107.89:3306" \
    DORIS_USERNAME="root" \
    DORIS_PASSWD="" \
    DORIS_URL="10.26.22.133:9030"

EXPOSE 8081

ENTRYPOINT ["bash", "realtime_protection/docker-springboot/start_springboot.sh"]