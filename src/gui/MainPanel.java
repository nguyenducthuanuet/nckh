package gui;

import javafx.stage.FileChooser;
import spoon.compiler.ModelBuildingException;
import util.InfixToPrefix;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import formula2.Core;


public class MainPanel extends JPanel {
	
	public MainPanel() {
		core = new Core();
		initUi();
	}
	
	protected void initUi() {

		setLayout(new BorderLayout());
		
//		setPreferredSize(new Dimension(1366, 768));
		
		headPn = createHeadPanel();
		add(headPn, BorderLayout.PAGE_START);
		
		
//		listData = new ArrayList<String>();
		
		list = new JList<String>();
		list.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
			
				index = e.getFirstIndex();
				System.out.println("source: " + listData[index]);
			}
		});
		list.setFont(new Font("Arial",Font.BOLD,14));
		
		JScrollPane listScrollPane = new JScrollPane(list);		
	
		
		resultTA = new JTextArea();
		resultTA.setEditable(false);
		String str = "";
		
		JPanel constraintPanel = createContraintsPanel();
		
		JPanel logPanel = createLogPanel();
		
		JPanel sourcePanel = createSourceViewPanel();
		
		JSplitPane splitpane2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
												constraintPanel, logPanel);
		splitpane2.setDividerLocation(100);
		
		JSplitPane splitpane1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
												splitpane2, sourcePanel);
		splitpane1.setDividerLocation(500);
		
		JSplitPane splitpane0 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
												listScrollPane, splitpane1);
		splitpane0.setDividerLocation(200);

        
		add(splitpane0, BorderLayout.CENTER);
		
   
       
        UIManager.LookAndFeelInfo[] infos = UIManager.getInstalledLookAndFeels();
        for (UIManager.LookAndFeelInfo lak: infos) {
        	System.out.println("class name: " + lak.getClassName());
        }
        try {
			UIManager.setLookAndFeel(infos[3].getClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}
        SwingUtilities.updateComponentTreeUI(MainPanel.this);

	}
	
	private JPanel createHeadPanel() {
		JPanel head = new JPanel();
		openBtn = new JButton("Open File");
		fileChooser = new FileChooser();
		JFXPanel temp = new JFXPanel();
		openBtn.addActionListener( new ActionListener() {
				
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						System.out.println("run");
						file = fileChooser.showOpenDialog(null);
						if (file != null) {
							System.out.println("filename: " + file.getName());
							try {
								core.setPathFile(file.getAbsolutePath());
								listData = core.getMethodSignatures();
								list.setListData( listData );
							} catch (ModelBuildingException e) {
								e.printStackTrace();
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							}
							
						}
					}
				});
			}
		});
		head.add(openBtn);
		vertificationBtn = new JButton("Vertification");
		vertificationBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String rawConstraints = constraintTA.getText();
				if (rawConstraints.equals("")) {
					JOptionPane.showMessageDialog(MainPanel.this,
                            "Constraints aren't empty");
					return;
				}
				
//				List<String> constraints = new ArrayList<String>();
//				constraints = new InfixToPrefix(list).getOutput(constraints);
//				constraints.add(rawConstraints);
			
				try {
					List<String> outputList = core.runSolver(listData[index], rawConstraints);
					resultTA.setText("");
					for (String str: outputList) {
						resultTA.append(str + "\n");
					}
					
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					JOptionPane.showMessageDialog(MainPanel.this,
                            e.getMessage());
				}
			
			}
		});
		head.add(vertificationBtn);
		
		return head;
	}
	
	private JPanel createContraintsPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		JLabel title = new JLabel("Log");
		panel.add(title, BorderLayout.PAGE_START);
		
		title.setFont(new Font("Arial", Font.PLAIN, 14));
		JScrollPane spResult = new JScrollPane(resultTA);
		
		panel.add(spResult, BorderLayout.CENTER);
		
		return panel;
	}
	
	private JPanel createLogPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		JLabel title = new JLabel("Constraints");
		panel.add(title, BorderLayout.PAGE_START);
		
		constraintTA = new JTextArea();
		JScrollPane spConstraint = new JScrollPane(constraintTA);
		
		panel.add(spConstraint, BorderLayout.CENTER);
		
		return panel;
	}
	
	private JPanel createSourceViewPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		JLabel title = new JLabel("Source code");
		panel.add(title, BorderLayout.PAGE_START);
		
		sourceView = new JTextArea();
		JScrollPane spConstraint = new JScrollPane(sourceView);
		
		panel.add(spConstraint, BorderLayout.CENTER);
		
		return panel;
	}
	
	
	public static void main(String[] args) {
		
		
		SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	JFrame frame = new JFrame("test");
            	frame.setLayout(new BorderLayout());
        		JPanel panel = new MainPanel();
        		
        		frame.add(panel);
        		frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
        	//	JFrame.
        	//	frame.setUndecorated(true);		// full screen
        	//	frame.setPreferredSize(new Dimension(1000, 400));
        		frame.pack();
        		frame.setVisible(true);
        		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            }
        });
	}
	
	Core core;
	
	String recentDirectory;
	File file;
	
	JPanel headPn;
	FileChooser fileChooser;
	JButton openBtn;
	JButton vertificationBtn;
	JList<String> list;
	String[] listData;
	
	JTextArea constraintTA;
	JTextArea resultTA;
	JTextArea sourceView;
	
	int index = -1;
}
