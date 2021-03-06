FROM jenkins/jenkins:lts

LABEL maintainer="Jonathan Bailey (j.bailey@ebsco.com)"
LABEL author="Jonathan Bailey"
LABEL email="j.bailey@ebsco.com"
LABEL version="1.0.0"
LABEL description="A dockerized jenkins server"

ENV USERNAME=admin
ENV PASSWORD=admin
ENV GITHUB_TOKEN='replace'
ENV GITHUB_ADMIN_ADDRESS='https://api.github.com'
ENV JENKINS_URL='http://127.0.0.1:8080/jenkins/'
ENV CREDENTIALS_ID='github'
ENV CRED_DESCRIPTION='GitHub Token'
ENV JAVA_OPTS="-Djenkins.install.runSetupWizard=false"
ENV JENKINS_OPTS="--prefix=/jenkins"
ENV CONFIG_REF="/usr/share/jenkins/ref"
ENV CONFIG_INIT="${CONFIG_REF}/init.groovy.d"
ENV CONFIG="jenkins_config"

USER root
RUN mkdir /var/cache/jenkins -p && chown jenkins:jenkins \
    /var/cache/jenkins 

RUN apt-get update && apt-get install software-properties-common python-pip && \
    apt-get install ansible && apt-get upgrade ansible

RUN pip install jmespath

USER jenkins

COPY ${CONFIG}/security.groovy ${CONFIG_INIT}/security.groovy
COPY ${CONFIG}/github.groovy ${CONFIG_INIT}/github.groovy
COPY ${CONFIG}/add_pipeline.groovy ${CONFIG_INIT}/add_pipeline.groovy

COPY plugins.txt ${CONFIG_REF}/plugins.txt
RUN /usr/local/bin/install-plugins.sh < ${CONFIG_REF}/plugins.txt