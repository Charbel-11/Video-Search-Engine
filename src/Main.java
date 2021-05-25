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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.JProgressBar;

//Entry point of the program, opens the main screen

public class Main extends JFrame {
	private JPanel contentPane;
	private JTextField pathDir;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main frame = new Main();
					frame.setLocationRelativeTo(null);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Main() {
		setTitle("Video Search Engine");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 749, 525);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblTitle = new JLabel("Video Search Engine");
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitle.setFont(new Font("Tahoma", Font.BOLD, 35));
		lblTitle.setBounds(141, 27, 446, 77);
		contentPane.add(lblTitle);
		
		JLabel lblDir = new JLabel("Directory");
		lblDir.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblDir.setHorizontalAlignment(SwingConstants.CENTER);
		lblDir.setBounds(91, 179, 105, 39);
		contentPane.add(lblDir);
		
		pathDir = new JTextField();
		pathDir.setFont(new Font("Tahoma", Font.PLAIN, 13));
		pathDir.setBounds(218, 181, 382, 39);
		contentPane.add(pathDir);
		pathDir.setColumns(10);
		
		
		JButton btnBrowse = new JButton("Browse");
		btnBrowse.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int res = chooser.showDialog(contentPane, "Choose Directory");
				if (res == JFileChooser.APPROVE_OPTION) {
				    File selectedFile = chooser.getSelectedFile();
				    pathDir.setText(selectedFile.getAbsolutePath());
				}
			}
		});
		btnBrowse.setBackground(Color.LIGHT_GRAY);
		btnBrowse.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btnBrowse.setBounds(308, 246, 166, 39);
		contentPane.add(btnBrowse);
		
		JLabel lblError = new JLabel("");
		lblError.setHorizontalAlignment(SwingConstants.CENTER);
		lblError.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblError.setForeground(Color.RED);
		lblError.setBounds(218, 333, 280, 33);
		contentPane.add(lblError);
		
		JLabel lblCnt = new JLabel();
		lblCnt.setHorizontalAlignment(SwingConstants.CENTER);
		lblCnt.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblCnt.setBounds(308, 350, 125, 25);
		contentPane.add(lblCnt);
		lblCnt.setVisible(false);
		
		JButton btnSubmit = new JButton("Submit");
		btnSubmit.setBackground(Color.GREEN);
		btnSubmit.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btnSubmit.setBounds(233, 388, 251, 53);
		contentPane.add(btnSubmit);
		
		Main ptr = this;
		btnSubmit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				File file = new File(pathDir.getText());
				if(!file.isDirectory()) {
					lblError.setText("Please choose a valid directory");
					return;
				}
						
				lblError.setText("");
				pathDir.setEditable(false);
				btnBrowse.setVisible(false);
				btnSubmit.setVisible(false);
				lblCnt.setVisible(true);
				lblCnt.setText("Preprocessing...");
				
				new Thread(new Runnable() {
					public void run() {
				        	try {
								Helper.preprocessDirectory(pathDir.getText(), ptr);
							} catch (Exception e) {
								e.printStackTrace();
							}
					}
				}).start();
			}
		});
	}
	
	public void openSearchPage() throws Exception {
		SearchPage newframe = new SearchPage(pathDir.getText());
		newframe.setLocationRelativeTo(null);
		newframe.setVisible(true);
		
    	dispose();
	}
}
