package com.marvin.test;

import com.marvin.config.AppConfig;
import com.marvin.service.UserInterface;
import com.marvin.service.UserService;
import com.spring.MarvinApplicationContext;

/**
 * <p>Description:
 * </p>
 *
 * @author marvin
 * Created at 2023/5/20
 */
public class SpringTest {
	public static void main(String[] args) {
		MarvinApplicationContext applicationContext = new MarvinApplicationContext(AppConfig.class);
		UserInterface userService = (UserInterface) applicationContext.getBean("userService");
		userService.test();

		System.out.println(applicationContext.getBean("orderService"));
	}
}
