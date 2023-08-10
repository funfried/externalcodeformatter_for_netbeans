package de.funfried.netbeans.plugins.external.formatter.eclipse.mechanic;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.validator.routines.UrlValidator;
import org.netbeans.api.annotations.common.NonNull;

import de.funfried.netbeans.plugins.external.formatter.eclipse.xml.EclipseFormatterUtils;

/**
 * A parser for Workspace Mechanic configuration files.
 */
public class WorkspaceMechanicConfigParser {

	private static final Logger LOG = Logger.getLogger(EclipseFormatterUtils.class.getName());

	private static final String MULTI_FILE_SETUP_PREFIX = "/instance/com.google.eclipse.mechanic/mechanicSourceDirectories";

	private static final String WORKSPACE_MECHANIC_FILE_POSTFIX = ".epf";

	private static final Pattern MULTI_FILE_PATH_PATTERN = Pattern.compile("^\\[\"(\\S*)\",\"\\S*\"\\]$");

	private static final int MATCHER_GROUP = 1;

	/**
	 * Parses and returns properties of the given {@code filePath} into a key value {@link Map}. If an optional
	 * {@code prefix} is specified, only the properties where the key starts with the given {@code prefix}
	 * are returned and the {@code prefix} will be removed from the keys in the returned {@link Map}.
	 *
	 * @param filePath a configuration file path
	 * @param prefix an optional key prefix
	 *
	 * @return properties of the given {@code file} as a key value {@link Map}
	 *
	 * @throws IOException if there is an issue accessing the given configuration file
	 */
	@NonNull
	public static Map<String, String> readPropertiesFromConfigurationFile(String filePath, String prefix) throws IOException {
		Properties properties = createPropertiesFromPath(filePath);
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
		// the latter path can be ignored
		Matcher matcher = MULTI_FILE_PATH_PATTERN.matcher(pathStruct);
		if (!matcher.matches()) {
			LOG.fine(String.format("No matching path to additional Workspace Mechanic files: '%s'.", pathStruct));
			return Collections.emptyList();
		}

		String additionalFilesPath = matcher.group(MATCHER_GROUP);
		Path pathToAdditionalFiles = Paths.get(additionalFilesPath);
		if (!Files.exists(pathToAdditionalFiles)) {
			LOG.fine(String.format("Ignoring path '%s' because it does not exist.", additionalFilesPath));
			return Collections.emptyList();
		}

		List<Properties> result = new ArrayList<>();
		final List<String> additionalFiles = Files.list(pathToAdditionalFiles)
				.filter(Files::isRegularFile)
				.filter(f -> f.toString().endsWith(WORKSPACE_MECHANIC_FILE_POSTFIX))
				.map(Path::toString)
				.collect(Collectors.toList());
		for (String mechanicFile : additionalFiles) {
			Properties additionalProperties = createPropertiesFromPath(mechanicFile);
			result.add(additionalProperties);
		}

		return result;
	}

	@NonNull
	private static Properties createPropertiesFromPath(String path) throws IOException {
		Properties properties = new Properties();

		if (UrlValidator.getInstance().isValid(path)) {
			try {
				URL url = new URL(path);

				properties.load(url.openStream());

				return properties;
			} catch (IOException ex) {
				LOG.log(Level.WARNING, "Could not read given path as URL, fallback to local file reading", ex);
			}
		}

		try (FileInputStream is = new FileInputStream(path)) {
			properties.load(is);
		}

		return properties;
	}
}
