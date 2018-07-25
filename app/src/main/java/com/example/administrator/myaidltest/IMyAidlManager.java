package com.example.administrator.myaidltest;

import android.os.IInterface;
import android.os.RemoteException;

import java.util.List;

/**
 * Created by Administrator on 2018/7/25.
 */

public interface IMyAidlManager extends IInterface{
    static final String DESCRIPTOR = "com.example.administrator.myaidltest.IMyAidl";
    static final int TRANSACTION_addPerson = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    static final int TRANSACTION_getPersonList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);

    public List<Person> getPersonList() throws RemoteException;
    public void addPerson(Person person) throws RemoteException;
}
