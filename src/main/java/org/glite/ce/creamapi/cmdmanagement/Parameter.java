package org.glite.ce.creamapi.cmdmanagement;

public class Parameter {
    private String name;
    private Object value;

    public Parameter() { 
        this(null, null);
    }

    public Parameter(String name, Object value) throws IllegalArgumentException { 
        if (name == null) {
            throw new IllegalArgumentException("name not specified!");
        }
        if (value == null) {
            throw new IllegalArgumentException("value not specified!");
        }
        this.name = name;
        this.value = value;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }
    
    public String getValueAsString() {
        return (String)value;
    }
    
    public void setValue(Object value) {
        this.value = value;
    }

    public boolean equals(Object obj) {
        if( obj==null || !(obj instanceof Parameter) )
            return false;

        return name.equals(((Parameter)obj).getName())
            && value.equals(((Parameter)obj).getValue());
    }
}
