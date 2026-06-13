package org.mryrt.airbnb.notification.config;

import jakarta.jms.ConnectionFactory;
import org.mryrt.airbnb.notification.NotificationPayload;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.connection.UserCredentialsConnectionFactoryAdapter;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.jndi.JndiTemplate;

import javax.naming.NamingException;
import java.util.Map;

@Configuration
public class JmsConfig {

    @Value("${airbnb.jms.jndi.connection-factory:java:/ConnectionFactory}")
    private String connectionFactoryJndi;

    @Value("${airbnb.jms.username}")
    private String jmsUsername;

    @Value("${airbnb.jms.password}")
    private String jmsPassword;

    @Bean
    public ConnectionFactory jmsConnectionFactory() throws NamingException {
        ConnectionFactory raw = new JndiTemplate().lookup(connectionFactoryJndi, ConnectionFactory.class);
        UserCredentialsConnectionFactoryAdapter adapter = new UserCredentialsConnectionFactoryAdapter();
        adapter.setTargetConnectionFactory(raw);
        adapter.setUsername(jmsUsername);
        adapter.setPassword(jmsPassword);
        return adapter;
    }

    @Bean
    public MessageConverter jmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        // must match the mapping declared in airbnb-notification's JmsConfig
        converter.setTypeIdMappings(Map.of("emailMessage", NotificationPayload.class));
        return converter;
    }

    @Bean
    public JmsTemplate jmsTemplate(ConnectionFactory jmsConnectionFactory,
                                   MessageConverter jmsMessageConverter) {
        JmsTemplate template = new JmsTemplate(jmsConnectionFactory);
        template.setMessageConverter(jmsMessageConverter);
        return template;
    }
}
