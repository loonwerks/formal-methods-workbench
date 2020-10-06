ARG BASE_IMG=maven:3.6.0-jdk-8
#ARG BASE_IMG=ubuntu:18.04
FROM $BASE_IMG

ARG UID
ARG UNAME
ARG GID
ARG GROUP

RUN groupadd -fg "${GID}" "${GROUP}" \
  && groupmod -g "${GID}" "${GROUP}" \
  && useradd -u "${UID}" -g "${GID}" "${UNAME}" \
  && passwd -d "${UNAME}" \
  && mkdir "/home/${UNAME}" \
  && chown -R "${UNAME}":"${GROUP}" "/home/${UNAME}" \
  && chmod -R ug+rw "/home/${UNAME}"

VOLUME /home/${UNAME}

RUN apt-get -q update \
  && apt-get -y -q install curl sudo vim wget \
  && apt-get clean autoclean \
  && apt-get autoremove --purge --yes \
  && rm -rf /var/lib/apt/lists/*

