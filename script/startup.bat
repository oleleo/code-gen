@echo off

set app_name=gen

java -Duser.timezone=Asia/Shanghai -jar -Xms128m -Xmx128m %app_name%.jar