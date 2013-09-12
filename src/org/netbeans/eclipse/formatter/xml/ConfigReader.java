package org.netbeans.eclipse.formatter.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import org.apache.commons.digester3.Digester;
import org.openide.filesystems.FileUtil;

import org.xml.sax.SAXException;

/**
 * This class reads a config file for Eclipse code formatter.
 *
 * @author jecki
 * @author Matt Blanchette
 */
public class ConfigReader {

    /**
     * Read from the
     * <code>input</code> and return it's configuration settings as a
     * {@link Map}.
     *
     * @param input
     * @return return {@link Map} with all the configurations read from the
     * config file, or throws an exception if there's a problem reading the
     * input, e.g.: invalid XML.
     * @throws SAXException
     * @throws IOException
     * @throws ConfigReadException
     */
    public Map read(File normalizedFile) throws IOException, SAXException, ConfigReadException {
        Digester digester = new Digester();
        digester.addRuleSet(new RuleSet());
        Object result = null;
        InputStream configInput = FileUtil.toFileObject(normalizedFile).getInputStream();
        try {
            result = digester.parse(configInput);
        } finally {
            try {
                configInput.close();
            } catch (IOException e) {
            }
        }
        if (result == null && !(result instanceof Profiles)) {
            throw new ConfigReadException("No profiles found in config file");
        }
        Profiles profiles = (Profiles) result;
        List list = profiles.getProfiles();
        if (list.size() == 0) {
            throw new ConfigReadException("No profile in config file of kind: "
                    + Profiles.PROFILE_KIND);
        }
        return (Map) list.get(0);
    }
    
}
