package com.sdses.ndk;

/**
 * Created by meng on 12/15/17.
 */

public class UsbComm {
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("usbcomm");
    }
    public native int open(String device);
    public native int close(int fd);
    public native int read(int fd,byte[] buf,int len);
    public native int write(int fd,byte[] buf,int len);
}
