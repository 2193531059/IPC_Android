package com.example.administrator.myaidltest;

import android.os.IBinder;

interface IBinderPool{
    IBinder queryBinder(int binderCode);
}