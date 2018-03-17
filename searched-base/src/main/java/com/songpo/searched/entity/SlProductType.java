package com.songpo.searched.entity;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "sl_product_type")
public class SlProductType implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 唯一标识符
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    /**
     * 名称
     */
    private String name;
    /**
     * 上级类别唯一标识符
     */
    @Column(name = "parent_id")
    private String parentId;
    /**
     * 标签唯一标识符
     */
    @Column(name = "tag_id")
    private String tagId;

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
     * 获取名称
     *
     * @return name - 名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置名称
     *
     * @param name 名称
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * 获取上级类别唯一标识符
     *
     * @return parent_id - 上级类别唯一标识符
     */
    public String getParentId() {
        return parentId;
    }

    /**
     * 设置上级类别唯一标识符
     *
     * @param parentId 上级类别唯一标识符
     */
    public void setParentId(String parentId) {
        this.parentId = parentId == null ? null : parentId.trim();
    }

    /**
     * 获取标签唯一标识符
     *
     * @return tag_id - 标签唯一标识符
     */
    public String getTagId() {
        return tagId;
    }

    /**
     * 设置标签唯一标识符
     *
     * @param tagId 标签唯一标识符
     */
    public void setTagId(String tagId) {
        this.tagId = tagId == null ? null : tagId.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", name=").append(name);
        sb.append(", parentId=").append(parentId);
        sb.append(", tagId=").append(tagId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}