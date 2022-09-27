package com.kd.appeasy;

import android.text.TextUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Elements {
    //日期、时间、温度、湿度、风向、风速度、降水、气压
    String date;
    String time;
    String wd;
    String sd;
    String fx;
    String fs;
    String js;
    String qy;
    String njd;
    String wea;
    String bright;
    int index;

    public Elements(String date, String time, String wd, String sd, String fx, String fs, String js, String qy, String njd, String wea, String bright, int index) {
        this.date = date;
        this.time = time;
        this.wd = wd;
        this.sd = sd;
        this.fx = fx;
        this.fs = fs;
        this.js = js;
        this.qy = qy;
        this.njd = njd;
        this.wea = wea;
        this.bright = bright;
        this.index = index;

    }
   public String fsdj = "1级";
    public Elements(String wd, String sd, String fx, String fs, String js, String qy) {
        this.wd = wd + "℃";
        this.sd = sd + "%";
        this.fx = fx;
        this.fs = fs + "m/s";
        this.js = js + "mm";
        this.qy = qy + "hPa";
        float dj = Float.valueOf(fs);
        if(dj>=1.5){
           int d =  (int)Math.ceil(dj/1.5);
          fsdj = d+"级";
        }
    }

    @Override
    public String toString() {
        return time + "\n"
                + "温度:" + wd + "℃\n"
                + "相对湿度:" + sd + "%\n"
                + "风向:" + fx + "\n"
                + "风速:" + fs + "m/s\n"
                + "雨量:" + js + "mm\n"
                + "气压:" + qy + "hPa";
    }

    public String getElementString() {
        return "气温:" + wd + "℃\n"
                + "湿度:" + sd + "%RH\n"

                + "风速:" + fs + "m/s\n"
                + "风向:" + fx + "\n"
                + "雨量:" + js + "mm\n"
                + "气压:" + qy + "hPa\n";
    }
    public int getElementindex() {

        return index ;
    }

    public String getElementRightText() {
        return   "风速:" + fs + "m/s\n"
                +"风向:\n" + fx + "\n"
                + "气压:\n" + qy + "hPa\n";
    }
    public int getbrightness11() {
        int x = 0xccccccff;
        if(!TextUtils.isEmpty(bright)){
        if(Integer.valueOf(bright) == 1){ x = 0xbbbbbbff; }
        if(Integer.valueOf(bright) == 2){ x = 0xddddddff; }
        if(Integer.valueOf(bright) == 3){ x = 0xeeeeeeff; }
        if(Integer.valueOf(bright) == 4){ x = 0xffffffff; }
        }
        return x;
    }
   public String getElementWdAndYl(){
       return "温度:"+ wd + "℃\n"
               + "雨量:" + js + "mm\n";
   }
    public String getElementFsAndFx(){
        return
                "风向:\n" + fx + "\n"
                + "风速:" + fs + "m/s\n"
               ;
    }
    public String getElementLeftText() {
        return "温度:"+ wd + "℃\n"
                +"湿度:" + sd + "%\n"

                + "雨量:" + js + "mm\n";
    }
    public String getElementString3() {
        return "能见度:" + njd + "m\n";
    }
    //hu
    public String getWea() {
        return wea;
    }
    static int cnt=0;
    public String getYear () {
        Log.i("TAG","date:"+date);

       // int week = calendar.get(Calendar.DAY_OF_WEEK);
        String[] date = this.date.split("-");
        String[] time2 = this.time.split(":");


        if(cnt <= 10) {
            cnt++;
            if(cnt == 8){
            int year1 = Integer.valueOf(date[0]);
            int month1 = Integer.valueOf(date[1]) - 1;
            int Day1 = Integer.valueOf(date[2]);
            int hour1 = Integer.valueOf(time2[0]);
            int minute1 = Integer.valueOf(time2[1]);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar c = Calendar.getInstance();
            //System.out.println(sdf.format(c.getTime()));
            c.set(Calendar.YEAR, year1);
            c.set(Calendar.MONTH, month1);//从0开始，0表是1月，1表示2月依bai次类du推
            c.set(Calendar.DAY_OF_MONTH, Day1);
            c.set(Calendar.HOUR_OF_DAY, hour1);
            c.set(Calendar.MINUTE, minute1);
            c.set(Calendar.SECOND, 0);
            Log.i("TAG_","系统时间校正====="+sdf.format(c.getTime()));}
        }


/*        Date  w = new Date(date[0]+"-"+date[1]+"-"+date[2]);*/
        String w = "";
        try{
            SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
/*          String time = date[0]+"-"+date[1]+"-"+date[2];*/
            String time1 = "";
            for(int i=0;i< date.length;i++){
                if(i==date.length-1){
                    time1 +=date[i];
                }else {
                    time1 +=date[i]+"-";
                }
            }
            // time1 = date[0]+"-"+date[1]+"-"+date[2];
             Date date1 = ft.parse(time1);
             w = getWeek(date1);
        }catch(ParseException e){
            e.printStackTrace();
        }


        return date[0] + "年" + date[1] + "月" + date[2] + "日"+"\n"+w +" "+ time;
    }

    //根据日期取得星期几

    public static String getWeek(Date date){

        String[] weeks = {"星期天","星期一","星期二","星期三","星期四","星期五","星期六"};

        Calendar cal = Calendar.getInstance();

        cal.setTime(date);

        int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;

        if(week_index<0){

            week_index = 0;

        }

        return weeks[week_index];

    }
}

