# SSFY系统 README

## 项目文件夹结构说明：

目前整体分为两大板块：**configuration**和**server**

### Configuration

所有业务的配置，包括切面、拦截器、中间件、消息处理等配置全部放置于此处。
任何带@Configuration注解的配置均放置于此

### server

server主要存放所有的业务逻辑以及Mapper接口。业务逻辑和Mapper需要按照业务进行划分进行分开放置

---
除了项目文件夹外，还有静态资源**resources**部分，主要存放**config**、**mappers**和静态网页资源以及下载文件。

**mappers**文件夹主要存放所有的Mybatis Mappers，以便于MyBatis进行扫描。
**config**文件夹目前仅存放application.yml文件，用于进行SpringBoot和MyBatis基础配置。