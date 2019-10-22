package application;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import javax.swing.JButton;
import javax.swing.JFileChooser;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;


public class EmptyFolderDeleterApp extends Application implements EventHandler<javafx.event.ActionEvent>{
	
	private javafx.scene.control.Button button;
	private static final String LOG_FILENAME = "log.txt";
	
	@Override
	public void start(Stage primaryStage) {
		try {
			StackPane root = new StackPane();
			button = new javafx.scene.control.Button("Select folder");
			button.setOnAction(this);
			
			root.getChildren().add(button);
			Scene scene = new Scene(root,400,400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			primaryStage.setTitle("Empty Folder Cleaner");
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);

	}

	@Override
	public void handle(javafx.event.ActionEvent event) {
		if(event.getSource() == button) {
			try {
				String folderPath = null;
				folderPath = selectFolder();
				if(folderPath != null)
					deleteEmptyFolders(folderPath);
				else
					showInvalidFolderError();
			} catch(Exception e){
				e.printStackTrace();
			}
			
		}
		
	}

	private void showInvalidFolderError() {
		Alert error = new Alert(Alert.AlertType.ERROR);
		error.setHeaderText("Invalid Folder!");
		error.showAndWait();	
	}
	
	private void showFinishedMessage() {
		Alert finished = new Alert(Alert.AlertType.INFORMATION);
		finished.setHeaderText("Finished!");
		finished.showAndWait();
	}

	private void createDeletedLog() {
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(
	              new FileOutputStream(LOG_FILENAME), StandardCharsets.UTF_8))) {
			
			writer.write("Deleted Folders:\n");
			
		} catch(IOException ex) {
			ex.printStackTrace();
		}
	}
	
	private void writeDeletedLog(String pathName) {
		
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(
	              new FileOutputStream(LOG_FILENAME,true), StandardCharsets.UTF_8))) {
			
			writer.append(pathName + "\n");
			
		} catch(IOException ex) {
			ex.printStackTrace();
		}
		
	}
	
	private void deleteEmptyFolders(String folderPath) {
		File folder = new File(folderPath);
		if(!folder.exists())
			showInvalidFolderError();
		else {
			createDeletedLog();
			deleteRecursively(folder);
			showFinishedMessage();
		}
	}

	private void deleteRecursively(File folder) {
		try {
			if(folder.length() == 0 && folder.isDirectory()) {
				if(folder.delete())
					writeDeletedLog(folder.getAbsolutePath());
			}
			else{
				File[] files = folder.listFiles();
				for (File currentFile : files) {
					if(currentFile.isDirectory())
						deleteRecursively(currentFile);
				}
				if(folder.length() == 0 && folder.isDirectory()) {
					if(folder.delete())
						writeDeletedLog(folder.getAbsolutePath());
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}



	private String selectFolder() {
		try {
			JButton open = new JButton();
			JFileChooser fc = new JFileChooser();
			fc.setCurrentDirectory(new java.io.File("C:/"));
			fc.setDialogTitle("Select folder to delete its empty folders");
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			
			if(fc.showOpenDialog(open) == JFileChooser.APPROVE_OPTION) {
				return fc.getSelectedFile().getAbsolutePath();
			}
			else {
				return null;
			}
		} catch(Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
}
