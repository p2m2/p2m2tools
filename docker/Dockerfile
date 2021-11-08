FROM openjdk:jdk-bullseye

LABEL author="Olivier Filangi"
LABEL mail="olivier.filangi@inrae.fr" 

ARG BRANCH=develop
ARG REPOSITORY_URL=https://github.com/p2m2/p2m2tools.git

# install sbt:https://www.scala-sbt.org/1.x/docs/Installing-sbt-on-Linux.html

RUN apt update &&\
    apt install -y git scala apt-transport-https curl gnupg -yqq &&\
    echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" | tee /etc/apt/sources.list.d/sbt.list &&\
    echo "deb https://repo.scala-sbt.org/scalasbt/debian /" | tee /etc/apt/sources.list.d/sbt_old.list &&\
    curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" \
      | gpg --no-default-keyring --keyring gnupg-ring:/etc/apt/trusted.gpg.d/scalasbt-release.gpg --import && \
    chmod 644 /etc/apt/trusted.gpg.d/scalasbt-release.gpg && \
    apt-get update && apt-get install sbt

RUN git clone ${REPOSITORY_URL} /p2m2tools_repo

WORKDIR /p2m2tools_repo

RUN git checkout ${BRANCH} && sbt coverageOff assembly

COPY p2m2tools /usr/bin/

RUN apt-get clean && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*


