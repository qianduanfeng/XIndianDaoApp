package com.szosen.dao12;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private List<Float> allDatas = new ArrayList<>();
    private MyBG myBG;
    private MyData myData;
    private ArrayList<String> data_source;
    private String dataSource;
    private Button bt_start;
    private Button bt_stop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setTitle("心电曲线测试");
        myBG = (MyBG) findViewById(R.id.mybg);
        myData = (MyData) findViewById(R.id.mydata);
        bt_start = (Button) findViewById(R.id.star);
        bt_stop = (Button) findViewById(R.id.stop);
        bt_start.setOnClickListener(this);
        bt_stop.setOnClickListener(this);
        verifyStoragePermissions(this);

    }


    //分组
//    private String arrayToString(float[] data){
//        StringBuilder stringBuilder = new StringBuilder();
//        if(null == data){
//            return "";
//        }
//        for (int i = 0; i < data.length; i++) {
//            if(i == 0){
//                stringBuilder.append(data[i]);
//            }else{
//                stringBuilder.append(","+data[i]);
//            }
//        }
//        return stringBuilder.toString();
//    }





    private  String array(Float[] data){
        StringBuilder stringBuilder = new StringBuilder();
        if(null == data){
            return "";
        }
        for (int i = 0; i < data.length; i++) {
            if(i == 0){
                stringBuilder.append(data[i]);
            }else{
                stringBuilder.append(","+data[i]);
            }
        }
        return stringBuilder.toString();
    }


    private void initSensorProManager() {
        //点击开启心电图   ecgData.getEcgData() ； 开始获取数据，数据源为float 数组 （每秒钟10组 一组大约50个数据） 获取到的数据都要除以100W 才是绘制图像用的数据
        //通常采用25mm/s纸速记录，
        // 横坐标1小格=1mm=0.04s,1大格=0.2s。
        // 纵坐标电压1小格=1mm=0.1mv,1大格=0.5mv。
        //也就是1s 只需要 25个数据，但是却给了500个数据？  每行50个10行
//        SensorProManager.getInstance().getCustomProvider().registerEcgCallback(new SensorProCallback<EcgData>() {
//            @Override
//            public void onResponse(int i, EcgData ecgData) {
// 141099.45,170595.47,200545.88,232705.94,265687.56,291965.5,317907.3,330805.78,328731.8,314272.12,
// 303881.4,312356.38,339537.75,377983.75,419802.3,457773.56,484304.9,497127.9,478826.28,455803.3,
// 414641.3,373899.84,330950.94,288836.0,253671.9,227360.81,211319.17,205199.83,206899.14,214735.86,
// 225303.88,234471.98,238427.61,234098.67,225716.16,205094.77,180369.81,150787.6,121079.22,96383.45,
// 74977.19,51328.246,24702.602,4990.632,-3966.093,21107.326,80380.8,162634.22,251506.92,332266.97,
//            }
//        });
    }


    private static ScheduledExecutorService scheduledExecutorService;

    private void times() {
        final int index = -1;
        final AtomicInteger atomicInteger = new AtomicInteger(index);
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdownNow();
        }
        scheduledExecutorService = new ScheduledThreadPoolExecutor(10);
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    final int mark = atomicInteger.incrementAndGet();
//                    Log.e("====", mark+">>>"+allDatas.get(mark) + "");
                    if (mark >= allDatas.size()) {
                        scheduledExecutorService.shutdownNow();
                        return;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            myData.addData(allDatas.get(mark));
                            //
                        }
                    });
                } catch (Exception e) {
                }
            }
        }, 200, 8, TimeUnit.MILLISECONDS);
        //延迟时间，间隔时间，单位
    }

    private void changeData(String starCareData) {
        allDatas.clear();
        for (int i = 0; i < 5; i++) {
            allDatas.add(0f);
        }
        String[] lines = starCareData.replace(" ", "").split("\n");
        //100ms一行 一行50个
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < lines.length; i++) {
            int start = lines[i].indexOf(":") + 1;
            String value = lines[i].substring(start);
            sb.append(value).append(",");


        }
        sb.deleteCharAt(sb.lastIndexOf(","));
        String[] valueList = sb.toString().split(",");
        for (int j = 0; j < valueList.length; j+=4) {
            float temp = Float.parseFloat(valueList[j]);
            float data = temp / 1000000.0f / 0.1f * 40; //除以0.1是将数据转换为页面单位，扩大40倍 （根据设备提供的数据来）
            allDatas.add(data);
        }

    }
    //打印
    private String arrayToString(float[] data){
        StringBuilder stringBuilder = new StringBuilder();
        if(null == data){
            return "";
        }
        for (int i = 0; i < data.length; i++) {
            if(i == 0){
                stringBuilder.append(data[i]);
            }else{
                stringBuilder.append(","+data[i]);
            }
        }
        return stringBuilder.toString();
    }
    //写入测试
    private  void  sensorRroTxt(){
        float[] datas={-170521.66f,-128134.7f,-85256.49f,-41477.324f,3350.936f,46120.2f,93468.02f,140593.36f,189663.4f,237658.55f,287983.66f,337676.56f,387587.4f,440720.97f,486184.03f,530120.44f,566726.25f,600341.7f,631487.6f,514677.0f,535036.75f,556425.9f,577358.8f,594854.0f,610932.7f,629005.6f,650332.75f,683701.9f,728421.7f,
                780338.75f,836076.06f,885363.0f,928341.44f,950863.5f,974902.94f};
        String base = Environment.getExternalStorageDirectory() + "/";
        File file = new File(base + "QQQQQ.txt");
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(file));
            bufferedWriter.append(arrayToString(datas) +"\n");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != bufferedWriter) {
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };


    public static void verifyStoragePermissions(Activity activity) {

        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.star:
                changeData(ReadAssetsFileUtils.readAssetsTxt(this, "StarCareData"));
                times();
                sensorRroTxt();
                break;
            case R.id.stop:
                if (scheduledExecutorService != null) {
                    scheduledExecutorService.shutdownNow();
                }
                break;
        }
    }
}
