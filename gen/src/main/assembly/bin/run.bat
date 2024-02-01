@echo off

java -Dsolon.config.add=conf/app.yml -Duser.timezone=Asia/Shanghai -jar -Xms64m -Xmx64m gen.jar
