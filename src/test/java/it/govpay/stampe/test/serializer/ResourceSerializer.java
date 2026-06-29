package it.govpay.stampe.test.serializer;

import org.springframework.core.io.ByteArrayResource;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ser.std.StdSerializer;

public class ResourceSerializer extends StdSerializer<ByteArrayResource> {

	private static final long serialVersionUID = 1L;

	public ResourceSerializer() {
		this(ByteArrayResource.class);
	}

	public ResourceSerializer(Class<ByteArrayResource> t) {
		super(t);
	}

	@Override
	public void serialize(ByteArrayResource value, JsonGenerator gen, SerializationContext provider) throws JacksonException {
		gen.writeBinary(value.getByteArray());
	}
}
