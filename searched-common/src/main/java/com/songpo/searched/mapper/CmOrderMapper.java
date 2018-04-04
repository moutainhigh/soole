package com.songpo.searched.mapper;

import com.songpo.searched.entity.SlActivityProduct;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface CmOrderMapper {

    List<Map<String, Object>> findList(@Param("userId") String userId);

    List<String> findUserAvatar(@Param("serialNumber") Object serialNumber);

    Map<String, Object> selectMyOrderInfo(@Param("userId") String userId, @Param("orderId") String orderId);

//    List<String> existUser(@Param("productId") String productId);

    Map<String, Object> findGroup(@Param("productId") String productId);

    Map<String, Object> findActivityProduct(@Param("productId") String productId);

    Integer selectOrdersCount(@Param("productId") String productId, @Param("userId") String userId, @Param("activityProductId") String activityProductId);

    List<String> findProductsRepositoryId(@Param("productId") String productId, @Param("activityId") String activityId);

    Integer groupOrdersByUser(@Param("productId") String productId, @Param("activityId") String activityId, @Param("userId") String userId);

    SlActivityProduct selectActivityProductByActivityAndRepositoryId(@Param("repositoryId") String repositoryId, @Param("activityId") String activityId);
}
