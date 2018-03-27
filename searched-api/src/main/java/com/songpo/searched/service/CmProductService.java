package com.songpo.searched.service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.songpo.searched.domain.BusinessMessage;
import com.songpo.searched.domain.CmProduct;
import com.songpo.searched.entity.SlActivityProduct;
import com.songpo.searched.entity.SlProduct;
import com.songpo.searched.entity.SlProductSaleModeOrderCount;
import com.songpo.searched.mapper.*;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定义商品服务类
 * <p>
 * 主要提供处理跟商品相关的服务
 */
@Slf4j
@Service
public class CmProductService {

    @Autowired
    private CmProductMapper mapper;

    @Autowired
    private CmProductCommentMapper cmProductCommentMapper;
    @Autowired
    private SlActivityProductMapper slActivityProductMapper;
    @Autowired
    private SlProductSaleModeOrderCountMapper slProductSaleModeOrderCountMapper;


    /**
     * 根据活动唯一标识符分页查询商品列表
     *
     * @param name         商品名称
     * @param salesModeId  销售模式唯一标识符
     * @param longitudeMin 最小经度
     * @param longitudeMax 最大经度
     * @param latitudeMin  最小维度
     * @param latitudeMax  最大维度
     * @param sortByPrice  按商品价格排序规则，取值 desc、asc、空，默认为空则不进行排序
     * @param sortByRating 按店铺评分排序规则，取值 desc、asc、空，默认为空则不进行排序
     * @param priceMin     价格区间最小值，默认为空。如果只有最小值，则选择大于等于此价格
     * @param priceMax     价格区间最大值，默认为空。如果只有最大值，则选择小于等于此价格
     * @param pageNum      页码
     * @param pageSize     容量
     * @return 商品分页列表
     */
    public PageInfo<Map<String, Object>> selectBySalesMode(String name,
                                                           String salesModeId,
                                                           Double longitudeMin,
                                                           Double longitudeMax,
                                                           Double latitudeMin,
                                                           Double latitudeMax,
                                                           String sortByPrice,
                                                           String sortByRating,
                                                           Integer priceMin,
                                                           Integer priceMax,
                                                           Integer pageNum,
                                                           Integer pageSize) {
        if (null == pageNum || pageNum <= 1) {
            pageNum = 1;
        }

        if (null == pageSize || pageSize <= 1) {
            pageSize = 10;
        }

        // 排序规则字符串
        String[] orderStrArray = new String[]{"DESC", "desc", "ASC", "asc"};

        // 过滤价格排序规则中的非法字符
        if (!StringUtils.containsAny(sortByPrice, orderStrArray)) {
            sortByPrice = StringUtils.trimToEmpty(sortByPrice);
        }

        // 过滤评分排序规则中的非法字符
        if (!StringUtils.containsAny(sortByRating, orderStrArray)) {
            sortByRating = StringUtils.trimToEmpty(sortByRating);
        }

        // 设置分页参数
        PageHelper.startPage(pageNum, pageSize);

        // 执行查询
        List<Map<String, Object>> list = this.mapper.selectBySalesMode(name, salesModeId, longitudeMin, longitudeMax, latitudeMin, latitudeMax, sortByPrice, sortByRating, priceMin, priceMax);

        return new PageInfo<>(list);
    }

    /**
     * 拼团商品列表
     *
     * @param actionId 活动唯一标识符
     * @param pageNum  页码
     * @param pageSize 容量
     * @return 商品分页列表
     */
    public PageInfo<SlProduct> selectByAction(String actionId, Integer pageNum, Integer pageSize) {
        log.debug("根据活动唯一标识符查询商品列表，活动唯一标识符：{}，页码：{}，容量：{}", actionId, pageNum, pageSize);
        if (null == pageNum || pageNum <= 1) {
            pageNum = 1;
        }

        if (null == pageSize || pageSize <= 1) {
            pageSize = 10;
        }

        // 设置分页参数
        PageHelper.startPage(pageNum, pageSize);

        // 执行查询
        List<SlProduct> list = this.mapper.selectByAction(actionId);

        return new PageInfo<>(list);
    }

