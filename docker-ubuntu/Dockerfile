FROM ubuntu:latest
LABEL authors="Endera"

WORKDIR /root

COPY prepare_ubuntu.sh prepare.sh

RUN bash prepare.sh

ENTRYPOINT ["/bin/bash"]