package com.songpo.searched.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "sl_transaction_detail")
public class SlTransactionDetail implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    /**
     * 来源id，
     */
    @Column(name = "source_id")
    private String sourceId;

    /**
     * 目标id
     */
    @Column(name = "target_id")
    private String targetId;

    /**
     * 红包id
     */
    @Column(name = "red_packet_id")
    private String redPacketId;

    /**
     * 订单id（购物相关使用）
     */
    @Column(name = "order_id")
    private String orderId;

    /**
     * 消费方式 （1-99：红包、转账业务）1.转账 2. 接收转账 3.发红包 4.抢红包 5.红包过期退回 6.余额提现   （100-199：活动相关） 100：新人礼包（平台赠送）  101：签到  102：邀请好友  （200-299：购物相关） 200：购物支付  201：购物赠送  202：评价晒单   （300-400：收益相关）
     */
    private Integer type;

    /**
     * 交易金额
     */
    private BigDecimal money;

    /**
     * 交易金豆数量
     */
    private Integer coin;

    /**
     * 交易银豆数量
     */
    private Integer silver;

    /**
     * 交易货币类型 1.账户余额 2.略 3.钱（暂时没用） 4.钱+豆 5.金豆 6.银豆
     */
    @Column(name = "deal_type")
    private Integer dealType;

    /**
     * 交易类型  1.支出  2.收入
     */
    @Column(name = "transaction_type")
    private Integer transactionType;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    private static final long serialVersionUID = 1L;

    /**
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    /**
     * 获取来源id，
     *
     * @return source_id - 来源id，
     */
    public String getSourceId() {
        return sourceId;
    }

    /**
     * 设置来源id，
     *
     * @param sourceId 来源id，
     */
    public void setSourceId(String sourceId) {
        this.sourceId = sourceId == null ? null : sourceId.trim();
    }

    /**
     * 获取目标id
     *
     * @return target_id - 目标id
     */
    public String getTargetId() {
        return targetId;
    }

    /**
     * 设置目标id
     *
     * @param targetId 目标id
     */
    public void setTargetId(String targetId) {
        this.targetId = targetId == null ? null : targetId.trim();
    }

    /**
     * 获取红包id
     *
     * @return red_packet_id - 红包id
     */
    public String getRedPacketId() {
        return redPacketId;
    }

    /**
     * 设置红包id
     *
     * @param redPacketId 红包id
     */
    public void setRedPacketId(String redPacketId) {
        this.redPacketId = redPacketId == null ? null : redPacketId.trim();
    }

    /**
     * 获取订单id（购物相关使用）
     *
     * @return order_id - 订单id（购物相关使用）
     */
    public String getOrderId() {
        return orderId;
    }

    /**
     * 设置订单id（购物相关使用）
     *
     * @param orderId 订单id（购物相关使用）
     */
    public void setOrderId(String orderId) {
        this.orderId = orderId == null ? null : orderId.trim();
    }

    /**
     * 获取消费方式 （1-99：红包、转账业务）1.转账 2. 接收转账 3.发红包 4.抢红包 5.红包过期退回 6.余额提现   （100-199：活动相关） 100：新人礼包（平台赠送）  101：签到  102：邀请好友  （200-299：购物相关） 200：购物支付  201：购物赠送  202：评价晒单   （300-400：收益相关）
     *
     * @return type - 消费方式 （1-99：红包、转账业务）1.转账 2. 接收转账 3.发红包 4.抢红包 5.红包过期退回 6.余额提现   （100-199：活动相关） 100：新人礼包（平台赠送）  101：签到  102：邀请好友  （200-299：购物相关） 200：购物支付  201：购物赠送  202：评价晒单   （300-400：收益相关）
     */
    public Integer getType() {
        return type;
    }

    /**
     * 设置消费方式 （1-99：红包、转账业务）1.转账 2. 接收转账 3.发红包 4.抢红包 5.红包过期退回 6.余额提现   （100-199：活动相关） 100：新人礼包（平台赠送）  101：签到  102：邀请好友  （200-299：购物相关） 200：购物支付  201：购物赠送  202：评价晒单   （300-400：收益相关）
     *
     * @param type 消费方式 （1-99：红包、转账业务）1.转账 2. 接收转账 3.发红包 4.抢红包 5.红包过期退回 6.余额提现   （100-199：活动相关） 100：新人礼包（平台赠送）  101：签到  102：邀请好友  （200-299：购物相关） 200：购物支付  201：购物赠送  202：评价晒单   （300-400：收益相关）
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * 获取交易金额
     *
     * @return money - 交易金额
     */
    public BigDecimal getMoney() {
        return money;
    }

    /**
     * 设置交易金额
     *
     * @param money 交易金额
     */
    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    /**
     * 获取交易金豆数量
     *
     * @return coin - 交易金豆数量
     */
    public Integer getCoin() {
        return coin;
    }

    /**
     * 设置交易金豆数量
     *
     * @param coin 交易金豆数量
     */
    public void setCoin(Integer coin) {
        this.coin = coin;
    }

    /**
     * 获取交易银豆数量
     *
     * @return silver - 交易银豆数量
     */
    public Integer getSilver() {
        return silver;
    }

    /**
     * 设置交易银豆数量
     *
     * @param silver 交易银豆数量
     */
    public void setSilver(Integer silver) {
        this.silver = silver;
    }

    /**
     * 获取交易货币类型 1.账户余额 2.略 3.钱（暂时没用） 4.钱+豆 5.金豆 6.银豆
     *
     * @return deal_type - 交易货币类型 1.账户余额 2.略 3.钱（暂时没用） 4.钱+豆 5.金豆 6.银豆
     */
    public Integer getDealType() {
        return dealType;
    }

    /**
     * 设置交易货币类型 1.账户余额 2.略 3.钱（暂时没用） 4.钱+豆 5.金豆 6.银豆
     *
     * @param dealType 交易货币类型 1.账户余额 2.略 3.钱（暂时没用） 4.钱+豆 5.金豆 6.银豆
     */
    public void setDealType(Integer dealType) {
        this.dealType = dealType;
    }

    /**
     * 获取交易类型  1.支出  2.收入
     *
     * @return transaction_type - 交易类型  1.支出  2.收入
     */
    public Integer getTransactionType() {
        return transactionType;
    }

    /**
     * 设置交易类型  1.支出  2.收入
     *
     * @param transactionType 交易类型  1.支出  2.收入
     */
    public void setTransactionType(Integer transactionType) {
        this.transactionType = transactionType;
    }

    /**
     * 获取创建时间
     *
     * @return create_time - 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间
     *
     * @param createTime 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", sourceId=").append(sourceId);
        sb.append(", targetId=").append(targetId);
        sb.append(", redPacketId=").append(redPacketId);
        sb.append(", orderId=").append(orderId);
        sb.append(", type=").append(type);
        sb.append(", money=").append(money);
        sb.append(", coin=").append(coin);
        sb.append(", silver=").append(silver);
        sb.append(", dealType=").append(dealType);
        sb.append(", transactionType=").append(transactionType);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}