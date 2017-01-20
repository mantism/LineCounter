package com.mantism;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import org.apache.commons.io.FilenameUtils;

import com.mantism.storage.DBWrapper;

public class LineCount {
	static LinkedList<String> fileQueue = new LinkedList<String>();
	/*
	 * Takes in two arguments, filepath or directory path and bdb path
	 */
	public static void main(String[] args) {
		
		
		if (args.length < 2) {
			throw new RuntimeException("Invalid number of arguments: Please provide"
					+ " paths for the file or directory for which you wish to"
					+ " count lines for and the path for your bdb store");
		}
		
		String filePath = args[0];
		String dbPath = args[1];
		ArrayList<String> forbidden = new ArrayList<String>();
		/*
		 * sets up array of forbidden file path elements to prevent from counting files
		 * that I did not write myself
		 */
		if (args.length > 2) {
			for (int i = 2; i < args.length; i++) {
				forbidden.add(args[i]);
			}
			
		}
		
		DBWrapper db = new DBWrapper(dbPath);
		
		/*
		 * if the first command line argument is print, will print out all the 
		 * languages and their line counts
		 */
		if (args[0].equals("print"))  {
			printCounts(db);
			return;
		}
		
		//queues up the files based off of the inputs
		if (enqueueFiles(filePath, forbidden)) {
			String curr;
			/*
			 * loops through the queue and counts the number of lines in each
			 * of the queues files
			 */
			while (!fileQueue.isEmpty()) {
				curr = fileQueue.removeFirst();
				try {
					int numLines = countLines(new File(curr));
					String language = FilenameUtils.getExtension(curr);
					db.updateCount(language, numLines);
					System.out.println(language + " " + curr + " " + numLines);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			printCounts(db);
		} else {
			throw new RuntimeException("Did not successfully queue files");
		}
		
	}
	
	/*method to enqueue all the files belonging to a specified path/directory
	 * returns true if successful, false otherwise
	 */
	public static boolean enqueueFiles(String path, ArrayList<String> forbidden) {
		File file = new File(path);
		if (file.exists()) {
			if (file.isFile()) {
				//checks for language types that i've programmed in
				String extension = FilenameUtils.getExtension(path);
				if (extension.equals("java") || extension.equals("cpp") || 
						extension.equals("js") || extension.equals("html") ||
						extension.equals("css") || extension.equals("c") ||
						extension.equals("tex") || extension.equals("sql") || extension.equals("ml")) {
					
						fileQueue.add(path);
				}
				return true;
				
			} else if (file.isDirectory()) {
				
				File[] subfiles = file.listFiles();
				
				/*
				 * handles if theres an empty directory, will return true since
				 * we don't want it to stop there
				 */
				if (subfiles == null || subfiles.length <= 0) {
					return true;
				}
				//recursively calls method on all the files in the directory
				for (int i = 0; i < subfiles.length; i++) {
					File curr = subfiles[i];
					String currPath = curr.getAbsolutePath();
					if (forbidden.size() > 0) {
						/*
						 * checks if the file path is forbidden as specified by
						 * user, if it isn't then it will enqueue
						 */
						if (isForbidden(currPath, forbidden)) {
							enqueueFiles(curr.getAbsolutePath(), forbidden);
						}
					} else {
						//enqueues if there is no forbidden list specified
						enqueueFiles(curr.getAbsolutePath(), forbidden);
					}
					
				}
				return true;
			} else {
				
				return false;
			}
		}
		
		return false;
	}
	/*
	 * method that loops through the forbidden list and returns false if the path
	 * contains anything in the list, true otherwise
	 */
	public static boolean isForbidden(String path, ArrayList<String> forbidden) {
		for (String s : forbidden) {
			if (path.contains(s)) {
				return false;
			}
		}
		return true;
	}
	
	/*
	 * Opens and reads the input file to count number of lines
	 */
	public static int countLines(File f) throws FileNotFoundException {
		int count = 0;
		BufferedReader br = new BufferedReader(new FileReader(f));
		String line;
		try {
			while ((line = br.readLine()) != null) {
				if (line.startsWith("//") || line.startsWith("/*") 
						|| line.startsWith("*") || line.startsWith("*/")) {
					continue;
				} else if (line.startsWith("(*")){
					while (!(line.contains("*)"))) {
						line = br.readLine();
					}
				} else if (line.isEmpty()){
					count++;
				}
			} 
			br.close();
			return count;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return 0;
	}
	
	/*
	 * Prints all the languages and their respective counts from the bdbstore
	 */
	public static void printCounts(DBWrapper db) {
		HashMap<String, Integer> counts = db.getAllCounts();
		
		for (String s : counts.keySet()) {
			System.out.println(s + " : " + counts.get(s));
		}
	}
}


