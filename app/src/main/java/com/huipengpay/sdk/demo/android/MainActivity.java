package com.huipengpay.sdk.demo.android;

import android.app.Activity;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import com.alibaba.fastjson.JSON;
import com.citicbank.cyberpay.assist.main.CyberPay;
import com.citicbank.cyberpay.assist.main.CyberPayListener;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {

    private IWXAPI wxapi;
    EditText payMoneyView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        payMoneyView = (EditText) findViewById(R.id.payMoney);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void eciticClick(View view) {
        new Thread(eciticRun).start();
    }

    Runnable eciticRun = new Runnable() {
        @Override
        public void run() {
            Looper.prepare();
            String postForObject = postForObject("ECITIC_APP");

            Assert.notNull(postForObject);

            goToCyberPay(JSON.parseObject(postForObject,Map.class));
            Looper.loop();
        }
    };

    private String postForObject(String payInterface){

        String url = "http://192.168.8.254:8080/pay";

        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String,String> map = new LinkedMultiValueMap<String,String>();
        map.add("pay_interface",payInterface);
        map.add("order_describe","abc");
        map.add("order_amount",payMoneyView.getText().toString());

        try{

            return restTemplate.postForObject(url, map, String.class);

        }catch (Exception ex){
            Log.e("123456789",ex.getMessage(),ex);
        }

        return null;
    }


    private void goToCyberPay(Map payResponse){
        CyberPay cyberPay = new CyberPay(getApplication());
        cyberPay.registerCallback(cyberPayListener);
        Map extra =  (Map)payResponse.get("extra");
        Map<String,String> payRequest = new HashMap<String,String>();
        payRequest.put("MERID",(String) extra.get("MERID"));
        payRequest.put("ORDERNO",(String) extra.get("ORDERNO"));
        cyberPay.pay(this,JSON.toJSONString(payRequest));
    }


    private CyberPayListener cyberPayListener = new CyberPayListener() {
        public void onPayEnd(String s) {
            Log.d("CyberPayEnd...",s);
        }
    };

    public void winxinClick(View view){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String postForObject = postForObject("WEIXIN_APP");
                Assert.notNull(postForObject);
                goToWeinxinPay(JSON.parseObject(postForObject,Map.class));
            }
        }).start();
    }

    private void goToWeinxinPay(Map payResponse){
        Log.d("goToWeinxinPay",JSON.toJSONString(payResponse));
        Map extra =  (Map)payResponse.get("extra");
        wxapi = WXAPIFactory.createWXAPI(MainActivity.this,null);
        wxapi.registerApp((String)extra.get("appid"));
        PayReq request = new PayReq();
        request.appId = (String)extra.get("appid");
        request.partnerId = (String) extra.get("partnerid");
        request.prepayId = (String) extra.get("prepayid") ;
        request.packageValue = (String) extra.get("package") ;
        request.nonceStr = (String) extra.get("noncestr");
        request.timeStamp = (String) extra.get("timestamp");
        request.sign = (String) extra.get("sign");
        wxapi.sendReq(request);

    }

    /**
     * 微信回调
     * @param resp
     */
    public void onResp(BaseResp resp){
        if(resp.getType()== ConstantsAPI.COMMAND_PAY_BY_WX){
            Log.d("weixin call back","1234");
        }
    }


}