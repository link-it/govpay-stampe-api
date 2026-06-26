package it.govpay.stampe.test.serializer;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.core.io.ByteArrayResource;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.cfg.EnumFeature;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;

public class ObjectMapperUtils {

	public static ObjectMapper createObjectMapper() {
		SimpleDateFormat sdf = new SimpleDateFormat(DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.getPattern());
		sdf.setTimeZone(TimeZone.getTimeZone("Europe/Rome"));
		sdf.setLenient(false);

		// Serializer custom per i campi di tipo ByteArrayResource (PDF in base64)
		SimpleModule module = new SimpleModule();
		module.addSerializer(ByteArrayResource.class, new ResourceSerializer());

		// In Jackson 3 l'ObjectMapper e' immutabile: ogni configurazione va impostata sul builder.
		// Il supporto a java.time e' integrato in jackson-databind (niente JavaTimeModule esplicito).
		return JsonMapper.builder()
				.addModule(module)
				.enable(EnumFeature.READ_ENUMS_USING_TO_STRING)
				.disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
				.enable(EnumFeature.WRITE_ENUMS_USING_TO_STRING)
				.enable(DateTimeFeature.WRITE_DATES_WITH_ZONE_ID)
				.defaultDateFormat(sdf)
				.build();
	}
}
