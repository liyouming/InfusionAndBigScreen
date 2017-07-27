package com.fugao.newbigscreen.model;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

/** 输液呼叫和等待
 * Created by li on 2016/6/18.
 */
public class InfusionCallAndWaitModel {
    @JsonProperty
    public List<QueueModel> WaitList;
    @JsonProperty
    public List<CallModel> OverCall;
    @JsonProperty
    public List<CallModel> FourRoom;
}
