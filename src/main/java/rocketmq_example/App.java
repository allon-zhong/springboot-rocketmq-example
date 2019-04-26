package rocketmq_example;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zjs.mic.other.EnableZjsAllAnnotation;

/**
 * 
 * @author zyg
 *
 */
@MapperScan("rocketmq_example.mqandmysqltraction")
@EnableTransactionManagement 
@EnableZjsAllAnnotation
@EnableHystrix
@SpringBootApplication
public class App {
	protected final static Logger logger = LoggerFactory.getLogger(App.class);

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
	
	/*@Bean
    public Object testBean(PlatformTransactionManager platformTransactionManager){
		logger.info("-----------------" + platformTransactionManager.getClass().getName());
        return new Object();
    }*/
}