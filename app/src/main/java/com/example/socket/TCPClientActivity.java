package com.example.socket;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.administrator.myaidltest.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TCPClientActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "TCPClientActivity";
    private final static int MESSAGE_RECEIVE_NEW_MESSAGE = 1;
    private final static int MESSAGE_SOCKET_CONNECTED = 2;
    private final static int MESSAGE_SEND = 3;

    private Button mSend;
    private EditText mEdit;
    private TextView mMessageShowText;
    private PrintWriter mPrintWriter;
    private Socket mClientSocket;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MESSAGE_SOCKET_CONNECTED:
                    mSend.setEnabled(true);
                    break;
                case MESSAGE_RECEIVE_NEW_MESSAGE:
                    mMessageShowText.setText(mMessageShowText.getText() + (String) msg.obj);
                    break;
                case MESSAGE_SEND:
                    mEdit.setText("");
                    mMessageShowText.setText(mMessageShowText.getText() + (String) msg.obj);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcpclient);
        mSend = findViewById(R.id.message_send);
        mEdit = findViewById(R.id.message_edit);
        mMessageShowText = findViewById(R.id.show_message_part);
        mSend.setOnClickListener(this);

        Intent service = new Intent(this, TCPServerService.class);
        startService(service);

        new Thread(){
            @Override
            public void run() {
                connectTCPServer();
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        if (mClientSocket != null) {
            try {
                mClientSocket.shutdownInput();
                mClientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }

    private void connectTCPServer(){
        Socket socket = null;
        while (socket == null) {
            try {
                socket = new Socket("localhost", 8688);
                mClientSocket = socket;
                mPrintWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                mHandler.sendEmptyMessage(MESSAGE_SOCKET_CONNECTED);
                Log.e(TAG, "connectTCPServer: TCPServer connect success");
            } catch (IOException e) {
                SystemClock.sleep(1000);
                Log.e(TAG, "connectTCPServer: TCPServer connect failed, retry.....");
            }
        }

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (!TCPClientActivity.this.isFinishing()){
                String msg = br.readLine();
                Log.e(TAG, "connectTCPServer: message receive : " + msg);
                if (msg != null) {
                    String time = formateDateTime(System.currentTimeMillis());
                    final String showMessage = "server " + time + " msg: " + msg + "\n";
                    mHandler.obtainMessage(MESSAGE_RECEIVE_NEW_MESSAGE, showMessage).sendToTarget();
                }
            }
            Log.e(TAG, "connectTCPServer: quit");
            if (mPrintWriter != null) {
                mPrintWriter.close();
            }
            if (br != null) {
                br.close();
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String formateDateTime(long time){
        return new SimpleDateFormat("(HH:mm:ss)").format(new Date(time));
    }

    @Override
    public void onClick(View v) {
        if (v == mSend) {
            final String msg = mEdit.getText().toString();
            if (!TextUtils.isEmpty(msg) && mPrintWriter != null) {
                new Thread(){
                    @Override
                    public void run() {
                        mPrintWriter.println(msg);
                        String time = formateDateTime(System.currentTimeMillis());
                        final String showMessage = "self " + time + " msg: " + msg + "\n";
                        mHandler.obtainMessage(MESSAGE_SEND, showMessage).sendToTarget();
                    }
                }.start();
            }
        }
    }
}
