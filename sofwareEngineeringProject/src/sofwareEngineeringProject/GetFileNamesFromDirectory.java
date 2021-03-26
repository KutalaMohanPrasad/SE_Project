package sofwareEngineeringProject;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GetFileNamesFromDirectory {
	
	public static void listOfFiles(File dirPath){
	      File filesList[] = dirPath.listFiles();
	      for(File file : filesList) {
	         if(file.isFile()) {
	            System.out.println("File path: "+file.getName());
	         } else {
	            listOfFiles(file);
	         }
	      }
	   }
	  
	public static void main(String[] args) throws IOException {
		File file = new File("C:\\Users\\rupa\\Downloads\\antmark-master\\tests");
	      //List of all files and directories
	      listOfFiles(file);
		
		//Set set=listFilesUsingDirectoryStream("C:\\Users\\rupa\\Downloads\\antmark-master\\tests\\own");
		//System.out.println(set);
	}

}
