package com.songpo.searched.service;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.songpo.searched.cache.SmsPasswordCache;
import com.songpo.searched.cache.SmsVerifyCodeCache;
import com.songpo.searched.domain.BusinessMessage;
import com.songpo.searched.entity.SlPhoneZone;
import com.songpo.searched.mapper.SlPhoneZoneMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.RandomStringGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static org.apache.commons.text.CharacterPredicates.DIGITS;

/**
 * 短信服务类
 */
@Service
public class SmsService {

    public static final Logger log = LoggerFactory.getLogger(SmsService.class);

    @Autowired
    private SmsVerifyCodeCache smsVerifyCodeCache;

    @Autowired
    private SmsPasswordCache smsPasswordCache;

    @Autowired
    private QcloudSmsService qcloudSmsService;
    @Autowired
    private SlPhoneZoneMapper slPhoneZoneMapper;

    /**
     * 发送短信验证码
     *
     * @param mobile 手机号码
     * @param zone   地区
     * @return 发送结果
     */
    public BusinessMessage<SendSmsResponse> sendSms(String mobile, String zone) {
        log.debug("发送短信验证码，手机号码：{}, 地区：{}", mobile, zone);
        BusinessMessage<SendSmsResponse> message = new BusinessMessage<>();

        // 从缓存获取短信验证码
        String code = this.smsVerifyCodeCache.get(mobile);
        if (!StringUtils.isBlank(code)) {
            message.setMsg("验证码尚未过期，请勿重复发送");
        } else {
            try {
                //设置超时时间-可自行调整
                System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
                System.setProperty("sun.net.client.defaultReadTimeout", "10000");
                //初始化ascClient需要的几个参数
                final String product = "Dysmsapi";//短信API产品名称（短信产品名固定，无需修改）
                final String domain = "dysmsapi.aliyuncs.com";//短信API产品域名（接口地址固定，无需修改）
                //替换成你的AK
                final String accessKeyId = "";//你的accessKeyId,参考本文档步骤2
                final String accessKeySecret = "";//你的accessKeySecret，参考本文档步骤2
                //初始化ascClient,暂时不支持多region（请勿修改）
                IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
                DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
                IAcsClient acsClient = new DefaultAcsClient(profile);
                //组装请求对象
                SendSmsRequest request = new SendSmsRequest();
                //使用post提交
                request.setMethod(MethodType.POST);
                //必填:待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式
                request.setPhoneNumbers(mobile);
                //必填:短信签名-可在短信控制台中找到
                request.setSignName("");
                //必填:短信模板-可在短信控制台中找到
                if(null==zone||"中国大陆".equals(zone)){
                    request.setTemplateCode("");
                }else {
                    request.setTemplateCode("");
                }

                RandomStringGenerator generator = new RandomStringGenerator.Builder().withinRange('0', '9').filteredBy(DIGITS).build();
                code = generator.generate(6);

                //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
                //友情提示:如果JSON中需要带换行符,请参照标准的JSON协议对换行符的要求,比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败
                request.setTemplateParam("{\"code\":\"" + code + "\"}");
                //可选-上行短信扩展码(扩展码字段控制在7位或以下，无特殊需求用户请忽略此字段)
                //request.setSmsUpExtendCode("90997");
                //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
//            request.setOutId("yourOutId");
                //请求失败这里会抛ClientException异常
                SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
                if (sendSmsResponse.getCode() != null && "OK".equals(sendSmsResponse.getCode())) {
                    //请求成功
                    log.debug("短信验证码发送成功，验证码：{}", code);
                    // 将验证码加入缓存
                    this.smsVerifyCodeCache.put(mobile, code, 5L, TimeUnit.MINUTES);
                    message.setData(sendSmsResponse);
                    message.setSuccess(true);
                } else {
                    log.debug("阿里云短信登录密码发送失败 {}",sendSmsResponse.getMessage());
                    //启用腾讯云短信备用通道
                    SlPhoneZone slPhoneZone =slPhoneZoneMapper.selectOne( new SlPhoneZone(){{
                        setZone(zone);
                    }});
                    String phone [] = new String []{mobile};
                    message = qcloudSmsService.sendQcTemple(phone,slPhoneZone.getMobilearea().toString(),code);
                    if ("0".equals(message.getCode())) {
                        //请求成功
                        log.debug("腾讯云短信登录密码发送成功，登录密码：{}", code);
                        // 将登录密码加入缓存
                        this.smsVerifyCodeCache.put(mobile, code, 5L, TimeUnit.MINUTES);
                    }
                }
            } catch (Exception e) {
                log.error("发送阿里云短信验证码失败，{}", e);
                //启用腾讯云短信备用通道
                SlPhoneZone slPhoneZone =slPhoneZoneMapper.selectOne( new SlPhoneZone(){{
                    setZone(zone);
                }});
                String phone [] = new String []{mobile};
                message = qcloudSmsService.sendQcTemple(phone,slPhoneZone.getMobilearea().toString(),code);
                if ("0".equals(message.getCode())) {
                    //请求成功
                    log.debug("腾讯云短信登录密码发送成功，登录密码：{}", code);
                    // 将登录密码加入缓存
                    this.smsVerifyCodeCache.put(mobile, code, 5L, TimeUnit.MINUTES);
                }else {
                    log.debug("腾讯云短信登录密码发送失败：{}", e);
                }
            }
        }
        return message;
    }

