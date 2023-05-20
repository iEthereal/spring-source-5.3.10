package com.spring;

import com.spring.annotation.Autowired;
import com.spring.annotation.Component;
import com.spring.annotation.ComponentScan;
import com.spring.annotation.Scope;
import com.spring.util.StringUtils;

import java.beans.Introspector;
import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.spring.annotation.Scope.SCOPE_SINGLETON;

/**
 * <p>Description:
 * </p>
 *
 * @author marvin
 * Created at 2023/5/20
 */
public class MarvinApplicationContext {

	private Class<?> configClass;
	private Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();
	private Map<String, Object> singletonObjects = new HashMap<>();
	private List<BeanPostProcessor> beanPostProcessorList = new ArrayList<>();

	public MarvinApplicationContext(Class<?> configClass) {
		this.configClass = configClass;

		// 扫描所有Bean定义
		scan(configClass);

		// 创建非懒加载单例Bean
		for (Map.Entry<String, BeanDefinition> entry : this.beanDefinitionMap.entrySet()) {
			String beanName = entry.getKey();
			BeanDefinition beanDefinition = entry.getValue();
			if (SCOPE_SINGLETON.equals(beanDefinition.getScope())) {
				Object bean = createBean(beanName, beanDefinition);
				singletonObjects.put(beanName, bean);
			}
		}
	}

	private Object createBean(String beanName, BeanDefinition beanDefinition) {
		try {
			Class<?> clazz = beanDefinition.getClassType();
			Object instance = clazz.newInstance();

			// 依赖 Bean 注入
			for (Field field : clazz.getDeclaredFields()) {
				if (field.isAnnotationPresent(Autowired.class)) {
					String dependentBeanName = field.getAnnotation(Autowired.class).value();
					field.setAccessible(true);
					field.set(instance, getBean(StringUtils.isEmpty(dependentBeanName) ? field.getName() : dependentBeanName));
				}
			}

			// Bean name 回调赋值
			if (instance instanceof BeanNameAware) {
				((BeanNameAware) instance).setBeanName(beanName);
			}

			// 执行 BeanPostProcessor 的 postProcessBeforeInitialization 方法
			for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
				instance = beanPostProcessor.postProcessBeforeInitialization(instance, beanName);
			}

			// 实现 InitializingBean 接口的 Bean 执行 afterPropertiesSet 方法
			if (instance instanceof InitializingBean) {
				((InitializingBean) instance).afterPropertiesSet();
			}

			// 执行 BeanPostProcessor 的 postProcessAfterInitialization 方法
			for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
				instance = beanPostProcessor.postProcessAfterInitialization(instance, beanName);
			}

			return instance;
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void scan(Class<?> configClass) {
		// 读取扫描路径
		if (configClass.isAnnotationPresent(ComponentScan.class)) {
			ComponentScan componentScanAnnotation = configClass.getAnnotation(ComponentScan.class);
			String path = componentScanAnnotation.value().replace(".", "/");

			// 从类加载器确定路径实际地址
			ClassLoader classLoader = MarvinApplicationContext.class.getClassLoader();
			URL resource = classLoader.getResource(path);
			if (Objects.nonNull(resource)) {
				File scanFile = new File(resource.getFile());

				// 遍历路径下的所有文件
				if (scanFile.isDirectory()) {
					for (File file : Optional.ofNullable(scanFile.listFiles()).orElse(new File[]{})) {
						String absolutePath = file.getAbsolutePath();

						try {
							// 通过类加载器加载文件类
							String classPath = absolutePath.substring(absolutePath.indexOf("com"), absolutePath.indexOf(".class"))
									.replace(File.separator, ".");
							Class<?> aClass = classLoader.loadClass(classPath);

							// 判断类是否有Component注解，即被声明为 Bean
							if (aClass.isAnnotationPresent(Component.class)) {

								// 判断类是否实现了 BeanPostProcessor 接口，即为 Bean 后置处理器
								if (BeanPostProcessor.class.isAssignableFrom(aClass)) {
									BeanPostProcessor processor = (BeanPostProcessor) aClass.getConstructor().newInstance();
									beanPostProcessorList.add(processor);
								}

								Component componentAnnotation = aClass.getAnnotation(Component.class);
								BeanDefinition beanDefinition = new BeanDefinition();
								if (!"".equals(componentAnnotation.value())) {
									beanDefinition.setBeanName(componentAnnotation.value());
								} else {
									beanDefinition.setBeanName(Introspector.decapitalize(aClass.getSimpleName()));
								}
								beanDefinition.setClassType(aClass);

								// 判断类是否有Scope注解，是否为单例
								if (aClass.isAnnotationPresent(Scope.class)) {
									Scope scopeAnnotation = aClass.getAnnotation(Scope.class);
									String scope = scopeAnnotation.value();
									beanDefinition.setScope(scope);
								} else {
									beanDefinition.setScope(SCOPE_SINGLETON);
								}

								// 缓存 Bean 定义
								beanDefinitionMap.put(beanDefinition.getBeanName(), beanDefinition);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	// 从单例池获取 或者 创建 Bean
	public Object getBean(String beanName) {
		if (!beanDefinitionMap.containsKey(beanName)) {
			throw new IllegalArgumentException("Bean is not defined: " + beanName);
		}
		BeanDefinition beanDefinition = Optional.ofNullable(beanDefinitionMap.get(beanName)).orElse(new BeanDefinition());
		if (SCOPE_SINGLETON.equals(beanDefinition.getScope())) {
			Object singleTonBean = singletonObjects.get(beanName);
			if (Objects.isNull(singleTonBean)) {
				singleTonBean = createBean(beanName, beanDefinition);
				singletonObjects.put(beanName, singleTonBean);
			}
			return singleTonBean;
		} else {
			return createBean(beanName, beanDefinition);
		}
	}
}
