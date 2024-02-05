FROM openjdk:8-jre-alpine3.9

MAINTAINER tanghc

EXPOSE 6969

VOLUME /tmp
RUN mkdir /gen

ADD dist/gen /gen/

# set jvm
ENV JAVA_OPTS="-server -Xmx64m -Xms64m"
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Dsolon.config.add=/gen/conf/app.yml -Duser.timezone=Asia/Shanghai -Djava.awt.headless=true -Djava.security.egd=file:/dev/./urandom -jar /gen/gen.jar" ]