    /**
     * 发送短信登录密码
     *
     * @param mobile 手机号码
     * @param zone 地区
     * @return 发送结果
     */
    public BusinessMessage<SendSmsResponse> sendPassword(String mobile, String zone) {
        log.debug("发送短信登录密码，手机号码：{}", mobile);
        BusinessMessage<SendSmsResponse> message = new BusinessMessage<>();

        // 从缓存获取短信登录密码
        String code = this.smsPasswordCache.get(mobile);
        if (!StringUtils.isBlank(code)) {
            message.setMsg("登录密码尚未过期，请勿重复发送");
        } else {
            try {
                //设置超时时间-可自行调整
                System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
                System.setProperty("sun.net.client.defaultReadTimeout", "10000");
                //初始化ascClient需要的几个参数
                final String product = "Dysmsapi";//短信API产品名称（短信产品名固定，无需修改）
                final String domain = "dysmsapi.aliyuncs.com";//短信API产品域名（接口地址固定，无需修改）
                //替换成你的AK
                final String accessKeyId = "";//你的accessKeyId,参考本文档步骤2
                final String accessKeySecret = "";//你的accessKeySecret，参考本文档步骤2
                //初始化ascClient,暂时不支持多region（请勿修改）
                IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
                DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
                IAcsClient acsClient = new DefaultAcsClient(profile);
                //组装请求对象
                SendSmsRequest request = new SendSmsRequest();
                //使用post提交
                request.setMethod(MethodType.POST);
                //必填:待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,登录密码类型的短信推荐使用单条调用的方式
                request.setPhoneNumbers(mobile);
                //必填:短信签名-可在短信控制台中找到
                request.setSignName("搜了平台");
                //必填:短信模板-可在短信控制台中找到
                if(null==zone||"中国大陆".equals(zone)){
                    request.setTemplateCode("");
                }else {
                    request.setTemplateCode("");
                }

                RandomStringGenerator generator = new RandomStringGenerator.Builder().withinRange('0', '9').filteredBy(DIGITS).build();
                System.out.println("generator=="+generator);
                code = generator.generate(6);

                //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的登录密码为${code}"时,此处的值为
                //友情提示:如果JSON中需要带换行符,请参照标准的JSON协议对换行符的要求,比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败
                request.setTemplateParam("{\"code\":\"" + code + "\"}");
                //可选-上行短信扩展码(扩展码字段控制在7位或以下，无特殊需求用户请忽略此字段)
                //request.setSmsUpExtendCode("90997");
                //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
//            request.setOutId("yourOutId");
                //请求失败这里会抛ClientException异常
                SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
                if (sendSmsResponse.getCode() != null && "OK".equals(sendSmsResponse.getCode())) {
                    //请求成功
                    log.debug("阿里云短信登录密码发送成功，登录密码：{}", code);

                    // 将登录密码加入缓存
                    this.smsPasswordCache.put(mobile, code, 5L, TimeUnit.MINUTES);

                    message.setData(sendSmsResponse);
                    message.setSuccess(true);
                }else {
                    log.debug("阿里云短信登录密码发送失败 {}",sendSmsResponse.getMessage());
                   //启用腾讯云短信备用通道
                    SlPhoneZone slPhoneZone =slPhoneZoneMapper.selectOne( new SlPhoneZone(){{
                        setZone(zone);
                    }});
                    String phone [] = new String []{mobile};
                        message = qcloudSmsService.sendQcTemple(phone,slPhoneZone.getMobilearea().toString(),code);
                        if ("0".equals(message.getCode())) {
                            //请求成功
                            log.debug("腾讯云短信登录密码发送成功，登录密码：{}", code);
                            // 将登录密码加入缓存
                            this.smsPasswordCache.put(mobile, code, 5L, TimeUnit.MINUTES);
                        }
                }
            } catch (Exception e) {
                log.error("发送阿里云短信登录密码失败，{}", e);
                //启用腾讯云短信备用通道
                SlPhoneZone slPhoneZone =slPhoneZoneMapper.selectOne( new SlPhoneZone(){{
                    setZone(zone);
                }});
                String phone [] = new String []{mobile};
                message = qcloudSmsService.sendQcTemple(phone,slPhoneZone.getMobilearea().toString(),code);
                if ("0".equals(message.getCode())) {
                    //请求成功
                    log.debug("腾讯云短信登录密码发送成功，登录密码：{}", code);
                    // 将登录密码加入缓存
                    this.smsPasswordCache.put(mobile, code, 5L, TimeUnit.MINUTES);
                }else{
                    log.debug("腾讯云短信登录密码发送失败：{}",e );
                }
            }
        }
        return message;
    }

