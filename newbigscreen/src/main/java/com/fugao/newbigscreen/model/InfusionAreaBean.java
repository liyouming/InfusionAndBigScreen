package com.fugao.newbigscreen.model;



public class InfusionAreaBean {
    private static final String TAG = "Fugao-InfusionAreaBea";
    /**
     * 代码
     */
    public int Id ;
    /**
     * 病区代码
     */
    public String DepartmentId ;

    /***
     * 科室名称
     */
    public String DepartmentName ;
    /**
     * 区域代码
     */
    public String AreaCode ;
    /**
     * 区域名称
     */
    public String AreaName ;
    /**
     * 屏幕
     */
    public String Screen ;

    /**
     * 类型 如果为1就是区域  如果为2就是注射室
     */
    public int Type;
    /**
     *
     */
    public String Current;
}