    /**
     * 商品分类首页推荐商品
     * 规则(按照时间排序 最新的6个)
     *
     * @return
     */
    public BusinessMessage recommendProduct() {
        BusinessMessage businessMessage = new BusinessMessage();
        businessMessage.setSuccess(false);
        try {
            List<CmProduct> cmProducts = this.mapper.findRecommendProduct();
            if (cmProducts.size() > 0) {
                businessMessage.setMsg("查询成功");
                businessMessage.setSuccess(true);
                businessMessage.setData(cmProducts);
            } else {
                businessMessage.setMsg("查询无数据");
                businessMessage.setSuccess(true);
            }
        } catch (Exception e) {
            businessMessage.setMsg("查询失败");
            log.error("查询商品失败", e);
        }

        return businessMessage;
    }

    /**
     * 根据分类查询商品  +  商品筛选  + 根据商品名称
     *
     * @param goodsType  商品分类ID
     * @param screenType 筛选类型
     * @param page
     * @param size
     * @return
     */
    public BusinessMessage screenGoods(String goodsType, Integer screenType, Integer page, Integer size, String name) {
        log.debug("查询 商品分类Id:{},筛选条件:{},页数:{},条数:{},商品名称:{}", goodsType, screenType, page, size, name);
        BusinessMessage businessMessage = new BusinessMessage();
        businessMessage.setSuccess(false);
        try {
            PageHelper.startPage(page == null || page == 0 ? 1 : page, size == null ? 10 : size);
            if (goodsType != null || screenType != null || name != null) {
                List<Map<String, Object>> list = this.mapper.screenGoods(goodsType, screenType, name);
                if (list.size() > 0) {
                    businessMessage.setMsg("查询成功");
                    businessMessage.setSuccess(true);
                    businessMessage.setData(new PageInfo<>(list));
                } else {
                    businessMessage.setMsg("查询无数据!");
                    businessMessage.setSuccess(true);
                }
            } else {
                businessMessage.setMsg("请传入正确参数");
            }

        } catch (Exception e) {
            businessMessage.setMsg("查询异常");
            log.error("查询商品异常", e);
        }
        return businessMessage;
    }

    /**
     * 根据商品Id 查询商品详情
     *
     * @param goodsId
     * @param saleModeType 商品销售类型,1普通 2拼团 3预收 4秒杀
     * @return
     */
    public BusinessMessage goodsDetail(String goodsId, Integer saleModeType) {
        JSONObject data = new JSONObject();
        log.debug("查询 商品Id{}", goodsId);
        BusinessMessage businessMessage = new BusinessMessage();
        businessMessage.setSuccess(false);
        try {
            //商品基础信息
            Map map = this.mapper.goodsBaseInfo(goodsId);
            data.put("productBase", map);
            if (map.isEmpty()) {
                businessMessage.setMsg("未查到商品相关信息");
                businessMessage.setSuccess(true);
            } else {
                //商品图片
                List<Map<String, Object>> mapImageUrls = this.mapper.goodsImageUrl(goodsId);
                data.put("productImages", mapImageUrls);
                //商品评论
                List<Map<String, Object>> mapComments = this.cmProductCommentMapper.goodsComments(goodsId, null);
                Map goodsComment = mapComments.get(0);
                //如果第一条数据有图 则查询图片 没有图 直接返回第一条数据
                if (goodsComment.get("status").equals(4)) {
                    //查询评论图片
                    mapComments.get(0).get("id").toString();
                    List<Map<String, Object>> commentImages = this.cmProductCommentMapper.commentImages(goodsComment.get("id").toString());
                    List list = new ArrayList();
                    list.add(commentImages);
                    list.add(goodsComment);
                    data.put("productComments", list);
                } else {
                    data.put("productComments", goodsComment);
                }

                if (saleModeType != null) {
                    //查询商品活动表 查出来相关活动需要的时间或者人数
                    SlActivityProduct goodsActivity = this.slActivityProductMapper.selectOne(new SlActivityProduct() {{
                        setProductId(goodsId);
                        //商品基础信息表里查出来的销售模式
                        setActivityId(map.get("sales_mode_id").toString());
                    }});
                    data.put("productActivityInfo",goodsActivity);
                    if (goodsActivity != null) {
                        //查询该商品销售模式订单成交量
                        SlProductSaleModeOrderCount productSaleModeOrderCount = this.slProductSaleModeOrderCountMapper.selectOne(new SlProductSaleModeOrderCount() {{
                            setProductId(goodsId);
                            setSalesModeId(map.get("sales_mode_id").toString());
                        }});
                        data.put("productSaleModeOrderCount",productSaleModeOrderCount);
                    }

                    //如果是拼团 1查询 所有关于这个商品的订单, 不同的订单有一个count
                    if (saleModeType == 3) {
                        //查询该商品拼团未完成 订单编号
                       List<Map<String,Object>> unFinishOrder = this.mapper.findGroupOrder(goodsId,saleModeType,goodsActivity.getPeopleNum());
                       //根据订单编号查询第一个完成支付的小伙子

                        List list = new ArrayList();
                        for(int i =0; i < unFinishOrder.size(); i++){
                            Map groupMap = new HashMap();
                            Map<String,Object> userMap = this.mapper.findGroupPeople(unFinishOrder.get(i).get("serial_number").toString()) ;
                            groupMap.put("groupUserInfo",userMap);
                            groupMap.put("unFinishOrder",unFinishOrder.get(i));
                            list.add(groupMap);

                        }
                        data.put("unFinishOrder",list);

                    }


                }


            }
            businessMessage.setSuccess(true);
            businessMessage.setData(data);
            businessMessage.setMsg("查询成功");
        } catch (Exception e) {
            businessMessage.setMsg("查询异常");
            log.error("查询商品异常", e);
        }
        return businessMessage;
    }

