
package sense.jsense;

import java.io.IOException;
import org.elasticsearch.common.xcontent.XContentFactory;

public abstract class SensorPub {
    private String name;
    private String description;
    private String valueType;
    private Object value;
    
    public static final String TYPE_STRING = "string";
    public static final String TYPE_DOUBLE = "double";
    public static final String TYPE_INTEGER = "integer";
    public static final String TYPE_GEOLOC = "geoloc";
    public static final String TYPE_BOOLEAN = "boolean";
    
    public static final String FIELD_NAME = "name";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_VALUE_TYPE = "valueType";
    public static final String FIELD_VALUE = "value";

    public SensorPub(String name, String description, String valueType, Object value) {
        this.name = name;
        this.description = description;
        this.valueType = valueType;
        this.value = value;
    }
    
    public String toJSON() throws SerializationException {
        String json;
        try {
            json = XContentFactory.jsonBuilder()
                    .startObject()
                    .field(FIELD_NAME, getName())
                    .field(FIELD_DESCRIPTION, getDescription())
                    .field(FIELD_VALUE_TYPE, getValueType())
                    .field(FIELD_VALUE, getValue())
                    .endObject()
                    .string();
        } catch (IOException ex) {
            throw new SerializationException("Could not serialize SensorPub " + name + " to JSON");
        }
        return json;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "{name: '" + getName() + "', "
                +"description: '" + getDescription() + "', "
                +"valueType: '" + getValueType() + "', "
                +"value: '" + getValue() + "'}";
    }
}
