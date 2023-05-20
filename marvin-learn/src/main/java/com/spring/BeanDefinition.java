package com.spring;

import lombok.Data;

/**
 * <p>Description:
 * </p>
 *
 * @author marvin
 * Created at 2023/5/20
 */
@Data
public class BeanDefinition {
	private String beanName;
	private Class<?> classType;
	private boolean isLazy;
	private String scope;
}
