package com.hubicsync;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class conflictFile {

	public conflictFile(){
		
	}
	public void write(String s){
		PrintWriter out;
		try {
			out =new PrintWriter(new FileOutputStream(
					    new File("/sdcard/hubic/conflict.txt"), 
					    true ));
			
			
			out.println(s);
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
