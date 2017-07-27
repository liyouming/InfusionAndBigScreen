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
 * @Modify-description: TODO 穿刺界面所需接口
 * @Modify-author: 修改人
 * @version: V1.0
 */

public class ChuanCiApi {
    private static String API_NAME = "api";

    private static String BASE_URL = API_NAME + "/";

    private static String GET_INFUSIONINFO = BASE_URL + "infusion/infusionInfo?";

    private static String POST_INFUSIONINFO = BASE_URL + "infusion/infusionInfo";

    private static String GET_INFUSIONQUEUE = BASE_URL + "infusion/InfusionQueue?";

    private static String POST_INFUSIONQUEUE = BASE_URL + "infusion/InfusionQueue";

    private static String POST_INFUSIONQUEUETO = BASE_URL + "infusion/InfusionQueue/UpdateQueue?";
    public static String POST_INFUSIONQUEUETOFORZHONGJI = BASE_URL + "infusion/InfusionQueue/UpdateQueue";

    private static String POST_EXCUTESEATNO = BASE_URL + "infusion/infusionseat?";

    private static String GET_CALL = BASE_URL + "infusion/agent?";

    private static String POST_PATROL = BASE_URL + "infusion/infusionpatrol";

    private static String POST_INFUSIONCALL = BASE_URL + "infusion/CallQueue?";
    /**
     * 获得输液区域的接口
     */
    private static String GET_INFUSION_AREA=BASE_URL+ "infusion/area?";


    private static String GET_INFUSIONCALL = BASE_URL + "infusion/CallQueue";
    /**
     * 通过瓶贴id获得该瓶贴信息
     * @param bottleId
     * @return
     */
    public static String url_getBottleByBottleId(String bottleId) {
        return GET_INFUSIONINFO + "BottleId=" + bottleId;
    }

    private static String Url_GetBottlesByKeyword(String keyword) {
        return GET_INFUSIONINFO + "keyword=" + keyword;
    }

    /**
     * 非第一次呼叫接口，通过手动进行你呼叫
     * @param departmentId
     * @param iid
     * @param areaId
     * @return
     */
    public static String url_getCallByHand(String departmentId, String iid, String areaId) {
        return GET_CALL + "infusionid=" + iid + "&departmentId=" + departmentId +"&areaId="+areaId;
    }

    /**
     * 非第一次呼叫，通过扫描呼叫
     * @param departmentId
     * @param iid
     * @param areaId
     * @return
     */
    public static String url_getCallForOverOne(String departmentId, String iid, String areaId ){
        return GET_INFUSIONQUEUE + "infusionid=" + iid + "&departmentId=" + departmentId +"&areaId="+areaId;
    }

    /**
     * 添加巡视记录
     * @return
     */
    public static String url_addPatorl() {
        return POST_PATROL ;
    }
    /**
     * 提交瓶贴信息
     * @return
     */
    public static String url_updateBottles() {
        return POST_INFUSIONINFO ;
    }


    /**
     * 穿刺更新队列
     * @param departmentId
     * @param areaId
     * @return
     */
    public static String url_updateQueues(String departmentId, String areaId) {
        return POST_INFUSIONQUEUETO +"departmentId=" + departmentId+"&areaId=" +areaId;
    }


    /**
     * 穿刺更新瓶贴和队列
     * @param type
     * @return
     */
    public static String url_updateBottlesAndQueues(String type) {
        return GET_INFUSIONQUEUE + "type=" + type+"&";
    }

    /**
     * 穿刺更新瓶贴和队列(分科室)
     * @param type
     * @return
     */
    public static String url_updateBottlesAndQueues(String type,String deptcode) {
        return GET_INFUSIONQUEUE + "type=" + type+"&deptcode="+ deptcode +"&";
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
        return POST_EXCUTESEATNO + "infusionId=" + infusionId+ "&seatNo="+ seatNo+ "&departmentId="
                + departmentId + "&flag="  + "&area="   +area;


    }

    /**
     * 取消完成输液
     *
     * @param rollback
     * @return
     */
    public static String url_getCancelInfusion(int rollback) {
        return GET_INFUSIONINFO + "rollback=" + rollback;
    }

    /**
     * 穿刺过号
     * @param deptId
     * @param infusionId
     * @param overNo
     * @return
     */
    public  static String url_getPostCall(String deptId,String infusionId,String overNo){
        return  POST_INFUSIONCALL +"deptId=" +deptId +"&infusionId="+infusionId +"&overNo="+overNo;
    }

    /**
     *  穿刺列表借口
     * @param departmentId
     * @param statusgroup
     * @return
     */
    public static String url_getBottlesByStatusGroup(String departmentId, String statusgroup) {
        return GET_INFUSIONINFO + "DepartmentId=" + departmentId + "&statusgroup=" + statusgroup;
    }

