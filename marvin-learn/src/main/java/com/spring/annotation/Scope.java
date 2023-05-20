package com.spring.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * <p>Description:
 * </p>
 *
 * @author marvin
 * Created at 2023/5/20
 */
@Target(value = {ElementType.TYPE})
@Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface Scope {
	String value() default "";

	String SCOPE_SINGLETON = "singleton";
	String SCOPE_PROTOTYPE = "prototype";
}
