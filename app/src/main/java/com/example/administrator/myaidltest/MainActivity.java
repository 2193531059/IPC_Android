package com.example.administrator.myaidltest;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.administrator.messengertest.MessengerService;
import com.example.administrator.myaidltest.aidl_impl.BinderPool;
import com.example.administrator.myaidltest.aidl_impl.MyAddImpl;
import com.example.administrator.myaidltest.aidl_impl.MyAidlImpl;

import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";

    private IMyAidl mAidl;
    private IMyAidlManager mAidlManager;
    private Button mBtn;
    private TextView mTextView;
    private Messenger mService;
    private Messenger getmService = new Messenger(new MessengerHandler());

    private static final int ON_NEW_PERSON_ARRIVED = 1;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ON_NEW_PERSON_ARRIVED:
                    Person person = (Person) msg.obj;
                    mTextView.setText(person.toString());
                    break;
            }
        }
    };

    private IOnNewPersonArrviedListener mListener = new IOnNewPersonArrviedListener.Stub() {
        @Override
        public void onNewPersonArrvied(Person person) throws RemoteException {
            mHandler.obtainMessage(ON_NEW_PERSON_ARRIVED, person).sendToTarget();
        }
    };

    private static class MessengerHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 2:
                    Log.e(TAG, "handleMessage: " + msg.getData().getString("reply"));
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            //此处运行在Binder线程池中，不可进行UI操作
            if (mAidl == null) {
                return;
            }
            mAidl.asBinder().unlinkToDeath(mDeathRecipient, 0);
            mAidl = null;
            Intent intent = new Intent(getApplicationContext(), MyService.class);
            bindService(intent, mConnection, BIND_AUTO_CREATE);
        }
    };

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mAidl = IMyAidl.Stub.asInterface(service);
//            mAidlManager = MyAidlManagerImpl.asInterface(service);
            try {
                mAidl.registerNewPersonArrviedListener(mListener);
                service.linkToDeath(mDeathRecipient, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mAidl = null;
        }
    };

    private ServiceConnection mconn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = new Messenger(service);
            Message message = Message.obtain(null, 1);
            Bundle data = new Bundle();
            data.putString("msg", "aaaaaaaaaaa");
            message.setData(data);
            message.replyTo = getmService;
            try {
                mService.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //BinderPool测试模块
//        new Thread(){
//            @Override
//            public void run() {
//                doWork();
//            }
//        }.start();

        //AIDL测试模块
        mBtn = findViewById(R.id.pressbtn);
        mBtn.setOnClickListener(this);
        mTextView = findViewById(R.id.showText);
        Intent intent = new Intent(getApplicationContext(), MyService.class);
        bindService(intent, mConnection, BIND_AUTO_CREATE);

        //Messenger测试模块
//        Intent intent1 = new Intent(getApplicationContext(), MessengerService.class);
//        bindService(intent1, mconn, BIND_AUTO_CREATE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.pressbtn:
                Random random = new Random();
                Person p = new Person("周", 1, random.nextInt(100), new PersonCar("Black", "BMW"));

                try {
                    mAidl.addPerson(p);
                    List<Person> mAcceptData = mAidl.getPersonList();
                    mTextView.setText(mAcceptData.toString());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void doWork(){
        BinderPool binderPool = BinderPool.getInstance(MainActivity.this);
        IBinder mAddBinder = binderPool.queryBinder(BinderPool.BINDER_IMYADD);
        IMyAdd myAdd = MyAddImpl.asInterface(mAddBinder);
        try {
            int a = myAdd.add(3, 5);
            Log.e(TAG, "doWork: a = " + a);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        IBinder aAidlBinder = binderPool.queryBinder(BinderPool.BINDER_IMYAIDL);
        IMyAidl aidl = MyAidlImpl.asInterface(aAidlBinder);
        try {
            aidl.addPerson(new Person("a", 2, 2, new PersonCar("red","BMW")));

            List<Person> personList = aidl.getPersonList();
            Log.e(TAG, "doWork: personList = " + personList);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAidl != null && mAidl.asBinder().isBinderAlive()) {
            try {
                Log.e(TAG, "onDestroy: unregisterListener = " + mListener);
                mAidl.unregisterNewPersonArrviedListener(mListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        unbindService(mConnection);
//        unbindService(mconn);
    }
}
