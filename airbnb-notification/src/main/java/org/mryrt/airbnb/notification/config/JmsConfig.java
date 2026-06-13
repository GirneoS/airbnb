package org.mryrt.airbnb.notification.config;

import jakarta.jms.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.UserCredentialsConnectionFactoryAdapter;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.jms.support.destination.JndiDestinationResolver;
import org.springframework.jndi.JndiTemplate;

import javax.naming.NamingException;
import java.util.Map;

@Configuration
@EnableJms
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
        // sender side sets _type=emailMessage, we map that to EmailMessage.class
        converter.setTypeIdPropertyName("_type");
        converter.setTypeIdMappings(Map.of(
                "emailMessage", org.mryrt.airbnb.notification.dto.EmailMessage.class
        ));
        return converter;
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(
            ConnectionFactory jmsConnectionFactory,
            MessageConverter jmsMessageConverter) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(jmsConnectionFactory);
        factory.setMessageConverter(jmsMessageConverter);
        // resolves @JmsListener destination names via JNDI
        factory.setDestinationResolver(new JndiDestinationResolver());
        factory.setConcurrency("1-3");
        return factory;
    }
}
