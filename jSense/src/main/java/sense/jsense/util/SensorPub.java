
package sense.jsense.util;

import java.util.Date;

public abstract class SensorPub {
    private String name;
    private String description;
    private String valueType;
    private Object value;
    private Date time;
    
    public static final String TYPE_STRING = "string";
    public static final String TYPE_DOUBLE = "double";
    public static final String TYPE_INTEGER = "integer";
    public static final String TYPE_GEOLOC = "geoloc";
    public static final String TYPE_BOOLEAN = "boolean";
    
    public static final String FIELD_NAME = "name";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_VALUE_TYPE = "valueType";
    public static final String FIELD_VALUE = "value";
    public static final String FIELD_TIME = "updatedAt";

    public SensorPub(String name, String description, String valueType, Object value, Date time) {
        this.name = name;
        this.description = description;
        this.valueType = valueType;
        this.time = time;
        
        switch(valueType) {
            case TYPE_INTEGER:
                if(value instanceof String)
                    this.value = new Integer((String)value);
                else
                    this.value = (Integer) value;
                break;
            case TYPE_STRING:
                this.value = (String) value;
                break;
            case TYPE_BOOLEAN:
                this.value = (Boolean) value;
                break;
            case TYPE_DOUBLE:
                this.value = (Double) value;
                break;
            case TYPE_GEOLOC:
                this.value = value.toString();
                break;
            default: 
                this.value = value;
        }
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

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "{name: '" + getName() + "', "
                +"description: '" + getDescription() + "', "
                +"valueType: '" + getValueType() + "', "
                +"value: '" + getValue() + "'}";
    }
    
    public Date getTime() {
        return time;
    }
}
