package com.spring.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * <p>Description:
 * </p>
 *
 * @author marvin
 * Created at 2023/5/20
 */
@Target(value = {java.lang.annotation.ElementType.TYPE})
@Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface ComponentScan {
	String value() default "";
}
