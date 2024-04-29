FROM protection_ubuntu:latest
LABEL authors="Endera"

COPY realtime_protection realtime_protection

#ENV MYSQL_USERNAME="root" \
#    MYSQL_PASSWD="IOmysql" \
#    MYSQL_URL="10.58.72.140:3306" \
#    DORIS_USERNAME="root" \
#    DORIS_PASSWD="" \
#    DORIS_URL="10.58.72.135:9030"\
#    KAFKA_SERVERS="10.58.72.125:9092,10.58.72.126:9092,10.58.72.127:9092,10.58.72.128:9092,10.58.72.129:9092,\
#     10.58.72.130:9092,10.58.72.131:9092,10.58.72.132:9092,10.58.72.133:9092,10.58.72.134:9092"


ENV MYSQL_USERNAME="root" \
    MYSQL_PASSWD="aiihhbfcsy123!@#" \
    MYSQL_URL="192.168.107.89:3306" \
    DORIS_USERNAME="root" \
    DORIS_PASSWD="" \
    DORIS_URL="10.26.22.133:9030"\
    KAFKA_SERVERS="172.29.128.1:9092"\



EXPOSE 8081

ENTRYPOINT ["bash", "realtime_protection/docker-springboot/start_springboot.sh"]