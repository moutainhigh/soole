package com.songpo.searched.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name = "sl_search_ring_news")
public class SlSearchRingNews implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    /**
     * 用户id
     */
    @Column(name = "user_id")
    private String userId;
    /**
     * 评论数
     */
    @Column(name = "comment_num")
    private Integer commentNum;
    /**
     * 点赞数
     */
    @Column(name = "goods_num")
    private Integer goodsNum;
    /**
     * 发表时间
     */
    @Column(name = "create_time")
    private Date createTime;
    /**
     * 编辑时间
     */
    @Column(name = "edit_time")
    private Date editTime;
    /**
     * 发布内容
     */
    private String content;

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
     * 获取用户id
     *
     * @return user_id - 用户id
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 设置用户id
     *
     * @param userId 用户id
     */
    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    /**
     * 获取评论数
     *
     * @return comment_num - 评论数
     */
    public Integer getCommentNum() {
        return commentNum;
    }

    /**
     * 设置评论数
     *
     * @param commentNum 评论数
     */
    public void setCommentNum(Integer commentNum) {
        this.commentNum = commentNum;
    }

    /**
     * 获取点赞数
     *
     * @return goods_num - 点赞数
     */
    public Integer getGoodsNum() {
        return goodsNum;
    }

    /**
     * 设置点赞数
     *
     * @param goodsNum 点赞数
     */
    public void setGoodsNum(Integer goodsNum) {
        this.goodsNum = goodsNum;
    }

    /**
     * 获取发表时间
     *
     * @return create_time - 发表时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置发表时间
     *
     * @param createTime 发表时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取编辑时间
     *
     * @return edit_time - 编辑时间
     */
    public Date getEditTime() {
        return editTime;
    }

    /**
     * 设置编辑时间
     *
     * @param editTime 编辑时间
     */
    public void setEditTime(Date editTime) {
        this.editTime = editTime;
    }

    /**
     * 获取发布内容
     *
     * @return content - 发布内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置发布内容
     *
     * @param content 发布内容
     */
    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", userId=").append(userId);
        sb.append(", commentNum=").append(commentNum);
        sb.append(", goodsNum=").append(goodsNum);
        sb.append(", createTime=").append(createTime);
        sb.append(", editTime=").append(editTime);
        sb.append(", content=").append(content);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}