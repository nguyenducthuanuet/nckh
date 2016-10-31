package gui;

import spoon.compiler.ModelBuildingException;
import util.InfixToPrefix;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import formula2.Core;


public class MainPanel extends JPanel {
	
	public MainPanel() {
		core = new Core();
		
		readDataFile();
		
		initUI();
	}
	
	protected void initUI() {

		setLayout(new BorderLayout());
		
		setPreferredSize(new Dimension(1366, 768));
		
		headPn = createHeadPanel();
		add(headPn, BorderLayout.PAGE_START);
		
		list = new JList<String>();
		list.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				index = e.getFirstIndex();
				System.out.println("source: " + listData[index]);
			}
		});
		list.setFont(new Font("Serif",Font.BOLD,14));
		

		
		//file browser

        fileRoot = new File("/");
        root = new DefaultMutableTreeNode(new FileNode(fileRoot));
        treeModel = new DefaultTreeModel(root);

        tree = new JTree(treeModel);
        tree.setShowsRootHandles(true);
        tree.setModel(null);
        
        JScrollPane scrollPane = new JScrollPane(tree);
       
        tree.addTreeSelectionListener(new TreeSelectionListener() {
			
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				// TODO Auto-generated method stub
				TreePath tp = e.getPath();
				if(tp != null){
					DefaultMutableTreeNode obj = (DefaultMutableTreeNode) tp.getLastPathComponent();
					Object file1 = obj.getUserObject();
					
					if(file1 instanceof FileNode){
						
						FileNode node = (FileNode) file1;
						file = node.getFile();
					
						if (file != null) {
							
							if (file.getParent() != null) {
								recentDirectory = file.getParent();
								writeDataFile();
							}
									
							try {
								loadSourceCode();
								
								core = new Core(file.getAbsolutePath());
			
								listData = core.getMethodSignatures();
								list.setListData( listData );
							} catch (ModelBuildingException e1) {
								JOptionPane.showMessageDialog(MainPanel.this,
			                            "Compile error!");
							} catch (FileNotFoundException e1) {
								e1.printStackTrace();
							}
							
						}
					}
				}
			}
		});
        
