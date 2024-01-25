FROM ubuntu
LABEL authors="Endera"

WORKDIR /root

COPY prepare_ubuntu.sh prepare_ubuntu.sh

RUN bash prepare_ubuntu.sh

ENV LANG C.UTF-8

ENTRYPOINT ["/bin/bash"]