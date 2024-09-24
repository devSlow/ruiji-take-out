package com.slow.ruijitakeout.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.slow.ruijitakeout.utils.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 公共字段自动填充
 * 1.实体类加上tableFiled 注解
 * 2.实现MetaObjectHandler接口
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段自动填充 insert");
        log.info(metaObject.toString());
//        进行自动填充
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime",LocalDateTime.now());
        metaObject.setValue("updateUser",BaseContext.getCurrentId());
        metaObject.setValue("createUser",BaseContext.getCurrentId());
        log.info("取出的用户id：{}",BaseContext.getCurrentId());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段自动填充 insert、update");
        log.info("元数据处理器："+metaObject.toString());
        metaObject.setValue("updateTime",LocalDateTime.now());
        metaObject.setValue("updateUser",BaseContext.getCurrentId());
        log.info("取出的用户id：{}",BaseContext.getCurrentId());
    }
}
