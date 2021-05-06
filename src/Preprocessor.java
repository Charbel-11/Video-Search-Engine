import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JLabel;
import javax.swing.JProgressBar;

public class Preprocessor {
	public static String cachePath = "C:\\Users\\PC\\Documents\\VideoSearchEngineCache";
	static String pathPython = "C:\\Users\\PC\\Documents\\pySTT2\\AudioTranscribe.py";
	
	//startTime is in seconds
	public static void openVideo(String path, int startTime) throws IOException {
		String[] cmd = new String[] {
				"C:\\Program Files (x86)\\VideoLAN\\VLC\\vlc.exe", 
				path, 
				"--start-time=" + startTime};
		
		Runtime r = Runtime.getRuntime();
		Process p = r.exec(cmd);
	}
	
	public static void preprocessDirectory(String dir, Main frame) throws Exception  {
		System.out.println("Started Conversion...");
		
		String [] cmd = new String[3];
		cmd[0] = "python";
		cmd[1] = pathPython;
		cmd[2] = dir;
		Runtime r = Runtime.getRuntime();
		Process p = r.exec(cmd);
		
		BufferedReader in = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		String line;
	    while((line = in.readLine()) != null){
	    	System.out.println(line);
	    }
	    
	    System.out.println("Speech to Text Done");
	    frame.openSearchPage();
	}
}
