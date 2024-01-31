FROM apache/incubator-doris:build-env-latest

USER root
WORKDIR /root
RUN echo '123456' | passwd root --stdin

RUN yum install -y vim net-tools man wget git mysql lsof bash-completion \
        && cp /var/local/thirdparty/installed/bin/thrift /usr/bin

# 更安全的使用，创建用户而不是使用 root
RUN yum install -y sudo \
        && useradd -ms /bin/bash endera && echo 123456 | passwd endera --stdin \
        && usermod -a -G wheel endera

USER endera
WORKDIR /home/endera
RUN git config --global color.ui true \
        && git config --global user.email "707475564@qq.com" \
        && git config --global user.name "EnderByEndera"