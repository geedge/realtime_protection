# SSFY系统 README

若要直接运行，请使用如下指令：
```shell
./gradlew clean && ./gradlew bootRun
```

## 项目文件夹结构说明：

目前整体分为两大板块：**configuration**和**server**

### Configuration

所有业务的配置，包括切面、拦截器、中间件、消息处理等配置全部放置于此处。
任何带@Configuration注解的配置均放置于此
1. entity文件夹：均为下述业务模块的实体类
2. exception文件夹：用于存放自定义类和全局异常处理配置类
3. response文件夹：用于存放返回配置类和审计类
4. satoken文件夹：用于存放用户登录相关配置类
5. swagger文件夹：用于存放swagger文档配置类
6. threadpool文件夹：用于存放线程池配置
7. utils文件夹：用于存放其他类型配置

### Server

server主要存放所有的业务逻辑以及Mapper接口。业务逻辑和Mapper需要按照业务进行划分进行分开放置。

目前包含的业务有：
1. alertmessage：告警xx模块，处理告警相关信息
2. command：指令模块，处理下/发/指令
3. defense：防御模块，包括fy对象、fy策略模板两个子模块，用于处理fy对象、fy策略模板信息
4. log：日志模块，目前无文件
5. rule：规则模块，用于处理静态/动态规则
6. task：任务模块，用于处理任务相关信息
7. user：用户模块
8. whitelist：白名单模块，用于处理白名单配置

### Resources
除了项目文件夹外，还有静态资源**resources**部分，主要存放**config**、**mappers**和静态网页资源以及下载文件。

**mappers**文件夹主要存放所有的Mybatis Mappers，以便于MyBatis进行扫描。

**config**文件夹存放包括
1. ```application.yml```
2. ```application-dev.yml```
3. ```application-test.yml```
4. ```application-prod.yml```

等文件， 用于进行SpringBoot和MyBatis基础配置以及方便进行配置切换。

**nginx**文件夹包括了所有的前端界面和Nginx配置，其中：
1. nginx.conf用于进行Nginx服务器基础配置，关键设置为```include /root/nginx/configs/project.conf```;
2. project.conf.template文件是project.conf的模板文件，其可以利用环境变量以及envsubst指令生成project.conf文件（详细代码位于```start_nginx.sh```文件）
3. defense文件夹是前端界面文件夹

### Docker相关配置
在docker-ubuntu文件夹中存放的是nginx和springboot的Dockerfile中的```protection_ubuntu```镜像的制作方法，
如果想要重新制作```protection_ubuntu.tar```文件，请重新运行docker-ubuntu文件夹中的```Dockerfile```以制作基础ubuntu镜像。可以使用如下指令制作：
```shell
docker build -t protection_ubuntu .
```
同时，在docker部署之前，请执行以下指令以联网获取GRADLE相关资源依赖以便进行断网部署：
```shell
export set GRADLE_USER_HOME=./gradle
./gradlew build && ./graldew clean
```
在docker-nginx中存放的是nginx前端服务镜像的制作方法，全程理论可以做到断网部署，具体制作方法请检查```Dockerfile```以及```start_nginx.sh```

在docker-springboot中存放的是springboot后端服务镜像的制作方法，全程理论可以做到断网部署，具体制作方法请检查```Dockerfile```以及```start_springboot.sh```

如果想要制作前后端Docker镜像并开启docker容器，请使用```load_nginx.sh```和```load_springboot.sh```两个脚本（```load_springboot_multi.sh```仅用于测试Nginx服务器负载均衡用）

### 数据库相关配置

目前该项目使用两种数据库：Doris和MySQL，导入Doris和MySQL表结构可使用如下语句：
```shell
# 导入Doris数据库以及表结构，IP、端口、用户名均需要修改
mysql -u root -h 192.168.107.89 -P 9030 < realtime_protection/sql/doris-2024-1-23.sql -p
# 导入MySQL数据库以及表结构，IP、端口、用户名均需要修改
mysql -u root -h 192.168.107.89 -P 3306 < realtime_protection/sql/mysql-2024-1-23.sql -p
# 导入MySQL数据库以及表结构和表中所有数据，IP、端口、用户名均需要修改
mysql -u root -h 192.168.107.89 -P 3306 < realtime_protection/sql/mysql-2024-1-23-with-data.sql -p
```
如果需要导出Doris表结构和MySQL表结构以及数据，可使用如下语句：
```shell
# 导出MySQL表结构和数据
mysqldump -u root -h 192.168.107.89 -P 3306 --databases realtime_protection > mysql-2024-01-23-with-data.sql -p
# 导出MySQL表结构，不导出数据
mysqldump -u root -h 192.168.107.89 -P 3306 --databases realtime_protection -no-data > mysql-2024-01-23-without-data.sql -p
# 导出doris表结构
mysqldump -u root -h 192.168.107.89 -P 9030 --databases command --no-tablespaces -no-data > doris-2024-01-23.sql -p
```