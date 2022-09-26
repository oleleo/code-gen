#!/bin/sh

git pull

sh build.sh

# 打包并运行docker镜像

# 创建镜像
docker build -t tanghc2020/gen .

sleep 1

echo "推送到docker，命令：docker push tanghc2020/gen:latest"

docker push tanghc2020/gen:latest
