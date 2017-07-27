package com.fugao.infusion.model;

import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

/**
 * Do one thing at a time, and do well!
 *
 * @Prject: FastApps
 * @Location: com.android.fastinfusion.model.InfusionStatus
 * @Description: TODO 通过扫描瓶贴时候会返回一个Model 和执行和未执行的个数
 * @author: 胡乐    hule@fugao.com
 * @date: 2014/12/21 23:15
 * @version: V1.0
 */

public class InfusioningModel implements Serializable {

    /**
     * 瓶贴对象
     */
    @JsonProperty
    public BottleModel bottle;
    /**
     * 还有多少个瓶贴没执行个数
     */
    @JsonProperty
   public int TodoCount;
    /**
     * 正在输液的瓶贴个数
     */
    @JsonProperty
   public int DoingCount;
}
