package com.fugao.bigscreen.model;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by li on 2016/6/14.
 */
public class People {
    /**
     * 姓名
     */
    @JsonProperty
    public String Name;
    /**
     * 年龄
     */
    @JsonProperty
    public String Age;
    /**
     * 性别
     */
    @JsonProperty
    public String Sex;
}
