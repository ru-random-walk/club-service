package ru.random.walk.club_service.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import ru.random.walk.dto.RegisteredUserInfoEvent;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {
    @Bean
    public KafkaListenerContainerFactory<?> registeredUserInfoEventKafkaListenerContainerFactory(
            KafkaProperties kafkaProperties
    ) {
        ConcurrentKafkaListenerContainerFactory<String, RegisteredUserInfoEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(
                consumerFactoryWithStringKey(kafkaProperties, getRegisteredUserInfoEventJsonDeserializer())
        );
        return factory;
    }

    public <T> ConsumerFactory<String, T> consumerFactoryWithStringKey(
            KafkaProperties kafkaProperties,
            JsonDeserializer<T> deserializer
    ) {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getConsumer().getGroupId());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer);
        return new DefaultKafkaConsumerFactory<>(properties, new StringDeserializer(), deserializer);
    }

    @NotNull
    private static JsonDeserializer<RegisteredUserInfoEvent> getRegisteredUserInfoEventJsonDeserializer() {
        var deserializer = new JsonDeserializer<>(RegisteredUserInfoEvent.class);
        deserializer.addTrustedPackages("*");
        deserializer.setRemoveTypeHeaders(false);
        deserializer.setUseTypeMapperForKey(true);
        return deserializer;
    }
}
