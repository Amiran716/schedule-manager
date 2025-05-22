package main.com.example.schedule_manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ScheduleManagerApplication {

	public static void main(String[] args) {
		var ctx = SpringApplication.run(ScheduleManagerApplication.class, args);

		// Проверка бинов (можно удалить после теста)
		System.out.println("ScheduleRepository bean: " +
				ctx.getBean("scheduleRepository"));
		System.out.println("GroupRepository bean: " +
				ctx.getBean("groupRepository"));
	}

}
