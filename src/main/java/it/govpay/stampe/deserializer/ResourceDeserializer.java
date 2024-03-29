package it.govpay.stampe.deserializer;

import java.io.IOException;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class ResourceDeserializer extends StdDeserializer<Resource> {

    private static final long serialVersionUID = 1L;

	public ResourceDeserializer() {
        this(null);
    }

    public ResourceDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Resource deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        String base64Data = node.asText(); 
        byte[] data = base64Data.getBytes();
        return new ByteArrayResource(data);
    }
}
