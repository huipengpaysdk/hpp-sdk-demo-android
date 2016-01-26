package com.huipengpay.sdk.demo.android;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;

public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void eciticClick(View view) {
        final EditText payMoneyView = (EditText) findViewById(R.id.payMoney);

        String url = "http://192.168.8.254:8080/pay";

        RestTemplate restTemplate = new RestTemplate();

        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

        HashMap<String,Object> map = new HashMap<>();
        map.put("pay_interface","ECITIC_APP");
        map.put("order_describe","abc");
        map.put("order_amount",new BigDecimal(payMoneyView.getText().toString()));

        String result = restTemplate.postForObject(url,map,String.class);

    }


}