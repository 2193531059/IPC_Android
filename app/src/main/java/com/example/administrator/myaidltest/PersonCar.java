package com.example.administrator.myaidltest;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2018/7/25.
 */

public class PersonCar implements Parcelable{
    public String color;
    public String brand;

    public PersonCar(String color, String brand) {
        this.color = color;
        this.brand = brand;
    }

    protected PersonCar(Parcel in) {
        color = in.readString();
        brand = in.readString();
    }

    public static final Creator<PersonCar> CREATOR = new Creator<PersonCar>() {
        @Override
        public PersonCar createFromParcel(Parcel in) {
            return new PersonCar(in);
        }

        @Override
        public PersonCar[] newArray(int size) {
            return new PersonCar[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(color);
        dest.writeString(brand);
    }

    @Override
    public String toString() {
        return "PersonCar{" +
                "color='" + color + '\'' +
                ", brand='" + brand + '\'' +
                '}';
    }
}
