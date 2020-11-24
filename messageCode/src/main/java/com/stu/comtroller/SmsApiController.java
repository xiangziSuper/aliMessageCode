package com.stu.comtroller;

import com.stu.service.SendSms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Author lwx
 * @Description
 */
@RestController
@CrossOrigin
public class SmsApiController {

    @Autowired
    SendSms sendSms;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @GetMapping("send/{phone}")
    public String code(@PathVariable("phone") String phone){
        String code = stringRedisTemplate.opsForValue().get(phone);
        if (!StringUtils.isEmpty(code)) {
            return  phone+":"+code+"已存在，没有过期";
        }
        code = UUID.randomUUID().toString().substring(0, 4);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("code", code);
        boolean send = sendSms.send(phone, hashMap);
        if (send) {
            stringRedisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);
            return  phone+":"+code+"发送成功";
        }else {
            return  phone+":"+code+"发送失败";
        }
    }
}
