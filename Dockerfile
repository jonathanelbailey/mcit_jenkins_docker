FROM jenkins/jenkins:lts

LABEL maintainer="Jonathan Bailey (j.bailey@ebsco.com)"
LABEL author="Jonathan Bailey"
LABEL email="j.bailey@ebsco.com"
LABEL version="1.0.0"
LABEL description="A dockerized jenkins server"

ARG USERNAME=admin
ARG PASSWORD=admin
ARG GITHUB_TOKEN
ENV JAVA_OPTS="-Djenkins.install.runSetupWizard=false"
ENV JENKINS_OPTS="--prefix=/jenkins"
ENV CONFIG_REF="/usr/share/jenkins/ref"
ENV CONFIG_INIT="${CONFIG_REF}/init.groovy.d"
ENV CONFIG="jenkins_config"

USER root
RUN mkdir /var/cache/jenkins -p && chown jenkins:jenkins \
    /var/cache/jenkins 
USER jenkins

COPY ${CONFIG}/security.groovy ${CONFIG_INIT}/security.groovy
COPY ${CONFIG}/github.groovy ${CONFIG_INIT}/github.groovy
COPY ${CONFIG}/artifactory.groovy ${CONFIG_INIT}/artifactory.groovy
COPY ${CONFIG}/docker.groovy ${CONFIG_INIT}/docker.groovy

COPY plugins.txt ${CONFIG_REF}/plugins.txt
RUN /usr/local/bin/install-plugins.sh < ${CONFIG_REF}/plugins.txt