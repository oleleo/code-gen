#!/bin/sh

# 关闭服务
app="gen"

pid=$(ps -ef | grep ${app}.jar | grep -v grep | awk '{print $2}')
if [ -n "$pid" ]; then
  echo "stop ${app}.jar, pid:" "$pid"
  kill -9 "$pid"
fi
