package com.example.administrator.myaidltest.aidl_impl;

import android.os.RemoteException;
import android.util.Log;

import com.example.administrator.myaidltest.IMyAidl;
import com.example.administrator.myaidltest.IOnNewPersonArrviedListener;
import com.example.administrator.myaidltest.Person;

import java.util.List;

/**
 * Created by Administrator on 2018/7/27.
 */

public class MyAidlImpl extends IMyAidl.Stub{
    private static final String TAG = "MyAidlImpl";

    @Override
    public void addPerson(Person person) throws RemoteException {
        Log.e(TAG, "addPerson: person = " + person);
    }

    @Override
    public List<Person> getPersonList() throws RemoteException {
        Log.e(TAG, "getPersonList: ---------------");
        return null;
    }

    @Override
    public void registerNewPersonArrviedListener(IOnNewPersonArrviedListener listener) throws RemoteException {
        Log.e(TAG, "registerNewPersonArrviedListener: --------------");
    }

    @Override
    public void unregisterNewPersonArrviedListener(IOnNewPersonArrviedListener listener) throws RemoteException {
        Log.e(TAG, "unregisterNewPersonArrviedListener: ------------------");
    }
}
