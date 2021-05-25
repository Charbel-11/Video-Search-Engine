import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JButton;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JScrollPane;
import java.awt.GridLayout;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

public class SearchPage extends JFrame {
	private JPanel contentPane;
	private JTextField searchQuery;
	private LuceneIndexer indexProvider;
	private HashMap<String, Integer> videoDirIndex;
	private ArrayList<String> idxToVideoDir;
	private JComboBox resRange;
	private JPanel resultsPanel;
	private List<SearchResult> searchResults;
	
	public SearchPage(String pathDir) throws Exception  {
		initializeLucene(pathDir);
		
		setTitle("Video Search Engine");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 749, 625);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblTitle = new JLabel("Video Search Engine");
		lblTitle.setBounds(143, 11, 446, 77);
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitle.setFont(new Font("Tahoma", Font.BOLD, 35));
		contentPane.add(lblTitle);
		
		searchQuery = new JTextField();
		searchQuery.setBounds(58, 99, 382, 39);
		searchQuery.setFont(new Font("Tahoma", Font.PLAIN, 13));
		contentPane.add(searchQuery);
		searchQuery.setColumns(10);
			
		resultsPanel = new JPanel();
		resultsPanel.setBorder(null);
		resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
		
		JButton btnSearch = new JButton("Search");
		btnSearch.setBounds(513, 99, 166, 39);
		btnSearch.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e)  {
				try {
					handleQuery(searchQuery.getText());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		btnSearch.setBackground(Color.LIGHT_GRAY);
		btnSearch.setFont(new Font("Tahoma", Font.PLAIN, 15));
		contentPane.add(btnSearch);
		
		JScrollPane scrollPane = new JScrollPane(resultsPanel);
		scrollPane.setBounds(25, 169, 685, 373);
		contentPane.add(scrollPane);
		
		resRange = new JComboBox();
		resRange.setBounds(312, 555, 112, 22);
		resRange.setVisible(false);
		resRange.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                Object selected = resRange.getSelectedItem();
                String rangeStr = selected.toString();
                int startRange = 0;
                int j = 0;
                while(rangeStr.charAt(j) != '-') {
                	startRange*=10; startRange += rangeStr.charAt(j) - '0';
                	j++;
                }
                displayResults(startRange);
            }
        });
		contentPane.add(resRange);

	}
	
	private void addResultToPanel(SearchResult res) {
		JPanel resultBorder = new JPanel();
		resultBorder.setLayout(new GridLayout(4, 1));

		String leftMargin = "          ";
		
		JLabel pathLbl = new JLabel(leftMargin + res.path);
		pathLbl.setForeground(Color.GRAY);
		pathLbl.setFont(new Font("Tahoma", Font.ITALIC, 10));
		resultBorder.add(pathLbl);
				
		JLabel resultTitle = new JLabel(leftMargin + res.title);
		resultTitle.setFont(new Font("Tahoma", Font.PLAIN, 15));
		resultTitle.setBounds(10, 11, 474, 32);
		if (res.startT != -2) {
			resultTitle.setForeground(Color.BLUE.darker());
			resultTitle.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			resultTitle.addMouseListener(new MouseAdapter() {			 			
			    @Override
			    public void mouseClicked(MouseEvent e) {
			        //open the corresponding doc
			    	try {
						if (res.isVid) { Helper.openVideo(res.path, res.startT); }
						else { Helper.openDoc(res.path); }
					} catch (IOException e1) {
						e1.printStackTrace();
					}
			    }
			});
		}
		resultBorder.add(resultTitle);
				
		JLabel bodyLbl = new JLabel(leftMargin + res.body);
		bodyLbl.setForeground(Color.BLACK);
		bodyLbl.setFont(new Font("Tahoma", 0, 10));
		resultBorder.add(bodyLbl);
		
		JLabel body2Lbl = new JLabel(leftMargin + res.body2);
		body2Lbl.setForeground(Color.BLACK);
		body2Lbl.setFont(new Font("Tahoma", 0, 10));
		resultBorder.add(body2Lbl);
		
		resultsPanel.add(resultBorder);
		resultsPanel.add(Box.createVerticalStrut(15));
	}
	
	//Try VSM, BM25, LM, report the best!
	private void initializeLucene(String pathDir) throws Exception {
		videoDirIndex = new HashMap<String, Integer>();
		idxToVideoDir = new ArrayList<String>();
		searchResults = new ArrayList<SearchResult>();
		
		File seen = new File(Helper.cachePath + "\\seen.txt");  
		FileReader fr = new FileReader(seen); 
		BufferedReader br = new BufferedReader(fr);  //creates a buffering character input stream  
		String line; int idx = 0;
		
		while((line=br.readLine()) != null)  
		{  
			videoDirIndex.put(line, idx);
			idxToVideoDir.add(line);
			idx += 1;
		}  
		fr.close();  
				
		indexProvider = new LuceneIndexer("VSM");		 
		indexProvider.ProcessDirectory(pathDir, videoDirIndex);
		
		System.out.println("Lucene Initialized");
	}
	
	private void handleQuery(String query) throws Exception {
		searchResults.clear();

		if (query.length() == 0) { return; }
		List<queryRes> docs = indexProvider.ProcessQuery(query, 100);
		if (docs.size() == 0) {
			searchResults.add(new SearchResult("No results available", "", "", -2, false));
			resRange.setVisible(false); displayResults(0);
			return;
		}
		
		List<queryRes> uniqueDocs = new ArrayList<queryRes>();
		HashSet<String> seenDocs = new HashSet<String>();
		
		for(int i = 0; i < docs.size(); i++) {
			String curPath = docs.get(i).doc.get("title");
			if (curPath.length() > 44 && curPath.substring(0, 44).equals(Helper.cachePath)) {
				int idx = 45;
				while(idx < curPath.length() && Character.isDigit(curPath.charAt(idx))) { idx++; }
				idx++;
				curPath = curPath.substring(0, idx);
			}
			
			if (seenDocs.contains(curPath)) { i++; continue; }
			seenDocs.add(curPath);
			uniqueDocs.add(docs.get(i));
		}
		
		int n = uniqueDocs.size();
		int pages = (n+9)/10;
		if (pages == 1) { resRange.setVisible(false); }
		else {
			resRange.setVisible(true);
			List<String> ranges = new ArrayList<String>();
			for(int j = 0; j < pages; j++) {
				ranges.add(Integer.toString(j*10 + 1) + "-" + Integer.toString((j+1)*10));
			}		
			resRange.setModel(new DefaultComboBoxModel(ranges.toArray()));
		}
		
		for (queryRes qR : uniqueDocs) {
			String curPath = qR.doc.get("title");
			String body = qR.doc.get("body");
//			System.out.println(curPath + " " + body);
			
			if (curPath.length() > 44 && curPath.substring(0, 44).equals(Helper.cachePath)) {
				int vidIdx = 0, idx = 45;
				while(idx < curPath.length() && Character.isDigit(curPath.charAt(idx))) {
					vidIdx *= 10;
					vidIdx += curPath.charAt(idx) - '0';
					idx++;
				}
				idx++;
								
				int partitionIdx = 0;
				while(idx < curPath.length() && Character.isDigit(curPath.charAt(idx))) {
					partitionIdx *= 10;
					partitionIdx += curPath.charAt(idx) - '0';
					idx++;
				}
				idx++;
				
				String realPath = idxToVideoDir.get(vidIdx);
				String docTitle = Paths.get(realPath).getFileName().toString();
				int ptIdx = docTitle.length() - 1;
				while(docTitle.charAt(ptIdx) != '.') { ptIdx--; }
				docTitle = docTitle.substring(0, ptIdx);

				searchResults.add(new SearchResult(docTitle, body, realPath, 20 * partitionIdx, true));
			}
			
			else {
				String docTitle = Paths.get(curPath).getFileName().toString();
				int ptIdx = docTitle.length() - 1;
				while(docTitle.charAt(ptIdx) != '.') { ptIdx--; }
				docTitle = docTitle.substring(0, ptIdx);
				searchResults.add(new SearchResult(docTitle, body, curPath, 0, false));
			}
		}
		
		displayResults(0);
	}
	
	//Displays results startRange->startRange+9
	private void displayResults(int startRange) {
		resultsPanel.removeAll();
		resultsPanel.revalidate();
		resultsPanel.repaint();
		
		int endRange = Math.min(startRange + 9, searchResults.size() - 1);
		for(int i = startRange; i <= endRange; i++) {
			addResultToPanel(searchResults.get(i));
		}
	}
}
