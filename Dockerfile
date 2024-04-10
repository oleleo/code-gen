FROM openjdk:8-jre-alpine3.9

MAINTAINER tanghc

EXPOSE 6969

VOLUME /tmp
RUN mkdir /gen

ADD dist/gen /gen/

# set jvm
ENV JAVA_OPTS="-server -Xmx64m -Xms64m"
ENV CONFIG_FILE="/gen/conf/app.yml"
ENV USE_DBMS="false"
ENV DB_HOST="localhost:3306"
ENV DB_USERNAME="gen"
ENV DB_PASSWORD="12345678"
ENV DB_URL="jdbc:mysql://${DB_HOST}/gen?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai"
ENV DB_DRIVE="com.mysql.cj.jdbc.Driver"

ENV JVM_PARAMS="-Djava.ext.dirs=$JAVA_HOME/lib/ext:/gen/ext -DUSE_DBMS=$USE_DBMS -DDB_URL=$DB_URL -DDB_USERNAME=$DB_USERNAME -DDB_PASSWORD=$DB_PASSWORD -Dsolon.config.add=$CONFIG_FILE -Duser.timezone=Asia/Shanghai -Djava.awt.headless=true -Djava.security.egd=file:/dev/./urandom"

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS $JVM_PARAMS -jar /gen/gen.jar" ]
