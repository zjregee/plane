核心文件

FlightRecommendation：后端代码

alizarin：反向服务器

WondKV：KV数据库

react-air：前端代码

air：数据爬取、处理、模拟、上传脚本

可执行文件位置

后端可执行文件：./FlightRecommendation-0.0.1-SNAPSHOT.jar

数据库可执行文件：./WondKV/target/release/WondKV

反向代理服务器可执行文件：./alizarin/server

其他

1. 由于事先编译的可执行文件只能在某一具体的环境下运行，如果无法运行可执行文件，可以根据设计说明文档以及Web服务启动指令中的说明编译源码生成可执行文件。
2. 可执行文件使用的默认运行参数不一定符合实际情况，可以参考设计说明文档以合适的参数运行程序。