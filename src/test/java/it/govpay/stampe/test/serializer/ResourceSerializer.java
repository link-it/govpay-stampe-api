package it.govpay.stampe.test.serializer;

import java.io.IOException;

import org.springframework.core.io.ByteArrayResource;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class ResourceSerializer extends StdSerializer<ByteArrayResource> {

	private static final long serialVersionUID = 1L;

	public ResourceSerializer() {
		this(null);
	}

	public ResourceSerializer(Class<ByteArrayResource> t) {
		super(t);
	}

	@Override
	public void serialize(ByteArrayResource value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		gen.writeBinary(value.getInputStream(), (int) value.contentLength());
	}
}