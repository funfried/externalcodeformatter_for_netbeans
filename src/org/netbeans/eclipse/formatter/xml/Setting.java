package org.netbeans.eclipse.formatter.xml;

/**
 * A class representing the setting XML element in the Eclipse formatter config
 * file, including the id and value attributes.
 *
 * @author Matt Blanchette
 */
public class Setting {

    private String id;
    private String value;

    public Setting() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
