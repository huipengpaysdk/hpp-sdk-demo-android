package com.huipengpay.sdk.demo.android;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import com.alibaba.fastjson.JSON;
import com.citicbank.cyberpay.assist.main.CyberPay;
import com.citicbank.cyberpay.assist.main.CyberPayListener;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {

    private CyberPay mCyberPay;
    private EditText payMoneyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mCyberPay = new CyberPay(getApplication());
        payMoneyView = (EditText) findViewById(R.id.payMoney);
    }

    public void eciticClick(View view) {
        String url = "http://192.168.8.254:8080/pay";

        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("pay_interface", "ECITIC_APP");
        map.add("order_describe", "abc");
        map.add("order_amount", payMoneyView.getText().toString());

        String postForObject = restTemplate.postForObject(url, map, String.class);

        goToCyberPay(JSON.parseObject(postForObject, Map.class));
    }


    private void goToCyberPay(Map payResponse) {
        CyberPayListener cyberPayListener = new CyberPayListener() {
            public void onPayEnd(String s) {
                Message msg = new Message();
                Bundle data = new Bundle();
                data.putString("value", s);
                msg.setData(data);
            }
        };
        mCyberPay.registerCallback(cyberPayListener);
        Map extra = (Map) payResponse.get("extra");
        Map<String, String> payRequest = new HashMap<>();
        payRequest.put("MERID", (String) extra.get("MERID"));
        payRequest.put("ORDERNO", (String) extra.get("ORDERNO"));

        mCyberPay.pay(this, JSON.toJSONString(payRequest));
    }


    Handler handler = new Handler() {

    };

}