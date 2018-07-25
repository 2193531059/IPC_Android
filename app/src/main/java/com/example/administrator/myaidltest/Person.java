package com.example.administrator.myaidltest;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2018/7/25.
 */

public class Person implements Parcelable{

    public String name;
    public int sex;
    public int age;
    public PersonCar car;

    public Person(String name, int sex, int age, PersonCar car) {
        this.name = name;
        this.sex = sex;
        this.age = age;
        this.car = car;
    }

    protected Person(Parcel in) {
        name = in.readString();
        sex = in.readInt();
        age = in.readInt();
        car = in.readParcelable(PersonCar.class.getClassLoader());
    }

    public static final Creator<Person> CREATOR = new Creator<Person>() {
        @Override
        public Person createFromParcel(Parcel in) {
            return new Person(in);
        }

        @Override
        public Person[] newArray(int size) {
            return new Person[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(age);
        dest.writeInt(sex);
        dest.writeParcelable(car, 0);
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", sex=" + sex +
                ", age=" + age +
                ", car=" + car +
                '}';
    }
}
