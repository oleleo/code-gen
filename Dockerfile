FROM openjdk:8-jre-alpine3.9

MAINTAINER tanghc

EXPOSE 6969

VOLUME /tmp
RUN mkdir /gen

ADD dist/gen /gen/

# set jvm
ENV JAVA_OPTS="-server -Xmx64m -Xms64m"
ENV CONFIG_FILE="/opt/gen/conf/app.yml"

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.ext.dirs=$JAVA_HOME/lib/ext:/opt/gen/ext -Dsolon.config.add=$CONFIG_FILE -Duser.timezone=Asia/Shanghai -Djava.awt.headless=true -Djava.security.egd=file:/dev/./urandom -jar /gen/gen.jar" ]
