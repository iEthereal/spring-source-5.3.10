package com.marvin.service;

import com.spring.BeanPostProcessor;
import com.spring.annotation.Component;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Proxy;

/**
 * <p>Description:
 * </p>
 *
 * @author marvin
 * Created at 2023/5/20
 */
@Slf4j
@Component
public class MarvinBeanPostProcessor implements BeanPostProcessor {
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) {

		if (bean instanceof UserInterface) {
			Object proxyInstance = Proxy.newProxyInstance(MarvinBeanPostProcessor.class.getClassLoader(), new Class[]{UserInterface.class}, (proxy, method, args) -> {
				log.info(method.getName() + "\t:切面逻辑\t: 原对象：" + bean);
				return method.invoke(bean, args);
			});
			log.info("代理对象：" + proxyInstance);
			return proxyInstance;
		}

		return bean;
	}
}
