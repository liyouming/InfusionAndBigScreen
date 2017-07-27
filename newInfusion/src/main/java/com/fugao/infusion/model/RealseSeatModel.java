package com.fugao.infusion.model;

import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;


public class RealseSeatModel implements Serializable{
    private static final String TAG = "Fugao-RealseSeatModel";

    @JsonProperty
    /**
     * 座位号
     */
    public String Seat;

    @JsonProperty
    /**
     * 病人信息
     */
    public QueueModel PeopleInfo;
}
