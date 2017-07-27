package com.fugao.newbigscreen.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.codehaus.jackson.annotate.JsonProperty;

/** 呼叫队列
 * Created by li on 2016/6/24.
 */
public class InfusionCallQueue implements Parcelable{
    @JsonProperty
    public String Name;
    @JsonProperty
    public String CallNo;
    @JsonProperty
    public String CallRoom;
    @JsonProperty
    public String Type;
    @JsonProperty
    public String DateAndTime;

    public static final Creator<InfusionCallQueue> CREATOR = new Creator<InfusionCallQueue>() {
        @Override
        public InfusionCallQueue createFromParcel(Parcel in) {
            InfusionCallQueue infusionCallQueue=new InfusionCallQueue();
            infusionCallQueue.Name=in.readString();
            infusionCallQueue.CallNo=in.readString();
            infusionCallQueue.CallRoom=in.readString();
            infusionCallQueue.Type=in.readString();
            infusionCallQueue.DateAndTime=in.readString();
            return infusionCallQueue;
        }

        @Override
        public InfusionCallQueue[] newArray(int size) {
            return new InfusionCallQueue[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Name);
        dest.writeString(CallNo);
        dest.writeString(CallRoom);
        dest.writeString(Type);
        dest.writeString(DateAndTime);
    }

}
