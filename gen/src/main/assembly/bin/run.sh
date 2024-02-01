#!/bin/sh

# 先关闭服务
sh shutdown.sh

# --server.port：启动端口
nohup java -Dsolon.config.add=./conf/app.yml -Duser.timezone=Asia/Shanghai -jar -Xms64m -Xmx64m gen.jar >/dev/null 2>&1 &
