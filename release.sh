#!/bin/sh

# 构建目录
dist_dir="dist"
# 服务端文件夹名称
# 执行文件名称
app_name="gen"

build_folder="${app_name}"

sh build.sh

echo "打成zip包"
cd $dist_dir
zip -r -q "${build_folder}.zip" ${build_folder}