//        CreateChildNodes ccn = 
//                new CreateChildNodes(fileRoot, root);
//        new Thread(ccn).start();
//		
//		JScrollPane listScrollPane = new JScrollPane(list);		
//	
		
		resultTA = new JTextArea();
		resultTA.setEditable(false);

		
		JPanel constraintPanel = createContraintsPanel();
		
		JPanel logPanel = createLogPanel();
		
		JPanel sourcePanel = createSourceViewPanel();
		
		JPanel functionList = createFunctionListPanel();
		
		JSplitPane splitpane0 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
												functionList, sourcePanel);
		splitpane0.setDividerLocation(300);
		
		JSplitPane splitpane1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
											logPanel,constraintPanel);
		splitpane1.setDividerLocation(300);
		
		JSplitPane splitPane2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
											splitpane0, splitpane1);
		splitPane2.setDividerLocation(400);
		
		JPanel temp = new JPanel(new BorderLayout());
		temp.setPreferredSize(new Dimension(800, 900));
		temp.add(splitPane2, BorderLayout.CENTER);
		
		JSplitPane splitpane2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
												scrollPane, temp);
		splitpane2.setDividerLocation(300);

        
		add(splitpane2, BorderLayout.CENTER);
		
   
       
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
		openBtn = new JButton("Open Project/File");
		
		openBtn.addActionListener( new ActionListener() {
				
			@Override
			public void actionPerformed(ActionEvent e) {
					
				JFileChooser chooser = new JFileChooser();
				
				chooser.setCurrentDirectory(new File(recentDirectory));
				
		        chooser.setDialogTitle("Please choose a file or project");
		        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		        FileNameExtensionFilter filter = new FileNameExtensionFilter("Java file", "java");
		        chooser.addChoosableFileFilter(filter);
		        chooser.setAcceptAllFileFilterUsed(false);
		        
		        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
		        	fileRoot = chooser.getSelectedFile();
		        } else {
		          System.out.println("No Selection ");
		          return;
		        }
		        
		        if (fileRoot.isDirectory())
		        	recentDirectory = fileRoot.getAbsolutePath();
		        else
		        	if (fileRoot.getParent() != null)
		        		recentDirectory = fileRoot.getParent();
		        writeDataFile();
		        
		        root = new DefaultMutableTreeNode(new FileNode(fileRoot));
		        treeModel = new DefaultTreeModel(root);

		        tree.setModel(treeModel);
		        tree.setShowsRootHandles(true);
		  //      JScrollPane scrollPane = new JScrollPane(tree);
		        CreateChildNodes ccn = 
		                new CreateChildNodes(fileRoot, root);
		        new Thread(ccn).start();
			}
		});
		
		head.add(openBtn);
		
		refreshBtn = new JButton("Refresh");
		refreshBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				refresh();
			}
		});
		head.add(refreshBtn);
		
		vertificationBtn = new JButton("Vertification");
		vertificationBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				vertification();
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
	
	private JPanel createFunctionListPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		JLabel title = new JLabel("Function List");
		panel.add(title, BorderLayout.PAGE_START);
		
		title.setFont(new Font("Serif", Font.PLAIN, 14));
		JScrollPane spList = new JScrollPane(list);
		
		panel.add(spList, BorderLayout.CENTER);
		
		return panel;
	}
	
	private JPanel createLogPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		JLabel title = new JLabel("Constraints");
		title.setFont(new Font("Arial", Font.PLAIN, 14));
		panel.add(title, BorderLayout.PAGE_START);
		
		JLabel label1 = new JLabel("Pre-condition:");
		label1.setFont(new Font("Serif", Font.ITALIC, 14));
		JLabel label2 = new JLabel("Post-condition:");
		label2.setFont(new Font("Serif", Font.ITALIC, 14));
		constraintTA1 = new JTextArea();
		constraintTA = new JTextArea();
		JScrollPane spConstraint1 = new JScrollPane(constraintTA1);
	
		JScrollPane spConstraint = new JScrollPane(constraintTA);
		
		JSplitPane tmp1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, label1, spConstraint1);
		tmp1.setDividerLocation(30);
		JSplitPane tmp2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, label2, spConstraint);
		tmp2.setDividerLocation(30);
		JSplitPane tmp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tmp1, tmp2);
		tmp.setDividerLocation(200);
		panel.add(tmp, BorderLayout.CENTER);
		
		return panel;
	}
	
	private JPanel createSourceViewPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		JLabel title = new JLabel("Source code");
		title.setFont(new Font("Arial", Font.PLAIN, 14));
		panel.add(title, BorderLayout.PAGE_START);
		
		sourceView = new JTextArea();
		//sourceView.setEditable(false);
		JScrollPane spConstraint = new JScrollPane(sourceView);
		
		panel.add(spConstraint, BorderLayout.CENTER);
