/*
 * Copyright (C) 2014 loQua.Xee <loquaciouser@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fugao.infusion.constant;

/**
 * Do one thing at a time, and do well!
 *
 * @Prject: FastAndroid
 * @Location: com.android.fastinfusion.InfusionApi
 * @Description: TODO
 * @Author: LoQua.Xee    loquaciouser@gmail.com
 * @Create-date: 2014/9/21 13:10
 * @Modify-date: 修改日期
 * @Modify-description: TODO 修改说明
 * @Modify-author: 修改人
 * @version: V1.0
 */

public class InfoApi {
    private static String API_NAME = "api";

    private static String BASE_URL = API_NAME + "/";

    private static String GET_INFUSIONINFO = BASE_URL + "infusion/infusionInfo?";

    private static String GET_RESTACOUNT = BASE_URL + "core/RestAccount?";

    private static String POST_INFUSIONINFO = BASE_URL + "infusion/infusionInfo";

    private static String GET_INFUSIONQUEUE = BASE_URL + "infusion/InfusionQueue?";

    private static String POST_INFUSIONQUEUE = BASE_URL + "infusion/InfusionQueue";

    private static String GET_INFUSIONCALL = BASE_URL + "infusion/CallQueue";

    private static String POST_EXCUTESEATNO = BASE_URL + "infusion/infusionseat?";

    private static String GET_EMPTYSEATNO = BASE_URL + "infusion/peopleinfo?";

    private static String GET_CALL = BASE_URL + "infusion/agent?";

    private static String GET_WORKLOAD = BASE_URL + "infusion/infusionwork?";

    private static String GET_PATROL = BASE_URL + "infusion/infusionpatrol?";

    public static String POST_PATROL = BASE_URL + "infusion/infusionpatrol";

    private static String POST_INFUSIONCALL = BASE_URL + "infusion/CallQueue?";

    public static String POST_RECORDCOMFIRMERROR = BASE_URL + "infusion/infusionevent?";//核对错误记录日志提交到服务器

    /**
     * 获得输液区域的接口
     */
    private static String GET_INFUSION_AREA = BASE_URL + "infusion/area?";

    public static String login(String username, String userpwd) {
        return GET_RESTACOUNT + "username=" + username + "&userpwd=" + userpwd;
    }

    /**
     * 登录(不包含科室病区),自动更新
     *
     * @param usenameValue
     * @param passwordValue
     * @param isRember
     * @param versioncode
     * @param appsName
     * @return
     */
    public static String login(String usenameValue, String passwordValue, boolean isRember,
                               int versioncode, String appsName) {
        String resultString = GET_RESTACOUNT + "username=" + usenameValue + "&userpwd=" +
                passwordValue + "&remember=0&os=android&versioncode=" + versioncode +
                "&appsName=" + appsName+"&paramaters=0";
        return resultString;
    }
    /**
     * 通过科室代码获取输液区域的代码
     *
     * @param deptID
     * @return
     */
    public static String getInfusionAreaByDeptID(String deptID) {
        String resultString = "";
        resultString = GET_INFUSION_AREA + "deptId=" + deptID;
        return resultString;
    }

    /**
     * 通过科室代码获取输液区域的代码  无参的
     *
     * @param
     * @return
     */
    public static String getInfusionAreadeptId() {
        String resultString = "";
        resultString = GET_INFUSION_AREA + "getdept=1";
        return resultString;
    }

    /**
     * 获取工作量接口
     *
     * @param uid     用户ID
     * @param catalog dateType(Month,Week,Day) 一天 一周 一月
     */
    public static String Url_GetWorkload(String uid, String catalog) {
        return GET_WORKLOAD + "userId=" + uid + "&dateType=" + catalog;
    }


    /**
     * 搜索功能接口
     *
     * @param keyword
     * @return
     */
    public static String url_getBottlesByKeyword(String keyword) {
        return GET_INFUSIONINFO + "keyword=" + keyword;
    }

    /**
     * 获取巡视列表
     * 根据角色和操作的工号既可以查询全部又可以查询自己添加的巡视
     * @param deptId
     * @return
     */
    public static String Url_GetAround(String deptId, String userid, int type) {
        return GET_PATROL + "departmentid=" + deptId +"&userid=" + userid +"&type=" +type;
    }

    /**
     * 通过瓶贴id获取瓶贴
     *
     * @param bottleId
     * @return
     */
    public static String url_getBottleByBottleId(String bottleId) {
        return GET_INFUSIONINFO + "BottleId=" + bottleId;
    }

