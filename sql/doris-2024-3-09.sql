-- MySQL dump 10.13  Distrib 8.0.35, for Linux (x86_64)
--
-- Host: 10.26.22.133    Database: command
-- ------------------------------------------------------
-- Server version	5.7.99

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `command`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `command`;

USE `command`;

--
-- Table structure for table `t_alertmessage`
--

DROP TABLE IF EXISTS `t_alertmessage`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_alertmessage` (
  `TASK_ID` bigint(20) NOT NULL COMMENT '关联任务ID',
  `DYNAMIC_RULE_ID` bigint(20) NOT NULL COMMENT '关联动态规则ID',
  `ADDR_TYPE` tinyint(4) NULL COMMENT 'IP地址类型',
  `SRC_IP` varchar(64) NULL COMMENT '源IP地址',
  `SRC_PORT` varchar(6) NULL COMMENT '源端口',
  `DST_IP` varchar(64) NULL COMMENT '目的IP地址',
  `DST_PORT` varchar(64) NULL COMMENT '目的端口',
  `PROTOCOL` smallint(6) NULL COMMENT '协议号',
  `MASK_SRC_IP` varchar(64) NULL COMMENT '源IP地址掩码',
  `MASK_SRC_PORT` varchar(6) NULL COMMENT '源端口掩码',
  `MASK_DST_IP` varchar(64) NULL COMMENT '目的IP地址掩码',
  `MASK_DST_PORT` varchar(6) NULL COMMENT '目的端口掩码',
  `MASK_PROTOCOL` varchar(6) NULL COMMENT '协议掩码',
  `COMMAND_UUID` varchar(36) NULL COMMENT '告警信息生产指令的uuid',
  `CREATE_TIME` datetime NULL COMMENT '告警信息创建时间',
  `LAST_UPDATE` datetime NULL COMMENT '告警信息上次修改时间',
  `ALERT_MESSAGE_ID` varchar(36) NULL,
  `IS_DELETE` tinyint(4) NULL DEFAULT "0",
  `CONTENT` varchar(65533) NULL
) ENGINE=OLAP
UNIQUE KEY(`TASK_ID`, `DYNAMIC_RULE_ID`, `ADDR_TYPE`, `SRC_IP`, `SRC_PORT`, `DST_IP`, `DST_PORT`, `PROTOCOL`, `MASK_SRC_IP`, `MASK_SRC_PORT`, `MASK_DST_IP`, `MASK_DST_PORT`, `MASK_PROTOCOL`)
COMMENT 'OLAP'
DISTRIBUTED BY HASH(`TASK_ID`, `DYNAMIC_RULE_ID`, `ADDR_TYPE`, `SRC_IP`, `SRC_PORT`, `DST_IP`, `DST_PORT`, `PROTOCOL`, `MASK_SRC_IP`, `MASK_SRC_PORT`, `MASK_DST_IP`, `MASK_DST_PORT`, `MASK_PROTOCOL`) BUCKETS 32
PROPERTIES (
"replication_allocation" = "tag.location.default: 3",
"in_memory" = "false",
"storage_format" = "V2",
"disable_auto_compaction" = "false"
);;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_command`
--

DROP TABLE IF EXISTS `t_command`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_command` (
  `TASK_ID` bigint(20) NULL COMMENT '关联任务ID',
  `ADDR_TYPE` tinyint(4) NULL DEFAULT "4" COMMENT 'IP地址类型',
  `SRC_IP` varchar(64) NULL COMMENT '源IP地址',
  `SRC_PORT` varchar(6) NULL COMMENT '源端口',
  `DST_IP` varchar(64) NULL COMMENT '目的IP地址',
  `DST_PORT` varchar(64) NULL COMMENT '目的端口',
  `PROTOCOL` smallint(6) NULL COMMENT '协议号',
  `MASK_SRC_IP` varchar(64) NULL COMMENT '源IP地址掩码',
  `MASK_SRC_PORT` varchar(6) NULL COMMENT '源端口掩码',
  `MASK_DST_IP` varchar(64) NULL COMMENT '目的IP地址掩码',
  `MASK_DST_PORT` varchar(6) NULL COMMENT '目的端口掩码',
  `MASK_PROTOCOL` varchar(6) NULL COMMENT '协议掩码',
  `COMMAND_ID` varchar(36) NOT NULL COMMENT '指令UUID',
  `TASK_ACT` varchar(32) NULL COMMENT '阻断',
  `FREQUENCY` int(11) NOT NULL COMMENT '指令下发频率（分钟）',
  `VALID_TIME` datetime NOT NULL COMMENT '指令生效时间',
  `INVALID_TIME` datetime NOT NULL COMMENT '指令失效时间',
  `IS_VALID` boolean NOT NULL DEFAULT "FALSE" COMMENT '指令是否生效',
  `IS_JUDGED` boolean NULL DEFAULT "TRUE" COMMENT '该指令是否研判',
  `SEND_TIMES` int(11) NOT NULL DEFAULT "0" COMMENT '指令下发次数',
  `SUCCESS_TIMES` int(11) NOT NULL DEFAULT "0" COMMENT '指令成功下发次数',
  `FIRST_SEND_TIME` datetime NULL COMMENT '指令首次下发时间',
  `LAST_SEND_TIME` datetime NULL COMMENT '指令最新下发时间',
  `CREATE_TIME` datetime NOT NULL COMMENT '指令创建时间',
  `LAST_UPDATE` datetime NOT NULL COMMENT '指令上次修改时间',
  `IS_DELETED` boolean NOT NULL DEFAULT "FALSE" COMMENT '指令是否已被逻辑删除'
) ENGINE=OLAP
UNIQUE KEY(`TASK_ID`, `ADDR_TYPE`, `SRC_IP`, `SRC_PORT`, `DST_IP`, `DST_PORT`, `PROTOCOL`, `MASK_SRC_IP`, `MASK_SRC_PORT`, `MASK_DST_IP`, `MASK_DST_PORT`, `MASK_PROTOCOL`)
COMMENT '下发指令信息表'
DISTRIBUTED BY HASH(`TASK_ID`, `ADDR_TYPE`, `SRC_IP`, `SRC_PORT`, `DST_IP`, `DST_PORT`, `PROTOCOL`, `MASK_SRC_IP`, `MASK_SRC_PORT`, `MASK_DST_IP`, `MASK_DST_PORT`, `MASK_PROTOCOL`) BUCKETS 32
PROPERTIES (
"replication_allocation" = "tag.location.default: 3",
"in_memory" = "false",
"storage_format" = "V2",
"disable_auto_compaction" = "false"
);;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-01-23 22:42:53
