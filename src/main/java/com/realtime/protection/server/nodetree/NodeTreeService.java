package com.realtime.protection.server.nodetree;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//李福连

@Service
public class NodeTreeService {
    @Autowired
    NodeTreeMapper nodeTreeMapper;

    public List<NodeEntity> get() {
        List<NodeEntity> nodes= nodeTreeMapper.get();

        // 用于存储每个节点的id和节点对象的映射
        Map<Integer, NodeEntity> nodeMap = new HashMap<>();

        // 用于存储所有根节点
        List<NodeEntity> roots = new ArrayList<>();

        // 将所有节点放入map中，以便根据id快速查找
        for (NodeEntity node : nodes) {
            nodeMap.put(node.id, node);
        }

        // 建立树结构
        for (NodeEntity node : nodes) {
            if (node.parent_id == 0) {
                // 如果parent_id为0，说明这是一个根节点
                roots.add(node);
            } else {
                // 根据parent_id找到父节点，然后将当前节点加入父节点的children列表中
                NodeEntity parentNode = nodeMap.get(node.parent_id);
                if (parentNode != null) {
                    parentNode.children.add(node);
                }
            }
        }


        return roots;

    }
}
