package com.fugao.infusion.model;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

/**
 * Do one thing at a time, and do well!
 *
 * @Prject: InfusionApps
 * @Location: com.fugao.infusion.model.UserRoleModel
 * @Description: TODO 用户权限model
 * @author: 胡乐    hule@fugao.com
 * @date: 2015/5/15 17:42
 * @version: V1.0
 */

public class UserRoleModel {
    private static final String TAG = "Fugao-UserRoleModel";
    @JsonIgnore
    public String Id;
    @JsonProperty
    public String Role;
    @JsonIgnore
    public String RoleId;
    @JsonIgnore
    public String RoleName;
    @JsonIgnore
    public String Remark;
    @JsonProperty
    public List<MenuListModel> MenuList;
}
