package com.example.socket;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class TCPServerService extends Service {
    private static final String TAG = "TCPServerService";

    private boolean mIsServiceDestroied = false;
    private String[] mDefinedMessage = new String[]{
            "你好啊，哈哈",
            "请问你叫什么名字呀？",
            "今天深圳天气不错呀，shy",
            "你知道吗？我可以同时和多人聊天哦",
            "给你讲个笑话吧：据说爱笑的人运气不会太差，不知道真假"
    };

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate: -----------------------");
        new Thread(new TCPServer()).start();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        mIsServiceDestroied = true;
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private class TCPServer implements Runnable{

        @Override
        public void run() {
            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(8688);
            } catch (IOException e) {
                Log.e(TAG, "run: establish tcp server failed,port:8688");
                e.printStackTrace();
                return;
            }
            while (!mIsServiceDestroied){
                try {
                    final Socket client = serverSocket.accept();
                    Log.e(TAG, "run: accept ");
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                responseClient(client);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void responseClient(Socket client) throws IOException{
        //用于接收客户端消息
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        //用于向客户端发送消息
        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
        out.println("欢迎来到聊天室");
        while (!mIsServiceDestroied) {
            String str = in.readLine();
            Log.e(TAG, "responseClient: message from client : + str");
            if (str == null) {
                break;
            }
            int i = new Random().nextInt(mDefinedMessage.length);
            String msg = mDefinedMessage[i];
            out.println(msg);
            Log.e(TAG, "responseClient: send: " + msg);
        }
        Log.e(TAG, "responseClient: client quit");
        if (in != null) {
            in.close();
        }
        if (out != null) {
            out.close();
        }
        client.close();
    }
}
