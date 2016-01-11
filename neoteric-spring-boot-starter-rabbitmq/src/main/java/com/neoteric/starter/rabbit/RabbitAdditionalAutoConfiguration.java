package com.neoteric.starter.rabbit;

import com.rabbitmq.client.Channel;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.apache.log4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.NeverRetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

import static com.neoteric.starter.rabbit.TracedRabbitTemplate.REQUEST_ID;

@Configuration
@ConditionalOnClass({RabbitTemplate.class, Channel.class})
@AutoConfigureAfter(RabbitAutoConfiguration.class)
public class RabbitAdditionalAutoConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(RabbitAdditionalAutoConfiguration.class);
    public static final String ERROR_EXCHANGE = "DLE";

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    @Qualifier("rabbitListenerContainerFactory")
    SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory;

    @PostConstruct
    public void setAdviceChain() {
        rabbitListenerContainerFactory.setAdviceChain(retryOperations(), tracingOnListener());
    }

    private Advice retryOperations() {
        return RetryInterceptorBuilder.stateless()
                .retryOperations(defaultRetryTemplate())
                .recoverer(new RepublishMessageRecoverer(rabbitTemplate, ERROR_EXCHANGE))
                .build();
    }

    private RetryTemplate defaultRetryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.registerListener(new LogOnRetryListener());
        retryTemplate.setBackOffPolicy(defaultBackOffPolicy());
        retryTemplate.setRetryPolicy(defaultRetryPolicy());
        return retryTemplate;
    }

    private RetryPolicy defaultRetryPolicy() {
        SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy();
        simpleRetryPolicy.setMaxAttempts(3);

        Map<Class<? extends Throwable>, RetryPolicy> dontRetryExceptions = new HashMap<>();
        dontRetryExceptions.put(AmqpRejectAndDontRequeueException.class, new NeverRetryPolicy());

        return new ListenerExceptionClassifierRetryPolicy(dontRetryExceptions, simpleRetryPolicy);
    }

    private ExponentialBackOffPolicy defaultBackOffPolicy() {
        ExponentialBackOffPolicy policy = new ExponentialBackOffPolicy();
        policy.setInitialInterval(1000);
        policy.setMultiplier(1);
        policy.setMaxInterval(500000);
        return policy;
    }

    private Advice tracingOnListener() {
        MethodInterceptor methodInterceptor = invocation -> {
            Message message = (Message) invocation.getArguments()[1];
            setRequestIdOnMdc(message);
            Object result;
            try {
                result = invocation.proceed();
            } finally {
                MDC.remove(REQUEST_ID);
            }
            return result;
        };
        return methodInterceptor;
    }

    public static void setRequestIdOnMdc(Message message) {
        String requestId = String.valueOf(message.getMessageProperties().getHeaders().get(REQUEST_ID));
        if (StringUtils.isEmpty(requestId)) {
            LOG.warn("No Request ID found in Message");
            return;
        }
        if (!StringUtils.isEmpty(MDC.get(REQUEST_ID))) {
            LOG.warn("Request ID already found in MDC: {}, overriding with: ", MDC.get(REQUEST_ID), requestId);
        }
        MDC.put(REQUEST_ID, requestId);
    }

    @Bean
    public TracedRabbitTemplate tracedRabbitTemplate() {
        return new TracedRabbitTemplate(this.connectionFactory);
    }

}
