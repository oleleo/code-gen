# 打包发布

docker buildx build -t trainliang/codegen:latest --platform=linux/amd64,linux/arm64/v8 . --push
docker buildx build -t trainliang/codegen:1.5.8.1 --platform=linux/amd64,linux/arm64/v8 . --push