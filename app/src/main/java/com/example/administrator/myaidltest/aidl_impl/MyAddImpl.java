package com.example.administrator.myaidltest.aidl_impl;

import android.os.RemoteException;

import com.example.administrator.myaidltest.IMyAdd;

/**
 * Created by Administrator on 2018/7/27.
 */

public class MyAddImpl extends IMyAdd.Stub{

    @Override
    public int add(int a, int b) throws RemoteException {
        return a + b;
    }
}
