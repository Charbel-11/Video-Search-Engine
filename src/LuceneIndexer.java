import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.search.similarities.LMSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;


@SuppressWarnings("deprecation")
public class LuceneIndexer {
	Directory memoryIndex = new RAMDirectory();
	String Model;
	public LuceneIndexer(String _model) {
		Model = _model;
	}
	
	void AddDocument(String title, String txt, IndexWriter writer) throws IOException {
		Document document = new Document();
		document.add(new TextField("title", title, Store.YES));
		document.add(new TextField("body", txt, Store.YES));
		writer.addDocument(document);
	}
	
	void ProcessDirectory(String inputBasePath, HashMap<String, Integer> videoDirIndex) throws Exception {
		IndexWriterConfig indexWriterConfig =  this.getConfig(); 
		IndexWriter writer = new IndexWriter(memoryIndex, indexWriterConfig);
		
		List<File> listOfFiles = new ArrayList<File>(Arrays.asList(new File(inputBasePath).listFiles()));
		List<String> fileNames = new ArrayList<String>();
		
		for(int i = 0; i < listOfFiles.size(); i++) {
			File file = listOfFiles.get(i);
			if (file.isDirectory()) { 
				File[] subFiles = new File(file.getAbsolutePath()).listFiles();
				listOfFiles.addAll(Arrays.asList(subFiles));
				continue; 
			}

			String fileName = file.getName();
			String fullPath = file.getAbsolutePath();
						
			if (fileName.length() > 4 && fileName.substring(fileName.length() - 4).equals(".mp4")) {
				int idx = videoDirIndex.get(fullPath);
				String curPath = Helper.cachePath + "\\" + Integer.toString(idx);
				File[] curTextFiles = new File(curPath).listFiles();
				
				for(File textFile : curTextFiles) {
					if (textFile.isFile()) { fileNames.add(curPath + "\\" + textFile.getName()); }
				}
			}
			else {
		        fileNames.add(fullPath);
		    }
		}
		
		TextProvider textProvider = new TextProvider();
		for(String fileName: fileNames) {
			String text = textProvider.GetTextFromFile(fileName);
			this.AddDocument(fileName, text, writer);
		}
		
		writer.close();
	}
	
	IndexWriterConfig getConfig() {
		CharArraySet stopWords = EnglishAnalyzer.getDefaultStopSet();
        Analyzer analyzer = new EnglishAnalyzer(stopWords);
		IndexWriterConfig indexWriterConfig =  new IndexWriterConfig(analyzer);
		
		if(Model.compareTo("BM25") == 0) {
			indexWriterConfig.setSimilarity(new BM25Similarity());
		}
		else if(Model.compareTo("LM") == 0) {
			indexWriterConfig.setSimilarity(new LMDirichletSimilarity());
		}
		else if(Model.compareTo("VSM") == 0){ 
			indexWriterConfig.setSimilarity(new ClassicSimilarity());
		}
		
		return indexWriterConfig;
	}
	
	List<queryRes> ProcessQuery(String queryText, int k) throws ParseException, IOException {
		IndexWriterConfig indexWriterConfig =  this.getConfig();
		
		Query query = new QueryParser("body", indexWriterConfig.getAnalyzer()).parse(queryText);
	    
	    IndexReader indexReader = DirectoryReader.open(memoryIndex);
	    IndexSearcher searcher = new IndexSearcher(indexReader);
	    searcher.setSimilarity(indexWriterConfig.getSimilarity());
	    
	    TopDocs topDocs = searcher.search(query, k);
	    List<queryRes> scoreDocs = new ArrayList<>();
	    
	    for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
	        scoreDocs.add(new queryRes(searcher.doc(scoreDoc.doc), scoreDoc.score));
	    }
	    
	    return scoreDocs;
	}
}

class queryRes{
	Document doc;
	float score;
	
	public queryRes(Document _doc, float _score) {
		doc = _doc;
		score = _score;
	}
}