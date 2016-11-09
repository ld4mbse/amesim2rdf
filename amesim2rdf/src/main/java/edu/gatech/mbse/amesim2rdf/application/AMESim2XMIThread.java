package edu.gatech.mbse.amesim2rdf.application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ProcessBuilder.Redirect;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import amesim2rdf.cli.AMESim2RDF;

public class AMESim2XMIThread extends Thread {

	public static boolean loadedFromJar = false; // = java or jar
	// User-defined location of AMESim models

	// variable capturing batch file location,
	// http://stackoverflow.com/questions/14286457/using-parameters-in-batch-files-at-dos-command-line
	// Passing argument to batch file,
	// http://stackoverflow.com/questions/14286457/using-parameters-in-batch-files-at-dos-command-line
	// Passing argument from command line to python script,
	// http://www.bnikolic.co.uk/blog/python-running-cline.html
	public void run() {

		// Execute Python script from the command line
		try {

			// finding out if program has been launched from jar
			URL classURL = AMESim2XMIThread.class.getResource("AMESim2XMIThread.class");
			if (classURL.toString().startsWith("jar")) {
				loadedFromJar = true;
			}
			// String[] cmdarray = new String[1];
			// cmdarray[0] = "AMEPython importAMESIM.py"; // (AMEPython process
			// doesn't end by
			// himself!!)
			// cmdarray[1] = "exit";

			// With the AMESim installation comes a python.bat file (loads
			// python.exe with all AMESim libraries by setting Python-specific
			// environment variables)
			// Process process = Runtime.getRuntime().exec("AMEPython
			// importAMESIM.py", null,
			// new File(AMESimManager.amesimPythonScriptFolder));

			// always delete XMI file
			if (new File("python/amesimWorkDir.xmi").exists()) {
				new File("python/amesimWorkDir.xmi").delete();
			}

			String[] amesimModelsPathArray = OSLC4JAMESimApplication.amesimModelPaths.split(",");

			String argumentString = "";

			int i = 0;
			for (String amesimModelsPath : amesimModelsPathArray) {

				if (i > 0) {
					argumentString = argumentString + " ";
				}
				argumentString = argumentString + "\"" + amesimModelsPath.replace(" ", "") + "\"";
				i++;
			}
			;

			// only necessary in jar mode
			// only have importAMESIM2.py script as external resource
			// copy it from the jar and place it outside the jar
			String folderContainingJarPath = Paths.get(".").toAbsolutePath().normalize().toString();
			if (loadedFromJar) {
				
				InputStream inputStream = Thread.currentThread().getContextClassLoader()
						.getResourceAsStream("python/importAMESIM2.py");
				// write the inputStream to a FileOutputStream
				OutputStream outputStream = new FileOutputStream(
						new File(folderContainingJarPath + "/importAMESIM2.py"));
				int read = 0;
				byte[] bytes = new byte[1024];

				while ((read = inputStream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, read);
				}
				outputStream.close();
			}

			ProcessBuilder pb = new ProcessBuilder("AMEPython", "importAMESIM2.py", argumentString);

			if(!loadedFromJar){
				pb.directory(new File("python"));
			}
			else{
				pb.directory(new File(folderContainingJarPath));
			}
			
			
			File log = new File("log");
			pb.redirectErrorStream(true);
			pb.redirectOutput(Redirect.appendTo(log));
			Process process = pb.start();
			assert pb.redirectInput() == Redirect.PIPE;
			assert pb.redirectOutput().file() == log;
			assert process.getInputStream().read() == -1;

			// new
			// File("C:/Users/rb16964/git/oslc4jamesim.wink/oslc4jamesim-wink/python"));
			// .exec("python importAMESIM.py", null, new
			// File(amesimPythonScriptFolder));

			process.waitFor();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
