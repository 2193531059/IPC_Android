package com.example.administrator.myaidltest;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/7/25.
 */

public class MyService extends Service {
    private static final String TAG = "MyService";

    private List<Person> mList;

    private IBinder mIBinder = new IMyAidl.Stub() {
        @Override
        public void addPerson(Person person) throws RemoteException {
            synchronized (mList) {
                if (!mList.contains(person)) {
                    mList.add(person);
                }
            }
        }

        @Override
        public List<Person> getPersonList() throws RemoteException {
            synchronized (mList) {
                return mList;
            }
        }
    };

    private IBinder mBinder = new MyAidlManagerImpl();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        mList = new ArrayList<>();
        Log.e(TAG, "onBind: --------------");
        return mBinder;
    }
}
