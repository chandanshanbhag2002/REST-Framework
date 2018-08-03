package TestRun;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;
import org.testng.collections.Lists;

import haplous.rest.init.XMLSuite;

public class RunSuite {
	public static TestNG testng = new TestNG();
	public static void main(String args[]) throws IOException {
		ZipFiles zipFiles = new ZipFiles();
		String HomeDir = System.getProperty("user.dir");
		File logDir = new File(HomeDir + File.separator + "log");
		File ExtentDir = new File(HomeDir + File.separator + "extent-output");
		File ScriptDir = new File(HomeDir + File.separator + "TestScripts");
		File SuiteDir = new File(HomeDir + File.separator + "TestSuites");
		File IUPDir = new File(HomeDir + File.separator + "TestScripts" + File.separator + "IUP" +File.separator+"Import");
		if (!ScriptDir.exists()) {
			System.out.println("TestScripts folder doesn't exist.");
		}

		if (!SuiteDir.exists()) {
			System.out.println("TestSuite  folder doesn't exist.");
		}

		if (SuiteDir.exists() && ScriptDir.exists()) {
			
			File[] IUPdirectories = IUPDir.listFiles(File::isDirectory);
			System.out.println("IUP files to Zip:"+IUPdirectories.length);
			if(IUPdirectories.length>0){
				for (File file : IUPdirectories) {
					System.out.println(file);
					new ZipFiles().zipDirectory(file, file.toString()+".zip");
				}
			}
			
			Properties prop = new Properties();
			InputStream input = null;
			input = new FileInputStream("Properties" + File.separator + "config.properties");
			prop.load(input);

			String suite = prop.getProperty("TestSuites");
			String[] suitearray = suite.split(",");

			if (logDir.exists()) {
				FileUtils.forceDelete(new File(HomeDir + File.separator + "log"));
			} else {
				logDir.mkdir();
			}

			if (ExtentDir.exists()) {
				FileUtils.forceDelete(new File(HomeDir + File.separator + "extent-output"));
			}

			
			List<String> suites = Lists.newArrayList();
			
			for(int i=0;i<suitearray.length;i++){
				suites.add(System.getProperty("user.dir") + File.separator +"TestSuites"+ File.separator + suitearray[i]);
				System.out.println("Suites added:"+suitearray[i]);
			}
			
			testng.setTestSuites(suites);
			testng.run();
		}

	}

}
