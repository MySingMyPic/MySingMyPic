package com.ylsg365.pai.activity;

import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.ylsg365.pai.R;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class FFmpeg4AndroidActivity extends ActionBarActivity {
    public ByteBuffer buffer;
    public ByteBuffer Imagbuf;
    private ImageView videoImageView;
    public Bitmap VideoBit;
    public ImageView mImag;
    public OutputStream outputStream = null;
    public InputStream inputStream = null;
    public static int packagesize;
    //net package
    public static short type = 0;
    public static int packageLen = 0;
    public static int sendDeviceID = 0;
    public static int revceiveDeviceID = 0;
    public static short sendDeviceType = 0;
    public static int dataIndex = 0;
    public static int dataLen = 0;
    public static int frameNum = 0;
    public static int commType = 0;
    //size
    public int width = 0;
    public int height = 0;
    public byte[] mout;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ffmpeg4_android);

        TextView infoTextView = (TextView) findViewById(R.id.info);
        TextView resTextView = (TextView) findViewById(R.id.res);
        videoImageView = (ImageView) findViewById(R.id.img_video);

        packagesize = 7 * 4 + 2 * 2;
        buffer = ByteBuffer.allocate(packagesize);
        // int ffpmegv = FfmpegIF.getffmpegv();
        //System.out.println("ffmpeg version is " + ffpmegv);
        width = 640;
        height = 480;
        mout = new byte[width * height * 2];
        Imagbuf = ByteBuffer.wrap(mout);
        VideoBit = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        //mImag.postInvalidate();
//        int ret = FFmpegNative.DecodeInit(width, height);
        //System.out.println(" ret is " + ret);
        mHandler = new Handler();
        new StartThread().start();
    }


    final Runnable mUpdateUI = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            VideoBit.copyPixelsFromBuffer(Imagbuf);
            mImag.setImageBitmap(VideoBit);
        }
    };

    class StartThread extends Thread {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            //super.run();
//            int datasize;
//            try {
//                socket = new Socket("192.168.1.15", 9876);
//                //System.out.println("socket");
//                SendCom(FFmpegNative.VIDEO_COM_STOP);
//                SendCom(FFmpegNative.VIDEO_COM_START);
//                //new ShowBuffer().start();
//                inputStream = socket.getInputStream();
//                byte[] Rbuffer = new byte[packagesize];
//                while(true) {
//                    inputStream.read(Rbuffer);
//                    //byte2hex(Rbuffer);
//                    SystemClock.sleep(3);
//                    datasize = getDataL(Rbuffer);
//                    if(datasize > 0) {
//                        byte[] Data = new byte[datasize];
//                        int size;
//                        size = inputStream.read(Data);
//                        FFmpegNative.Decoding(Data, size, mout);
//                        //VideoBit.copyPixelsFromBuffer(Imagbuf);
//                        //mImag.setImageBitmap(VideoBit);
//                        mHandler.post(mUpdateUI);
//                        //System.out.println("read datalen is " + size);
//                        //SystemClock.sleep(10);
//                        SendCom(FFmpegNative.VIDEO_COM_ACK);
//                    }
//                }
//            }catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
        }

//    public void SendCom(int comtype) {
//        byte[] Bbuffer = new byte[packagesize];
//        try {
//            outputStream = socket.getOutputStream();
//            type = FFmpegNative.TYPE_MODE_COM;
//            packageLen = packagesize;
//            commType = comtype;
//            putbuffer();
//            Bbuffer = buffer.array();
//            outputStream.write(Bbuffer);
//            //System.out.println("send done");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

        public void putbuffer() {
            buffer.clear();
            buffer.put(ShorttoByteArray(type));
            buffer.put(InttoByteArray(packageLen));
            buffer.put(InttoByteArray(sendDeviceID));
            buffer.put(InttoByteArray(revceiveDeviceID));
            buffer.put(ShorttoByteArray(sendDeviceType));
            buffer.put(InttoByteArray(dataIndex));
            buffer.put(InttoByteArray(dataLen));
            buffer.put(InttoByteArray(frameNum));
            buffer.put(InttoByteArray(commType));
            //System.out.println("putbuffer done");
        }

        private byte[] ShorttoByteArray(short n) {
            byte[] b = new byte[2];
            b[0] = (byte) (n & 0xff);
            b[1] = (byte) (n >> 8 & 0xff);
            return b;
        }

        private byte[] InttoByteArray(int n) {
            byte[] b = new byte[4];
            b[0] = (byte) (n & 0xff);
            b[1] = (byte) (n >> 8 & 0xff);
            b[2] = (byte) (n >> 16 & 0xff);
            b[3] = (byte) (n >> 24 & 0xff);
            return b;
        }

        public short getType(byte[] tpbuffer) {
            short gtype = (short) ((short) tpbuffer[0] + (short) (tpbuffer[1] << 8));
            //System.out.println("gtype is " + gtype);
            return gtype;
        }

        public int getPakL(byte[] pkbuffer) {
            int gPackageLen = ((int) (pkbuffer[2]) & 0xff) | ((int) (pkbuffer[3] & 0xff) << 8) | ((int) (pkbuffer[4] & 0xff) << 16) | ((int) (pkbuffer[5] & 0xff) << 24);
            //System.out.println("gPackageLen is " + gPackageLen);
            return gPackageLen;
        }

        public int getDataL(byte[] getbuffer) {
            int gDataLen = (((int) (getbuffer[20] & 0xff)) | ((int) (getbuffer[21] & 0xff) << 8) | ((int) (getbuffer[22] & 0xff) << 16) | ((int) (getbuffer[23] & 0xff) << 24));
            //System.out.println("gDataLen is " + gDataLen);
            return gDataLen;
        }

        public int getFrameN(byte[] getbuffer) {
            int getFrameN = (int) (((int) (getbuffer[24])) + ((int) (getbuffer[25]) << 8) + ((int) (getbuffer[26]) << 16) + ((int) (getbuffer[27]) << 24));
            //System.out.println("getFrameN is " + getFrameN);
            return getFrameN;
        }

        private void byte2hex(byte[] buffer) {
            String h = "";
            for (int i = 0; i < buffer.length; i++) {
                String temp = Integer.toHexString(buffer[i] & 0xFF);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                h = h + " " + temp;
            }
            // System.out.println(h);
        }
    }
}
