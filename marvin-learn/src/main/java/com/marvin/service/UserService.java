package com.marvin.service;

import com.spring.BeanNameAware;
import com.spring.InitializingBean;
import com.spring.annotation.Autowired;
import com.spring.annotation.Component;
import com.spring.annotation.MarvinValue;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:
 * </p>
 *
 * @author marvin
 * Created at 2023/5/20
 */
@Slf4j
@Component(value = "userService")
// @Scope(value = "singleton")
public class UserService implements UserInterface, InitializingBean, BeanNameAware {

	@Autowired(value = "orderService")
	private OrderService orderService;

	@MarvinValue(value = "注解注入字段值")
	private String selfValue;

	public void test() {
		log.info("this:" + this);
		log.info("orderService:" + orderService);
		log.info("selfValue:" + selfValue);
	}

	@Override
	public void afterPropertiesSet() {
		log.info("afterPropertiesSet");
	}

	@Override
	public void setBeanName(String name) {
		log.info("beanName:" + name);
	}
}
