package com.sgtech.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@Profile("local")
public class AutoCreateConfig {

    @Value("${spring.kafka.topic}")
    private String topic;

    @Value("${topics.retry:library-events.RETRY}")
    private String retryTopic;

    @Value("${topics.dlt:library-events.DLT}")
    private String deadLetterTopic;


    @Bean
    public NewTopic libraryEvents(){
        return TopicBuilder.name(topic)
                .partitions(3)
                .replicas(1)
                .build();
    }


    @Bean
    public NewTopic retryTopic(){
        return TopicBuilder.name(retryTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic deadLetterTopic(){
        return TopicBuilder.name(deadLetterTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