    /**
     * 根据商品Id 查询商品规格
     *
     * @param id
     * @return
     */
    public BusinessMessage goodsSpecification(String id) {
        log.debug("商品规格详情,商品id:{}", id);
        JSONObject data = new JSONObject();
        BusinessMessage businessMessage = new BusinessMessage();
        businessMessage.setSuccess(false);
        try {
            //搜索商品规格Detail
            List<Map<String, Object>> specificationDetailMap = this.mapper.goodsSpecificationDetail(id);
            if (specificationDetailMap.size() > 0) {
                //查询商品规格名称
                List<Map<String, Object>> specificationMap = this.mapper.goodsSpecification(id);
                List list = new ArrayList();
                for (int i = 0; i < specificationMap.size(); i++) {
                    Map map = new HashMap();
                    map.put("specification", specificationMap.get(i));
                    for (int j = 0; j < specificationDetailMap.size(); j++) {
                        if (specificationDetailMap.get(j).get("specification_id").equals(specificationMap.get(i).get("specification_id"))) {
                            map.put("specificationDetail", specificationDetailMap.get(j));
                        }
                    }
                    list.add(map);
                }
                //step 1查询商品对应的库存
                List<Map<String, Object>> goodsRepositoryList = this.mapper.goodsRepository(id);
                //step 2根据商品分组查询对应的规格信息
                if (goodsRepositoryList.size() > 0) {
                    List lists = new ArrayList();
                    for (int i = 0; i < goodsRepositoryList.size(); i++) {
                        Map map = new HashMap();
                        List<Map<String, Object>> goodsRepositorySpecification = this.mapper.goodsRepositorySpecification(goodsRepositoryList.get(i).get("product_detail_group_serial_number").toString());
                        map.put("goodsRepositorySpecification", goodsRepositorySpecification);
                        map.put("goodsRepository", goodsRepositoryList.get(i));
                        lists.add(map);
                    }
                    list.add(lists);
                }

                businessMessage.setSuccess(true);
                businessMessage.setMsg("查询成功");
                businessMessage.setData(list);
            } else {
                businessMessage.setMsg("该商品无规格");
            }
        } catch (Exception e) {
            log.error("查询商品规格异常", e);
            businessMessage.setMsg("查询异常");
        }
        return businessMessage;
    }
}
