package client;


import common.MessageType;
import common.TransferrableData;
import connectionscreen.ConnectionScreenController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientMain extends Application {
		private static GoNatureClient client;
		
	    /**
	     * Starts the JavaFX application.
	     * 
	     * @param primaryStage The primary stage for the application.
	     * @throws Exception If an error occurs during application startup.
	     */
	    @Override
	    public void start(Stage primaryStage) throws Exception {
	        // Load the FXML file
	    	
	    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/connectionscreen/connectionscreen.fxml"));
	        Parent root = loader.load();
	        
	        // Get the controller
	        ConnectionScreenController controller = loader.getController();
	        controller.setStage(primaryStage);

	        // Set up the stage
	        primaryStage.setTitle("Connection Traveler GUI");
	        primaryStage.setScene(new Scene(root));
	        primaryStage.show();
	    }
	    
	    /**
	     * The main method of the application.
	     * 
	     * @param args The command line arguments.
	     */
	    public static void main(String[] args) {
	    	launch(args);// CloseClient
	    	if(ClientMain.client.get_worker() != null) {
	    		ClientMain.client.handleMessageFromClientUI(new TransferrableData(MessageType.TravlerLogOut, ClientMain.client.get_worker()));
	    	}
	    	if(ClientMain.client.get_traveler() != null) {
	    		ClientMain.client.handleMessageFromClientUI(new TransferrableData(MessageType.TravlerLogOut, ClientMain.client.get_traveler()));
	    	}
	    	ClientMain.client.handleMessageFromClientUI(new TransferrableData(MessageType.CloseClient, new String [] {ClientMain.client.getInetAddress().getHostAddress(),ClientMain.client.get_port()}));
	    	try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	    	System.exit(0);
	    }
	    
	    /**
	     * Sets the client instance for the application.
	     * 
	     * @param client The client instance to be set.
	     */
	    public static void setConnectioClient(GoNatureClient client) {
	    	ClientMain.client = client;
	    }
	    
	    /**
	     * Gets the client instance associated with the application.
	     * 
	     * @return The client instance.
	     */
	    public static GoNatureClient getConnectioClient() {
	    	return ClientMain.client;
	    }
	    
}
