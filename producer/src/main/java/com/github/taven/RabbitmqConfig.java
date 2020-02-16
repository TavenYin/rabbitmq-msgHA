package com.github.taven;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
public class RabbitmqConfig {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MsgService msgService;

    @Configuration
    public class RabbitTemplateWrapper {
        public RabbitTemplateWrapper(RabbitTemplate rabbitTemplate) {
            rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
                // 是否成功发送到Exchange
                if (correlationData != null && correlationData.getId() != null) {
                    Integer id = Integer.valueOf(correlationData.getId());
                    msgService.producerAck(id, ack);

                    if (ack) {
                        log.info("id:[{}], publish success", correlationData.getId());
                    } else {
                        log.error("id:[{}], publish failed, cause: [{}]", correlationData.getId(), cause);
                    }
                }

            });

            rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
                // 消息无法转到队列，会走这个回调
                String correlationId = message.getMessageProperties().getCorrelationId();
                if (!StringUtils.isEmpty(correlationId)) {
                    msgService.producerAck(Integer.valueOf(correlationId), false);
                    log.error("id:{}, replyCode:{}, replyText:{}, message:{}", correlationId, replyCode, replyText, message);
                }

            });
        }
    }
}
