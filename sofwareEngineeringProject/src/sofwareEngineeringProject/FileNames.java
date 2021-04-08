package sofwareEngineeringProject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.jfree.ui.RefineryUtilities;

import sample.*;
import sample.FileNameRuleParser.RContext;


public class FileNames  {
	
	
	public static int errorCount=0;
	public static int untitledCount=0;
	public static int copyFileNames=0;
	public static int numberOfFilenamesExceedLength=0;
	private static final String MD_EXT = ".md";
	static ArrayList<String> textFilesList=new ArrayList<String>();
	public static int  runParser(String fileName) throws IOException {
		
		try {
			@SuppressWarnings("deprecation")
			ANTLRInputStream input = new ANTLRInputStream(fileName);

			FileNameRuleLexer lexer = new FileNameRuleLexer(input);

			CommonTokenStream tokens = new CommonTokenStream(lexer);

			FileNameRuleParser parser = new FileNameRuleParser(tokens);
			RContext tree = parser.r(); // begin parsing at rule 'r'
			//System.out.println(tree.exception);
			if(parser.getNumberOfSyntaxErrors() > 0)
				errorCount+=1;
			//System.out.println(tree.toStringTree(parser)); // print LISP-style tree
			return errorCount;
			
		} catch (RecognitionException e) {
			  
		      System.out.println(e);
		      return 0;
		}
		
		
	}
	
	
	public static ArrayList<String> listOfFiles(File dirPath){
	      File filesList[] = dirPath.listFiles();
	      
	      for(File file : filesList) {
	         if(file.isFile()) {
	        	 String lowercaseName = file.getName().toLowerCase();
		            if (lowercaseName.endsWith(MD_EXT)) {
		            	textFilesList.add(lowercaseName);
		            	System.out.println("File path: "+file.getName());
		            } 	        	 
	            
	         } else {
	            listOfFiles(file);
	         }
	      }
	      return textFilesList;
	   }

	public static void main(String[] directory) throws Exception {

//		File directoryPath = new File(directory[0]);//E:\\LHU\text books\Second Sem\SE\project\RMarkdownFiles
//		// List of all files and directories
//		
//		FilenameFilter textFilefilter = new FilenameFilter(){
//	         public boolean accept(File dir, String name) {
//	            String lowercaseName = name.toLowerCase();
//	            if (lowercaseName.endsWith(MD_EXT)) {
//	               return true;
//	            } else {
//	               return false;
//	            }
//	         }
//	      };
//	      
//	      String textFilesList[] = directoryPath.list(textFilefilter);
//	      //System.out.println("List of the text files in the specified directory:");
		File directoryPath = new File("tests");
		ArrayList<String> textFilesList=listOfFiles(directoryPath);
		ArrayList<Integer> fileLengths= new ArrayList<Integer>();
	      for(String fileName : textFilesList) {
	    	  fileLengths.add(fileName.length());
	    	  if(fileName.length()>=256)
	    		  numberOfFilenamesExceedLength+=1;
	    	  if(fileName.toLowerCase().contains("untitled"))
	    		  untitledCount+=1;
	    	  if(fileName.toLowerCase().contains("copy"))
	    		  copyFileNames+=1;
	    	  runParser(fileName.toLowerCase());
	      }
	      
	     // runParser("019_bevezetes.rmd");
	      System.out.println("Number Of Filenames Exceeds Length of 256 characters: "+numberOfFilenamesExceedLength);
	      System.out.println("Untitled Files: "+untitledCount);
	      System.out.println("Number Of File Titles having 'COPY' String: "+copyFileNames);
	      System.out.println("Mismatched Files: "+errorCount);
	      System.out.println("Matched Files : "+(textFilesList.size()-errorCount));
	      System.out.println("Total Markdown Files:"+textFilesList.size());
	      
	      //plotting
	        final BoxAndWhisker demo = new BoxAndWhisker("File Names Lengths",fileLengths);
	        demo.pack();
	        RefineryUtilities.centerFrameOnScreen(demo);
	        demo.setVisible(true);

		
	}

}
