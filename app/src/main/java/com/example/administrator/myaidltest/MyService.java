package com.example.administrator.myaidltest;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Administrator on 2018/7/25.
 */

public class MyService extends Service {
    private static final String TAG = "MyService";

    private CopyOnWriteArrayList<Person> mList = new CopyOnWriteArrayList<>();
    private RemoteCallbackList<IOnNewPersonArrviedListener> mListenerList = new RemoteCallbackList<>();
    private AtomicBoolean mIsServiceDestroied = new AtomicBoolean(false);

    private IBinder mIBinder = new IMyAidl.Stub() {
        @Override
        public void addPerson(Person person) throws RemoteException {
            if (!mList.contains(person)) {
                mList.add(person);
            }
        }

        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            int check = checkCallingOrSelfPermission("com.example.administrator.myaidltest.MyService");
            if (check == PackageManager.PERMISSION_DENIED) {
                return false;
            }

            String packageName = null;
            String[] packages = getPackageManager().getPackagesForUid(getCallingUid());
            if (packages != null && packages.length > 0) {
                packageName = packages[0];
            }
            if (!packageName.startsWith("com.example.administrator")) {
                return false;
            }
            return super.onTransact(code, data, reply, flags);
        }

        @Override
        public List<Person> getPersonList() throws RemoteException {
            //如果是耗时操作，由于此处运行在Binder线程池，不需另开线程，但UI调用时，需另开线程，以解决ANR问题
            return mList;
        }

        @Override
        public void registerNewPersonArrviedListener(IOnNewPersonArrviedListener listener) throws RemoteException {
            mListenerList.register(listener);
            logListeneres("register: ");
        }

        @Override
        public void unregisterNewPersonArrviedListener(IOnNewPersonArrviedListener listener) throws RemoteException {
            mListenerList.unregister(listener);
            logListeneres("unregister: ");
        }
    };

    private IBinder mBinder = new MyAidlManagerImpl();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind: --------------");
        return mIBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mList.add(new Person("zhou", 1, 24, new PersonCar("red", "AUDI")));
        mList.add(new Person("xia", 0, 22, new PersonCar("blue", "BENZI")));
        new Thread(new ServiceWorker()).start();
    }

    private void logListeneres(String message){
        final  int N = mListenerList.beginBroadcast();
        Log.e(TAG, "logListeneres: " + message + N);
        mListenerList.finishBroadcast();
    }

    private void onNewPersonArrvied(Person person) throws RemoteException{
        mList.add(person);
        final int N = mListenerList.beginBroadcast();
        for (int i = 0; i<N; i++) {
            IOnNewPersonArrviedListener listener = mListenerList.getBroadcastItem(i);
            if (listener != null) {
                listener.onNewPersonArrvied(person);
            }
        }
        mListenerList.finishBroadcast();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mIsServiceDestroied.set(true);
    }

    private class ServiceWorker implements Runnable{

        @Override
        public void run() {
            while (!mIsServiceDestroied.get()) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int personID = mList.size() + 1;
                Person person = new Person("xia" + personID, 1, personID, new PersonCar("red", "XXX"));
                try {
                    onNewPersonArrvied(person);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
