package com.example.administrator.myaidltest;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/7/25.
 */

public class MyAidlManagerImpl extends Binder implements IMyAidlManager {
    private List<Person> mList;
    /**
     * Construct the stub at attach it to the interface.
     */
    public MyAidlManagerImpl() {
        mList = new ArrayList<>();
        this.attachInterface(this, DESCRIPTOR);
    }

    public static IMyAidlManager asInterface(IBinder obj){
        if (obj == null) {
            return null;
        }
        IInterface inn = obj.queryLocalInterface(DESCRIPTOR);
        if ((inn != null) && inn instanceof IMyAidlManager) {
            return (IMyAidlManager) inn;
        }
        return new MyAidlManagerImpl.Proxy(obj);
    }

    @Override
    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        switch (code) {
            case INTERFACE_TRANSACTION:
                reply.writeString(DESCRIPTOR);
                return true;
            case TRANSACTION_getPersonList:
                data.enforceInterface(DESCRIPTOR);
                List<Person> result = this.getPersonList();
                reply.writeNoException();
                reply.writeTypedList(result);
                return true;
            case TRANSACTION_addPerson:
                data.enforceInterface(DESCRIPTOR);
                Person arg0;
                if (0 != data.readInt()) {
                    arg0 = Person.CREATOR.createFromParcel(data);
                } else {
                    arg0 = null;
                }
                this.addPerson(arg0);
                reply.writeNoException();
                return true;
        }
        return super.onTransact(code, data, reply, flags);
    }

    @Override
    public List<Person> getPersonList() throws RemoteException {
        synchronized (mList) {
            return mList;
        }
    }

    @Override
    public void addPerson(Person person) throws RemoteException {
        synchronized (mList) {
            if (!mList.contains(person)) {
                mList.add(person);
            }
        }
    }

    @Override
    public IBinder asBinder() {
        return this;
    }

    private static class Proxy implements IMyAidlManager{
        private IBinder mRemote;

        Proxy(IBinder remote){
            mRemote = remote;
        }

        public String getInterfaceDescriptor(){
            return DESCRIPTOR;
        }

        @Override
        public List<Person> getPersonList() throws RemoteException {
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            List<Person> result;
            try {
                data.writeInterfaceToken(DESCRIPTOR);
                mRemote.transact(TRANSACTION_getPersonList, data, reply, 0);
                reply.readException();
                result = reply.createTypedArrayList(Person.CREATOR);
            } finally {
                reply.recycle();
                data.recycle();
            }
            return result;
        }

        @Override
        public void addPerson(Person person) throws RemoteException {
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            try {
                data.writeInterfaceToken(DESCRIPTOR);
                if (person != null) {
                    data.writeInt(1);
                    person.writeToParcel(data, 0);
                } else {
                    data.writeInt(0);
                }
                mRemote.transact(TRANSACTION_addPerson, data, reply, 0);
                reply.readException();
            } finally {
                reply.recycle();
                data.recycle();
            }
        }

        @Override
        public IBinder asBinder() {
            return mRemote;
        }
    }
}
