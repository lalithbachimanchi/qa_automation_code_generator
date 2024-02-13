FROM jenkins/jenkins:lts

USER root

RUN apt-get update && apt-get install -y lsb-release


RUN apt-get update && \
    DEBIAN_FRONTEND=noninteractive apt-get install -y python3 python3-pip python3-dev python3.11-venv &&  \
    apt-get clean


RUN apt-get update && \
    DEBIAN_FRONTEND=noninteractive apt-get install  -y \
    wget \
    vim \
    sudo \
    curl \
    && apt-get clean

RUN apt-get update && apt-get install -y \
    build-essential \
    libffi-dev \
    libssl-dev \
    pkg-config


COPY plugins.txt /usr/share/jenkins/ref/plugins.txt


RUN mkdir /opt/gpt-integration/
RUN chmod 777 /opt/gpt-integration/
RUN mkdir /opt/jenkins_code_generator_artifacts
RUN chmod 777 /opt/jenkins_code_generator_artifacts

COPY requirements.txt /opt/gpt-integration/requirements.txt

RUN python3 -m venv /opt/gpt-integration/venv
RUN /opt/gpt-integration/venv/bin/pip install -r /opt/gpt-integration/requirements.txt



COPY build.py /opt/gpt-integration/build.py


RUN jenkins-plugin-cli -f /usr/share/jenkins/ref/plugins.txt


COPY jenkins_casc.yaml /var/jenkins_home/jenkins_casc.yaml

COPY code_generator_job.groovy /usr/local/code_generator_job.groovy

COPY code_runner_job.groovy /usr/local/code_runner_job.groovy

USER jenkins
