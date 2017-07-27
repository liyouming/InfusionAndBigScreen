package com.fugao.newbigscreen.constant;

/** 大屏接口
 * Created by li on 2016/6/20.
 */
public class InfoApi {
    private static String BASE_URL="api/";
    private static String GET_CALLANDWAIT = BASE_URL+"infusion/callqueue?";
    private static String GET_CALL=BASE_URL+"infusion/InfusionQueue?";
    /**
     * 获得输液区域的接口
     */
    private static String GET_INFUSION_AREA = BASE_URL + "infusion/area?";
    private static String POST_EXCUTESEATNO = BASE_URL + "infusion/infusionseat?";
    /**
     * 得到呼叫和等候信息
     * type=0是得到穿刺信息，type=1是得到等候信息
     */
    public static String getGetCallandwait(String departmentId,String limit,String type){
        String resultString="";
        resultString=GET_CALLANDWAIT+"departmentid="+departmentId+"&status=0&limit="+limit+"&bigsreen="+type;
        return resultString;
    }
    /**
     * 得到呼叫信息
     */
    public static String getGetCall(String departmentId){
        String resultString="";
        resultString=GET_CALL+"dpt="+departmentId;
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
     *  获取输液室下座位信息
     * @param deparId
     * @param action
     * @return
     */
    public static String url_GetSeatAndInfo(String deparId, String action) {
        return POST_EXCUTESEATNO + "deparId=" + deparId + "&action=" + action;
    }
    /**
     * 获取呼叫队列信息
     */
    public static String getinfusionCallQueueUrl(String type){
        return GET_CALL+"type="+type;
    }
    /**
     * 获取同步时间
     */
    public static String getSyctime(){
        return GET_CALLANDWAIT+"bigscreen=0";
    }
}
