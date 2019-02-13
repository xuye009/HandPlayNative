package com.handscape.sdk.bean;

import android.graphics.PointF;
import android.os.Parcel;
import android.os.Parcelable;

public class HSBaseKeyBean implements Parcelable {

    protected HSKeyData hsKeyData;

    public HSBaseKeyBean() {
        hsKeyData = new HSKeyData();
        hsKeyData.setOffsetPoint(new PointF());
    }

    public HSKeyData getHsKeyData() {
        return hsKeyData;
    }

    public void setHsKeyData(HSKeyData hsKeyData) {
        this.hsKeyData = hsKeyData;
    }

    public void setPoint(PointF point) {
        if (hsKeyData != null)
            hsKeyData.setPoint(point);
    }

    protected HSBaseKeyBean(Parcel in) {
        hsKeyData = in.readParcelable(HSKeyData.class.getClassLoader());
    }

    public static final Creator<HSBaseKeyBean> CREATOR = new Creator<HSBaseKeyBean>() {
        @Override
        public HSBaseKeyBean createFromParcel(Parcel in) {
            return new HSBaseKeyBean(in);
        }

        @Override
        public HSBaseKeyBean[] newArray(int size) {
            return new HSBaseKeyBean[size];
        }
    };

    public final void setKeyType(int keyType) {
        if (hsKeyData != null)
            hsKeyData.setKeyType(keyType);
    }

    public final int getKeyType() {
        if (hsKeyData != null)
           return hsKeyData.getKeyType();
        return 0;
    }

    public PointF getPoint() {
        if (hsKeyData != null)
            return hsKeyData.getPoint();
        return null;
    }

    public void setKeyIndex(int keyIndex) {
        if (hsKeyData != null)
            hsKeyData.setKeyIndex(keyIndex);
    }

    public int getKeyIndex() {
        if (hsKeyData != null)
            return hsKeyData.getKeyIndex();
        return 0;
    }

    public void setKeyCode(int code) {
        if (hsKeyData != null)
            hsKeyData.setKeyCode(code);
    }

    public int getKeyCode() {
        if (hsKeyData != null)
           return hsKeyData.getKeyCode();
        return 0;
    }

    public long getKeyDelayTime() {
        if(hsKeyData!=null)
            return hsKeyData.getKeyDelayTime();
        return 0;
    }

    public void setKeyDelayTime(long keyDelayTime) {
        if(hsKeyData!=null){
            hsKeyData.setKeyDelayTime(keyDelayTime);
        }
    }

    public PointF map(int mainPid,int index, int action, float touchX, float touchY, int keyCode, boolean isinConfigMode, boolean undefineMap) {
        return null;
    }

    //默认按键类型
    public int getdefaultKeyType() { return 0; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(hsKeyData,flags);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof HSBaseKeyBean) {
            HSBaseKeyBean newbean = (HSBaseKeyBean) obj;
            return hsKeyData.keyIndex== newbean.getKeyIndex() && hsKeyData.keyCode == newbean.getKeyCode();
        }
        return super.equals(obj);
    }

    public boolean allequals(Object obj) {
        if (obj instanceof HSBaseKeyBean) {
            HSBaseKeyBean newbean = (HSBaseKeyBean) obj;
            return hsKeyData.keyIndex== newbean.getKeyIndex() && hsKeyData.keyCode == newbean.getKeyCode()&&hsKeyData.point.equals(newbean.getPoint());
        }
        return super.equals(obj);
    }
}
