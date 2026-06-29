package it.govpay.stampe;

import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;

import com.fasterxml.jackson.annotation.JsonInclude;

import tools.jackson.databind.module.SimpleModule;

import it.govpay.stampe.deserializer.ResourceDeserializer;

@SpringBootApplication
public class Application extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Value("${stampe.time-zone:Europe/Rome}")
	String timeZone;

	/**
	 * Personalizza il JsonMapper primario di Spring Boot (Jackson 3): impostazione del
	 * timezone e registrazione del deserializer custom per i campi di tipo Resource.
	 * Usare il customizer del builder, invece di esporre un bean ObjectMapper, garantisce
	 * che la configurazione sia applicata al mapper effettivamente usato dai message
	 * converter HTTP.
	 */
	@Bean
	public JsonMapperBuilderCustomizer jsonCustomizer() {
		SimpleModule resourceModule = new SimpleModule();
		resourceModule.addDeserializer(Resource.class, new ResourceDeserializer());

		return builder -> builder
				.defaultTimeZone(TimeZone.getTimeZone(this.timeZone))
				// Jackson 3 di default omette i null: ripristina l'inclusione dei null nelle risposte
				.changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(JsonInclude.Include.ALWAYS))
				.addModule(resourceModule);
	}
}
