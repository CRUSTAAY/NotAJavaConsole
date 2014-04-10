package com.krowcraft.NotAJavaCommandLine;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;



public class MainApplet extends Applet implements ActionListener, Runnable {

	
	/*
	 * Class wide variable declaration
	 * 		Variable textbox contains output of console 
	 * 		Variable consolecommandfield contains user input for console
	 * 		Variable serialVersionUID contains random unique identification long
	 * 		Variable Width and Height contains dimensions of textbox
	 * 		Variable Location contains current console directory
	 * 		Variable p contains object used to start external applications with args and its output
	 * 		Variable textthread contains Thread used to separate variable p's output from the main thread preventing pausing while waiting for output from p
	 */
	private static final long serialVersionUID = 670630357259275877L;
	
	private TextArea textbox;
	private TextField consolecommandfield;
	private String lastString;
	private int Width = 120;
	private int Height = 35;
	
	private String Location = (System.getenv("windir") +"\\system32\\");
	private java.lang.Process p;
	private Thread textthread;
	
	
	/*
	 * 		Method WritetoConsole(String, Boolean) outputs String argument to textbox, Boolean determines if a new line is used or not 
	 * 		Method WritetoConsole(String) outputs String argument to textbox, this method, unlike the former, always outputs to a new line and does not take a boolean
	 * 
	 * */
	public void WritetoConsole(String string, boolean newline){
		if(newline){
			textbox.append("\n" + string);
		} else {
			textbox.append(string); 	
		}
	}
	public void WritetoConsole(String string){
		textbox.append("\n" + string);
	}
	
	
	
	
	/*
	 *		Method init() runs on applet startup 
	 * 		Method setEditable() prevents the user from entering text/commands into the console output instead of the console input
	 *
	 */
	public void init(){
		textbox = new TextArea(Height,Width);
		consolecommandfield = new TextField(Width);
		add(textbox);
		add(consolecommandfield);
		consolecommandfield.addActionListener(this);
		textbox.setEditable(false);
		textthread = new Thread(this);
	}
	

	@Override
	public void actionPerformed(ActionEvent e){
		lastString = consolecommandfield.getText();
		consolecommandfield.setText(null);
		WritetoConsole(Location + " >>" + lastString);
		String cmdcomponents[] = lastString.split("\\s+");
		
		if(cmdcomponents[0].equalsIgnoreCase("cd")){				//Command: CD		(Change Directory)
			String newLoc = lastString.replaceFirst("cd ", "");
			File testvalid = new File(newLoc);

			if(testvalid.isAbsolute() == true){
				Location = newLoc;
				System.out.println(newLoc);	
			}
			
			
		} else if(cmdcomponents[0].equalsIgnoreCase("dir")){		//Command: DIR		(List Directory Content)
			File folder = new File(Location);
			File[] listOfFiles = folder.listFiles();

			    for (int i = 0; i < listOfFiles.length; i++) {
			      if (listOfFiles[i].isFile()) {
			    	  WritetoConsole("/" + listOfFiles[i].getName());
			      } else if (listOfFiles[i].isDirectory()) {
			    	  WritetoConsole("/" + listOfFiles[i].getName() + "/");
			      }
			    }

			    
		 } else if(cmdcomponents[0].equalsIgnoreCase("echo")){		//Command: ECHO		(Echo a string)
			 String EchoText = lastString.replaceFirst("echo ", "");
			 System.out.println(lastString);
			 WritetoConsole(EchoText);
		      
		
			 
			 
		/* Command test code
		  
		  } else if(cmdcomponents[0].equalsIgnoreCase("COMMAND")){
		  
		 */
		} else {													//Not an internal Command
		
			try{
				File testvalid = new File(lastString);
				String exec;
				System.out.println(testvalid);
				if(testvalid.isFile()){
					exec = lastString;
				} else{
					exec = Location + lastString;
				}

	        	p = Runtime.getRuntime().exec(exec);
	        	textthread.start();
	        	
	        	
	        	
	    	} catch (Exception E) {
	    		WritetoConsole(E.getMessage() + " or is not a valid command");   
	    	}
		}
		
	}
	@Override
	public void run() {
    	String line;
		BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
    	try {
			while ((line = input.readLine()) != null) { //Display lines of text from command output
				WritetoConsole(line);
			}
	    input.close();
		} catch (IOException e) {e.printStackTrace();}

	
		
	}

}
