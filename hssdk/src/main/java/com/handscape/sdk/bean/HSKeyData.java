package com.handscape.sdk.bean;

import android.graphics.PointF;
import android.os.Parcel;
import android.os.Parcelable;

public class HSKeyData implements Parcelable{

    //按键的code
    protected int keyCode=0;
    //第几个点
    protected int keyIndex=0;
    //按键延时，单位 毫秒 限制>=0
    protected long keyDelayTime=0;
    //按键距离上一个按钮的延时
    protected long intervalDelayTime=0;
    //按键的ID
    //在触摸过程中实时生成
    protected int keyPointId=0;
    //按键类型
    protected int keyType = 0;
    //映射到的中心坐标点
    protected PointF point = null;
    //位移量，用来辅助实现一些自定义按键
    protected PointF offsetPoint = null;

    public long getKeyDelayTime() {
        return keyDelayTime;
    }

    public long getIntervalDelayTime() {
        return intervalDelayTime;
    }

    public void setIntervalDelayTime(long intervalDelayTime) {
        this.intervalDelayTime = intervalDelayTime;
    }

    public void setKeyDelayTime(long keyDelayTime) {
        this.keyDelayTime = keyDelayTime;
    }

    public int getKeyPointId() { return keyPointId; }

    public int getKeyIndex() { return keyIndex; }

    public int getKeyCode() {
        return keyCode;
    }

    public int getKeyType() {
        return keyType;
    }

    public PointF getPoint() { return point; }

    public void setKeyPointId(int keyPointId) { this.keyPointId = keyPointId; }

    public void setKeyIndex(int keyIndex) {
        this.keyIndex = keyIndex;
    }

    public void setKeyCode(int keyCode) {
        this.keyCode = keyCode;
    }


    public void setKeyType(int keyType) {
        this.keyType = keyType;
    }

    public void setOffsetPoint(PointF offsetPoint) {
        this.offsetPoint = offsetPoint;
    }

    public void setPoint(PointF point) {
        this.point = point;
    }

    public PointF getOffsetPoint() {
        return offsetPoint;
    }

    public HSKeyData(){
        offsetPoint=new PointF();
    }

    protected HSKeyData(Parcel in) {
        keyPointId=in.readInt();
        keyType = in.readInt();
        keyCode = in.readInt();
        keyIndex = in.readInt();
        keyDelayTime=in.readLong();
        intervalDelayTime=in.readLong();
        point = in.readParcelable(PointF.class.getClassLoader());
        offsetPoint = in.readParcelable(PointF.class.getClassLoader());
    }

    public static final Creator<HSKeyData> CREATOR = new Creator<HSKeyData>() {
        @Override
        public HSKeyData createFromParcel(Parcel in) {
            return new HSKeyData(in);
        }

        @Override
        public HSKeyData[] newArray(int size) {
            return new HSKeyData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(keyPointId);
        dest.writeInt(keyType);
        dest.writeInt(keyCode);
        dest.writeInt(keyIndex);
        dest.writeLong(keyDelayTime);
        dest.writeLong(intervalDelayTime);
        dest.writeParcelable(point, flags);
        dest.writeParcelable(offsetPoint, flags);
    }
}
