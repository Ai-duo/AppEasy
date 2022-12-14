package com.kd.appeasy;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;


import com.wits.serialport.SerialPort;
import com.xixun.joey.uart.BytesData;
import com.xixun.joey.uart.IUartListener;
import com.xixun.joey.uart.IUartService;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ElementsService extends Service {
    public IUartService uart;
    StringBuffer builder = new StringBuffer();
    boolean start = false;
    boolean dmgd = false, weaf = false, timing = false, bright = false;
    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            uart = IUartService.Stub.asInterface(iBinder);
            Log.i("TAG_uart", "================ onServiceConnected ====================");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.i("TAG_uart", "================== onServiceDisconnected ====================");
            uart = null;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("TAG_Service", "服务开启");
        //  weaTimer();
        // getCardSystemUartAidl();
        initPort();
    }

    public final static String DONGYANG = "58558";
    public final static String YONGJIA = "58658";
    public final static String LANXI = "58548";
    public final static String PINGYANG = "58751";
    public final static String RUIAN = "58752";
    public final static String DONGTOU = "58760";
    public final static String WENCHENG = "58750";
    public final static String TAISHUN = "58746";
    public final static String LEQING = "58656";
    public final static String CANGNAN = "58755";
    public final static String QINGTIAN = "58657";
    public final static String JINGNING = "58648";
    public final static String JINHUA = "58549";
    public static String sitenum = DONGTOU;

    //天气预报地址
    public static String dayurl = "http://61.153.246.242:8888/qxdata/QxService.svc/getdayybdata/" + sitenum;
    Timer weaTimer;

    private void weaTimer() {
        weaTimer = new Timer();
        weaTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).build();
                String result = null;
                Request request = new Request.Builder()
                        .url(dayurl)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        result = response.body().string();
                        JSONObject json = new JSONObject(result);
                        String Info = json.optString("DATE");
                        String dayinfo = json.optString("wea_txt1");
                        // site_name = json.optString("wea_logo");
                        if (!TextUtils.isEmpty(Info)) {
                            EventBus.getDefault().post(dayinfo);
                            try {
                                Thread.sleep(60 * 60 * 1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        Log.i("TAG", "获取天气数据失败");
                    }
                } catch (Exception e) {
                    Log.i("TAG", "网络异常");
                }

            }
        }, 0, 3000);
    }

    String s2 = "";
    ArrayList<Byte> byteArrayList = new ArrayList<>();
    static int ii = 0;
    boolean PmFlag = false;
    //String port = "/dev/ttyMT3";
    String port = "/dev/ttyS4";

    public void getCardSystemUartAidl() {
        Intent intent = new Intent("xixun.intent.action.UART_SERVICE");
        intent.setPackage("com.xixun.joey.cardsystem");
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                do {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.i("TAG_uart", "正在获取uart======================");
                } while (null == uart);

                if (ii == 0) {
 /*                   try {
                        uart.write("/dev/ttyMT3", new byte[]{0x44, 0x4d, 0x47, 0x44});
                        Log.i("TAG_uart", "发送  DMGD ======================");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }*/
                  /*  PmFlag = true;
                    try {
                        uart.write("/dev/ttyMT3", new byte[]{0x01, 0x03,0X00,0X00,0X00,0x02, (byte) 0xc4,(byte)0x0b});
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }*/
                /* new Thread(new Runnable() {
                      @Override
                      public void run() {
                          while(true) {
                              try {
                                  uart.write(port, new byte[]{0x15, 0x03, 0X00, 0X64, 0X00, 0x08, 0x06, (byte) 0xc7});
                                  Log.i("TAG_uart", "写入数据");
                              } catch (RemoteException e) {
                                  e.printStackTrace();
                              }
                              try {
                                  Thread.sleep(5000);
                              } catch (InterruptedException e) {
                                  e.printStackTrace();
                              }
                          }
                      }
                  }).start();*/

                    Log.i("TAG_uart", "类初始化 ======================");
                    ii++;
                }
                try {

                    //监听/dev/ttyMT2，获取数据/dev/s3c2410_serial3
                    uart.read(port, new IUartListener.Stub() {
                        @Override
                        public void onReceive(BytesData data) throws RemoteException {
                            Log.i("TAG_uart", "========获取到串口数据===========");
                            if (true) {
                                for (byte a : data.getData()) {
                                    String s1 = "0x" + Integer.toHexString(a & 0xFF) + " ";
                                    char ss = (char) a;
                                    Log.i("TAG_uart", "ss:" + ss + ";s1:" + s1);
                                    if (ss == 'D') {
                                        dmgd = true;
                                        start = true;
                                        builder.append(ss);
                                    } else if (ss == 'M') {
                                        weaf = true;
                                        start = true;
                                        builder.append(ss);
                                    } else if (ss == 'G') {
                                        timing = true;
                                        start = true;
                                        builder.append(ss);
                                    } else if (ss == 'D') {
                                        bright = true;
                                        start = true;
                                        builder.append(ss);
                                    } else if (start) {
                                        start = true;
                                        builder.append(ss);
                                    }
                                    Log.i("TAG_uart", builder.toString());
                                    if (builder.length() == 1) {
                                        //||!builder.toString().equals("FE")
                                        if (!builder.toString().equals("D") && !builder.toString().equals("M") && !builder.toString().equals("G") && !builder.toString().equals("D")) {
                                            builder.delete(0, builder.length());
                                            dmgd = false;
                                            weaf = false;
                                            timing = false;
                                            bright = false;
                                            start = false;
                                        }
                                    }
                                    if (builder.length() == 2) {
                                        //||!builder.toString().equals("FE")
                                        if (!builder.toString().equals("DM") && !builder.toString().equals("WE") && !builder.toString().equals("ON") && !builder.toString().equals("BR")) {
                                            builder.delete(0, builder.length());
                                            dmgd = false;
                                            weaf = false;
                                            timing = false;
                                            bright = false;
                                            start = false;
                                        }
                                    }
                                    if (builder.length() == 4) {
                                        //||!builder.toString().equals("FE")
                                        if (!builder.toString().equals("DMGD") && !builder.toString().equals("WEAF") && !builder.toString().equals("ONOF") && !builder.toString().equals("BRIG")) {
                                            builder.delete(0, builder.length());
                                            dmgd = false;
                                            weaf = false;
                                            timing = false;
                                            bright = false;
                                            start = false;
                                        }
                                    }
                                    if (((dmgd && 'T' == ss) || (';' == ss && (weaf || timing || bright))) && builder.length() > 4) {
                                        dmgd = false;
                                        weaf = false;
                                        timing = false;
                                        bright = false;
                                        start = false;
                                        Log.i("TAG_uart1234", builder.toString());
                                        getElements(builder.toString());
                                        builder.delete(0, builder.length());
                                        Log.i("TAG_uart1234", "END");
                                    }

                                }
                            } else {
                                for (byte b : data.getData()) {
                                    byteArrayList.add(b);
                                    if ((int) byteArrayList.get(0) != 21) {
                                        byteArrayList.clear();
                                    }
                                    s2 += "0x" + Integer.toHexString(b & 0xff) + " ";
                                }
                                // String ss = StrUtil.bytesToAscii(byteArrayList);
                                Log.i("TAG", "收到的PM：" + s2 + ";byteArrayList:" + byteArrayList.size());
                                //Log.i("TAG", "收到的PM：" + ss);
                                if (byteArrayList.size() >= 21) {
                                    int b1 = (int) byteArrayList.get(0);
                                    int b2 = (int) byteArrayList.get(1);
                                    int b3 = (int) byteArrayList.get(2);
                                    Log.i("TAG", "b1:" + b1 + ";" + "b2:" + b2 + ";" + "b3:" + b3 + ";");
                                    if (b1 == 21 && b2 == 3 && b3 == 16) {
                                        int pm = byteArrayList.get(7) * 256 + byteArrayList.get(8);
                                        Log.i("TAG", "pm:" + pm);
                                        //MainActivity.pm_text = "PM2.5:" + pm  + "ug/m³";
                                        //   Log.i("TAG","pm_text:"+MainActivity.pm_text);
                                        byteArrayList.clear();
                                        PmFlag = false;
                                        s2 = "";
                                    } else {
                                        byteArrayList.clear();
                                        s2 = "";
                                    }
                                }
                            }
                        }
                    });
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    ArrayList<Integer> index = new ArrayList<Integer>();

    public int getCharCount(String chars) {
        if (TextUtils.isEmpty(chars)) {
            return 0;
        }
        index.clear();
        char[] chars1 = chars.toCharArray();
        int count = 0;
        for (int j = 0; j < chars1.length; j++) {
            if (chars1[j] == '1') {
                count++;
            }
            if (j == 0 || j == 1 || j == 12 || j == 14 || j == 20 || j == 25 || j == 55) {
                index.add(count - 1);
            }
        }
        return count;
    }

    static String WEA;

    public void getElements(String info) {
        //builder.delete(0, builder.length());
        //开始接受PM2.5数据
        Log.i("TAG", "getElements:");
        if (TextUtils.isEmpty(info)) {
            return;
        }
        boolean setsituation = false;
        //
        if (TextUtils.isEmpty(WEA)) {
            WEA = "天气预报TEST";
        }
        if (info.startsWith("ONOF") && (info.endsWith(";"))) {
            setsituation = true;
            String[] infoss2 = info.split(" ");
            for (int i = 0; i < infoss2.length; i++) {
                Log.i("TAG_uart", i + "定时时间::" + infoss2[i]);
            }

            try {
                uart.write("/dev/ttyMT3", new byte[]{0x4f, 0x4b, 0x21});
                Log.i("TAG_uart", "发送  ok! ======================");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        if (info.startsWith("BRIG") && (info.endsWith(";"))) {
            setsituation = true;
            String[] infoss3 = info.split(" ");
            for (int i = 0; i < infoss3.length; i++) {
                Log.i("TAG_uart", i + "亮度设置::" + infoss3[i]);
            }
            if (infoss3[1].equals("1") || infoss3[1].equals("2") || infoss3[1].equals("3") || infoss3[1].equals("4")) {
                try {
                    uart.write("/dev/ttyMT3", new byte[]{0x4f, 0x4b, 0x21});
                    Log.i("TAG_uart", "发送  ok! ======================");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        if (info.startsWith("WEAF") && (info.endsWith(";"))) {
            String[] infoss1 = info.split(" ");
            for (int i = 0; i < infoss1.length; i++) {
                Log.i("TAG_uart", i + "infoss1::" + infoss1[i]);
            }
            String FC = "";
            for (int i = 2; i < infoss1.length - 1; i++) {

                if (i == 2) {
                    FC = FC + infoss1[i];
                } else {
                    FC = FC + " " + infoss1[i];
                }
            }

            char[] H = FC.toCharArray();
            String GB = "";
            for (int k = 0; k < H.length; k++) {
                int h = (int) H[k];
                GB = GB + Integer.toHexString(h & 0xFF);
            }
            Log.i("TAG_uart", "GB::" + GB);
            String WEA1 = "";
            try {
                WEA1 = stringToGbk(GB);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            String[] infoss2 = WEA1.split(" ");
            WEA = "";
            for (int i = 0; i < infoss2.length; i++) {
                if (i == 0) {
                    if (infoss2[i].equals("LF")) {
                        WEA = WEA + "\n";
                    }
                    if (!infoss2[i].equals("LF")) {
                        WEA = WEA + infoss2[i];
                    }

                }
                if (i != 0) {
                    if (infoss2[i].equals("LF")) {
                        WEA = WEA + "\n";
                    }
                    if (!infoss2[i].equals("LF")) {
                        WEA = WEA + " " + infoss2[i];
                    }

                }
            }


            Log.i("TAG_uart", "WEA::" + WEA);
            // EventBus.getDefault().post(new Elements("", "", "", "", "", "", "", "", WEA, 1));
        }


        if (info.startsWith("DMGD") && (info.endsWith("F") || info.endsWith("T"))) {

            String[] infoss = info.split(" ");
            for (int i = 0; i < infoss.length; i++) {
                Log.i("TAG_uart", i + ":" + infoss[i]);
            }
            //日期
            String date = infoss[2];
            Log.i("TAG", "日期:" + date);
            //时间
            String time = infoss[3];
            Log.i("TAG", "时间:" + time);
            int count = getCharCount(infoss[4]);
            //风向
            String fx = "";//1
            int qc = 5;
            if (infoss[5].length() >= 4) {
                qc = 6;
            }
            if (infoss[4].charAt(0) == '1') {
                fx = infoss[qc + index.get(0)];
                Log.i("TAG", "风向:" + fx);
                if (isNum(fx)) {
                    float f = Float.valueOf(fx);
                    if ((f >= 0 && f < 12.25) || (f > 348.76 && f <= 360)) {
                        fx = "北";
                    } else if (f > 12.26 && f < 33.75) {//22.5
                        fx = "北偏东北";
                    } else if (f > 33.76 && f < 56.25) {
                        fx = "东北";
                    } else if (f > 56.25 && f < 78.75) {
                        fx = "东偏东北";
                    } else if (f > 78.75 && f < 101.25) {
                        fx = "东";
                    } else if (f > 101.25 && f < 123.75) {
                        fx = "东偏东南";
                    } else if (f > 123.76 && f < 146.25) {
                        fx = "东南";
                    } else if (f > 146.26 && f < 168.75) {
                        fx = "南偏东南";
                    } else if (f > 168.75 && f < 191.25) {
                        fx = "南";
                    } else if (f > 191.25 && f < 213.75) {
                        fx = "南偏西南";
                    } else if (f > 213.75 && f < 236.25) {
                        fx = "西南";
                    } else if (f > 236.25 && f < 258.75) {
                        fx = "西偏西南";
                    } else if (f > 258.75 && f < 281.25) {
                        fx = "西";
                    } else if (f > 281.25 && f < 303.75) {
                        fx = "西偏西北";
                    } else if (f > 303.75 && f < 326.25) {
                        fx = "西北";
                    } else if (f > 326.25 && f < 348.75) {
                        fx = "北偏西北";
                    }

                } else {
                    fx = "";
                }
            }
            //风速
            String fs = "";//2
            if (infoss[4].charAt(1) == '1') {
                fs = infoss[qc + index.get(1)];
                Log.i("TAG", "风速:" + fs);
                if (!isNum(fs)) {
                    fs = "";
                } else {
                    fs = Float.parseFloat(infoss[qc + index.get(1)]) / 10 + "";
                }
            }
            //降水
            String js = "";//13
            if (infoss[4].charAt(12) == '1') {
                js = infoss[qc + index.get(2)];
                Log.i("TAG", "降水:" + js);
                if (!isNum(js)) {
                    js = "";
                } else {
                    js = Float.parseFloat(infoss[qc + index.get(2)]) / 10 + "";
                }
            }
            //温度
            String wd = "";
            if (infoss[4].charAt(14) == '1') {
                wd = infoss[qc + index.get(3)];
                Log.i("TAG", "温度:" + wd);
                if (wd.charAt(0) == '/') {
                    wd = " ";
                } else if (wd.charAt(0) == '-') {
                    wd = wd.substring(1, wd.length());
                    wd = "-" + Float.parseFloat(wd) / 10 + "";
                } else {
                    wd = Float.parseFloat(infoss[qc + index.get(3)]) / 10 + "";
                }
            }
            //湿度
            String sd = "";//21
            if (infoss[4].charAt(20) == '1') {
                sd = infoss[qc + index.get(4)];
                Log.i("TAG", "湿度:" + sd);
                if (!isNum(sd)) {
                    sd = "";
                }
            }
            //气压
            String qy = "";//26
            if (infoss[4].charAt(25) == '1') {
                qy = infoss[qc + index.get(5)];
                Log.i("TAG", "气压:" + qy);
                if (!isNum(qy)) {
                    qy = "";
                } else {
                    qy = Float.parseFloat(infoss[qc + index.get(5)]) / 10 + "";
                }
            }
            //能见度
            String njd = "";
            if (infoss[4].charAt(55) == '1') {
                njd = infoss[qc + index.get(6)];
                Log.i("TAG", "能见度:" + njd);
                if (!isNum(njd)) {
                    njd = "12345";
                }

            }
            EventBus.getDefault().post(new Elements(wd, sd, fx, fs, js, qy));
        }

    }

    public void initPort() {
        try {
            getSerialPort();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mSerialPort = getSerialPort();
         //   mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();
            Log.i(TAG, "-----------------mReadThread.start");
        } catch (SecurityException e) {
            Log.e(TAG, "-----------------SecurityException");
        } catch (IOException e) {
            Log.e(TAG, "-----------------IOException");
        } catch (InvalidParameterException e) {
            Log.e(TAG, "-----------------InvalidParameterException");
        }
        new ReadThread().start();
    }

    public boolean isNum(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("[0-9]{1,}");
        Matcher matcher = pattern.matcher((CharSequence) str);
        boolean result = matcher.matches();
        return result;
    }

    public static SerialPort mSerialPort;
    public static OutputStream mOutputStream;
    public static InputStream mInputStream;
    public static ReadThread mReadThread;
    private int boardNum = 0;

    public SerialPort getSerialPort() throws SecurityException, IOException,
            InvalidParameterException {
        // M0,M1,M2榛樿涓插彛鍙蜂负ttyS7,M3涓插彛鍙锋槸ttyS5
        String Adress = zcBoard.getTTYName().get(8).toString();

        Log.i("TAG", "请选择正确主板");
        File file = new File(Adress);
        if (!file.canRead() || !file.canWrite()) {
            Toast.makeText(this, "请选择正确主板！", Toast.LENGTH_SHORT).show();
            return null;
        }

        mSerialPort = new SerialPort(file, 9600, 0);

        return mSerialPort;
    }

    //鍏抽棴涓插彛
    public static String TAG = "TAG";

    public void closeSerialPort() {
        Log.i(TAG, "closeSerialPort");
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
    }

    public class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (true) {
                try {

                    if (mInputStream == null) {
                        break;
                    }
                    byte[] bf = new byte[1];
                    int len = -1;
                    while ((len = mInputStream.read(bf)) != -1) {
                        byte a = bf[0];
                        String s1 = "0x" + Integer.toHexString(a & 0xFF) + " ";
                        char ss = (char) a;
                        Log.i("TAG_uart", "ss:" + ss + ";s1:" + s1);
                        if (ss == 'D') {
                            dmgd = true;
                            start = true;
                            builder.append(ss);
                        } else if (ss == 'M') {
                            weaf = true;
                            start = true;
                            builder.append(ss);
                        } else if (ss == 'G') {
                            timing = true;
                            start = true;
                            builder.append(ss);
                        } else if (ss == 'D') {
                            bright = true;
                            start = true;
                            builder.append(ss);
                        } else if (start) {
                            start = true;
                            builder.append(ss);
                        }
                        Log.i("TAG_uart", builder.toString());
                        if (builder.length() == 1) {
                            //||!builder.toString().equals("FE")
                            if (!builder.toString().equals("D") && !builder.toString().equals("M") && !builder.toString().equals("G") && !builder.toString().equals("D")) {
                                builder.delete(0, builder.length());
                                dmgd = false;
                                weaf = false;
                                timing = false;
                                bright = false;
                                start = false;
                            }
                        }
                        if (builder.length() == 2) {
                            //||!builder.toString().equals("FE")
                            if (!builder.toString().equals("DM") && !builder.toString().equals("WE") && !builder.toString().equals("ON") && !builder.toString().equals("BR")) {
                                builder.delete(0, builder.length());
                                dmgd = false;
                                weaf = false;
                                timing = false;
                                bright = false;
                                start = false;
                            }
                        }
                        if (builder.length() == 4) {
                            //||!builder.toString().equals("FE")
                            if (!builder.toString().equals("DMGD") && !builder.toString().equals("WEAF") && !builder.toString().equals("ONOF") && !builder.toString().equals("BRIG")) {
                                builder.delete(0, builder.length());
                                dmgd = false;
                                weaf = false;
                                timing = false;
                                bright = false;
                                start = false;
                            }
                        }
                        if (((dmgd && 'T' == ss) || (';' == ss && (weaf || timing || bright))) && builder.length() > 4) {
                            dmgd = false;
                            weaf = false;
                            timing = false;
                            bright = false;
                            start = false;
                            Log.i("TAG_uart1234", builder.toString());
                            getElements(builder.toString());
                            builder.delete(0, builder.length());
                            Log.i("TAG_uart1234", "END");
                        }


                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        unbindService(conn);
        super.onDestroy();
    }

    public String stringToGbk(String string) throws Exception {
        byte[] bytes = new byte[string.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            byte high = Byte.parseByte(string.substring(i * 2, i * 2 + 1), 16);
            byte low = Byte.parseByte(string.substring(i * 2 + 1, i * 2 + 2), 16);
            bytes[i] = (byte) (high << 4 | low);
        }
        String result = new String(bytes, "gbk");
        return result;
    }

}