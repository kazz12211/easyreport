package jp.tsubakicraft.easyreport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan("jp.tsubakicraft.easyreport")
public class EasyreportApplication {

	public static void main(String[] args) {
		SpringApplication.run(EasyreportApplication.class, args);
	}
}
