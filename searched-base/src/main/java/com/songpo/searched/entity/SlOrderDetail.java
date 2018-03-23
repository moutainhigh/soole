package com.songpo.searched.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.*;

@Table(name = "sl_order_detail")
public class SlOrderDetail implements Serializable {
    /**
     * 唯一标识符
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    /**
     * 订单唯一标识符
     */
    @Column(name = "order_id")
    private String orderId;

    /**
     * 店铺唯一标识符
     */
    @Column(name = "shop_id")
    private String shopId;

    /**
     * 店铺仓库唯一标识
     */
    @Column(name = "repository_id")
    private String repositoryId;

    /**
     * 商品唯一标识符
     */
    @Column(name = "product_id")
    private String productId;

    /**
     * 数量
     */
    private String quantity;

    /**
     * 单个商品价格
     */
    private BigDecimal price;

    /**
     * 单个商品扣除的银豆数量
     */
    @Column(name = "deduct_total_silver")
    private Integer deductTotalSilver;

    /**
     * 单个商品扣除的金豆数量
     */
    @Column(name = "deduct_total_gold")
    private Integer deductTotalGold;

    /**
     * 折扣
     */
    private Integer discount;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private String createTime;

    /**
     * 修改人
     */
    private String modifier;

    /**
     * 修改时间
     */
    @Column(name = "modification_time")
    private String modificationTime;

    /**
     * 快递费
     */
    @Column(name = "post_fee")
    private BigDecimal postFee;

    private static final long serialVersionUID = 1L;

    /**
     * 获取唯一标识符
     *
     * @return id - 唯一标识符
     */
    public String getId() {
        return id;
    }

    /**
     * 设置唯一标识符
     *
     * @param id 唯一标识符
     */
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    /**
     * 获取订单唯一标识符
     *
     * @return order_id - 订单唯一标识符
     */
    public String getOrderId() {
        return orderId;
    }

    /**
     * 设置订单唯一标识符
     *
     * @param orderId 订单唯一标识符
     */
    public void setOrderId(String orderId) {
        this.orderId = orderId == null ? null : orderId.trim();
    }

    /**
     * 获取店铺唯一标识符
     *
     * @return shop_id - 店铺唯一标识符
     */
    public String getShopId() {
        return shopId;
    }

    /**
     * 设置店铺唯一标识符
     *
     * @param shopId 店铺唯一标识符
     */
    public void setShopId(String shopId) {
        this.shopId = shopId == null ? null : shopId.trim();
    }

    /**
     * 获取店铺仓库唯一标识
     *
     * @return repository_id - 店铺仓库唯一标识
     */
    public String getRepositoryId() {
        return repositoryId;
    }

    /**
     * 设置店铺仓库唯一标识
     *
     * @param repositoryId 店铺仓库唯一标识
     */
    public void setRepositoryId(String repositoryId) {
        this.repositoryId = repositoryId == null ? null : repositoryId.trim();
    }

    /**
     * 获取商品唯一标识符
     *
     * @return product_id - 商品唯一标识符
     */
    public String getProductId() {
        return productId;
    }

    /**
     * 设置商品唯一标识符
     *
     * @param productId 商品唯一标识符
     */
    public void setProductId(String productId) {
        this.productId = productId == null ? null : productId.trim();
    }

    /**
     * 获取数量
     *
     * @return quantity - 数量
     */
    public String getQuantity() {
        return quantity;
    }

    /**
     * 设置数量
     *
     * @param quantity 数量
     */
    public void setQuantity(String quantity) {
        this.quantity = quantity == null ? null : quantity.trim();
    }

    /**
     * 获取单个商品价格
     *
     * @return price - 单个商品价格
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * 设置单个商品价格
     *
     * @param price 单个商品价格
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * 获取单个商品扣除的银豆数量
     *
     * @return deduct_total_silver - 单个商品扣除的银豆数量
     */
    public Integer getDeductTotalSilver() {
        return deductTotalSilver;
    }

    /**
     * 设置单个商品扣除的银豆数量
     *
     * @param deductTotalSilver 单个商品扣除的银豆数量
     */
    public void setDeductTotalSilver(Integer deductTotalSilver) {
        this.deductTotalSilver = deductTotalSilver;
    }

    /**
     * 获取单个商品扣除的金豆数量
     *
     * @return deduct_total_gold - 单个商品扣除的金豆数量
     */
    public Integer getDeductTotalGold() {
        return deductTotalGold;
    }

    /**
     * 设置单个商品扣除的金豆数量
     *
     * @param deductTotalGold 单个商品扣除的金豆数量
     */
    public void setDeductTotalGold(Integer deductTotalGold) {
        this.deductTotalGold = deductTotalGold;
    }

    /**
     * 获取折扣
     *
     * @return discount - 折扣
     */
    public Integer getDiscount() {
        return discount;
    }

    /**
     * 设置折扣
     *
     * @param discount 折扣
     */
    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    /**
     * 获取创建人
     *
     * @return creator - 创建人
     */
    public String getCreator() {
        return creator;
    }

    /**
     * 设置创建人
     *
     * @param creator 创建人
     */
    public void setCreator(String creator) {
        this.creator = creator == null ? null : creator.trim();
    }

    /**
     * 获取创建时间
     *
     * @return create_time - 创建时间
     */
    public String getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间
     *
     * @param createTime 创建时间
     */
    public void setCreateTime(String createTime) {
        this.createTime = createTime == null ? null : createTime.trim();
    }

    /**
     * 获取修改人
     *
     * @return modifier - 修改人
     */
    public String getModifier() {
        return modifier;
    }

    /**
     * 设置修改人
     *
     * @param modifier 修改人
     */
    public void setModifier(String modifier) {
        this.modifier = modifier == null ? null : modifier.trim();
    }

    /**
     * 获取修改时间
     *
     * @return modification_time - 修改时间
     */
    public String getModificationTime() {
        return modificationTime;
    }

    /**
     * 设置修改时间
     *
     * @param modificationTime 修改时间
     */
    public void setModificationTime(String modificationTime) {
        this.modificationTime = modificationTime == null ? null : modificationTime.trim();
    }

    /**
     * 获取快递费
     *
     * @return post_fee - 快递费
     */
    public BigDecimal getPostFee() {
        return postFee;
    }

    /**
     * 设置快递费
     *
     * @param postFee 快递费
     */
    public void setPostFee(BigDecimal postFee) {
        this.postFee = postFee;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", orderId=").append(orderId);
        sb.append(", shopId=").append(shopId);
        sb.append(", repositoryId=").append(repositoryId);
        sb.append(", productId=").append(productId);
        sb.append(", quantity=").append(quantity);
        sb.append(", price=").append(price);
        sb.append(", deductTotalSilver=").append(deductTotalSilver);
        sb.append(", deductTotalGold=").append(deductTotalGold);
        sb.append(", discount=").append(discount);
        sb.append(", creator=").append(creator);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifier=").append(modifier);
        sb.append(", modificationTime=").append(modificationTime);
        sb.append(", postFee=").append(postFee);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}