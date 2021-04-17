package sofwareEngineeringProject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.jfree.ui.RefineryUtilities;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class TestRunnerForMdExecution {
	private static HashMap<String, Integer> exceptionList = new HashMap<String, Integer>();

	public static void main(String[] args) {
		try {
			Result result = JUnitCore.runClasses(MarkdownTest.class);

//	      for (Failure failure : result.getFailures()) {
//	         //System.out.println(failure.toString());
//	      }
			String passed = (result.wasSuccessful() ? "YES" : "NO");

			System.out.println("Passed: " + passed);
			System.out.println("Total Test Cases: " + result.getRunCount());
			System.out.print(
					"Successful: " + (result.getRunCount() - result.getFailureCount() - result.getIgnoreCount()));
			System.out.print("  Ignored: " + result.getIgnoreCount());
			System.out.println("  Failed: " + result.getFailureCount());
			System.out.println("Run Time: " + ((double) result.getRunTime() / 1000) + " seconds");

			exceptionList.put("Total_Test_Cases", result.getRunCount());
			exceptionList.put("Successful",
					(result.getRunCount() - result.getFailureCount() - result.getIgnoreCount()));
			exceptionList.put("Ignored", result.getIgnoreCount());
			exceptionList.put("Failed", result.getFailureCount());
			File f1 = new File("tests/Output.properties");
			if (!f1.exists()) {
				// f1.createNewFile();
				throw new Exception("Output File not created");
			}
			// FileWriter fileWritter = new FileWriter(f1.getName(), true);

			BufferedWriter bf = null;

			// create new BufferedWriter for the output file
			bf = new BufferedWriter(new FileWriter(f1, true));

			// iterate map entries
			for (Map.Entry<String, Integer> entry : exceptionList.entrySet()) {

				// put key and value separated by a colon
				bf.write(entry.getKey() + ":" + entry.getValue());
//				System.out.println("test writing into file:");
//				System.out.println(entry.getKey() + ":" + entry.getValue());
				// new line
				bf.newLine();

			}

			bf.flush();
			bf.close();

			System.out.println("reading from file:");

			loadMaps();

		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public static void loadMaps() {
		try {

//			ObjectInputStream ois = new ObjectInputStream(new FileInputStream("tests/Output.properties"));
//			Object readMap = ois.readObject();
//			if (readMap != null && readMap instanceof HashMap) {
//				exceptionList.putAll((HashMap) readMap);
//			}
//			ois.close();
//
//			for (Entry<String, Integer> entry : exceptionList.entrySet()) {
//				System.out.println(entry.getKey() + ":" + entry.getValue());
//			}

			FileReader reader = new FileReader("tests/Output.properties");
			HashMap<String, Integer> exceptionList = new HashMap<String, Integer>();
			Properties p = new Properties();
			p.load(reader);
			Set set = p.entrySet();
			String count=null;
			
			Iterator itr = set.iterator();
			while (itr.hasNext()) {
				
				Map.Entry entry = (Map.Entry) itr.next();
				if(entry.getKey().toString().equalsIgnoreCase("Ignored") || entry.getKey().toString().equalsIgnoreCase("Failed") 
						|| entry.getKey().toString().equalsIgnoreCase("Successful") //|| entry.getKey().toString().equalsIgnoreCase("Syntax_errors")
						)
					exceptionList.put((String) entry.getKey(), Integer.parseInt(entry.getValue().toString()));
				if(entry.getKey().toString().equalsIgnoreCase("Total_Test_Cases"))
					count=entry.getValue().toString();
				System.out.println(entry.getKey() + " = " + entry.getValue());
			}
			String passCountString= "Total Files Executed:"+1600;
			BarChart chart = new BarChart("Execution Results Chart", "Count", "Failures and Errors",exceptionList,passCountString);
			chart.pack();
			RefineryUtilities.centerFrameOnScreen(chart);
			chart.setVisible(true);

		} catch (Exception e) {
			System.out.println(e);
		}
	}

}
