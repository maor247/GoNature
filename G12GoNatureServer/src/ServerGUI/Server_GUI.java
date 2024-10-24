package ServerGUI;

import java.io.IOException;


import common.MessageType;
import common.TransferrableData;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import server.DBController;
import server.GoNatureServer;

public class Server_GUI extends Application {
	public static final int WIDTH = 600, HEIGHT = 450;
	public static GoNatureServer serverInstance = null;

	@Override
	public void start(Stage primaryStage) throws Exception {
		VBox vbox;
		//try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("Server_GUI.fxml")); // loads the fxml file
			try {
			vbox = loader.load(); // saves the root Node in 'vbox'
			} 
			catch (Exception e){
				e.printStackTrace();
				return;
			}

	        if (vbox == null) { // handles the case of not finding the root
	            System.out.println("VBox not found");
	            return;
	        }
	        
	        Scene scene = new Scene(vbox, WIDTH, HEIGHT);
			scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm()); // applies the css file on scene 

	        primaryStage.getIcons().add(new Image("/ServerGUI/GoNature_Logo.png")); // sets the icon for the primary stage
			primaryStage.setTitle("GoNature - Server"); // sets title
			primaryStage.setScene(scene);
			
			primaryStage.setOnCloseRequest(event -> {
	            try {
	            	if(serverInstance != null) {
	            		Server_Controller.get_server().sendToAllClients(new TransferrableData(MessageType.TravlerLogOutALL, null));
	            		DBController.logOutEveryone();
	            		serverInstance.close();
	            	}
	            	interruptThreads();
				} catch (IOException e) {
					e.printStackTrace();
				}
	        });
			
			primaryStage.show(); // displays
//		} catch (IOException e) {
//			e.printStackTrace();
//			return;
//		}
		
	}
	
	private void interruptThreads() {
		if(Server_Controller.findAvailableVisitsSlots != null)
    		Server_Controller.findAvailableVisitsSlots.interrupt();
		if(Server_Controller.comingVisitsRequests != null)
			Server_Controller.comingVisitsRequests.interrupt();
		if(Server_Controller.approvingVisitsRemindersChecking != null)
			Server_Controller.approvingVisitsRemindersChecking.interrupt();
		if(Server_Controller.checkWaitingLists != null)
			Server_Controller.checkWaitingLists.interrupt();
		if(Server_Controller.deleteReservationThatPassed != null)
			Server_Controller.deleteReservationThatPassed.interrupt();
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
