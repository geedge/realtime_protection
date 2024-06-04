package com.realtime.protection.configuration.entity.alert;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.realtime.protection.configuration.entity.task.FiveTupleWithMask;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class AlertMessage {

    @JsonProperty("task_id")
    private Long taskId;

    @JsonProperty("rule_id")
    private Integer dynamicRuleId;


    @JsonProperty("five_tuple_with_mask")
    private FiveTupleWithMask fiveTupleWithMask;

    @JsonProperty("content")
    private String content;

//    @JsonProperty("is_distribute")
//    private Boolean isDistribute;//待删除

    @JsonProperty("command_uuid")
    private String commandUUID;

    @JsonProperty("create_time")
    private String createTime;
    @JsonProperty("modify_time")
    private String modifyTime;
    @JsonProperty("alert_message_uuid")
    private String alertMessageUUID;
//1代表防护对象命中告警信息里的是目的ip，0代表防护对象命中告警信息里的是源ip
    @JsonProperty("protect_object_is_src_dst")
    private int protectIsSrcOrDst;

    @JsonProperty("c_netnum")
    private Integer c_netnum;
    @JsonProperty("c_ip")
    private Long c_ip;
    @JsonProperty("c_inoutid")
    private Integer c_inoutid;
    @JsonProperty("c_gkid")
    private Integer c_gkid;
    @JsonProperty("c_provid")
    private Integer c_provid;
    @JsonProperty("c_event_id")
    private Long c_event_id;
    @JsonProperty("c_priority")
    private Integer c_priority;
    @JsonProperty("c_time")
    private Long c_time;
    @JsonProperty("c_flowid")
    private String c_flowid;
    @JsonProperty("c_src_ipv4")
    private Long c_src_ipv4;
    @JsonProperty("c_src_ipv6")
    private String c_src_ipv6;
    @JsonProperty("c_src_port")
    private Integer c_src_port;
    @JsonProperty("c_s_tunnel_ip")
    private Long c_s_tunnel_ip;
    @JsonProperty("c_s_tunnel_port")
    private Integer c_s_tunnel_port;
    @JsonProperty("c_dest_ipv4")
    private Long c_dest_ipv4;
    @JsonProperty("c_dest_ipv6")
    private String c_dest_ipv6;
    @JsonProperty("c_dest_port")
    private Integer c_dest_port;
    @JsonProperty("c_d_tunnel_ip")
    private Long c_d_tunnel_ip;
    @JsonProperty("c_d_tunnel_port")
    private Integer c_d_tunnel_port;
    @JsonProperty("c_proto_type")
    private Integer c_proto_type;


    @JsonProperty("c_s_boundary")
    private Long c_s_boundary;
    @JsonProperty("c_s_region")
    private Long c_s_region;
    @JsonProperty("c_s_city")
    private Long c_s_city;
    @JsonProperty("c_s_district")
    private Long c_s_district;
    @JsonProperty("c_s_operators")
    private Long c_s_operators;
    @JsonProperty("c_s_owner")
    private String c_s_owner;
    @JsonProperty("c_d_boundary")
    private Long c_d_boundary;
    @JsonProperty("c_d_region")
    private Long c_d_region;
    @JsonProperty("c_d_city")
    private Long c_d_city;
    @JsonProperty("c_d_district")
    private Long c_d_district;
    @JsonProperty("c_d_operators")
    private Long c_d_operators;
    @JsonProperty("c_d_owner")
    private String c_d_owner;
    @JsonProperty("c_ret_file_type")
    private Integer c_ret_file_type;
    @JsonProperty("c_ret_file_name")
    private String c_ret_file_name;
    @JsonProperty("c_ret_file")
    private String c_ret_file;
    @JsonProperty("c_url")
    private String c_url;
    @JsonProperty("c_s_mark1")
    private Long c_s_mark1;
    @JsonProperty("c_s_mark2")
    private Long c_s_mark2;
    @JsonProperty("c_s_mark3")
    private Long c_s_mark3;
    @JsonProperty("c_s_mark4")
    private Long c_s_mark4;
    @JsonProperty("c_s_mark5")
    private Long c_s_mark5;
    @JsonProperty("c_d_mark1")
    private Long c_d_mark1;
    @JsonProperty("c_d_mark2")
    private Long c_d_mark2;
    @JsonProperty("c_d_mark3")
    private Long c_d_mark3;
    @JsonProperty("c_d_mark4")
    private Long c_d_mark4;
    @JsonProperty("c_d_mark5")
    private Long c_d_mark5;

    @JsonProperty("display_id")
    private String display_id;

    @JsonProperty("c_return_info")
    private String c_return_info;



//                "c_priority": 0,
//                "c_time": 1714528212,
//                "c_flowid": "ca0c192021",
//                "c_src_ipv4": 1921297587,
//                "c_src_ipv6": "fe80::1",
//                "c_src_port": 1234,
//                "c_s_tunnel_ip": "172.16.0.1",
//                "c_s_tunnel_port": 5678,
//                "c_dest_ipv4": 310737541,
//                "c_dest_ipv6": "fe80::2",
//                "c_dest_port": 4321,
//                "c_d_tunnel_ip": 0,
//                "c_d_tunnel_port": 0,
//                "c_proto_type": 6,
//                "c_return_info": "r=\\00\\05\\00",
//                "c_s_boundary": 2,
//                "c_s_region": 510000,
//                "c_s_city": 511600,
//                "c_s_district": 0,
//                "c_s_operators": 2,
//                "c_s_owner": "Owner1",
//                "c_d_boundary": 2,
//                "c_d_region": 530000,
//                "c_d_city": 530100,
//                "c_d_district": 0,
//                "c_d_operators": 2,
//                "c_d_owner": "Owner2",
//                "c_ret_file_type": 0,
//                "c_ret_filename": "example.txt",
//                "c_ret_file": "This is a test file.",
//                "c_url": "http://example.com",
//                "c_s_mark1": 1,
//                "c_s_mark2": 0,
//                "c_s_mark3": 0,
//                "c_s_mark4": 0,
//                "c_s_mark5": 0,
//                "c_d_mark1": 0,
//                "c_d_mark2": 0,
//                "c_d_mark3": 0,
//                "c_d_mark4": 0,
//                "c_d_mark5": 1594
}
