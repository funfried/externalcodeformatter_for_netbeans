package org.netbeans.eclipse.formatter.xml;

import java.util.HashMap;
import java.util.Map;

/**
 * A class representing the profile XML element in the Eclipse formatter config
 * file, including the kind attribute and Map of setting id and value.
 *
 * @author Matt Blanchette
 */
public class Profile {

    private String kind;
    private Map settings = new HashMap();

    public Profile() {
    }

    public void addSetting(Setting setting) {
        settings.put(setting.getId(), setting.getValue());
    }

    public Map getSettings() {
        return settings;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }
    
}
