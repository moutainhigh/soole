package com.songpo.searched.service;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.github.wxpay.sdk.WXPayUtil;
import com.songpo.searched.alipay.service.AliPayService;
import com.songpo.searched.entity.SlOrder;
import com.songpo.searched.mapper.SlOrderMapper;
import com.songpo.searched.wxpay.service.WxPayService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 支付通知处理服务
 * 1、微信支付通知
 * 2、支付宝支付通知
 *
 * @author 刘松坡
 */
@Service
public class PaymentService {

    public static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final WxPayService payService;

    private final AliPayService aliPayService;

    @Autowired
    private ProcessOrders processOrders;
    @Autowired
    private SlOrderMapper slOrderMapper;

    @Autowired
    public PaymentService(WxPayService payService, AliPayService aliPayService) {
        this.payService = payService;
        this.aliPayService = aliPayService;
    }

    /**
     * 返回给微信端的状态
     *
     * @param returnCode 返回状态码 此字段是通信标识，非交易标识，交易是否成功需要查看result_code来判断
     * @param returnMsg  返回信息 返回信息，如非空，为错误原因 签名失败 参数格式校验错误
     * @return 通知参数
     */
    public static String wxPayNotifyProcess(String returnCode, String returnMsg) {
        return "<xml>" +
                "<return_code><![CDATA[" + returnCode + "]]></return_code>" +
                "<return_msg><![CDATA[" + returnMsg + "]]></return_msg>" +
                "</xml>";
    }

    /**
     * 处理微信支付通知
     *
     * @param request 请求参数
     * @return 处理支付通知结果
     */
    public String wxPayNotify(HttpServletRequest request) {
        log.debug("微信支付通知参数:{}", "request = [" + request + "]");
        String retStr = wxPayNotifyProcess("FAIL", "处理通知失败");
        log.info("微信支付通知retStr is:{}", retStr);
        try (InputStream is = request.getInputStream()) {
            // 读取支付回调参数
            byte[] bytes = IOUtils.readFully(is, request.getContentLength());
            if (bytes.length > 0) {
                String result = new String(bytes, StandardCharsets.UTF_8);
                if (StringUtils.isNotBlank(result)) {
                    // 支付回调参数
                    Map<String, String> resParams = WXPayUtil.xmlToMap(result);
                    log.debug("微信支付通知回调参数: {}", resParams);
                    log.debug("微信支付通知验签结果: {}", payService.wxpay.isPayResultNotifySignatureValid(resParams));
                    // 验签
                    if (payService.wxpay.isPayResultNotifySignatureValid(resParams)) {
                        log.debug("微信支付通知7");
                        // 签名正确
                        // 进行处理。
                        // 注意特殊情况：订单已经退款，但收到了支付结果成功的通知，不应把商户侧订单状态从退款改成支付成功
                        String orderId = resParams.get("out_trade_no");
                        log.debug("orderId is:{}", orderId);
                        if (null != orderId) {
                            SlOrder slOrder = slOrderMapper.selectOne(new SlOrder(){{
                                setOutOrderNumber(orderId);
                            }});
                            if (null == slOrder ){
                                processOrders.processOrders(orderId, 1);
                            }else {
                                processOrders.saleOrders(orderId, 1);
                            }
                        }
                        // 处理订单支付通知成功逻辑
                        // 通知微信服务器处理支付通知成功
                        retStr = wxPayNotifyProcess("SUCCESS", "OK");
                    } else {
                        // 签名错误，如果数据里没有sign字段，也认为是签名错误
//                        retStr = wxPayNotifyProcess("FAIL", "通知支付金额与订单金额不符");
                    }
                }
            } else {
                retStr = wxPayNotifyProcess("FAIL", "支付通知参数为空");
            }
        } catch (Exception e) {
            log.error("响应微信支付回调信息失败，{}", e);
        }
        return retStr;
    }

    /**
     * 手机网站支付结果异步通知
     * https://docs.open.alipay.com/203/105286/
     * 对于手机网站支付产生的交易，支付宝会根据原始支付API中传入的异步通知地址notify_url，通过POST请求的形式将支付结果作为参数通知到商户系统。
     * 无法接收到异步通知怎么办？更多关于异步通知的问题可以参考关于支付宝异步通知那些事帖子。
     *
     * @param request 请求参数
     * @return 处理支付通知结果
     */
    public String aliPayNotify(HttpServletRequest request) {
        log.debug("阿里支付通知参数:{}","request = [" + request + "]");
        // 返回给支付宝的通知
        String result = "fail";
        //获取支付宝POST过来反馈信息
        Map<String, String> maps = new HashMap<String, String>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            // 乱码解决，这段代码在出现乱码时使用。
//                 valueStr = new String(valueStr.getBytes("ISO-8859-1"),"utf-8");
            maps.put(name, valueStr);
        }
        log.debug("支付宝回调通知参数: {}", maps);
        //切记alipayPublickey是支付宝的公钥，请去open.alipay.com对应应用下查看。
        // TODO 处理系统订单状态等业务逻辑
        try {
            log.debug("支付宝验签类型: {}",aliPayService.getSignType());
            // 执行验签
            boolean flag = AlipaySignature.rsaCheckV1(maps, aliPayService.getAlipayPublicKey(), "UTF-8", aliPayService.getSignType());
            log.debug("支付宝执行验签结果: {}",flag);
            // 如果验签成功，则开始处理跟订单相关的业务，否则不进行处理，等待下一次通知回调
            if (flag) {
                String orderId = maps.get("out_trade_no");
                if (null != orderId) {
                    SlOrder slOrder = slOrderMapper.selectOne(new SlOrder(){{
                        setOutOrderNumber(orderId);
                    }});
                    if (null == slOrder){
                        processOrders.processOrders(orderId,2);
                    }else {
                        processOrders.saleOrders(orderId,2);
                    }
                }
                // 通知支付宝服务端支付回调通知已处理成功
                result = "success";
            }

        } catch (AlipayApiException e) {
            log.error("支付宝支付通知验签失败，{}", e);
        }
        return result;
    }
}
