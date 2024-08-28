package com.realtime.protection.configuration.entity.user;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

/**
 * @author Yixiang Zhao
 **/
@Data
public class UserFull {
    public String ticket;
    public List<Group> groups;
    public List<Role> roles;
    public List<Org> orgs;
    public String mobile;
    public String nickName;
    public String email;
    public String uid;
    public String employeeNumber;
    public String name;
    public Object resoures;
    public Object orgCode;



    public String getOrgCode() {
        if (orgs.size() > 0) {
            return orgs.get(0).orgCode;
        }
        return "";
    }

    public String getOrgName() {
        if (orgs.size() > 0) {
            return orgs.get(0).orgName;
        }
        return "";
    }

    public String getRoleKey() {
        if (roles.size() > 0) {
            return roles.get(0).roleKey;
        }
        return "";
    }
}
@Data
class Group {
    public int groupId;
    public int applicationId;
    public String groupKey;
    public String groupName;
    public String groupTag; // Assume it's a JSON String, otherwise it could be List<Tag> or similar
    public String groupRemark;
}
@Data
class Role {
    public int roleId;
    public int applicationId;
    public String roleKey;
    public String roleName;
    public String roleRemark;
    public String roleTag; // Same assumption as above
    public List<String> res; // Assuming a Resource class exists
//    public List<String> resources; // Assuming a Resource class exists
    public Object resources;
}
@Data
class Org {
    public String orgName;
    public String orgDescription;
    public int orgParentId;
    public String orgCode;
    public String orgTag; // Same assumption as above
    public int orgId;
    public String userOrgworkType;
}
