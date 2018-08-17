package com.songpo.searched.service;

;
import com.alibaba.fastjson.JSONArray;
import com.songpo.searched.constant.BaseConstant;
import com.songpo.searched.domain.BusinessMessage;
import com.alibaba.fastjson.JSONObject;
import com.songpo.searched.entity.SlPhoneZone;
import com.songpo.searched.entity.SlSystemConnector;
import com.songpo.searched.entity.SlUser;
import com.songpo.searched.mapper.SlPhoneZoneMapper;
import com.songpo.searched.mapper.SlSystemConnectorMapper;
import com.songpo.searched.mapper.SlUserMapper;
import com.songpo.searched.util.AESUtils;


import com.songpo.searched.util.HttpUtil;
import com.songpo.searched.util.MD5SignUtils;
import com.songpo.searched.util.RSAUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;


/**
 * 第三方钱包接口 （目前主要针对搜了贝）
 * @author  heming
 * @date  2018年7月16日14:32:17
 */
@Service
public class ThirdPartyWalletService {

    public static final Logger log = LoggerFactory.getLogger(ThirdPartyWalletService.class);

    @Autowired
    public Environment env;
    @Autowired
    private  LoginUserService loginUserService;
    @Autowired
    private SlPhoneZoneMapper slPhoneZoneMapper;
    @Autowired
    private SlUserMapper slUserMapper;
    @Autowired
    private SlSystemConnectorMapper slSystemConnectorMapper;

    /**
     * 获取 加密随机串
     * @return
     */
    public String getNoteStr(){
        String noteStr =  String.valueOf(System.currentTimeMillis());
        noteStr =StringUtils.leftPad(noteStr,16);
        return noteStr;
    }

    /**
     * 公钥加密随机串
     * @return
     */
    public String getEncodedNoteStr(String noteStr){
        String encodedNoteStr = RSAUtils.encryptByPublicKey(noteStr, env.getProperty("wallet.publicKey"));
        return encodedNoteStr;
    }
    //公钥加密随机串


    /**
     * 用户注册
     * @param mobile
     * @param pwd
     * @param moblieArea
     * @return 0:成功  小于0：操作不成功
     */
    public String UserRegister (String mobile, String pwd, String moblieArea){
        //截取指定的电话号码
        mobile = subPhone(mobile);

        String returnStr = "";
        //公钥
        String publicKey = env.getProperty("wallet.publicKey");
        //生成加密随机串
        String noteStr =  getNoteStr();
        //加密登录密码
        String loginPwd = pwd;
        loginPwd = AESUtils.encode(loginPwd, noteStr);

        //公钥加密随机串
        String encodedNoteStr = RSAUtils.encryptByPublicKey(noteStr, publicKey);

        //生成签名
        SortedMap<String, String> packageParams = new TreeMap<String, String>();
        packageParams.put("mobile", mobile);
        packageParams.put("loginPwd", loginPwd);
        packageParams.put("mobileArea", moblieArea);
        packageParams.put("noteStr", encodedNoteStr);
        String sign = MD5SignUtils.createMD5Sign(packageParams, MD5SignUtils.CHARSET_NAME_DEFAULT);

        String url = env.getProperty("wallet.url") + BaseConstant.WALLET_API_USERREGISTER;
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("mobile", mobile);
        params.put("loginPwd", loginPwd);
        params.put("mobileArea", moblieArea);
        params.put("noteStr", encodedNoteStr);
        params.put("sign", sign);
        try {
            String result = HttpUtil.doPost(url, params);
            //返回值处理
            JSONObject jsonObject = JSONObject.parseObject(result);
            String  codeMap =  jsonObject.get("resultCode").toString();
            String  messageMap =  jsonObject.get("message").toString();
            log.debug(messageMap);
            returnStr = codeMap;
        } catch (Exception e ) {
            log.error("钱包APP注册出错",e);
        }
        return returnStr;
    }


