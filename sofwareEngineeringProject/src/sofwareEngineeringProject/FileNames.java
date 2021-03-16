package sofwareEngineeringProject;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import sample.FileNameRuleLexer;
import sample.FileNameRuleParser;
import sample.FileNameRuleParser.RContext;

public class FileNames  {
	public static int errorCount=0;
	public static void runParser(String fileName) throws IOException {
		
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
			
		} catch (RecognitionException e) {
			  
		      System.out.println(e);	
		}
		
		
	}

	public static void main(String[] args) throws Exception {

		File directoryPath = new File(args[0]);//E:\\LHU\text books\Second Sem\SE\project\RMarkdownFiles
		// List of all files and directories
		
		FilenameFilter textFilefilter = new FilenameFilter(){
	         public boolean accept(File dir, String name) {
	            String lowercaseName = name.toLowerCase();
	            if (lowercaseName.endsWith(".rmd")) {
	               return true;
	            } else {
	               return false;
	            }
	         }
	      };
	      
	      String textFilesList[] = directoryPath.list(textFilefilter);
	      //System.out.println("List of the text files in the specified directory:");
	      for(String fileName : textFilesList) {
	    	  runParser(fileName.toLowerCase());
	      }
	      
	     // runParser("019_bevezetes.rmd");
	      
	      System.out.println("Mismatched Files: "+errorCount);
	      System.out.println("Matched Files : "+(textFilesList.length-errorCount));
	      

		
	}

}