    /**
     *  重症穿刺列表借口
     * @param departmentId
     * @param statusgroup
     * @return
     */
    public static String url_getBottlesByStatusGroup(String departmentId, String statusgroup,String sFlag) {
        return GET_INFUSIONINFO + "DepartmentId=" + departmentId + "&statusgroup=" + statusgroup+"&sFlag="+sFlag;
    }

    /**
     *  穿刺列表借口
     * @param departmentId
     * @param statusgroup
     * @return
     */
    public static String url_getBottlesByStatusGroup_CHUANCHIBydate(String departmentId, String statusgroup,String dateTime) {
        return (GET_INFUSIONINFO + "DepartmentId=" + departmentId + "&statusgroup=" + statusgroup+"&dateTime="+dateTime).replaceAll(" ","%20");
    }

    /**
     * 成功的话就返回 一个对象，否则为空
     *返回的对象为瓶贴集合
     *呼叫下一个 ==等效第一次呼叫
     * @param deptId
     * @param areaId
     * @return
     */
    public static String url_getNextCall(String deptId, String areaId){
        return GET_INFUSIONQUEUE + "&departmentId=" + deptId +"&areaId="+areaId;
    }

    /**
     * 通过科室代码获取输液区域的代码
     * @param deptID
     * @return
     */
    public  static String getInfusionAreaByDeptID(String deptID){
        String resultString="";
        resultString=GET_INFUSION_AREA+"deptId="+deptID;
        return  resultString;
    }

    /**
     * 结束全部输液
     */
    public static String Url_End(String uid, String pid) {
        return POST_INFUSIONINFO + "?end=" + uid + "&PatId=" + pid;
    }

    /**
     * 结束全部输液 , 进行判断提示
     * @param patid
     * @param checkgroup
     * @return
     */
    public static String Url_overInfusion(String patid, String checkgroup) {
        return GET_INFUSIONINFO + "patid=" + patid + "&checkgroup=" + checkgroup;
    }


    public static String Url_finishCurrentinfuion(int checklast){

        return GET_INFUSIONINFO + "checklast="+checklast;
    }
    public static String Url_GetBottlesByPid(String patid) {
        return GET_INFUSIONINFO + "patid=" + patid;
    }

    /**
     * 获取病人主动呼叫的病人列表(控制呼叫铃铛)
     *
     * @return
     */
    public static String Url_GetCallList(int type, String areaId) {
        return GET_INFUSIONCALL + "?" + "type=" + type + "&patlist=1" +"&areaId=" +areaId;
    }

    /**
     * 相应病人呼叫
     *
     * @param infusionid 队列好
     * @param uid 输液ID
     * @param msg 手动取消呼叫理由 （必填）
     */
    public static String Url_CancelCall(String infusionid, String uid, String msg) {
        return GET_INFUSIONCALL + "?" + "infusionid=" + infusionid + "&userid=" + uid + "&msg=" + msg;
    }

    /**
     * 相应病人呼叫  根据座位号取消
     *
     * @param msg 手动取消呼叫理由 （必填）
     */
    public static String Url_CancelCallBySeat(String seatNo,String uid,String msg) {
        return GET_INFUSIONCALL + "?" + "seatNo=" + seatNo + "&userid=" + uid+ "&msg=" + msg;
    }
    public static String Url_GetCall(int type,String areaId) {
        return GET_INFUSIONCALL + "?" + "type=" +type +"&areaId=" +areaId ;
    }

    /**
     * 非第一次呼叫，通过扫描呼叫 (重症输液巡视角色专用)
     * @param departmentId
     * @param iid
     * @return
     */
    public static String url_getConfirmInfoByZhongZheng(String departmentId, String iid){
        return GET_INFUSIONQUEUE + "infusionid=" + iid + "&departmentId=" + departmentId +"&nocall=1";
    }
    /**
     * 根据一个瓶贴得到当前病人所有未配液瓶贴
     */
    public static String getBottlesByone(String oneBottleId){
        return GET_INFUSIONINFO+"oneBottleId="+oneBottleId +"&statusGroup=1_2_3";
    }
    /**
     * 根据一个瓶贴得到当前病人所有未配液瓶贴,分科室
     */
    public static String getBottlesByone(String oneBottleId,String deptId){
        return GET_INFUSIONINFO+"oneBottleId="+oneBottleId +"&statusGroup=1_2_3"+"&deptId="+deptId;
    }

    /**
     * 取消穿刺
     * @return
     */
    public static String cancleChuanCiByBottleId() {
        return GET_INFUSIONINFO + "cancle=cancle&";
    }
    /**
     * 根据一流水号得到当前病人所有未配液瓶贴,分科室
     * @return
     */
    public static String getBottlesByDateAndLsh(String date,String lsh,String statusGroup,String deptId) {
        return GET_INFUSIONINFO + "date="+date +"&lsh="+lsh+"&statusGroup="+statusGroup+"&deptId="+deptId;
    }

}
