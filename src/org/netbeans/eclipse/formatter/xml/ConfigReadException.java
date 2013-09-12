package org.netbeans.eclipse.formatter.xml;

/**
 * An exception thrown when there is an error reading settings from the code
 * formatter profile of an Eclipse formatter config file.
 * 
 * @author Matt Blanchette
 */
public class ConfigReadException extends Exception {

        private static final long serialVersionUID = 1L;

        public ConfigReadException(String message) {
                super(message);
        }

}
