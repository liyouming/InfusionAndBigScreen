package com.fugao.infusion.model;

import java.util.List;

/**
 *
 *
 * @Prject: FastApps
 * @Location: com.android.fastinfusion.model.TimeOneStatusModel
 * @Description: TODO
 * @author: 张洋    zhangyang@fugao.com
 * @date: 2014/11/21 17:42
 * @version: V1.0
 *
 *
 * 登记，排药，配液，输液，结束
 */

public class UserRoleTimelineModel {
    private static final String TAG = "Fugao-TimeOneStatusModel";
    /* 状态名称 */
    private String statusName;
    /* 预计完成时间 */
    private String completeTime;
    /* 二级状态list */
    private List<UserDetailTimelineModel> twoList;

    public String getStatusName() {
        return statusName;
    }
    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }
    public String getCompleteTime() {
        return completeTime;
    }
    public void setCompleteTime(String completeTime) {
        this.completeTime = completeTime;
    }
    public List<UserDetailTimelineModel> getTwoList() {
        return twoList;
    }
    public void setTwoList(List<UserDetailTimelineModel> twoList) {
        this.twoList = twoList;
    }


}
