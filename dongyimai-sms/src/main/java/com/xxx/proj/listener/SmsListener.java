package com.xxx.proj.listener;

import com.cloopen.rest.sdk.BodyType;
import com.cloopen.rest.sdk.CCPRestSmsSDK;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class SmsListener {
    @Value("${sms.accountSId}")
    String accountSId;
    @Value("${sms.accountToken}")
    String accountToken;
    @Value("${sms.appId}")
    String appId;

    @JmsListener(destination = "dongyimai-sms")
    public void receive(Map<String, String> map) {
        String serverIp = "app.cloopen.com";
        //请求端口
        String serverPort = "8883";

        CCPRestSmsSDK sdk = new CCPRestSmsSDK();
        sdk.init(serverIp, serverPort);
        sdk.setAccount(accountSId, accountToken);
        sdk.setAppId(appId);
        sdk.setBodyType(BodyType.Type_JSON);
        String to = map.get("mobile");
        String templateId = "1027023";
        String[] datas = {map.get("code")};
        String subAppend = "1234";  //可选 扩展码，四位数字 0~9999
        String reqId = System.currentTimeMillis() + "";  //可选 第三方自定义消息id，最大支持32位英文数字，同账号下同一自然天内不允许重复
        //HashMap<String, Object> result = sdk.sendTemplateSMS(to,templateId,datas);
        HashMap<String, Object> result = sdk.sendTemplateSMS(to, templateId, datas, subAppend, reqId);
        if ("000000".equals(result.get("statusCode"))) {
            //正常返回输出data包体信息（map）
            HashMap<String, Object> data = (HashMap<String, Object>) result.get("data");
            Set<String> keySet = data.keySet();
            for (String key : keySet) {
                Object object = data.get(key);
                System.out.println(key + " = " + object);
            }
        } else {
            //异常返回输出错误码和错误信息
            System.out.println("错误码=" + result.get("statusCode") + " 错误信息= " + result.get("statusMsg"));
        }
    }
}
