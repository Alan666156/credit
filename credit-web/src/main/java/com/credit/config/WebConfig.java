package com.credit.config;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.servlet.ErrorPage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import com.github.pagehelper.PageHelper;


/**
 * 
 * @author Alan Fu
 */
@Configuration
public class WebConfig {
	private final static Logger LOGGER = LoggerFactory.getLogger(WebConfig.class);
	
	/**
	 * mybatis分页插件
	 * 
	 * @return
	 */
	@Bean
	public PageHelper pageHelper() {
		LOGGER.info("注册MyBatis分页插件PageHelper");
		PageHelper pageHelper = new PageHelper();
		Properties p = new Properties();
		// 设置为true时，会将RowBounds第一个参数offset当成pageNum页码使用
		p.setProperty("offsetAsPageNum", "true");
		// 设置为true时，使用RowBounds分页会进行count查询
		p.setProperty("rowBoundsWithCount", "true");
		p.setProperty("reasonable", "true");
		pageHelper.setProperties(p);
		return pageHelper;
	}
	
	
	/**
	 * 错误异常页面
	 * @return
	 */
	@Bean
	public EmbeddedServletContainerCustomizer containerCustomizer() {

		return new EmbeddedServletContainerCustomizer() {
			@Override
			public void customize(ConfigurableEmbeddedServletContainer container) {
				ErrorPage error400Page = new ErrorPage(HttpStatus.BAD_REQUEST, "/html/400.html");
				ErrorPage error401Page = new ErrorPage(HttpStatus.UNAUTHORIZED, "/html/401.html");
				ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND, "/html/404.html");
				ErrorPage error405Page = new ErrorPage(HttpStatus.METHOD_NOT_ALLOWED, "/html/405.html");
				ErrorPage error408Page = new ErrorPage(HttpStatus.REQUEST_TIMEOUT, "/html/405.html");
				ErrorPage error500Page = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/html/500.html");
				container.addErrorPages(error400Page, error401Page, error404Page, error405Page, error408Page, error500Page); 
			}
		};
	}

	
}

