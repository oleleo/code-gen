#!/bin/sh

# 构建目录
dist_dir="dist"
# 服务端文件夹名称
# 执行文件名称
app_name="gen"

build_folder="${app_name}"

# 输出目录
target_dir="$dist_dir/${build_folder}"

# 先执行前端构建
cd front

sh build.sh

cd ..

# ----

echo "开始构建服务端..."

mvn clean package

# 复制文件

rm -rf $dist_dir

if [ ! -d "$target_dir" ]; then
  mkdir -p $target_dir
fi

# 复制前端资源
echo "复制前端文件到$target_dir"
cp -r front/dist ./$target_dir

# 复制服务端资源
echo "复制服务端文件到$target_dir"
cp -r gen/target/gen/gen/* ./$target_dir

echo "服务端构建完毕，构建结果在${target_dir}文件夹下"
