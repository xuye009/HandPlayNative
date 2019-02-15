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


    public PointF map(int mainPid, int index, int action, float touchX, float touchY, int keyCode) {
        return null;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(hsKeyData, flags);
    }
}
