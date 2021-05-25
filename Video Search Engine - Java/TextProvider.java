import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;


public class TextProvider {
	void WriteTextToFile(String text, String path) throws IOException {
		FileWriter writer = new FileWriter(path);    
		writer.write(text);    
		writer.close();
	}
	
	String GetTextFromFile(String path) throws Exception {
		String text = this.ReadFileAsString(path);
		return text; 
	}
	
	public String ReadFileAsString(String path) throws Exception { 
		String data = ""; 
		data = new String(Files.readAllBytes(Paths.get(path))); 
		return data; 
    }
}
