package com.realtime.protection.configuration.entity.task;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.stat.TableStat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Data
public class DynamicTaskInfo {

    @Data
    private static class SimpleProtectObject {

        @JsonProperty("ip")
        private String IP;

        @JsonProperty("port")
        private Integer port;

        @JsonProperty("url")
        private String URL;

        @JsonProperty("protocol")
        private String protocol;
    }

    // 从任务中获取
    @JsonProperty("task_id")
    private Long taskId;

    @JsonProperty("start_time")
    private LocalDateTime startTime;

    @JsonProperty("end_time")
    private LocalDateTime endTime;

    // 从规则中获取
    @JsonProperty("rule_id")
    private Integer ruleId;

    @JsonProperty("source_system")
    private String sourceSystem;

    @JsonProperty("event_type")
    private String eventType;

//    @JsonProperty("event_ids")
//    private List<Long> eventIds;
//
//    @JsonProperty("c_inoutid")
//    private List<Long> inoutId;
//
//    @JsonProperty("c_netnum")
//    private List<Long> netNum;

    @JsonProperty("conditions")
    private HashMap<String,String> conditions = new HashMap<>();

//    @JsonProperty("eq_condition")
//    private HashMap<String,String> eqConditions = new HashMap<>();;

    @JsonProperty("table_name")
    private List<String> tableNames = new ArrayList<>();

    @JsonProperty("bw_sql")
    private String bwSql;

    @JsonProperty("select_columns")
    private List<String> selectColumns = new ArrayList<>();

    // 从防护对象列表中获取
    @JsonProperty("protect_objects")
    private List<SimpleProtectObject> protectObjects;

    public void parseSql()  {
        String bwSql = this.bwSql;
        //解析SQL语句
        SQLStatementParser parser = new MySqlStatementParser(bwSql);
        // 使用Parser解析生成AST，这里SQLStatement就是AST
        SQLStatement sqlStatement = parser.parseStatement();

        // 检查是否是SELECT语句
        if (sqlStatement instanceof SQLSelectStatement) {
            // 创建访问者
            MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
            sqlStatement.accept(visitor);

            // 进一步细化，确保只获取SELECT子句的字段
            SQLSelectStatement selectStatement = (SQLSelectStatement) sqlStatement;
            SQLSelect select = selectStatement.getSelect();
            SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) select.getQuery();

            queryBlock.getSelectList().forEach(item -> this.selectColumns.add(item.toString()));

            Map<TableStat.Name, TableStat> tables = visitor.getTables();
            for (TableStat.Name t : tables.keySet()) {
                this.tableNames.add(t.getName());
            }
            //获取where条件
            List<TableStat.Condition> conditions = visitor.getConditions();
            conditions.forEach(condition -> {
//                log.info("解析sql后的查询条件：{}", condition.getColumn().getName());
                if (condition.getOperator().equals("IN")){
                    this.conditions.put(condition.getColumn().getName(),condition.getValues().toString());
                }
                if (condition.getOperator().equals("=")){
                    this.conditions.put(condition.getColumn().getName(),condition.getValues().toString());
                }
            });
//            log.info("解析sql后的查询条件：{}",conditions);

        } else {
            throw new IllegalArgumentException("不是SELECT查询语句，不进行字段解析。");
        }


    }

}
