/**
 * Create module - Create module inside the ioChem-BD software.
 * Copyright © 2014 ioChem-BD (contact@iochem-bd.org)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.ICIQ.eChempad.web.definitions;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CustomProperties {

	private static final Logger log = LogManager.getLogger(CustomProperties.class.getName());

	private static Properties props = new Properties();

	static {
		reloadProperties();
	}

	public static void reloadProperties() {
	       String propertiesFilePath = null;
		try {
			propertiesFilePath = ""; // TODO FileService.getCreatePath(File.separatorChar + Constants.CREATE_PROPERTIES_FILE);
			props = new Properties();
			props.load(new FileInputStream(propertiesFilePath));
		} catch (Exception e) {
			props = new Properties();
			log.error("Error raised loading resources.properties file located on :" + propertiesFilePath);
			log.error(e.getMessage());
		}
	}

	public static String getProperty(String name) {
		return props.getProperty(name);
	}

	public static String getProperty(String name, String defaultValue) {
	    return props.getProperty(name, defaultValue);	    
	}

	public static Long getLongProperty(String name, Long defaultValue) {
	    String value = CustomProperties.getProperty(name);
	    if(value != null && !value.isEmpty()){
            try {
                return Long.parseLong(value.trim());
            }catch(NumberFormatException e) {
                log.error("Invalid configuration parameter value " + name);
            }
        }
        return defaultValue;
	}

    public static String[] getMultivaluedProperty(String name) {
        String value = CustomProperties.getProperty(name,"");
        if(value.isEmpty())
            return new String[0];
        return value.split("[:, ;]+");
    }
}
