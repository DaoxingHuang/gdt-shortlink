package com.gdtc.deeplink.manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.text.SimpleDateFormat;
import java.util.Date;

@EnableAspectJAutoProxy
@SpringBootApplication
public class DeeplinkToolApplication {

	public static void main(String[] args) {
		SpringApplication.run(DeeplinkToolApplication.class, args);
		System.out.println(new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss]").format(new Date()) + " DeepLink-Tool server started!");
	}

}
