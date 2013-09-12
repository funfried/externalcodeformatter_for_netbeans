package org.netbeans.eclipse.formatter.xml;

import java.util.ArrayList;
import java.util.List;

/**
 * A class representing the profiles XML element in the Eclipse formatter config
 * file, including a List of profile setting Maps with id and value.
 *
 * @author Matt Blanchette
 */
public class Profiles {

    public static final String PROFILE_KIND = "CodeFormatterProfile";
    private List profiles = new ArrayList();

    public Profiles() {
    }

    public void addProfile(Profile profile) {
        if (PROFILE_KIND.equals(profile.getKind())) {
            profiles.add(profile.getSettings());
        }
    }

    public List getProfiles() {
        return profiles;
    }

}