//		panel.setPreferredSize(new Dimension(600, 400));
		
		return panel;
	}
	
	private void loadSourceCode() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String nextLine = "";
			sourceView.setText("");
			int position = 0;
			int lineNo = 100;
			int countLine = 1;
			while (true) {
				nextLine = br.readLine();
				if (nextLine != null)
					sourceView.append(nextLine + "\n");
				else
					break;
				
				if (countLine < lineNo) {
					countLine++;
					position = position + 1 + nextLine.length();
				}
			}
		//	sourceView.setCaretPosition(0);
			sourceView.setCaretPosition(position);
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void readDataFile() {
		dataFile = new File(dateFilePath);
		if (dataFile == null)
			return;
		
		try {
			BufferedReader bf = new BufferedReader( new FileReader(dataFile) );
			recentDirectory = bf.readLine();
			bf.close();
		} catch (FileNotFoundException e) {
		//	e.printStackTrace();
		} catch (IOException e) {
		//	e.printStackTrace();
		}
	}
	
	private void writeDataFile() {
		dataFile = new File(dateFilePath);
		if (recentDirectory == null || dataFile == null)
			return;
		
		try {
			BufferedWriter bf = new BufferedWriter( new FileWriter(dataFile) );
			System.out.println(recentDirectory);
			bf.write(recentDirectory);
			bf.flush();
			bf.close();
		} catch (FileNotFoundException e) {
	//		e.printStackTrace();
		} catch (IOException e) {
	//		e.printStackTrace();
		}
	}
	
	private void vertification() {
		String rawConstraints = constraintTA.getText();
		
		if (rawConstraints.equals("")) {
			JOptionPane.showMessageDialog(MainPanel.this,
                    "Constraints aren't empty");
			return;
		}
		
		
//		List<String> constraints = new ArrayList<String>();
//		constraints = new InfixToPrefix(list).getOutput(constraints);
//		constraints.add(rawConstraints);
		
		if (index < 0) {
			JOptionPane.showMessageDialog(MainPanel.this,
                   "You must choose a method!");
			return;
		}
		try {
			resultTA.setText("");
			
			List<String> outputList = core.runSolver(listData[index], rawConstraints);
			String state = outputList.get(0);
			if (state.equals("unsat"))
				resultTA.setText("Satification");
			else if (state.equals("unknown"))
				resultTA.setText("Unknown");
			else {
				resultTA.append("Unsatification\n");
				for (int i = 1; i < outputList.size(); i++) {
					resultTA.append(outputList.get(i) + "\n");
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(MainPanel.this,
                    e.getMessage());
		}
	
	}
	
	private void refresh() {
		if (file == null)
			return;
		try {
			loadSourceCode();
			
			core = new Core(file.getAbsolutePath());

			listData = core.getMethodSignatures();
			list.setListData( listData );
		} catch (ModelBuildingException e1) {
			JOptionPane.showMessageDialog(MainPanel.this,
                    "Compile error!");
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
	}
	
	public int getLinePosition(int lineNumber){
        Vector linelength=new Vector();
        String txt=sourceView.getText();
        int width=sourceView.getWidth();
        StringTokenizer st=new StringTokenizer(txt,"\n ",true);
        String str=" ";
        int len=0;
        linelength.addElement(new Integer(0)); //position of the first line
        while(st.hasMoreTokens()){
          String token=st.nextToken();
              //get the width of the string
              int w=sourceView.getGraphics().getFontMetrics(sourceView.getGraphics().getFont()).stringWidth(str+token);
              if(w>width || token.charAt(0)=='\n'){
                len=len+str.length();
                    if(token.charAt(0)=='\n'){
                   linelength.addElement(new Integer(len)); //positon of the line
                    }            
                    else{
                       linelength.addElement(new Integer(len-1)); //positon of the line
                    }            
                    str=token;
              }      
              else{
                str=str+token;
              }      
              
        }  
        
        return len;
    } //get 
	
	public static void main(String[] args) {
		
		
		SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	JFrame frame = new JFrame("Công cụ kiểm chứng tính chất của chương trình");
            	frame.setLayout(new BorderLayout());
        		JPanel panel = new MainPanel();
        		
        		frame.add(panel);
        		frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
     
        	//	frame.setUndecorated(true);		// full screen
        	//	frame.setPreferredSize(new Dimension(1000, 400));
        		frame.pack();
        		frame.setVisible(true);
        		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            }
        });
	}
	
	// ham de thuc hien filebrowser
	 public class FileNode {

        private File file;

        public FileNode(File file) {
            this.file = file;
        }
        
        public File getFile() {
        	return file;
        }

        @Override
        public String toString() {
            String name = file.getName();
            if (name.equals("")) {
                return file.getAbsolutePath();
            } else {
                return name;
            }
        }
	 }
	 
	 public class CreateChildNodes implements Runnable {

        private DefaultMutableTreeNode root;

        private File fileRoot;

        public CreateChildNodes(File fileRoot, 
                DefaultMutableTreeNode root) {
            this.fileRoot = fileRoot;
            this.root = root;
        }

        @Override
        public void run() {
            createChildren(fileRoot, root);
        }

        private void createChildren(File fileRoot, 
                DefaultMutableTreeNode node) {
            File[] files = fileRoot.listFiles();
            if (files == null) return;

            for (File file : files) {
            		if ( !isJavaFile(file) && !file.isDirectory() )
            			continue;
	            		
	                DefaultMutableTreeNode childNode = 
	                        new DefaultMutableTreeNode(new FileNode(file));
	                node.add(childNode);
	                if (file.isDirectory()) {
	                    createChildren(file, childNode);
	                }
            	}
            }
        

    }
	 
	 private boolean isJavaFile(File file) {
		 if (file == null || file.isDirectory())
			 return false;
		 String name = file.getName();
		 String extension;
		 
		 String[] temp = name.split("\\.");
	
		 if (temp.length < 2)	// file ko co duoi
			 return false;
		 extension = temp[temp.length-1];
		 if (extension.equals("java"))
			 return true;
		 
		 return false;
	 }
	
	Core core;
	
	String dateFilePath = "data";
	File dataFile;
	String recentDirectory;
	File file;
	
	JPanel headPn;
	JButton openBtn;
	JButton vertificationBtn;
	JButton refreshBtn;
	
	JList<String> list;
	String[] listData;
	
	
	JTextArea constraintTA1;
	JTextArea constraintTA;
	JTextArea resultTA;
	JTextArea sourceView;
	
	private DefaultMutableTreeNode root;

    private DefaultTreeModel treeModel;

    private JTree tree;
	File fileRoot;
	int index = -1;
}