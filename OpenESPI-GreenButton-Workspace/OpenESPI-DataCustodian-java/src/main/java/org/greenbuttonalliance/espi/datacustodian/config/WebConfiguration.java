/*
 *
 *    Copyright (c) 2018-2025 Green Button Alliance, Inc.
 *
 *    Portions (c) 2013-2018 EnergyOS.org
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */

package org.greenbuttonalliance.espi.datacustodian.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.config.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * Web configuration for the OpenESPI Data Custodian Resource Server.
 * 
 * This configuration replaces the legacy Spring MVC XML configuration and provides
 * modern Spring Boot 3.5 web configuration with ESPI-specific customizations.
 * 
 * Key Features:
 * - JAXB XML marshalling for ESPI Atom feeds
 * - JSON serialization with proper date/time handling
 * - Content negotiation for XML and JSON responses
 * - WebClient for external HTTP communication
 * - CORS and resource handling configuration
 */
@Configuration
@EnableWebMvc
public class WebConfiguration implements WebMvcConfigurer {

    @Value("${espi.xml.pretty-print:true}")
    private boolean xmlPrettyPrint;

    @Value("${espi.xml.include-namespaces:true}")
    private boolean includeNamespaces;

    /**
     * Configure HTTP message converters for XML and JSON.
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // Add JAXB XML converter for ESPI Atom feeds
        converters.add(createXmlConverter());
        
        // Add JSON converter with proper date handling
        converters.add(createJsonConverter());
    }

    /**
     * Configure content negotiation for ESPI endpoints.
     */
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
            .favorParameter(false)
            .favorPathExtension(false)
            .ignoreAcceptHeader(false)
            .useRegisteredExtensionsOnly(false)
            .defaultContentType(MediaType.APPLICATION_JSON)
            .mediaType("xml", MediaType.APPLICATION_XML)
            .mediaType("json", MediaType.APPLICATION_JSON)
            .mediaType("atom", MediaType.APPLICATION_ATOM_XML);
    }

    /**
     * JAXB2 Marshaller for ESPI XML processing.
     */
    @Bean
    public Jaxb2Marshaller jaxb2Marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        
        // Set context path for ESPI domain objects
        marshaller.setPackagesToScan(
            "org.greenbuttonalliance.espi.common.domain",
            "org.greenbuttonalliance.espi.common.models.atom"
        );
        
        // Configure marshaller properties
        marshaller.setMarshallerProperties(java.util.Map.of(
            Marshaller.JAXB_FORMATTED_OUTPUT, xmlPrettyPrint,
            Marshaller.JAXB_ENCODING, "UTF-8"
        ));
        
        return marshaller;
    }

    /**
     * Create XML message converter with JAXB marshalling.
     */
    private HttpMessageConverter<?> createXmlConverter() {
        MarshallingHttpMessageConverter xmlConverter = new MarshallingHttpMessageConverter();
        xmlConverter.setMarshaller(jaxb2Marshaller());
        xmlConverter.setUnmarshaller(jaxb2Marshaller());
        
        // Support both XML and Atom media types
        xmlConverter.setSupportedMediaTypes(Arrays.asList(
            MediaType.APPLICATION_XML,
            MediaType.APPLICATION_ATOM_XML,
            MediaType.TEXT_XML
        ));
        
        return xmlConverter;
    }

    /**
     * Create JSON message converter with proper date handling.
     */
    private HttpMessageConverter<?> createJsonConverter() {
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        
        jsonConverter.setObjectMapper(objectMapper);
        jsonConverter.setSupportedMediaTypes(Arrays.asList(
            MediaType.APPLICATION_JSON,
            MediaType.APPLICATION_JSON_UTF8
        ));
        
        return jsonConverter;
    }

    /**
     * WebClient for external HTTP communication.
     */
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
            .codecs(configurer -> configurer
                .defaultCodecs()
                .maxInMemorySize(1024 * 1024) // 1MB buffer
            )
            .build();
    }

    /**
     * Configure static resource handling.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
            .addResourceLocations("classpath:/static/")
            .setCachePeriod(3600);
        
        registry.addResourceHandler("/images/**")
            .addResourceLocations("classpath:/static/images/")
            .setCachePeriod(86400);
    }

    /**
     * Configure CORS mappings.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/espi/**")
            .allowedOriginPatterns("http://localhost:*", "https://*.greenbuttonalliance.org")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600);
    }

    /**
     * JAXBContext for manual ESPI XML processing.
     */
    @Bean
    public JAXBContext espiJaxbContext() throws JAXBException {
        return JAXBContext.newInstance(
            "org.greenbuttonalliance.espi.common.domain:" +
            "org.greenbuttonalliance.espi.common.models.atom"
        );
    }

    /**
     * JAXB Marshaller for ESPI XML export.
     */
    @Bean
    public Marshaller espiMarshaller(JAXBContext espiJaxbContext) throws JAXBException {
        Marshaller marshaller = espiJaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, xmlPrettyPrint);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        
        if (includeNamespaces) {
            marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, 
                "http://naesb.org/espi espi.xsd");
        }
        
        return marshaller;
    }

    /**
     * JAXB Unmarshaller for ESPI XML import.
     */
    @Bean 
    public Unmarshaller espiUnmarshaller(JAXBContext espiJaxbContext) throws JAXBException {
        return espiJaxbContext.createUnmarshaller();
    }
}