package org.fastser.web.rest.scanner;

import static org.fastser.web.constant.RestConstant.GENERIC_RESTFUL_SERVICE;
import static org.springframework.util.Assert.notNull;

import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.fastser.web.rest.RestDataUtils;
import org.fastser.web.rest.annotation.RestAnotationMetadata;
import org.fastser.web.rest.annotation.RestAnotationType;
import org.fastser.web.rest.annotation.RestMapping;
import org.fastser.web.rest.annotation.RestMethodMappingInfo;
import org.fastser.web.rest.handler.RestHandlerMethod;
import org.fastser.web.rest.handler.RestHandlerMethodMapping;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyResourceConfigurer;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;


public class RestComponentScanner implements BeanDefinitionRegistryPostProcessor, InitializingBean, ApplicationContextAware, BeanNameAware {
	
	private String basePackage;
	
	private String beanName;
	
	private ApplicationContext applicationContext;
	
	private boolean processPropertyPlaceHolders;
	
	private Class<? extends Annotation> annotationMappingClass = RestMapping.class;
	private Class<? extends Annotation> annotationValidatorClass = org.fastser.web.rest.annotation.RestValidator.class;

	public void postProcessBeanFactory(
			ConfigurableListableBeanFactory beanFactory) throws BeansException {
	}

	public void setBeanName(String name) {
		this.beanName = name;
	}

	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

	public void afterPropertiesSet() throws Exception {
		notNull(this.basePackage, "Property 'basePackage' is required");
	}
	
	public void setBasePackage(String basePackage) {
	    this.basePackage = basePackage;
	}
	
	public void setProcessPropertyPlaceHolders(boolean processPropertyPlaceHolders) {
	    this.processPropertyPlaceHolders = processPropertyPlaceHolders;
	}
	
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		if (this.processPropertyPlaceHolders) {
		      processPropertyPlaceHolders();
		}
	    Scanner scanner = new Scanner(registry);
	    scanner.setResourceLoader(this.applicationContext);

	    scanner.scan(StringUtils.tokenizeToStringArray(this.basePackage, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS));
	}
	
	private void processPropertyPlaceHolders() {
	    Map<String, PropertyResourceConfigurer> prcs = applicationContext.getBeansOfType(PropertyResourceConfigurer.class);

	    if (!prcs.isEmpty() && applicationContext instanceof GenericApplicationContext) {
	      BeanDefinition mapperScannerBean = ((GenericApplicationContext) applicationContext)
	          .getBeanFactory().getBeanDefinition(beanName);

	      DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
	      factory.registerBeanDefinition(beanName, mapperScannerBean);

	      for (PropertyResourceConfigurer prc : prcs.values()) {
	        prc.postProcessBeanFactory(factory);
	      }

	      PropertyValues values = mapperScannerBean.getPropertyValues();

	      this.basePackage = updatePropertyValue("basePackage", values);
	    }
	  }

	  private String updatePropertyValue(String propertyName, PropertyValues values) {
	    PropertyValue property = values.getPropertyValue(propertyName);

	    if (property == null) {
	      return null;
	    }

	    Object value = property.getValue();

	    if (value == null) {
	      return null;
	    } else if (value instanceof String) {
	      return value.toString();
	    } else if (value instanceof TypedStringValue) {
	      return ((TypedStringValue) value).getValue();
	    } else {
	      return null;
	    }
	  }
	  
	  
	  
	  private final class Scanner extends ClassPathBeanDefinitionScanner {
		  
		  	private BeanNameGenerator beanNameGenerator = new AnnotationBeanNameGenerator();

		    public Scanner(BeanDefinitionRegistry registry) {
		      super(registry);
		    }
		    
		    
		    @Override
		    protected void registerDefaultFilters() {
		      // if specified, use the given annotation and / or marker interface
		      if (RestComponentScanner.this.annotationMappingClass != null) {
		        addIncludeFilter(new AnnotationTypeFilter(RestComponentScanner.this.annotationMappingClass));
		      }
		    }
		    

		    @Override
		    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
		    	Set<BeanDefinitionHolder> beanDefinitionHolders = super.doScan(basePackages);
		    	Assert.notEmpty(basePackages, "At least one base package must be specified");
		    	Set<BeanDefinition> candidates = findCandidateComponents(basePackage);
			    if (candidates.isEmpty()) {
			        logger.warn("No MyBatis mapper was found in 'ffff' package. Please check your configuration.");
			    } else {
			    	for (BeanDefinition beanDefinition : candidates) {
			    		if(beanDefinition instanceof ScannedGenericBeanDefinition){
			    			ScannedGenericBeanDefinition sgBeanDefinition = (ScannedGenericBeanDefinition)beanDefinition;
			    			Map<String, Object> metaMap = sgBeanDefinition.getMetadata().getAnnotationAttributes(annotationMappingClass.getName());
							String parent = RestDataUtils.typeAnnotationAttributesResolver(metaMap);
							String beanName = this.beanNameGenerator.generateBeanName(beanDefinition, super.getRegistry());
			    			registerRestInfo(beanName, beanDefinition, sgBeanDefinition, parent, RestAnotationType.MAPPING);
			    			registerRestInfo(beanName, beanDefinition, sgBeanDefinition, parent, RestAnotationType.VALIDATOR);
			    		}
			    	}
			    }
			    RestHandlerMethodMapping.registerGenericRestful(applicationContext.getBean(GENERIC_RESTFUL_SERVICE));
			    
				return beanDefinitionHolders;
		    }

			private void registerRestInfo(String beanName, BeanDefinition beanDefinition,
					ScannedGenericBeanDefinition sgBeanDefinition, String parent, RestAnotationType type) {
				String annotationClassName = null;
				
				if(RestAnotationType.MAPPING == type){
					annotationClassName = annotationMappingClass.getName();
				}else if(RestAnotationType.VALIDATOR == type){
					annotationClassName = annotationValidatorClass.getName();
				}
				Set<MethodMetadata> methodMetadata = sgBeanDefinition.getMetadata().getAnnotatedMethods(annotationClassName);
				Iterator<MethodMetadata> iterator = methodMetadata.iterator();
				while(iterator.hasNext()){
					RestAnotationMetadata info = new RestAnotationMetadata();
					info.setParent(parent);
					MethodMetadata mmd = iterator.next();
					Map<String, Object> valueMap = mmd.getAnnotationAttributes(annotationClassName);
					if(null != valueMap){
						info = RestDataUtils.methodAnnotationAttributesResolver(valueMap, info);
						info.setType(type);
					}
					RestMethodMappingInfo restMethodMappingInfo = RestDataUtils.buildRestMethodMappingInfo(beanDefinition.getBeanClassName(), mmd.getMethodName());
					info.setMethodMappingInfo(restMethodMappingInfo);
					if(RestAnotationType.MAPPING == type){
						RestHandlerMethodMapping.registerRestMethodMapping(info);
					}else if(RestAnotationType.VALIDATOR == type){
						RestHandlerMethodMapping.registerRestValidatorMapping(info);
					}
					RestHandlerMethod restHandlerMethod = RestDataUtils.buildRestHandlerMethod(beanName, mmd.getMethodName(), info.getTable());
					if(null != restMethodMappingInfo && null != restHandlerMethod){
						RestHandlerMethodMapping.registerHandlerMethod(restMethodMappingInfo, restHandlerMethod);
					}
				}
			}

		    
	  }
}
