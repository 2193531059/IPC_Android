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

import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "MainActivity";

//    private IMyAidl mAidl;
    private IMyAidlManager mAidlManager;
    private Button mBtn;
    private TextView mTextView;
    private Messenger mService;
    private Messenger getmService = new Messenger(new MessengerHandler());

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
            if (mAidlManager == null) {
                return;
            }
            mAidlManager.asBinder().unlinkToDeath(mDeathRecipient, 0);
            mAidlManager = null;
            Intent intent = new Intent(getApplicationContext(), MyService.class);
            bindService(intent, mConnection, BIND_AUTO_CREATE);
        }
    };

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
//            mAidl = IMyAidl.Stub.asInterface(service);
            mAidlManager = MyAidlManagerImpl.asInterface(service);
            try {
                service.linkToDeath(mDeathRecipient, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mAidlManager = null;
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
        mBtn = findViewById(R.id.pressbtn);
        mBtn.setOnClickListener(this);
        mTextView = findViewById(R.id.showText);
        Intent intent = new Intent(getApplicationContext(), MyService.class);
        bindService(intent, mConnection, BIND_AUTO_CREATE);
        Intent intent1 = new Intent(getApplicationContext(), MessengerService.class);
        bindService(intent1, mconn, BIND_AUTO_CREATE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.pressbtn:
                Random random = new Random();
                Person p = new Person("周", 1, random.nextInt(100), new PersonCar("Black", "BMW"));

                try {
                    mAidlManager.addPerson(p);
                    List<Person> mAcceptData = mAidlManager.getPersonList();
                    mTextView.setText(mAcceptData.toString());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
        unbindService(mconn);
    }
}
