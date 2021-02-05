package DBMS.attributetype;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class AttributeTypeDeserializer implements JsonDeserializer<AttributeType> {

    @Override
    public AttributeType deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        AttributeType attributeType;
        String attributeTypeText = jsonElement.getAsString().toLowerCase();
        switch (attributeTypeText) {
            case "int":
                attributeType = AttributeType.INT;
                break;
            case "string":
                attributeType = AttributeType.STRING;
                break;
            case "double":
                attributeType = AttributeType.DOUBLE;
                break;
            default:
                attributeType = null;
        }
        return attributeType;
    }
}
