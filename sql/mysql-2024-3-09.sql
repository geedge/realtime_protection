-- MySQL dump 10.13  Distrib 8.0.35, for Linux (x86_64)
--
-- Host: 192.168.107.89    Database: realtime_protection
-- ------------------------------------------------------
-- Server version	8.0.35-0ubuntu0.22.04.1

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
-- Current Database: `realtime_protection`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `realtime_protection` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `realtime_protection`;

--
-- Table structure for table `chanct_log_usual`
--

DROP TABLE IF EXISTS `chanct_log_usual`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chanct_log_usual` (
  `id` bigint NOT NULL COMMENT '编号',
  `service_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '服务ID',
  `server_host` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '服务器名',
  `server_ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '服务器IP地址',
  `env` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '系统环境',
  `log_level` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '日志级别',
  `log_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '日志业务id',
  `log_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '日志数据',
  `method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '操作方式',
  `request_uri` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '请求URI',
  `remote_ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '操作IP地址',
  `method_class` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '方法类',
  `method_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '方法名',
  `user_agent` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户代理',
  `params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '操作提交的数据',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `tenant_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='通用审计表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_command_statistics`
--

DROP TABLE IF EXISTS `t_command_statistics`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_command_statistics` (
  `command_id` bigint unsigned NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`command_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='处置指令统计表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_dynamic_rule`
--

