package com.example.mqttcharts;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.AAChartCoreLib.AAChartCreator.AAChartModel;
import com.example.AAChartCoreLib.AAChartCreator.AAChartView;
import com.example.AAChartCoreLib.AAChartCreator.AASeriesElement;
import com.example.AAChartCoreLib.AAChartEnum.AAChartType;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public final String TAG = MainActivity.class.getSimpleName();
    private Button btAdd1;
    private Button btAdd2;
    private Button btReset;
    AAChartModel aaChartModel = new AAChartModel();
    AAChartView aaChartView;
    Integer[] sum = new Integer[]{9, 4, 5, 8, 11, 15, 17, 16, 14};
    String token;
    String clientId;//设备ID
    String apiUrl = "http://a3.easemob.com";//"https://a1.easemob.com";
    String appKey = "xxxxxxxxxxxxxxxxxxx#xxxx";
    //mqtt
    String appId = "xxxxx";
    String host = "tcp://xxxxxx.cn1.mqtt.chat:1883";
    String topic = "mqtt-chart";
    String username = "chart"+System.currentTimeMillis();
    String password = "xxxxxx";
    private static MqttAndroidClient  mqttAndroidClient;
    final ExecutorService executorService = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    initMQTT();
                } catch (MqttException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        aaChartView = findViewById(R.id.AAChartView);
        btAdd1 = findViewById(R.id.bt_add1);
        btAdd2 = findViewById(R.id.bt_add2);
        btReset = findViewById(R.id.bt_reset);
        initChart();//初始化
        btAdd1.setOnClickListener(this);
        btAdd2.setOnClickListener(this);
        btReset.setOnClickListener(this);
    }

    private void initMQTT() throws MqttException, InterruptedException {
        clientId = username+"@" + appId ;//System.currentTimeMillis() + "@" + appId;
        mqttAndroidClient = new MqttAndroidClient(this, "tcp://xxxxxx.sandbox.mqtt.chat:1883", clientId);

        Log.d(TAG, "获取token前，token=："+token);
        mqttGetToken();
        Log.d(TAG, "注册token前，token=："+token);
        mqttRegister(username,password);
        Log.d(TAG, "登录token前，token=："+token);
        mqttLogin(username,password);
        Log.d(TAG, "连接token前，token=："+token);
        mqttConnect();
        Log.d(TAG, "连接后，token=："+token);

    }

    private void mqttRegister(String uname,String passwd) {
        Log.d(TAG, "开始注册用户："+uname);
        JSONObject json = new JSONObject();
        json.put("username", uname);
        json.put("password", passwd);
        json.put("password", passwd);
        try{
            String ret = httpPost(apiUrl + "/" + appKey.replace("#", "/") + "/users", json.toJSONString(),"Bearer "+token);
            Log.d(TAG, "注册返回："+ret);
        }catch (Exception e) {
            Log.d(TAG, uname+"注册失败："+e.toString());
        }
    }
    private void mqttGetToken() {
        Log.d(TAG, "开始获取token");
        JSONObject json = new JSONObject();
        json.put("client_id", "Yxxxxxxxxxxxxxxxxx");
        json.put("client_secret", "YXxxxxxxxxxxxxxxxxxxxxxxxxxxxxxe4");
        json.put("grant_type", "client_credentials");
        try {
            String ret = httpPost(apiUrl + "/" + appKey.replace("#", "/") + "/token", json.toJSONString(),"getToken");
            Log.d(TAG, "获取token返回："+ret);
            JSONObject result = JSONObject.parseObject(ret);
            token = result.getString("access_token");
            Log.d(TAG, "拿到token："+token);
        } catch (Exception e) {
            token = "";
            Log.d(TAG, "获取token失败："+e.toString());
        }
    }
    private void mqttLogin(String uname,String passwd) {
        Log.d(TAG, "开始登录："+uname);
        JSONObject json = new JSONObject();
        json.put("username", uname);
        json.put("password", passwd);
        json.put("grant_type", "password");
        try {
            String ret = httpPost(apiUrl + "/" + appKey.replace("#", "/") + "/token", json.toJSONString(),"getToken");
            Log.d(TAG, uname+"登录返回："+ret);
            JSONObject result = JSONObject.parseObject(ret);
            token = result.getString("access_token");
        } catch (Exception e) {
            Log.d(TAG, uname+"登录失败："+e.toString());
        }
    }
    private void mqttConnect() throws MqttException, InterruptedException {
        Log.d(TAG, "开始连接,token="+token);
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setPassword(token.toCharArray());
        mqttConnectOptions.setCleanSession(true);
        mqttConnectOptions.setKeepAliveInterval(90);
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setMqttVersion(4);
        mqttConnectOptions.setConnectionTimeout(5000);

        mqttAndroidClient.connect(mqttConnectOptions);
        Log.d(TAG, "连接 success");
        //暂停1秒钟，等待连接订阅完成
        Thread.sleep(1000);
        try {
            mqttAndroidClient.subscribe(topic, 2);
            Log.d(TAG, "已订阅成功，token="+token);
        } catch (MqttException e) {
            Log.d(TAG, "订阅失败，原因："+e.getMessage());
            e.printStackTrace();
        }
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            /**
             * 连接完成回调方法
             * @param b
             * @param s
             */
            @Override
            public void connectComplete(boolean b, String s) {
                /**
                 * 客户端连接成功后就需要尽快订阅需要的Topic。
                 */
                Log.d(TAG, "connect success");
                executorService.submit(() -> {
                    try {
                        final String[] topicFilter = {topic};
                        final int[] qos = {2};
                        mqttAndroidClient.subscribe(topicFilter, qos);
                        Log.d(TAG, "已订阅,token="+token);
                    } catch (Exception e) {
                        Log.d(TAG, "订阅失败："+e.toString());
                        e.printStackTrace();
                    }
                });
            }
            /**
             * 连接失败回调方法
             * @param throwable
             */
            @Override
            public void connectionLost(Throwable throwable) {
                System.out.println("connection lost");
                throwable.printStackTrace();
            }
            /**
             * 接收消息回调方法
             * @param s
             * @param mqttMessage
             */
            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) {
                Log.d(TAG,"MQTT receive msg from topic " + s + " , body is " + new String(mqttMessage.getPayload()));
                List<Integer> list = JSON.parseArray(new String(mqttMessage.getPayload()), Integer.class);
                sum = list.toArray(new Integer[list.size()]);
                aaChartView.aa_onlyRefreshTheChartDataWithChartOptionsSeriesArray(
                        new AASeriesElement[]{new AASeriesElement().name("编程语言").data(sum)},
                        false);
            }
            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                Log.d(TAG,"MQTT send msg succeed topic is : " + iMqttDeliveryToken.getTopics()[0]);
            }
        });
    }

    private void initChart() {
        aaChartModel.chartType(AAChartType.Bar)
                .title("世界上最好的编程语言？")
                .subtitle("MQTT TEST")
                .backgroundColor("#ffffff")//("#4b2b7f")
                .categories(new String[]{"Java", "Swift", "Python", "Ruby", "JS", "PHP", "Go", "C", "C++"})
                .dataLabelsEnabled(true)
                .tooltipEnabled(false)
                .touchEventEnabled(true)
                .legendEnabled(false)
                .yAxisGridLineWidth(0f)
                .series(new AASeriesElement[]{
                        new AASeriesElement()
                                .data(sum)
                });
        aaChartView.aa_drawChartWithChartModel(aaChartModel);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_add1:
                sum[0] = sum[0].intValue() + 1;
                aaChartView.aa_onlyRefreshTheChartDataWithChartOptionsSeriesArray(
                        new AASeriesElement[]{new AASeriesElement().name("编程语言").data(sum)},
                        true);
                new Thread(new Runnable(){
                    @Override
                    public void run() {
                        MqttMessage message = new MqttMessage(sum.toString().getBytes());
                        //设置传输质量
                        message.setQos(2);
                        try {
                            mqttConnect();
                        } catch (MqttException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        try {
                            mqttAndroidClient.publish(topic, message);
                        } catch (MqttException e) {
                            e.printStackTrace();
                        }
                        }
                }).start();
                Toast.makeText(MainActivity.this, username+"发送了消息", Toast.LENGTH_LONG).show();
                break;
            case R.id.bt_add2:
                sum[4] = sum[4].intValue() + 1;
                aaChartView.aa_onlyRefreshTheChartDataWithChartOptionsSeriesArray(
                        new AASeriesElement[]{new AASeriesElement().name("编程语言").data(sum)},
                        true);
                new Thread(new Runnable(){
                    @Override
                    public void run() {
                        MqttMessage message = new MqttMessage(sum.toString().getBytes());
                        //设置传输质量
                        message.setQos(2);
                        try {
                            mqttConnect();
                        } catch (MqttException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        try {
                            mqttAndroidClient.publish(topic, message);
                        } catch (MqttException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                Toast.makeText(MainActivity.this, username+"发送了消息", Toast.LENGTH_LONG).show();
                break;
            case R.id.bt_reset:
                sum = new Integer[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
                aaChartView.aa_onlyRefreshTheChartDataWithChartOptionsSeriesArray(
                        new AASeriesElement[]{new AASeriesElement().name("编程语言").data(sum)},
                        true);
                new Thread(new Runnable(){
                    @Override
                    public void run() {
                        MqttMessage message = new MqttMessage(sum.toString().getBytes());
                        //设置传输质量
                        message.setQos(2);
                        try {
                            mqttConnect();
                        } catch (MqttException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        try {
                            mqttAndroidClient.publish(topic, message);
                        } catch (MqttException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                Toast.makeText(MainActivity.this, username+"点了重置", Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
    }

    private String httpPost(String url, String params,String auth) {
        String ret = "";
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        Request request = new Request.Builder()
                .addHeader("Authorization", auth)
                .url(url)
                .post(RequestBody.create(mediaType, params))
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        Call call = okHttpClient.newCall(request);
        try {
            Response response = call.execute();
            ret = response.body().string();
            Log.d(TAG,"ret="+ret);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}