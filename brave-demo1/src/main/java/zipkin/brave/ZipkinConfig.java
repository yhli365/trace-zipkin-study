package zipkin.brave;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.kristofa.brave.Brave;
import com.github.kristofa.brave.Sampler;
import com.github.kristofa.brave.http.DefaultSpanNameProvider;
import com.github.kristofa.brave.okhttp.BraveOkHttpRequestResponseInterceptor;
import com.github.kristofa.brave.servlet.BraveServletFilter;

import okhttp3.OkHttpClient;
import zipkin.reporter.AsyncReporter;
import zipkin.reporter.Reporter;
import zipkin.reporter.urlconnection.URLConnectionSender;

@Configuration
public class ZipkinConfig {
	private static Logger log = LoggerFactory.getLogger(ZipkinConfig.class);

	@Autowired
	private ZipkinProperties properties;

	/**
	 * SpanCollector 配置收集器
	 * 
	 * @return
	 */
	@Bean
	public Reporter<zipkin.Span> spanReporter() {
		// https://github.com/openzipkin/zipkin-reporter-java
		Reporter<zipkin.Span> reporter;
		// reporter = Reporter.CONSOLE;

		reporter = AsyncReporter.builder(URLConnectionSender.create(properties.getUrl())).build();

		return reporter;
	}

	/**
	 * Brave
	 * 各工具类的封装,其中builder.traceSampler(Sampler.ALWAYS_SAMPLE)设置采样比率，0-1之间的百分比
	 * 
	 * @param spanCollector
	 * @return
	 */
	@Bean
	public Brave brave(Reporter<zipkin.Span> spanReporter) {
		String serviceName = properties.getServiceName();
		log.info("com.zipkin.serviceName = " + serviceName);
		Brave.Builder builder = new Brave.Builder(serviceName); // 指定state
		builder.reporter(spanReporter);
		builder.traceSampler(Sampler.ALWAYS_SAMPLE);
		Brave brave = builder.build();
		return brave;
	}

	/**
	 * BraveServletFilter
	 * 作为拦截器，需要serverRequestInterceptor,serverResponseInterceptor 分别完成sr和ss操作
	 * 
	 * @param brave
	 * @return
	 */
	@Bean
	public BraveServletFilter braveServletFilter(Brave brave) {
		BraveServletFilter filter = new BraveServletFilter(brave.serverRequestInterceptor(),
				brave.serverResponseInterceptor(), new DefaultSpanNameProvider());
		return filter;
	}

	/**
	 * OkHttpClient 添加拦截器，需要clientRequestInterceptor,clientResponseInterceptor
	 * 分别完成cs和cr操作,该功能由
	 * brave中的brave-okhttp模块提供，同样的道理如果需要记录数据库的延迟只要在数据库操作前后完成cs和cr即可，
	 * 当然brave提供其封装。
	 * 
	 * @param brave
	 * @return
	 */
	@Bean
	public OkHttpClient okHttpClient(Brave brave) {
		OkHttpClient client = new OkHttpClient.Builder()
				.addInterceptor(new BraveOkHttpRequestResponseInterceptor(brave.clientRequestInterceptor(),
						brave.clientResponseInterceptor(), new DefaultSpanNameProvider()))
				.build();
		return client;
	}

}
