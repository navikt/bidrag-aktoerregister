package no.nav.bidrag.aktoerregister.config;

import java.util.Set;
import no.nav.bidrag.aktoerregister.converter.AktoerTilAktoerDTOConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ConverterConfig implements WebMvcConfigurer {

  @Override
  public void addFormatters(FormatterRegistry formatterRegistry) {
    formatterRegistry.addConverter(new AktoerTilAktoerDTOConverter());
  }

  @Bean
  public ConversionService conversionService(ConversionServiceFactoryBean factory) {
    return factory.getObject();
  }

  @Bean
  public ConversionServiceFactoryBean conversionServiceFactoryBean(Set<Converter<?, ?>> converters) {
    ConversionServiceFactoryBean factory = new ConversionServiceFactoryBean();
    factory.setConverters(converters);
    return factory;
  }

}