DROP TABLE IF EXISTS `t_dynamic_rule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_dynamic_rule` (
  `dynamic_rule_id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `dynamic_rule_used_task_id` bigint unsigned DEFAULT NULL COMMENT '动态规则使用的任务ID',
  `dynamic_rule_name` varchar(32) NOT NULL COMMENT 'VARCHAR(32)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `dynamic_rule_create_username` varchar(32) DEFAULT NULL COMMENT '规则创建人名称',
  `dynamic_rule_create_depart` varchar(32) DEFAULT NULL COMMENT '创建人处室',
  `dynamic_rule_protect_level` tinyint DEFAULT NULL COMMENT '防护等级',
  `dynamic_rule_priority` tinyint NOT NULL COMMENT '优先级',
  `dynamic_rule_range` varchar(32) NOT NULL COMMENT '局点选择',
  `dynamic_rule_frequency` tinyint NOT NULL COMMENT '下发频率',
  `dynamic_rule_create_user_id` int DEFAULT NULL,
  `template_id` bigint unsigned NOT NULL COMMENT '策略模板ID',
  `log_rule_id` bigint unsigned DEFAULT NULL COMMENT '日志规则ID',
  PRIMARY KEY (`dynamic_rule_id`),
  KEY `t_dynamic_rule_t_task_task_id_fk` (`dynamic_rule_used_task_id`),
  CONSTRAINT `t_dynamic_rule_t_task_task_id_fk` FOREIGN KEY (`dynamic_rule_used_task_id`) REFERENCES `t_task` (`task_id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='动态规则信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_protect_level`
--

DROP TABLE IF EXISTS `t_protect_level`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_protect_level` (
  `protect_level_id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '防护等级ID',
  `has_protect_object_ip` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否包含防护对象IP',
  `has_protect_object_port` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否包含防护对象端口',
  `has_protocol` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否包含协议',
  `has_url` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否包含URL',
  `has_peer_ip` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否包含对端IP',
  `has_peer_port` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否包含对端端口',
  `has_dns` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否包含DNS字段',
  PRIMARY KEY (`protect_level_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9746 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='防护等级表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_protect_object`
--

DROP TABLE IF EXISTS `t_protect_object`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_protect_object` (
  `protect_object_id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `protect_object_name` varchar(32) NOT NULL COMMENT '防护对象名称',
  `protect_object_system_name` varchar(32) DEFAULT NULL COMMENT '防护对象系统名称',
  `protect_object_ip` int unsigned DEFAULT NULL COMMENT '防护对象IP',
  `protect_object_port` smallint unsigned NOT NULL COMMENT '防护对象业务端口',
  `protect_object_url` varchar(255) DEFAULT NULL COMMENT '防护对象URL',
  `protect_object_protocol` varchar(32) NOT NULL COMMENT '防护对象使用协议',
  `protect_object_audit_status` tinyint NOT NULL DEFAULT '0' COMMENT '防护对象审核状态',
  `protect_object_create_username` varchar(32) DEFAULT NULL COMMENT '防护对象创建人',
  `protect_object_create_depart` varchar(32) DEFAULT NULL COMMENT '防护对象创建人处室',
  `protect_object_create_user_id` int DEFAULT NULL COMMENT '防护对象创建人ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`protect_object_id`)
) ENGINE=InnoDB AUTO_INCREMENT=23557 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='防护对象信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_protect_object_dynamic_rule_conn`
--

DROP TABLE IF EXISTS `t_protect_object_dynamic_rule_conn`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_protect_object_dynamic_rule_conn` (
  `conn_id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `protect_object_id` bigint unsigned NOT NULL COMMENT '防护对象ID',
  `dynamic_rule_id` bigint unsigned NOT NULL COMMENT '动态规则ID',
  PRIMARY KEY (`conn_id`),
  KEY `t_protect_object_dynamic_rule_id_fk` (`dynamic_rule_id`),
  KEY `t_protect_object_id_fk` (`protect_object_id`),
  CONSTRAINT `t_protect_object_dynamic_rule_id_fk` FOREIGN KEY (`dynamic_rule_id`) REFERENCES `t_dynamic_rule` (`dynamic_rule_id`) ON DELETE CASCADE,
  CONSTRAINT `t_protect_object_id_fk` FOREIGN KEY (`protect_object_id`) REFERENCES `t_protect_object` (`protect_object_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=49 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='防护对象-动态规则关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_static_rule`
--

DROP TABLE IF EXISTS `t_static_rule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_static_rule` (
  `static_rule_id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `static_rule_name` varchar(32) NOT NULL COMMENT '静态规则名称',
  `static_rule_create_time` datetime NOT NULL COMMENT '静态规则创建时间',
  `static_rule_create_username` varchar(32) DEFAULT NULL COMMENT '创建人名称',
  `static_rule_audit_status` tinyint DEFAULT NULL COMMENT '静态规则审核状态，分为未审核（0）、退回（1）、已审核（2）',
  `static_rule_create_depart` varchar(32) DEFAULT NULL COMMENT '规则创建人处室',
  `static_rule_create_user_id` bigint unsigned DEFAULT NULL COMMENT '规则创建人ID',
  `static_rule_used_task_id` bigint unsigned DEFAULT NULL COMMENT '使用该静态规则的任务主键id',
  `static_rule_sip` int unsigned DEFAULT NULL COMMENT '源ip',
  `static_rule_msip` int unsigned DEFAULT NULL COMMENT '源ip掩码',
  `static_rule_sport` smallint unsigned DEFAULT NULL COMMENT '源端口',
  `static_rule_msport` smallint unsigned DEFAULT NULL COMMENT '源端口掩码',
  `static_rule_dip` int unsigned DEFAULT NULL COMMENT '目的ip',
  `static_rule_mdip` int unsigned DEFAULT NULL COMMENT '目的ip掩码',
  `static_rule_dport` smallint unsigned DEFAULT NULL COMMENT '目的端口',
  `static_rule_mdport` smallint unsigned DEFAULT NULL COMMENT '目的端口掩码',
  `static_rule_protocol` varchar(32) DEFAULT NULL COMMENT '协议v',
  `static_rule_mprotocol` varchar(32) DEFAULT NULL COMMENT '协议掩码',
  `static_rule_dns` varchar(255) DEFAULT NULL COMMENT 'dns地址',
  `static_rule_url` varchar(255) DEFAULT NULL COMMENT 'url',
  `static_rule_priority` tinyint NOT NULL COMMENT '优先级，1代表高、2代表中、3代表低',
  `static_rule_range` varchar(255) NOT NULL COMMENT '局点选择',
  `static_rule_frequency` tinyint NOT NULL COMMENT '下发频率',
  `static_rule_modify_time` datetime DEFAULT NULL COMMENT '静态规则修改时间',
  PRIMARY KEY (`static_rule_id`),
  KEY `t_static_rule_t_task_task_id_fk` (`static_rule_used_task_id`),
  CONSTRAINT `t_static_rule_t_task_task_id_fk` FOREIGN KEY (`static_rule_used_task_id`) REFERENCES `t_task` (`task_id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=1132 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='静态关键信息库表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_strategy_template`
--

DROP TABLE IF EXISTS `t_strategy_template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_strategy_template` (
  `strategy_template_id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `strategy_template_name` varchar(64) NOT NULL COMMENT '策略模板名称',
  `strategy_template_create_username` varchar(32) DEFAULT NULL COMMENT '策略模板创建人名称',
  `strategy_template_create_depart` varchar(32) DEFAULT NULL COMMENT '策略模板创建人处室',
  `strategy_template_create_user_id` int DEFAULT NULL COMMENT '策略模板创建人ID',
  `strategy_template_low_level_id` int unsigned DEFAULT NULL COMMENT '策略模板日常态等级ID',
  `strategy_template_medium_level_id` int unsigned DEFAULT NULL COMMENT '策略模板应急态等级ID',
  `strategy_template_high_level_id` int unsigned DEFAULT NULL COMMENT '策略模板紧急态等级ID',
  `strategy_template_source_system` varchar(32) NOT NULL COMMENT '默认数据来源系统',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`strategy_template_id`),
  UNIQUE KEY `t_strategy_template_pk` (`strategy_template_name`,`strategy_template_source_system`),
  KEY `t_strategy_template_t_protect_level_protect_level_id_fk` (`strategy_template_low_level_id`),
  KEY `t_strategy_template_t_protect_level_protect_level_id_fk_2` (`strategy_template_medium_level_id`),
  KEY `t_strategy_template_t_protect_level_protect_level_id_fk_3` (`strategy_template_high_level_id`),
  CONSTRAINT `t_strategy_template_t_protect_level_protect_level_id_fk` FOREIGN KEY (`strategy_template_low_level_id`) REFERENCES `t_protect_level` (`protect_level_id`) ON DELETE SET NULL,
  CONSTRAINT `t_strategy_template_t_protect_level_protect_level_id_fk_2` FOREIGN KEY (`strategy_template_medium_level_id`) REFERENCES `t_protect_level` (`protect_level_id`) ON DELETE SET NULL,
  CONSTRAINT `t_strategy_template_t_protect_level_protect_level_id_fk_3` FOREIGN KEY (`strategy_template_high_level_id`) REFERENCES `t_protect_level` (`protect_level_id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=3252 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='策略模板信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_task`
--

DROP TABLE IF EXISTS `t_task`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_task` (
  `task_id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `task_name` varchar(32) NOT NULL COMMENT '任务名称',
  `task_start_time` datetime NOT NULL COMMENT '任务开始时间',
  `task_end_time` datetime NOT NULL COMMENT '任务结束时间',
  `task_create_time` datetime NOT NULL COMMENT '任务创建时间',
  `task_modify_time` datetime NOT NULL COMMENT '任务信息修改时间',
  `task_type` tinyint unsigned NOT NULL COMMENT '任务类型',
  `task_act` varchar(32) NOT NULL COMMENT '任务行为',
  `task_create_username` varchar(32) DEFAULT NULL COMMENT '任务创建人名称',
  `task_create_depart` varchar(32) DEFAULT NULL COMMENT '任务创建人处室',
  `task_create_userid` bigint unsigned DEFAULT NULL COMMENT '任务创建人ID',
  `task_status` tinyint NOT NULL DEFAULT '0' COMMENT '任务状态',
  `task_audit_status` tinyint NOT NULL DEFAULT '0' COMMENT '任务审核状态',
  PRIMARY KEY (`task_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10933 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='任务信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_white_list`
--

DROP TABLE IF EXISTS `t_white_list`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_white_list` (
  `white_list_id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '白名单对象ID',
  `white_list_name` varchar(32) NOT NULL COMMENT '白名单对象名称',
  `white_list_system_name` varchar(32) DEFAULT NULL COMMENT '白名单对象系统名称',
  `white_list_ip` int unsigned DEFAULT NULL COMMENT '白名单对象IP',
  `white_list_url` varchar(255) DEFAULT NULL COMMENT '白名单对象域名',
  `white_list_port` smallint unsigned DEFAULT NULL COMMENT '白名单对象端口',
  `white_list_audit_status` tinyint NOT NULL COMMENT '白名单对象审核状态',
  `white_list_protocol` varchar(32) DEFAULT NULL COMMENT '白名单对象使用协议',
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  PRIMARY KEY (`white_list_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7056 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='白名单信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Temporary view structure for view `template_view`
--

DROP TABLE IF EXISTS `template_view`;
/*!50001 DROP VIEW IF EXISTS `template_view`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `template_view` AS SELECT 
 1 AS `strategy_template_id`,
 1 AS `strategy_template_name`,
 1 AS `strategy_template_low_level_id`,
 1 AS `strategy_template_medium_level_id`,
 1 AS `strategy_template_high_level_id`,
 1 AS `strategy_template_used_times`,
 1 AS `strategy_template_running_tasks`,
 1 AS `low_protect_level_id`,
 1 AS `low_has_protect_object_ip`,
 1 AS `low_has_protect_object_port`,
 1 AS `low_has_peer_ip`,
 1 AS `low_has_peer_port`,
 1 AS `low_has_protocol`,
 1 AS `low_has_url`,
 1 AS `low_has_dns`,
 1 AS `medium_protect_level_id`,
 1 AS `medium_has_protect_object_ip`,
 1 AS `medium_has_protect_object_port`,
 1 AS `medium_has_peer_ip`,
 1 AS `medium_has_peer_port`,
 1 AS `medium_has_protocol`,
 1 AS `medium_has_url`,
 1 AS `medium_has_dns`,
 1 AS `high_protect_level_id`,
 1 AS `high_has_protect_object_ip`,
 1 AS `high_has_protect_object_port`,
 1 AS `high_has_peer_ip`,
 1 AS `high_has_peer_port`,
 1 AS `high_has_protocol`,
 1 AS `high_has_url`,
 1 AS `high_has_dns`*/;
SET character_set_client = @saved_cs_client;

--
-- Current Database: `realtime_protection`
--

USE `realtime_protection`;

--
-- Final view structure for view `template_view`
--

/*!50001 DROP VIEW IF EXISTS `template_view`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`%` SQL SECURITY DEFINER */
/*!50001 VIEW `template_view` AS select `tst`.`strategy_template_id` AS `strategy_template_id`,`tst`.`strategy_template_name` AS `strategy_template_name`,`tst`.`strategy_template_low_level_id` AS `strategy_template_low_level_id`,`tst`.`strategy_template_medium_level_id` AS `strategy_template_medium_level_id`,`tst`.`strategy_template_high_level_id` AS `strategy_template_high_level_id`,count(distinct `tdr`.`dynamic_rule_id`) AS `strategy_template_used_times`,count(distinct `tt`.`task_id`) AS `strategy_template_running_tasks`,`low_tpl`.`protect_level_id` AS `low_protect_level_id`,`low_tpl`.`has_protect_object_ip` AS `low_has_protect_object_ip`,`low_tpl`.`has_protect_object_port` AS `low_has_protect_object_port`,`low_tpl`.`has_peer_ip` AS `low_has_peer_ip`,`low_tpl`.`has_peer_port` AS `low_has_peer_port`,`low_tpl`.`has_protocol` AS `low_has_protocol`,`low_tpl`.`has_url` AS `low_has_url`,`low_tpl`.`has_dns` AS `low_has_dns`,`medium_tpl`.`protect_level_id` AS `medium_protect_level_id`,`medium_tpl`.`has_protect_object_ip` AS `medium_has_protect_object_ip`,`medium_tpl`.`has_protect_object_port` AS `medium_has_protect_object_port`,`medium_tpl`.`has_peer_ip` AS `medium_has_peer_ip`,`medium_tpl`.`has_peer_port` AS `medium_has_peer_port`,`medium_tpl`.`has_protocol` AS `medium_has_protocol`,`medium_tpl`.`has_url` AS `medium_has_url`,`medium_tpl`.`has_dns` AS `medium_has_dns`,`high_tpl`.`protect_level_id` AS `high_protect_level_id`,`high_tpl`.`has_protect_object_ip` AS `high_has_protect_object_ip`,`high_tpl`.`has_protect_object_port` AS `high_has_protect_object_port`,`high_tpl`.`has_peer_ip` AS `high_has_peer_ip`,`high_tpl`.`has_peer_port` AS `high_has_peer_port`,`high_tpl`.`has_protocol` AS `high_has_protocol`,`high_tpl`.`has_url` AS `high_has_url`,`high_tpl`.`has_dns` AS `high_has_dns` from (((((`t_strategy_template` `tst` left join `t_protect_level` `low_tpl` on((`low_tpl`.`protect_level_id` = `tst`.`strategy_template_low_level_id`))) left join `t_protect_level` `medium_tpl` on((`medium_tpl`.`protect_level_id` = `tst`.`strategy_template_medium_level_id`))) left join `t_protect_level` `high_tpl` on((`high_tpl`.`protect_level_id` = `tst`.`strategy_template_high_level_id`))) left join `t_dynamic_rule` `tdr` on((`tdr`.`template_id` = `tst`.`strategy_template_id`))) left join `t_task` `tt` on((`tt`.`task_id` = `tdr`.`dynamic_rule_used_task_id`))) group by `tst`.`strategy_template_id` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-01-23 22:51:22
