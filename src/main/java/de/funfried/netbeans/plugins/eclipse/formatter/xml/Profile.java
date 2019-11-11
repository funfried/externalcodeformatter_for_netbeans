package de.funfried.netbeans.plugins.eclipse.formatter.xml;

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

	private String version;

	private String name;

	private final Map<String, String> settings = new HashMap<>();

	public void addSetting(Setting setting) {
		settings.put(setting.getId(), setting.getValue());
	}

	public Map<String, String> getSettings() {
		return settings;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
