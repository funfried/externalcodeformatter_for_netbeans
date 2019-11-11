package de.funfried.netbeans.plugins.eclipse.formatter.xml;

import java.util.ArrayList;
import java.util.List;

/**
 * A class representing the profiles XML element in the Eclipse formatter config
 * file, including a List of profile settings.
 *
 * @author Matt Blanchette
 */
public class Profiles {
	public static final String PROFILE_KIND = "CodeFormatterProfile";

	private final List<Profile> profiles = new ArrayList<>();

	public void addProfile(Profile profile) {
		if (PROFILE_KIND.equals(profile.getKind())) {
			profiles.add(profile);
		}
	}

	public List<Profile> getProfiles() {
		return profiles;
	}
}
