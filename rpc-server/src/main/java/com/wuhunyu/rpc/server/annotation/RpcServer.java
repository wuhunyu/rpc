package com.wuhunyu.rpc.server.annotation;

import java.lang.annotation.*;

/**
 * rpc服务暴露标识
 *
 * @author wuhunyu
 * @version 1.0
 * @date 2022-09-03 21:32
 */

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcServer {

    /**
     * 实现接口
     */
    Class<?>[] value() default {};

    /**
     * 是否启用(用于避免多实现时bean冲突)
     */
    boolean enabled() default true;

}
