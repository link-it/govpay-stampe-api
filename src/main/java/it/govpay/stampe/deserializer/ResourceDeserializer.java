package it.govpay.stampe.deserializer;

import java.util.Base64;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.deser.std.StdDeserializer;

public class ResourceDeserializer extends StdDeserializer<Resource> {

    private static final long serialVersionUID = 1L;

	public ResourceDeserializer() {
        this(Resource.class);
    }

    public ResourceDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Resource deserialize(JsonParser jp, DeserializationContext ctxt) throws JacksonException {
        JsonNode node = ctxt.readTree(jp);
        String base64Data = node.asString();
        byte[] data = Base64.getDecoder().decode(base64Data);

        return new ByteArrayResource(data);
    }
}
