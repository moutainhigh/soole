package com.songpo.searched.service;

import com.songpo.searched.domain.BusinessMessage;
import com.songpo.searched.entity.SlOrder;
import com.songpo.searched.entity.SlOrderDetail;
import com.songpo.searched.entity.SlRepository;
import com.songpo.searched.entity.SlUser;
import com.songpo.searched.util.OrderNumGeneration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class OrderService {

    @Autowired
    private UserService userService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 新增预下单订单
     *
     * @param slOrder
     * @param orderDetail
     * @return
     */
    public BusinessMessage addOrder(SlOrder slOrder, List<SlOrderDetail> orderDetail) {
        Logger log = LoggerFactory.getLogger(SlOrderDetail.class);
        BusinessMessage message = new BusinessMessage();
        BigDecimal money = null;
        try
        {
            if (StringUtils.hasLength(slOrder.getUserId()))
            {
                SlUser user = userService.selectOne(new SlUser() {{
                    setId(slOrder.getUserId());
                }});
                if (null != user)
                {
                    if (StringUtils.hasLength(slOrder.getShippngAddressId()))
                    {
                        slOrder.setSerialNumber(OrderNumGeneration.getOrderIdByUUId());// 生成订单编号


                        for (SlOrderDetail slOrderDetail : orderDetail)
                        {
                            if (StringUtils.hasLength(slOrderDetail.getProductId()))
                            {
                                SlRepository repository = this.repositoryService.selectOne(new SlRepository() {{
                                    setId(slOrderDetail.getProductId());
                                }});
                                if (null != repository && StringUtils.hasLength(slOrderDetail.getQuantity()))
                                {
//                                    money += repository.getPrice();
                                    orderDetailService.insertSelective(new SlOrderDetail() {{
                                        setId(UUID.randomUUID().toString());
                                        setQuantity(slOrderDetail.getQuantity()); // 商品数量
                                        setPrice(repository.getPrice()); // 商品价格
                                        setProductId(slOrderDetail.getProductId());// 商品ID
                                        setShopId(repository.getShopId());// 店铺唯一标识
                                        setRepositoryId(repository.getId()); // 店铺仓库ID
                                        setDeductPulse(repository.getPulse()); // 扣除了豆数量
                                    }});
                                }
                            }
                        }
                    } else
                    {
                        message.setMsg("收货地址不能为空");
                    }
                } else
                {
                    message.setMsg("用户不存在");
                }
            } else
            {
                message.setMsg("用户ID为空");
            }
        } catch (Exception e)
        {
            log.debug("error:", e.getMessage());
        }
        return message;
    }
}