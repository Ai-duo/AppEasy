package com.kd.appeasy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    Typeface fontFace;
    TextView time, wd, sd, js, fs, fx, qy;
    MarqueeView wea;
    String info = "金华市气象台2022年09月09日16时发布的天气预报：今天傍晚到夜里多云；明天多云；后天多云到阴。偏东风2～3级；明天早晨最低温度21～23℃，与今天相比偏低1～2℃，明天白天最高温度32～34℃，与今天相比基本持平，后天早晨最低温度20～22℃，后天白天最高温度31～33℃。";
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, ElementsService.class);
        startService(intent);
        fontFace = Typeface.createFromAsset(getAssets(), "fonts/simsun.ttc");//HYQiHei-25JF.ttf//simfang.ttf//PixelMplus10-Regular
        initView();
        initTimer();
        EventBus.getDefault().post(info);

    }

    public void initView() {
        time = findViewById(R.id.time);
        wd = findViewById(R.id.wd);
        sd = findViewById(R.id.sd);
        js = findViewById(R.id.js);
        fx = findViewById(R.id.fx);
        fs = findViewById(R.id.fs);
        qy = findViewById(R.id.qy);
        wea = findViewById(R.id.weatherinfo);
        wea.getPaint().setTypeface(fontFace);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getElement(Elements elements) {
        wd.setText(elements.wd);
        sd.setText(elements.sd);
        js.setText(elements.js);
        fs.setText(elements.fs);
        fx.setText(elements.fx);
        qy.setText(elements.qy);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getWea(String weainfo) {
        wea.setContent(weainfo);
        wea.continueRoll();
    }
    SimpleDateFormat sdf =new SimpleDateFormat("yyyy年MM月dd日   E   HH:mm", Locale.CHINA);
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getTime(Date date) {
        time.setText(sdf.format(date));
    }
    Timer timer;
    public void initTimer(){
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                EventBus.getDefault().post(new Date());
            }
        },0,60*1000);
    }

}