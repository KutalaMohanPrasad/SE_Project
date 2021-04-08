package sofwareEngineeringProject;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import junit.framework.TestResult;
import sample.MarkdownLexer;
import sample.MarkdownParser;

@RunWith(value = Parameterized.class)
public class ProgrammingFeaturesValidation {

	private static final String TESTS_DIR = "tests";
	private static final String MD_EXT = ".md";
	private static final String RMD_EXT = ".Rmd";
	private static final String HTML_EXT = ".html";
	private static int errorCount = 0;
	private static int syntaxErrorCount = 0;

	private String filename;
	private String markdownInput;
	private String htmlOutput;
	private static HashMap<String, Integer> exceptionList = new HashMap<String, Integer>();

	private static String readFileAsString(String filePath) throws IOException {
		StringBuffer fileData = new StringBuffer();
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
		}
		reader.close();
		return fileData.toString();
	}

	@Parameters(name = "{0}")
	public static Collection<String[]> getTestParameters() {
		ArrayList<String[]> inoutPairs = new ArrayList<String[]>();
		try {
			File templateDir = new File(TESTS_DIR);
			if (!templateDir.isDirectory()) {
				return null;
			}
			addTestsFrom(templateDir, inoutPairs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Collections.sort(inoutPairs, new Comparator<String[]>() {
			@Override
			public int compare(String[] item1, String[] item2) {
				return item1[0].compareTo(item2[0]);
			}
		});
		return inoutPairs;
	}

	private static void addTestsFrom(File templateDir, ArrayList<String[]> inoutPairs) throws Exception {
		File[] files = templateDir.listFiles();
		for (int f = 0; f < files.length; f++) {
			if (files[f].isDirectory() && !files[f].getName().endsWith(".disabled")) {
				addTestsFrom(files[f], inoutPairs);
			} else if (files[f].getName().endsWith(MD_EXT) || files[f].getName().endsWith(".text")
					|| files[f].getName().endsWith(RMD_EXT)) {
				String filename = files[f].getName();
				/*
				 * Used to test only a smaller subset of tests. if
				 * (!filename.contains("blockq")) continue;
				 */
				String path = files[f].getAbsolutePath();
				String dir = path.substring(0, path.lastIndexOf("\\"));
				String ext = filename.substring(filename.lastIndexOf("."));
				// Try .html .
				String htmlFilename = dir + "/" + filename.substring(0, filename.length() - ext.length()) + HTML_EXT;
				File htmlFile = new File(htmlFilename);
				if (!htmlFile.isFile()) {
					// Try .out .
					htmlFilename = dir + "/" + filename.substring(0, filename.length() - ext.length()) + ".out";
					htmlFile = new File(htmlFilename);
					if (!htmlFile.isFile()) {
						System.err.println(htmlFilename + " not found.");
						continue;
					}
				}
				String[] test = new String[3];
				test[0] = files[f].getName();
				// DOC: see note in Lexer setex heading about why we need this.
				test[1] = readFileAsString(files[f].getAbsolutePath()) + "\n";
				test[2] = readFileAsString(htmlFile.getAbsolutePath());
				inoutPairs.add(test);
			}
		}
	}

	public ProgrammingFeaturesValidation(String filename, String input, String output) {
		this.filename = filename;
		this.markdownInput = input;
		this.htmlOutput = output;
	}

	@Test
	public void testInOut() {
		try {
			System.out.println("Test " + filename);
			ANTLRInputStream input = new ANTLRInputStream(markdownInput);
			MarkdownLexer lexer = new MarkdownLexer(input);
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			MarkdownParser parser = new MarkdownParser(tokens);
			SyntaxErrorListener listener = new SyntaxErrorListener();

			ParseTree tree = parser.document();
			parser.addErrorListener(listener);
			// System.out.println(listener.getSyntaxErrors());
			// System.out.println("my
			// own:"+parser.getErrorListenerDispatch().getClass().getName());
			// System.out.println(errorCount);
			errorCount += parser.getErrorListeners().size();
			if (parser.getNumberOfSyntaxErrors() > 0) {
				syntaxErrorCount += parser.getNumberOfSyntaxErrors();
				throw new Exception("Syntax error in test " + filename);
			}
			//
			// System.out.println(syntaxErrorCount);

			ParseTreeWalker walker = new ParseTreeWalker();
			MarkdownTranslator translator = new MarkdownTranslator(tree, parser);
			//walker.walk(translator, tree);
			if (translator.isWithReferences()) {
				translator.clearHtml();
				walker.walk(translator, tree);
			}
			parser.addErrorListener(new BaseErrorListener() {
				@Override
				public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
						int charPositionInLine, String msg, RecognitionException e) {
					throw new IllegalStateException("failed to parse at line " + line + " due to " + msg, e);

				}
			});
			assertEquals(htmlOutput, translator.getHtml());
//		TestResult result=new TestResult();
//		Test(result);

		} catch (Exception e) {
			exceptionList.put(e.getClass().getName(), 1);
			System.out.println("Exception: " + e);
		}
	}

	public static void Test(TestResult result) {
		int e = result.errorCount();
		int f = result.failureCount();
		System.out.println(e + "Syntax_errors:" + syntaxErrorCount);
	}

	@AfterClass
	public static void printFailedTestsCount() {
		try {
			// Properties properties=new Properties();

//			for(Entry<String, Integer> entry: exceptionList.entrySet())
//			{
//				properties.put(entry.getKey(),entry.getValue());
////			}
//			properties.put("Syntax_errors:",syntaxErrorCount);
//			properties.store(new FileOutputStream("tests/Output.properties"), null);
//			exceptionList.put("Syntax_errors:",syntaxErrorCount);
//			ObjectOutputStream oos = new ObjectOutputStream (new FileOutputStream("tests/Output.properties"));
//	        oos.writeObject(exceptionList);
//	        oos.close();
			exceptionList.put("Syntax_errors",syntaxErrorCount);
			File file = new File("tests/Output_PFV.properties");

			BufferedWriter bf = null;
			

			// create new BufferedWriter for the output file
			bf = new BufferedWriter(new FileWriter(file));

			// iterate map entries
			for (Map.Entry<String, Integer> entry : exceptionList.entrySet()) {

				// put key and value separated by a colon
				bf.write(entry.getKey() + ":" + entry.getValue());
//				System.out.println("writing into file:");
//				System.out.println(entry.getKey() + ":" + entry.getValue());
				// new line
				bf.newLine();

			}
			bf.flush();
			bf.close();
		} catch (Exception e) {
			System.out.println(e);
		}

	}

}
