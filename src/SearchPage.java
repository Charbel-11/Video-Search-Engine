import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.Scrollable;
import javax.swing.JInternalFrame;
import javax.swing.Box;
import javax.swing.BoxLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JScrollBar;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.BevelBorder;
import java.awt.Panel;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.CardLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.LayoutStyle.ComponentPlacement;

public class SearchPage extends JFrame {
	private JPanel contentPane;
	private JTextField searchQuery;
	private LuceneIndexer indexProvider;
	private HashMap<String, Integer> videoDirIndex;
	private ArrayList<String> idxToVideoDir;

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
			
		JPanel resultsPanel = new JPanel();
		resultsPanel.setBorder(null);
		resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
		
		JButton btnSearch = new JButton("Search");
		btnSearch.setBounds(513, 99, 166, 39);
		btnSearch.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e)  {
				try {
					handleQuery(resultsPanel, searchQuery.getText());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		btnSearch.setBackground(Color.LIGHT_GRAY);
		btnSearch.setFont(new Font("Tahoma", Font.PLAIN, 15));
		contentPane.add(btnSearch);
		
		for(int i = 0; i < 5; i++) {
			addResult(resultsPanel, "testLink", "sssss", "C:\\Users\\PC\\Desktop\\test\\22.mp4", 100);
		}			
		
		JScrollPane scrollPane = new JScrollPane(resultsPanel);
		scrollPane.setBounds(25, 169, 685, 373);
		contentPane.add(scrollPane);
	}
	
	private void addResult(JPanel parentPanel, String title, String body, String path, int startT) {
		JPanel resultBorder = new JPanel();
//		resultBorder.setBorder(new LineBorder(new Color(0, 0, 0)));
		resultBorder.setLayout(new GridLayout(4, 1));

		String leftMargin = "          ";
		
		JLabel pathLbl = new JLabel(leftMargin + path);
		pathLbl.setForeground(Color.GRAY);
		pathLbl.setFont(new Font("Tahoma", Font.ITALIC, 10));
		resultBorder.add(pathLbl);
		
		JLabel resultTitle = new JLabel(leftMargin + title);
		resultTitle.setForeground(Color.BLUE.darker());
		resultTitle.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		resultTitle.setFont(new Font("Tahoma", Font.PLAIN, 15));
		resultTitle.setBounds(10, 11, 474, 32);
		resultTitle.addMouseListener(new MouseAdapter() {			 
		    @Override
		    public void mouseClicked(MouseEvent e) {
		        //open the corresponding doc
		    	try {
					Preprocessor.openVideo(path, startT);
					//check if video, if not open as usual?
				} catch (IOException e1) {
					e1.printStackTrace();
				}
		    }
		});
		resultBorder.add(resultTitle);
		
		String body2 = "";
		if (body.length() > 130) {
			body2 = body.substring(130, body.length());
			body = body.substring(0, 130);
			if (body.charAt(129) != ' ' && body2.charAt(0) != ' ') { body += "-"; }
			if (body2.charAt(0) == ' ') { body2 = body2.substring(1); }
		}
		if (body2.length() > 130) {
			body2 = body2.substring(0, 130) + " ...";
		}
		
		JLabel bodyLbl = new JLabel(leftMargin + body);
		bodyLbl.setForeground(Color.BLACK);
		bodyLbl.setFont(new Font("Tahoma", 0, 10));
		resultBorder.add(bodyLbl);
		
		JLabel body2Lbl = new JLabel(leftMargin + body2);
		body2Lbl.setForeground(Color.BLACK);
		body2Lbl.setFont(new Font("Tahoma", 0, 10));
		resultBorder.add(body2Lbl);
		
		parentPanel.add(resultBorder);
		parentPanel.add(Box.createVerticalStrut(15));
	}
	
	//Try VSM, BM25, LM, report the best!
	private void initializeLucene(String pathDir) throws Exception {
		videoDirIndex = new HashMap<String, Integer>();
		idxToVideoDir = new ArrayList<String>();
		
		File seen = new File(Preprocessor.cachePath + "\\seen.txt");  
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
	
	private void handleQuery(JPanel parentPanel, String query) throws Exception {
		parentPanel.removeAll();
		parentPanel.revalidate();
		parentPanel.repaint();
		
		if (query.length() == 0) { return; }
		List<queryRes> docs = indexProvider.ProcessQuery(query, 10);
		for (queryRes qR : docs) {
			String curPath = qR.doc.get("title");
			String body = qR.doc.get("body");
//			System.out.println(curPath + " " + body);
			
			if (curPath.length() > 44 && curPath.substring(0, 44).equals(Preprocessor.cachePath)) {
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

//				System.out.println("Got " + Integer.toString(vidIdx) + " " + Integer.toString(partitionIdx));
				addResult(parentPanel, docTitle, body, realPath, 20 * partitionIdx);
			}
			
			else {
				addResult(parentPanel, "testLink", body, curPath, 0);
			}
		}
	}
}