    /**
     * 发送钱包app注册通知
     *
     * @param phone 手机号码
     * @param zone 地区
     * @param password 密码
     * @return 发送结果
     */
    public BusinessMessage<SendSmsResponse> sendMess(String phone, String zone, String password) {
        log.debug("发送钱包app注册通知，手机号码：{}", phone);
        BusinessMessage<SendSmsResponse> message = new BusinessMessage<>();
        try {
            //设置超时时间-可自行调整
            System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
            System.setProperty("sun.net.client.defaultReadTimeout", "10000");
            //初始化ascClient需要的几个参数
            final String product = "Dysmsapi";//短信API产品名称（短信产品名固定，无需修改）
            final String domain = "dysmsapi.aliyuncs.com";//短信API产品域名（接口地址固定，无需修改）
            //替换成你的AK
            final String accessKeyId = "";//你的accessKeyId,参考本文档步骤2
            final String accessKeySecret = "";//你的accessKeySecret，参考本文档步骤2
            //初始化ascClient,暂时不支持多region（请勿修改）
            IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
            IAcsClient acsClient = new DefaultAcsClient(profile);
            //组装请求对象
            SendSmsRequest request = new SendSmsRequest();
            //使用post提交
            request.setMethod(MethodType.POST);

            //必填:短信签名-可在短信控制台中找到
            request.setSignName("");
            //必填:短信模板-可在短信控制台中找到
            if(null==zone||"中国大陆".equals(zone)){
                request.setTemplateCode("");
            }else {
                SlPhoneZone slPhoneZone = slPhoneZoneMapper.selectOne(new SlPhoneZone(){{
                    setZone(zone);
                }});
                phone = "00"+slPhoneZone.getMobilearea()+phone;
                request.setTemplateCode("");
            }
            //必填:待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,登录密码类型的短信推荐使用单条调用的方式
            request.setPhoneNumbers(phone);
            request.setTemplateParam("{\"phone\":\"" + phone + "\",\"password\":\"" + password + "\"}");
            SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
            if (sendSmsResponse.getCode() != null && "OK".equals(sendSmsResponse.getCode())) {
                //请求成功
                log.debug("钱包app注册通知发送成功", sendSmsResponse);
                message.setData(sendSmsResponse);
                message.setSuccess(true);
            }else {
                log.debug("钱包app注册阿里云通知发送失败 {}",sendSmsResponse.getMessage());
                //启用腾讯云短信备用通道
                SlPhoneZone slPhoneZone =slPhoneZoneMapper.selectOne( new SlPhoneZone(){{
                    setZone(zone);
                }});
                String mobile[] = new String[]{phone};
                message =  qcloudSmsService.sendSlbMsgId(mobile,slPhoneZone.getMobilearea().toString(),phone, password);
                if ("0".equals(message.getCode())) {
                    //请求成功
                    log.debug("腾讯钱包app注册通知发送成功，用户：{}",phone);
                }else{
                    log.debug("腾讯钱包app注册通知发送失败 用户：{}",phone);
                }
            }
        } catch (Exception e) {
            log.error("发送钱包app注册通知失败，{}", e);
            //启用腾讯云短信备用通道
            SlPhoneZone slPhoneZone =slPhoneZoneMapper.selectOne( new SlPhoneZone(){{
                setZone(zone);
            }});
            String mobile[] = new String[]{phone};
            message =  qcloudSmsService.sendSlbMsgId(mobile,slPhoneZone.getMobilearea().toString(),phone, password);
            if ("0".equals(message.getCode())) {
                //请求成功
                log.debug("腾讯钱包app注册通知发送成功，用户：{}",phone);
            }else{
                log.debug("腾讯钱包app注册通知发送失败 用户：{}",phone);
            }
        }
        return message;
    }

}
