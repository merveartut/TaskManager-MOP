package com.merveartut.task_manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.merveartut.task_manager")
public class TaskManagerApplication {

	public static void main(String[] args) {
		try {
			ConfigurableApplicationContext context = SpringApplication.run(TaskManagerApplication.class, args);
			System.out.println("Application started successfully!");
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Application failed to start: " + e.getMessage());
		}
	}

}
