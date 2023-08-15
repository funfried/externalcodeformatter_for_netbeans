package de.funfried.netbeans.plugins.external.formatter.eclipse.mechanic;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.netbeans.api.annotations.common.NonNull;

import de.funfried.netbeans.plugins.external.formatter.eclipse.xml.EclipseFormatterUtils;

/**
 * A parser for Workspace Mechanic configuration files.
 */
public class WorkspaceMechanicConfigParser {
	private static final Logger log = Logger.getLogger(WorkspaceMechanicConfigParser.class.getName());

	private static final String MULTI_FILE_SETUP_PREFIX = "/instance/com.google.eclipse.mechanic/mechanicSourceDirectories";

	/**
	 * Parses and returns properties of the given {@code path} into a key value {@link Map}. If an optional
	 * {@code prefix} is specified, only the properties where the key starts with the given {@code prefix}
	 * are returned and the {@code prefix} will be removed from the keys in the returned {@link Map}.
	 *
	 * @param path a configuration file path or URL
	 * @param prefix an optional key prefix
	 *
	 * @return properties of the given {@code path} as a key value {@link Map}
	 *
	 * @throws IOException if there is an issue accessing the given configuration file
	 */
	@NonNull
	public static Map<String, String> readPropertiesFromConfiguration(String path, String prefix) throws IOException {
		Properties properties = createPropertiesFromPath(path);
		if (properties.containsKey(MULTI_FILE_SETUP_PREFIX)) {
			parseAdditionalFiles((String) properties.get(MULTI_FILE_SETUP_PREFIX)).stream().forEach(p -> properties.putAll(p));

			properties.remove(MULTI_FILE_SETUP_PREFIX);
		}

		return EclipseFormatterUtils.toMap(properties, prefix);
	}

	@NonNull
	private static List<Properties> parseAdditionalFiles(String pathStruct) throws IOException {
		// the pathStruct looks as follows:
		// ["/path/to/additional/mechanic/files","/path/to/origin/mechanic/file"]
		pathStruct = StringUtils.trimToEmpty(pathStruct);
		pathStruct = StringUtils.removeStart(pathStruct, "[");
		pathStruct = StringUtils.removeEnd(pathStruct, "]");

		List<Properties> result = new ArrayList<>();
		String[] additionalFilesPaths = StringUtils.split(pathStruct, ",");
		for (String additionalFilesPath : additionalFilesPaths) {
			additionalFilesPath = StringUtils.removeStart(additionalFilesPath, "\"");
			additionalFilesPath = StringUtils.removeEnd(additionalFilesPath, "\"");

			Properties additionalProperties = createPropertiesFromPath(additionalFilesPath);
			result.add(additionalProperties);
		}

		return result;
	}

	@NonNull
	private static Properties createPropertiesFromPath(String path) throws IOException {
		if (StringUtils.isBlank(path)) {
			return new Properties();
		}

		if (UrlValidator.getInstance().isValid(path)) {
			try {
				URL url = new URL(path);

				Properties properties = new Properties();
				properties.load(url.openStream());

				return properties;
			} catch (IOException ex) {
				log.log(Level.WARNING, "Could not read given path as URL, fallback to local file reading", ex);
			}
		}

		return createPropertiesFromFile(new File(path));
	}

	@NonNull
	private static Properties createPropertiesFromFile(File file) throws IOException {
		Properties properties = new Properties();

		if (file != null && file.exists()) {
			if (file.isFile()) {
				try (FileInputStream is = new FileInputStream(file)) {
					properties.load(is);
				}
			} else if (file.isDirectory()) {
				File[] files = file.listFiles();
				for (File f : files) {
					if (file.isDirectory() || (file.isFile() && StringUtils.endsWith(file.getName(), EclipseFormatterUtils.EPF_FILE_EXTENSION))) {
						Properties additionalProperties = createPropertiesFromFile(f);
						properties.putAll(additionalProperties);
					}
				}
			}
		}

		return properties;
	}
}