    /**
     * SLB资产装入
     * @param walletAddress 钱包地址
     * @param lockBeginDate 锁仓开始时间
     * @param lockEndDate 锁仓结束时间
     * @param releaseNum 释放批次
     * @param releasePercent 每次释放比例(单位%)
     * @param transfAmount 兑换数量
     * @param orderSn 订单ID,服务端验证订单是否已经同步过
     * @param batchType A ：A轮B ：B轮 C ：C轮 D ：D轮E ：E轮
     * @return  0:成功  小于0：操作不成功
     */
    public String transferToSlbSc (String walletAddress,String lockBeginDate,String lockEndDate,String releaseNum,
                                   String releasePercent,String transfAmount,String orderSn,String batchType){
        log.debug("钱包 {} 开始转入{}slb ",walletAddress,transfAmount);
        String returnStr = "";
       String  noteStr =  getNoteStr();
        //公钥
        String publicKey = env.getProperty("wallet.publicKey");
        String encodedNoteStr = RSAUtils.encryptByPublicKey(noteStr, publicKey);
        String  platTransPwd = env.getProperty("wallet.platTransPwd");
        String endcodePaltTransPwd = AESUtils.encode(platTransPwd, noteStr);
        //生成签名
        SortedMap<String, String> packageParams = new TreeMap<String, String>();
        packageParams.put("walletAddress", walletAddress);
        packageParams.put("platTransPwd", endcodePaltTransPwd);
        packageParams.put("lockBeginDate", lockBeginDate);
        packageParams.put("lockEndDate", lockEndDate);
        packageParams.put("releaseNum", releaseNum);
        packageParams.put("releasePercent", releasePercent);
        packageParams.put("transfAmount", transfAmount);
        packageParams.put("noteStr", encodedNoteStr);
        packageParams.put("orderSn", orderSn);
        packageParams.put("batchType", batchType);
        packageParams.put("noteStr", encodedNoteStr);
        String sign = MD5SignUtils.createMD5Sign(packageParams, MD5SignUtils.CHARSET_NAME_DEFAULT);
        String url =  env.getProperty("wallet.url")+BaseConstant.WALLET_API_TRANSFERTOSLBSC;
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("walletAddress", walletAddress);
        params.put("platTransPwd", endcodePaltTransPwd);
        params.put("lockBeginDate", lockBeginDate);
        params.put("lockEndDate", lockEndDate);
        params.put("releaseNum", releaseNum);
        params.put("releasePercent", releasePercent);
        params.put("transfAmount", transfAmount);
        params.put("noteStr", encodedNoteStr);
        params.put("orderSn", orderSn);
        params.put("batchType", batchType);
        params.put("noteStr", encodedNoteStr);
        params.put("sign", sign);
        String result = HttpUtil.doPost(url, params);
        //返回值处理
        JSONObject jsonObject = JSONObject.parseObject(result);
        String codeMap = jsonObject.get("resultCode").toString();
        String messageMap = jsonObject.get("message").toString();
        log.debug(messageMap);
        returnStr = codeMap;
        return returnStr;
    }
    /**
     * 查询用户是否注册
     * @param mobile 手机号
     */
    public  Boolean checkUserRegister(String mobile, String mobileArea){

        //截取指定的电话号码
        mobile = subPhone(mobile);

        Boolean bool = false;
        //获取加密随机串
        String noteStr =  getNoteStr();
        //生成签名
        SortedMap<String, String> packageParams = new TreeMap<String, String>();
        packageParams.put("mobile", mobile);
        packageParams.put("mobileArea", mobileArea);
        packageParams.put("noteStr", noteStr);
        String sign = MD5SignUtils.createMD5Sign(packageParams, MD5SignUtils.CHARSET_NAME_DEFAULT);
        //第三方钱包接口地址
        String url = env.getProperty("wallet.url")+BaseConstant.WALLET_API_CHECKUSERREGISTER;
        //
        // 接口所需参数
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("mobile", mobile);
        params.put("mobileArea", mobileArea);
        params.put("noteStr", noteStr);
        params.put("sign", sign);
        //获取返回值
        String result = HttpUtil.doPost(url, params);
        //解析返回值 转换成json格式
        JSONObject jsonObject = JSONObject.parseObject(result);
        Integer code =  jsonObject.getInteger("resultCode");
        //操作成功
        if (code >= 0){
            Map map = (Map<String,String>)jsonObject.get("data");
            //用户已注册
            if (Integer.valueOf(map.get("exist").toString()) == 1){
                bool = true;
            }
        }
        return bool;
    }
    /**
     * 获取钱包地址列表
     * @param mobile
     * @return walletAddress
     */
    public String getWalletList(String mobile, String mobileArea){

        //截取指定的电话号码
        mobile = subPhone(mobile);

        String walletAddress = "";
        //公钥
        String publicKey =env.getProperty("wallet.publicKey") ;
        //获取加密随机串
        String noteStr =  getNoteStr();
        //加密平台密码
        String platTransPwd = env.getProperty("wallet.platTransPwd");
        platTransPwd = AESUtils.encode(platTransPwd, noteStr);

        //公钥加密随机串
        String encodedNoteStr = RSAUtils.encryptByPublicKey(noteStr, publicKey);

        //生成签名
        SortedMap<String, String> packageParams = new TreeMap<String, String>();
        packageParams.put("mobile", mobile);
        packageParams.put("mobileArea", mobileArea);
        packageParams.put("platTransPwd", platTransPwd);
        packageParams.put("noteStr", encodedNoteStr);
        String sign = MD5SignUtils.createMD5Sign(packageParams, MD5SignUtils.CHARSET_NAME_DEFAULT);

        String url = env.getProperty("wallet.url") + BaseConstant.WALLET_API_GETWALLETLIST;

        Map<String,Object> params = new HashMap<String,Object>();
        params.put("mobile", mobile);
        params.put("mobileArea", mobileArea);
        params.put("platTransPwd", platTransPwd);
        params.put("noteStr", encodedNoteStr);
        params.put("sign", sign);
        String result = HttpUtil.doPost(url, params);
        //解析返回值 转换成json格式
        JSONObject jsonObject = JSONObject.parseObject(result);
        Integer code =  jsonObject.getInteger("resultCode");
        //操作成功
        if (code >= 0){
                Map map = (Map<String,String>)jsonObject.get("data");
                walletAddress = map.get("walletAddress").toString();
        }
        return  walletAddress;
    }
    /**
     *查询钱包总SLB锁仓余额
     * @return
     */
    @Transactional
    public BusinessMessage getSlbScAmount(String phone){
//        SlUser user = loginUserService.getCurrentLoginUser();
        SlUser user = slUserMapper.selectOne(new SlUser(){{
            setPhone(phone);
        }});
        BusinessMessage message = new BusinessMessage();
        try {
            if (null != user){
                String mobile = user.getPhone();
                SlPhoneZone slPhoneZone = slPhoneZoneMapper.selectOne(new SlPhoneZone(){{
                    setZone(user.getZone());
                }});
                if (checkUserRegister(mobile, slPhoneZone.getMobilearea().toString())){
                    message = retunObject(mobile, slPhoneZone.getMobilearea().toString());
                }else {
                    // 注册 String mobile, String pwd, String moblieArea

                   String res = UserRegister(mobile, BaseConstant.WALLET_DEFAULT_LOGIN_PASSWORD, slPhoneZone.getMobilearea().toString());
                    message = retunObject(mobile, slPhoneZone.getMobilearea().toString());
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return  message;
    }
    public BusinessMessage retunObject(String mobile, String mobileArea){

        //截取指定的电话号码
        mobile = subPhone(mobile);

        BusinessMessage businessMessage = new BusinessMessage();
        //公钥
        String publicKey = env.getProperty("wallet.publicKey");
        //生成加密随机串
        String noteStr =  getNoteStr();

        String platTransPwd = env.getProperty("wallet.platTransPwd");
        String endcodePaltTransPwd = AESUtils.encode(platTransPwd, noteStr);

        //公钥加密随机串
        String encodedNoteStr = RSAUtils.encryptByPublicKey(noteStr, publicKey);

        //生成签名
        SortedMap<String, String> packageParams = new TreeMap<String, String>();
        packageParams.put("mobile", mobile);
        packageParams.put("mobileArea", mobileArea);
        packageParams.put("noteStr", encodedNoteStr);
        packageParams.put("platTransPwd", endcodePaltTransPwd);

        String sign = MD5SignUtils.createMD5Sign(packageParams, MD5SignUtils.CHARSET_NAME_DEFAULT);

        String url = env.getProperty("wallet.url") + BaseConstant.WALLET_API_GETSLBSCAMOUNT;

        Map<String,Object> params = new HashMap<String,Object>();
        params.put("mobile", mobile);
        params.put("mobileArea", mobileArea);
        params.put("platTransPwd", endcodePaltTransPwd);
        params.put("noteStr", encodedNoteStr);
        params.put("sign", sign);
        String result = HttpUtil.doPost(url, params);
        //解析返回值 转换成json格式
       JSONObject jsonObject = JSONObject.parseObject(result);
       if (jsonObject.getInteger("resultCode") == 0){
            businessMessage.setSuccess(true);
            businessMessage.setCode(jsonObject.getString("resultCode"));
            businessMessage.setData(jsonObject.get("data"));
            businessMessage.setMsg(jsonObject.getString("message"));
       }else{
           businessMessage.setSuccess(false);
       }
       return  businessMessage;
    }

    //截取指定长度的手机号
    public String subPhone(String mobile){
        if (mobile.length() == 13){
            mobile = mobile.substring(mobile.length()-8, mobile.length());
        }
        return mobile;
    }
    /**
     * 使用SLB币支付
     * @param walletAddress 钱包地址
     * @param walletPwd  登录密码
     * @param payAmount 支付金额
     * @param orderSn   订单号
     */
    public Integer paySlbAmount(String walletAddress, String walletPwd, BigDecimal payAmount, String orderSn){
        //公钥
        String publicKey = env.getProperty("wallet.publicKey");
        //生成加密随机串
        String noteStr =  String.valueOf(System.currentTimeMillis());
        noteStr = StringUtils.leftPad(noteStr, 16,  "0");

        walletPwd = AESUtils.encode(walletPwd, noteStr);

        //公钥加密随机串
        String encodedNoteStr = RSAUtils.encryptByPublicKey(noteStr, publicKey);

        //生成签名
        SortedMap<String, String> packageParams = new TreeMap<String, String>();
        packageParams.put("walletAddress", walletAddress);
        packageParams.put("walletPwd", walletPwd);
        packageParams.put("payAmount", payAmount.toString());
        packageParams.put("noteStr", encodedNoteStr);
        packageParams.put("orderSn", orderSn);

        String sign = MD5SignUtils.createMD5Sign(packageParams, MD5SignUtils.CHARSET_NAME_DEFAULT);

        String url = env.getProperty("wallet.url") + BaseConstant.WALLET_API_PAYSLBAMOUNT;

        Map<String,Object> params = new HashMap<String,Object>();
        params.put("walletAddress", walletAddress);
        params.put("walletPwd", walletPwd);
        params.put("payAmount", payAmount.toString());
        params.put("noteStr", encodedNoteStr);
        params.put("orderSn", orderSn);

        params.put("sign", sign);
        String result = HttpUtil.doPost(url, params);
        //解析返回值 转换成json格式
        JSONObject jsonObject = JSONObject.parseObject(result);
        Integer code =  jsonObject.getInteger("resultCode");
        return code;
    }
    /**
     * 从金豆提取到SLB币
     * @param userId         用户ID
     * @param walletPwd     登录密码
     * @param transfAmount  兑换slb数量
     */
    public BusinessMessage transferToSLB(String userId,String walletPwd, String transfAmount ){
        log.debug("userId="+userId+",walletPwd="+walletPwd+",transfAmount="+transfAmount);
        BusinessMessage message = new BusinessMessage();
        //获取用户信息
        SlUser slUser = slUserMapper.selectByPrimaryKey(userId);
        //获取slb汇率
        SlSystemConnector slSystemConnector = slSystemConnectorMapper.selectOne(new SlSystemConnector(){{
            setVar("bean");
        }});
        //兑换的slb数量转换为对应的金币
        Integer coin = Integer.valueOf(transfAmount) * Integer.valueOf(slSystemConnector.getAppId());
        //钱包地址
        String walletAddress = "";
        try {
            if (null != slUser){
                //获取手机区号
                SlPhoneZone slPhoneZone = slPhoneZoneMapper.selectOne(new SlPhoneZone(){{
                    setZone(slUser.getZone()==""?"中国大陆":slUser.getZone());
                }});
                //查询用户是否注册钱包
                if (checkUserRegister(slUser.getPhone(), slPhoneZone.getMobilearea().toString())){
                    //获取钱包地址
                    walletAddress = getWalletList(slUser.getPhone(),slPhoneZone.getMobilearea().toString());
                }else {
                    //用户注册钱包地址
                    UserRegister(slUser.getPhone(), BaseConstant.WALLET_DEFAULT_LOGIN_PASSWORD, slPhoneZone.getMobilearea().toString());
                    //获取钱包地址
                    walletAddress = getWalletList(slUser.getPhone(),slPhoneZone.getMobilearea().toString());
                }
                if (slUser.getCoin()>coin){
                    //公钥
                    String publicKey = env.getProperty("wallet.publicKey");
                    //生成加密随机串
                    String noteStr =  String.valueOf(System.currentTimeMillis());
                    noteStr = StringUtils.leftPad(noteStr, 16,  "0");

                    walletPwd = AESUtils.encode(walletPwd, noteStr);

                    //公钥加密随机串
                    String encodedNoteStr = RSAUtils.encryptByPublicKey(noteStr, publicKey);
                    String orderSn = String.valueOf(System.currentTimeMillis());

                    //生成签名
                    SortedMap<String, String> packageParams = new TreeMap<String, String>();
                    packageParams.put("walletAddress", walletAddress);
                    packageParams.put("walletPwd", walletPwd);
                    packageParams.put("transfAmount", transfAmount);
                    packageParams.put("noteStr", encodedNoteStr);
                    packageParams.put("orderSn", orderSn);

                    String sign = MD5SignUtils.createMD5Sign(packageParams, MD5SignUtils.CHARSET_NAME_DEFAULT);

                    String url = env.getProperty("wallet.url") + BaseConstant.WALLET_API_TRANSFERTOSLB;
                    Map<String,Object> params = new HashMap<String,Object>();
                    params.put("walletAddress", walletAddress);
                    params.put("walletPwd", walletPwd);
                    params.put("transfAmount", transfAmount.toString());
                    params.put("noteStr", encodedNoteStr);
                    params.put("orderSn", orderSn);

                    params.put("sign", sign);
                    String result = HttpUtil.doPost(url, params);
                    //解析返回值 转换成json格式
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    Integer code =  jsonObject.getInteger("resultCode");
                    if (0 == code){
                        slUser.setCoin(slUser.getCoin()-coin);
                        slUserMapper.updateByPrimaryKey(slUser);
                        message.setMsg("slb兑换成功");
                        message.setSuccess(true);
                    }else if (-100 == code){
                        message.setMsg("先去钱包APP设置支付密码");
                        message.setSuccess(false);
                        return message;
                    }else {
                        message.setMsg(jsonObject.getString("message"));
                        message.setSuccess(false);
                        return message;
                    }
                }else {
                    message.setMsg("金币不足");
                    message.setSuccess(false);
                    return message;
                }
            }else {
                message.setMsg("用户不存在");
                message.setSuccess(false);
                return message;
            }
        }catch (Exception e){
            log.error("兑换异常",e);
            message.setSuccess(false);
            message.setMsg("兑换异常");
        }

        return message;
    }
    /**
     *1、 ROUND_UP：远离零方向舍入。向绝对值最大的方向舍入，只要舍弃位非0即进位。
     *2、 ROUND_DOWN：趋向零方向舍入。向绝对值最小的方向输入，所有的位都要舍弃，不存在进位情况。
     *3、 ROUND_CEILING：向正无穷方向舍入。向正最大方向靠拢。若是正数，舍入行为类似于ROUND_UP，若为负数，舍入行为类似于ROUND_DOWN。Math.round()方法就是使用的此模式。
     *4、 ROUND_FLOOR：向负无穷方向舍入。向负无穷方向靠拢。若是正数，舍入行为类似于ROUND_DOWN；若为负数，舍入行为类似于ROUND_UP。
     *5、 HALF_UP：最近数字舍入(5进)。这是我们最经典的四舍五入。
     *6、 HALF_DOWN：最近数字舍入(5舍)。在这里5是要舍弃的。
     *7、 HAIL_EVEN：银行家舍入法。
     * 金币转化搜了贝 根据金币转化率
     * @param toCoin 转化的金币
     * @return
     */
    public String coinToSlb( Integer toCoin){
        //获取转换率
        SlSystemConnector slSystemConnector = slSystemConnectorMapper.selectOne(new SlSystemConnector(){{
            setVar("bean");
        }});
        BigDecimal slb = BigDecimal.valueOf(toCoin).divide(new BigDecimal(slSystemConnector.getAppId()),8,BigDecimal.ROUND_CEILING);
        return slb.toString();
    }
}
