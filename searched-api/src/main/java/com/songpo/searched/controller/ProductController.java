package com.songpo.searched.controller;

import com.github.pagehelper.PageInfo;
import com.songpo.searched.domain.BusinessMessage;
import com.songpo.searched.entity.SlProduct;
import com.songpo.searched.service.CmProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@Api(description = "商品管理")
@CrossOrigin
@RestController
@RequestMapping("/api/common/v1/product")
public class ProductController {

    @Autowired
    private CmProductService productService;

    /**
     * 根据活动唯一标识符分页查询商品列表
     *
     * @param name        商品名称
     * @param salesModeId 销售模式唯一标识符
     * @param pageNum     页码
     * @param pageSize    容量
     * @return 商品分页列表
     */
    @ApiOperation(value = "根据销售模式查询商品列表")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "name", value = "商品名称", paramType = "form"),
            @ApiImplicitParam(name = "salesModeId", value = "销售模式唯一标识符", paramType = "form", required = true),
            @ApiImplicitParam(name = "pageNum", value = "页码，从1开始", paramType = "form"),
            @ApiImplicitParam(name = "pageSize", value = "数量，必须大于0", paramType = "form")
    })
    @GetMapping("by-sales-mode")
    public BusinessMessage<PageInfo<Map<String, Object>>> selectBySalesMode(String name, String salesModeId, Integer pageNum, Integer pageSize) {
        log.debug("分页查询商品，名称：{}，销售模式唯一标识符：{}，页码：{}，容量：{}", name, salesModeId, pageNum, pageSize);
        BusinessMessage<PageInfo<Map<String, Object>>> message = new BusinessMessage<>();
        try {
            PageInfo<Map<String, Object>> data = this.productService.selectBySalesMode(name, salesModeId, pageNum, pageSize);

            message.setData(data);
            message.setSuccess(true);
        } catch (Exception e) {
            log.error("分页查询商品失败，{}", e);

            message.setMsg("分页查询商品失败，请重试");
        }
        return message;
    }

    /**
     * 根据活动唯一标识符分页查询商品列表
     *
     * @param actionId 销售模式
     * @param pageNum  页码
     * @param pageSize 容量
     * @return 商品分页列表
     */
    @ApiOperation(value = "根据活动分页查询商品列表")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "actionId", value = "活动唯一标识符", paramType = "form", required = true),
            @ApiImplicitParam(name = "pageNum", value = "页码，从1开始", paramType = "form"),
            @ApiImplicitParam(name = "pageSize", value = "数量，必须大于0", paramType = "form")
    })
    @GetMapping("by-activity")
    public BusinessMessage<PageInfo<SlProduct>> selectByActivity(String actionId, Integer pageNum, Integer pageSize) {
        log.debug("分页查询商品，页码：{}，数量：{}", pageNum, pageSize);
        BusinessMessage<PageInfo<SlProduct>> message = new BusinessMessage<>();
        try {
            PageInfo<SlProduct> data = this.productService.selectByAction(actionId, pageNum, pageSize);

            message.setData(data);
            message.setSuccess(true);
        } catch (Exception e) {
            log.error("分页查询商品失败，{}", e);

            message.setMsg("分页查询商品失败，请重试");
        }
        return message;
    }


    /**
     * 分类页面 推荐商品
     * 取最新商品的前6个
     *
     * @return
     */
    @ApiOperation(value = "分类页面,推荐商品")
    @ApiImplicitParams(value = {
    })
    @GetMapping("recommend-product")
    public BusinessMessage recommendProduct() {
        return this.productService.recommendProduct();
    }


    /**
     * 根据商品分类查询商品,商品筛选分类 + 筛选
     *
     * @param goodsType  商品分类ID
     * @param screenType 筛选类型
     * @param page       页码
     * @param size       数量
     * @return
     */
    @ApiOperation(value = "根据商品分类查询商品+筛选商品+商品名称")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "goodsType", value = "分类ID", paramType = "form"),
            @ApiImplicitParam(name = "screenType", value = "筛选条件1销量倒序2销量正序3价格倒序4价格正序567商品类型", paramType = "form"),
            @ApiImplicitParam(name = "page", value = "页码", paramType = "form"),
            @ApiImplicitParam(name = "size", value = "页码", paramType = "form")
    })
    @GetMapping("screen-goods")
    public BusinessMessage screenGoods(String goodsType, Integer screenType, Integer page, Integer size, String name) {
        log.debug("分页查询商品，分类Id:{},筛选Id:{},页码：{}，数量：{},商品名称:{},", goodsType,page, size,name);
        return this.productService.screenGoods(goodsType, screenType, page, size, name);
    }


    @ApiOperation(value = "根据商品Id查询商品详情")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "Id", value = "商品ID", paramType = "form", required = true)
    })
    @GetMapping("Id")
    public BusinessMessage goodsDetail(String Id) {
        log.debug("根据商品Id查询商品，商品Id：{}", Id);
        if (Id != null) {
            return this.productService.goodsDetail(Id);
        } else {
            BusinessMessage businessMessage = new BusinessMessage();
            businessMessage.setMsg("商品ID为空");
            businessMessage.setSuccess(false);
            return businessMessage;
        }
    }


}