    /**
     * 结束正在执行的瓶贴
     *
     * @param patId
     * @param userId
     * @param userName
     * @return
     */
    public static String url_finshinfusionRuning(String patId, String userId, String userName) {
        return GET_INFUSIONQUEUE + "patId=" + patId + "&userId=" + userId + "&userName=" + userName;
    }

    /**
     * 扫描操作拿到瓶贴model和该人有几组在执行几组未执行
     *
     * @param bottleid
     * @param patid
     * @return
     */
    public static String url_infusioningCount(String bottleid, String patid) {
        return GET_INFUSIONINFO + "bottleid=" + bottleid + "&patid=" + patid;
    }

    /**
     *  获取空座位列表
     * @param departmentId
     * @param type
     * @param areaid
     * @return
     */
    public static String url_getEmptySeatsList(String departmentId, int type, String areaid) {
        return GET_EMPTYSEATNO + "departId=" + departmentId + "&type=" + type +"&areaids="+ areaid;
    }
    /**
     *释放座位的
     * @param departmentId
     * @param infusionId 输液id
     * @return
     */
    public static String url_ReleaseSeat(String departmentId, String infusionId) {
        return POST_EXCUTESEATNO + "infusionList=" + infusionId + "&departId=" + departmentId;
    }

    /**
     *
     * @param deparId
     * @param action
     * @return
     */
    public static String url_GetSeatAndInfo(String deparId, String action) {
        return POST_EXCUTESEATNO + "deparId=" + deparId + "&action=" + action;
    }

    /**
     *  排药界面长按事件进行呼叫
     * @param departmentId
     * @param pid
     * @param msg
     * @param doctor
     * @r    */
    public static String url_paiYaoCustomCall(String departmentId, String pid, String msg, String doctor) {
        return GET_CALL + "patId=" + pid + "&msg=" + msg + "&departmentId=" + departmentId +"&doctor=" + doctor;
    }

    /**
     *  非排药界面的长按呼叫
     * @param departmentId
     * @param pid
     * @param msg
     * @return
     */
    public static String url_getCustomCall(String departmentId, String pid, String msg) {
        return GET_CALL + "patId=" + pid + "&msg=" + msg + "&departmentId=" + departmentId;
    }

    /**
     * 结束全部输液
     */
    public static String url_end(String uid, String pid,String endName) {
        return POST_INFUSIONINFO + "?end=" + uid + "&PatId=" + pid +"&endName="+endName;
    }

    /**
     * 分配座位
     *
     * @param departmentId 科室号
     * @param infusionId   输液ID
     * @param seatNo       新座位号
     * @param flag         0-手动 1-自动
     */
    public static String url_updateSeat(String departmentId, String infusionId, String seatNo,
                                         int flag, String area) {
        return POST_EXCUTESEATNO
                + "infusionId="
                + infusionId
                + "&seatNo="
                + seatNo
                + "&departmentId="
                + departmentId
                + "&flag="
                + flag
                + "&area="
                +area;
    }

    /**
     * 通过patientID判断是否要结束正在执行的瓶贴，如果要结束结束就传门诊号否则就传0更新当前瓶贴
     *
     * @param patid
     * @return
     */
    public static String url_updateBottleAndfinishRuning(String patid) {
        return GET_INFUSIONINFO + "patid=" + patid ;
    }

    public static String url_updateBottlesAndQueues(String uid) {
        return POST_INFUSIONQUEUE + "?type=" + uid + "&";
    }

    /**
     * 修改密码的功能
     * @param userName
     * @param oldPassword
     * @param newPassword
     * @return
     */
    public static String modifyPassword(String userName, String oldPassword, String newPassword){
        return  GET_RESTACOUNT +"userName="+userName +"&pass="+oldPassword +"&newpass="+newPassword;
    }
    /**
     * 更新配液信息
     */
    public static String updatePeiyeBybottleId(String bottleId,String uid,String userName){
        return GET_INFUSIONINFO+"bottleId="+bottleId+"&uid="+uid+"&userName="+userName+"";
    }
    /**
     * 更新配液信息,带科室
     */
    public static String updatePeiyeBybottleId(String bottleId,String uid,String userName,String deptcode){
        return GET_INFUSIONINFO+"bottleId="+bottleId+"&uid="+uid+"&userName="+userName+""+"&deptcode="+deptcode;
    }
}
