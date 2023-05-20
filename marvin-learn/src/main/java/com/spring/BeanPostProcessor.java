package com.spring;

/**
 * <p>Description:
 * </p>
 *
 * @author marvin
 * Created at 2023/5/20
 */
public interface BeanPostProcessor {

	default Object postProcessBeforeInitialization(Object bean, String beanName) {
		return bean;
	}

	default Object postProcessAfterInitialization(Object bean, String beanName) {
		return bean;
	}

}
