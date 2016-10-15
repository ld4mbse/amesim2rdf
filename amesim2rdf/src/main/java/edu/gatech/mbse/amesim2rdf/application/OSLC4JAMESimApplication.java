
package edu.gatech.mbse.amesim2rdf.application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;




















import org.eclipse.lyo.oslc4j.core.exception.OslcCoreApplicationException;
import org.eclipse.lyo.oslc4j.core.model.OslcConstants;
import org.eclipse.lyo.oslc4j.core.model.ServiceProviderCatalog;
import org.eclipse.lyo.oslc4j.provider.jena.JenaProvidersRegistry;



















import org.eclipse.lyo.oslc4j.provider.json4j.Json4JProvidersRegistry;

import amesim2rdf.cli.AMESim2RDF;
import edu.gatech.mbsec.adapter.amesim.resources.AMESimCircuit;
import edu.gatech.mbsec.adapter.amesim.resources.AMESimComponent;
import edu.gatech.mbsec.adapter.amesim.resources.AMESimGlobalParameter;
import edu.gatech.mbsec.adapter.amesim.resources.AMESimLine;
import edu.gatech.mbsec.adapter.amesim.resources.AMESimParameter;
import edu.gatech.mbsec.adapter.amesim.resources.AMESimPort;
import edu.gatech.mbsec.adapter.amesim.resources.Constants;




public class OSLC4JAMESimApplication {

	public static String hostName = "localhost";
	public static String contextPath = "oslc4jamesim";

	public static String amesimEcoreLocation = null;
	public static String amesimModelPaths = null;
	
	public static String portNumber = null;


	public static String warConfigFilePath = "../oslc4jamesim configuration/config.properties";
	public static String localConfigFilePath = "oslc4jamesim configuration/config.properties";
	public static String configFilePath = null;
	


	public static void main(String[] args) {

		loadPropertiesFile();

		AMESim2RDF.rdfFileLocation = "C:/Users/rb16964/git/amesim2rdf/amesim2rdf/generated.rdf";
		

		readDataFirstTime();

		AMESim2RDF.outputMode = "rdfxml";
		AMESimManager.writeRDF();

	}



	

	private static void loadPropertiesFile() {
		Properties prop = new Properties();
		InputStream input = null;

		try {
			// loading properties file
			// input = new FileInputStream("./configuration/config.properties");
			input = new FileInputStream(warConfigFilePath); // for war file
			configFilePath = warConfigFilePath;
		} catch (FileNotFoundException e) {
			try {
				input = new FileInputStream(localConfigFilePath);
				configFilePath = localConfigFilePath;
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} // for war file
		}

		// load property file content and convert backslashes into forward
		// slashes
		String str;
		if (input != null) {
			try {
				str = readFile(configFilePath, Charset.defaultCharset());
				prop.load(new StringReader(str.replace("\\", "/")));

				// get the property value
				
				String amesimModelsDirectoryFromUser = prop
						.getProperty("amesimModelsDirectory");
				String pythonScriptsDirectoryFromUser = prop
						.getProperty("pythonScriptsDirectory");
				
				

				
				amesimModelPaths = amesimModelsDirectoryFromUser;
				
				
				portNumber = prop.getProperty("portNumber");
				
				
				
				
				

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {

				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}

	}
	
	private static void loadPropertiesFile2() {
		Properties prop = new Properties();
		InputStream input = null;

		try {
			// loading properties file
			// input = new FileInputStream("./configuration/config.properties");
			input = new FileInputStream(warConfigFilePath); // for war file
			configFilePath = warConfigFilePath;
		} catch (FileNotFoundException e) {
			try {
				input = new FileInputStream(localConfigFilePath);
				configFilePath = localConfigFilePath;
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} // for war file
		}

		// load property file content and convert backslashes into forward
		// slashes
		String str;
		if (input != null) {
			try {
				str = readFile(configFilePath, Charset.defaultCharset());
				prop.load(new StringReader(str.replace("\\", "/")));

				// get the property value
				
//				String amesimModelsDirectoryFromUser = prop
//						.getProperty("amesimModelsDirectory");
//				amesimModelPaths = amesimModelsDirectoryFromUser;
				
				amesimModelPaths = AMESim2RDF.amesimFileLocations;
				
				
				portNumber = prop.getProperty("portNumber");
				
				
				
				
				

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {

				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}

	}

	static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return encoding.decode(ByteBuffer.wrap(encoded)).toString();
	}

	public static void readDataFirstTime() {
		Thread thread = new Thread() {
			public void start() {
				
				reloadAMESimModels();								
			}
		};
		thread.start();
		try {
			thread.join();
			System.out.println("AMESim files read");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

	public static void reloadAMESimModels() {
		
		AMESimManager.amesimWorkingDirectory = null; // to reload models
		AMESimManager.loadAMESimModels();	
		
	}
	
	public static void run() {

		loadPropertiesFile2();

		readDataFirstTime();

		AMESimManager.writeRDF();
		
		

	}
}

