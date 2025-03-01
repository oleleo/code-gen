#!/bin/sh

# 构建docker并推送到阿里云
# 执行方式：sh docker-build-push.sh <版本号>
# 如：sh docker-build-push.sh 1.15.2

git pull

sh release.sh

app_name="gen"

# 容器域名/空间名
# 如果构建服务器是阿里云ECS，则可以使用对应的vpc域名，上传速度快一点
docker_host="registry-vpc.cn-hangzhou.aliyuncs.com/tanghc"

docker_path="${docker_host}/${app_name}"

echo "开始创建docker hub镜像:latest"
docker build -f Dockerfile -t ${docker_path}:latest .

sleep 1

echo "推送镜像到docker hub:latest，执行命令：docker push ${docker_path}:latest"
docker push ${docker_path}:latest

sleep 1

# 如果有参数
if [ -n "${1}" ];then

  echo "tag 镜像，命令：docker tag ${docker_path}:latest ${docker_path}:${1}"
  docker tag ${docker_path}:latest ${docker_path}:${1}

  sleep 1

  echo "推送镜像到docker hub:${1}，执行命令：docker push ${docker_path}:${1}"
  docker push ${docker_path}:${1}
fi
