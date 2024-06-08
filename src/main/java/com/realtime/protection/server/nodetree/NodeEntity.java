package com.realtime.protection.server.nodetree;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


//李福连

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NodeEntity {
    int id;
    int parent_id;
    String code;
    String dict_key;
    String dict_value;
    int sort;
    String remark;
    int is_deleted;
    // 子节点列表
    List<NodeEntity> children = new ArrayList<>();

    public NodeEntity(int id, int parent_id, String code, String dict_key, String dict_value, int sort, String remark, int is_deleted) {
        this.id = id;
        this.parent_id = parent_id;
        this.code = code;
        this.dict_key = dict_key;
        this.dict_value = dict_value;
        this.sort = sort;
        this.remark = remark;
        this.is_deleted = is_deleted;
    }
}
