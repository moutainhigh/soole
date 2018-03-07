package com.songpo.controller;

import com.songpo.entity.SlMyCollection;
import com.songpo.entity.SlUser;
import com.songpo.service.CollectionService;
import com.songpo.service.UserService;
import com.songpo.validator.CollectionValidator;
import com.songpo.validator.UserValidator;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(description = "我的收藏管理")
@RestController
@RequestMapping("/api/v1/collection")
public class CollectionController extends BaseController<SlMyCollection, String> {

    public CollectionController(CollectionService service) {
        // 设置业务服务类
        super.service = service;
        // 设置实体校验器
        super.validatorHandler = new CollectionValidator(service);
    }

}
