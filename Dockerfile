FROM openjdk:8-jre-slim-buster

MAINTAINER tanghc

RUN echo http://mirrors.aliyun.com/alpine/v3.10/main/ > /etc/apk/repositories &&  echo http://mirrors.aliyun.com/alpine/v3.10/community/ >> /etc/apk/repositories
RUN  apk add  curl

# 解决时差8小时问题
ENV TZ=Asia/Shanghai
RUN apk add tzdata && cp /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone


EXPOSE 6969

VOLUME /tmp
RUN mkdir /gen

ADD dist/gen gen

# set jvm
ENV JAVA_OPTS="-server -Xmx128m -Xms128m"
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Dsolon.config.add=/gen/conf/app.yml -Duser.timezone=Asia/Shanghai -Djava.awt.headless=true -Djava.security.egd=file:/dev/./urandom -jar /gen/gen.jar" ]
