package nl.tue.simulatorgui.core;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Enumeration;

public class Properties {

	private static final String PROPERTIES_FILE = "spa.properties";

	private String lastFolder;

	public String getLastFolder() {
		return lastFolder;
	}

	public void setLastFolder(String lastFolder) {
		this.lastFolder = lastFolder;
	}

	/**
	 * Saves the properties to a Java properties file with the name in the PROPERTIES_FILE field in the current working folder.
	 * The method is reflective, so it is not necessary to adapt it when adding/removing a property.
	 */
	public void saveProperties(){
		OutputStream output = null;
		try {
			output = new FileOutputStream(PROPERTIES_FILE);

			java.util.Properties prop = new java.util.Properties();
			for (Field property: Properties.class.getDeclaredFields()){
				property.setAccessible(true);
				if (!Modifier.isStatic(property.getModifiers()) && (property.get(this) != null)){
					prop.setProperty(property.getName(), (String) property.get(this));
				}
			}
			prop.store(output, null);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Loads the properties from a Java properties file with the name in the PROPERTIES_FILE field in the current working folder. 
	 * The method is reflective, so it is not necessary to adapt it when adding/removing a property.
	 */
	public void loadProperties(){
		InputStream input = null;

		try {
			input = new FileInputStream(PROPERTIES_FILE);

			java.util.Properties prop = new java.util.Properties();
			prop.load(input);
			@SuppressWarnings("rawtypes")
			Enumeration propertyNames = prop.propertyNames();
			while (propertyNames.hasMoreElements()){
				String propertyName = (String) propertyNames.nextElement();
				String propertyValue = prop.getProperty(propertyName);
				try {
					String setterName = "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1, propertyName.length());
					Properties.class.getMethod(setterName, String.class).invoke(this, propertyValue);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException ex) {
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
