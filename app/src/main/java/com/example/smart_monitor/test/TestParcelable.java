package com.example.smart_monitor.test;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.fragment.app.Fragment;

public class TestParcelable implements Parcelable {
    private String name;
    private Integer age;
    private Integer sex;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public TestParcelable(String name, Integer age, Integer sex){
        this.name = name;
        this.age = age;
        this.sex = sex;
    }

    protected TestParcelable(Parcel in) {
        name = in.readString();
        age = in.readInt();
        sex = in.readInt();

    }

    public static final Creator<TestParcelable> CREATOR = new Creator<TestParcelable>() {
        @Override
        public TestParcelable createFromParcel(Parcel in) {
            return new TestParcelable(in);
        }

        @Override
        public TestParcelable[] newArray(int size) {
            return new TestParcelable[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(sex);
        dest.writeInt(age);
//        dest.writeParcelable();
    }
}
