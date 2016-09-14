package gui;


import javax.swing.*;

import java.io.File;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.embed.swing.SwingNode;
import javafx.scene.layout.StackPane;

import javafx.application.Platform;
import java.awt.event.*;
import java.awt.Dimension;

public class FileChooserSample4 extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start (Stage stage) {
        final SwingNode swingNode = new SwingNode();

        createSwingContent(swingNode, stage);

        StackPane pane = new StackPane();
        pane.getChildren().add(swingNode);

        stage.setTitle("Swing in JavaFX");
        stage.setScene(new Scene(pane, 250, 150));
        stage.show();
    }

    private void createSwingContent(final SwingNode swingNode, Stage stage) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                swingNode.setContent(new FileChooserPanel(stage));
            }
        });
    }
}
 
class FileChooserPanel extends JPanel {
	FileChooser fileChooser;
	
	public FileChooserPanel(final Stage stage) {
		
		super();
		setPreferredSize(new Dimension(250, 150));
		fileChooser = new FileChooser();
	
	 
        JButton openButton = new JButton("Open a Picture...");
 
 
        openButton.addActionListener( new ActionListener() {

			@Override
			public void actionPerformed(java.awt.event.ActionEvent arg0) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						System.out.println("run");
						if (stage == null) {
							System.out.println("stage is null");
							System.exit(-1);
						}
						System.out.println("stage is not null");
					  File file = fileChooser.showOpenDialog(stage);
					  if (file != null) {
                        System.out.println("filename: " + file.getName());
						}
					}
				});
			}
			
			
        });
 
 
        add(openButton);
	}
	 
}