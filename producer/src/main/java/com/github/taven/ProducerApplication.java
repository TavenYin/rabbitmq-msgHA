package com.github.taven;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@RestController
public class ProducerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProducerApplication.class, args);
	}

	@Autowired
	private MsgService msgService;
	@Autowired
	private RabbitTemplate rabbitTemplate;

	@GetMapping("produce")
	public Object produce(String value) {
		Map jsonObject = new HashMap<>();
		jsonObject.put("biz_key", value);
		Msg msg = msgService.create(jsonObject);

		rabbitTemplate.convertAndSend("user_exchange", "user_routingKey", msg, new CorrelationData(msg.getId().toString()));

		return "消息已发送";
	}

}
