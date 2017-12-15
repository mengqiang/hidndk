package com.sdses.ndk;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.sdses.ndk.tool.Util;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{



    private String TAG = MainActivity.class.getSimpleName();

    private String path = "/dev/hidg0";
    private UsbComm myUsbComm;
    int fd = 0;

    TextView tv = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        tv = (TextView) findViewById(R.id.sample_text);
        //Log.d(TAG,"stringFromJNI()="+stringFromJNI());
        findViewById(R.id.buttonOpen).setOnClickListener(this);
        findViewById(R.id.buttonWrite).setOnClickListener(this);
        findViewById(R.id.buttonRead).setOnClickListener(this);
        findViewById(R.id.buttonClose).setOnClickListener(this);

        myUsbComm =  new UsbComm();
    }


    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.buttonOpen:
                Log.d(TAG,"buttonOpen Clicked");
                tv.setText("buttonOpen Clicked");
                fd = myUsbComm.open(path);
                Log.d(TAG,"open()="+fd);
                break;
            case R.id.buttonWrite:
                Log.d(TAG,"buttonWrite Clicked");
                tv.setText("buttonWrite Clicked");
                if(fd>0) {
                    String send2 = String.format("%-512s","Hello\nworld");
                    String send1 = String.format("%-516s","1234");
                    String send3 = "中文";
                    int temp = 0;
                    try {
                        temp= myUsbComm.write(fd,send1.getBytes("UTF-8"),send1.getBytes("UTF-8").length);
                        Log.d(TAG,"已发送"+temp+"字节");
                        temp= myUsbComm.write(fd,send2.getBytes("UTF-8"),send2.getBytes("UTF-8").length);
                        Log.d(TAG,"已发送"+temp+"字节");
                        temp= myUsbComm.write(fd,send3.getBytes("UTF-8"),send3.getBytes("UTF-8").length);
                        Log.d(TAG,"已发送"+temp+"字节");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.buttonRead:
                Log.d(TAG,"buttonRead Clicked");
                tv.setText("buttonRead Clicked");
                byte[] recv = new byte[512];
                int recvLen = myUsbComm.read(fd,recv,512);
                Log.d(TAG,"recvLen="+recvLen);
                Log.d(TAG,"recv="+ Util.toHexStringWithSpace(recv,recv.length));
                int len = recv[1];
                try {
                    String recvString = new String(recv,2,len,"GB2312");
                    Log.d(TAG,"recvString="+recvString);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.buttonClose:
                Log.d(TAG,"buttonClose Clicked");
                tv.setText("buttonClose Clicked");
                if(fd>0) {
                    int close = myUsbComm.close(fd);
                    Log.d(TAG, "close()=" + close);
                }
                break;
        }
    }
}
