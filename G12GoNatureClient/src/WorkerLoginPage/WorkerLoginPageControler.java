package WorkerLoginPage;

import java.io.IOException;

import common.MessageType;
import common.TransferrableData;
import connectionscreen.ConnectionScreenController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class WorkerLoginPageControler {

	@SuppressWarnings("unused")
	private Stage stage; // Reference to the stage of the current scene
	@SuppressWarnings("unused")
	private boolean login_access = false;
    @FXML
    private Button backButton;

    @FXML
    private Button loginButton;

    @FXML
    private TextField passwordTextbox;

    @FXML
    private TextField usernameTextbox;
    
    @FXML
    private Text error_message_text;
    private ActionEvent last_event;
    // Setter method to set the stage reference
 	public void setStage(Stage stage) {
 	      this.stage = stage;
 	}
    
 	@FXML
    void pressReturn(ActionEvent event) {
 		openNextScene("/TravelerGUi/TravelerGui.fxml",event); // Open scene
    }
 	
    @FXML
    void pressLogin(ActionEvent event) {
    	
//    	String username = "";
//    	String password = "";
//    	username = usernameTextbox.getText();
//    	username = usernameTextbox.getText();
	    
	      // Check if the username and pass exists in the database (andy need to implement this logic)
	    //boolean ExistsInDatabase = checkIfExistsInDatabase(username,password);

    	check_user_worker(event);
//	    if (check_user_worker()) {
//	    	this.last_event = event;
//	        openNextScene("/WorkerDashboard/WorkerDashboard.fxml",event); // Open scene
//	    }      
    }
    
    public void success_worker_login() {
    	Platform.runLater(new Runnable() {
    	      @Override
    	      public void run() {
    	    	  String permission = ConnectionScreenController.client.get_worker().get_permmision();
    	    	  if(permission.equals("Regular")) {
    	    		  openNextScene("/WorkerDashboard/WorkerDashboard.fxml",last_event); // Open scene
    	    	  }
    	    	  else if(permission.equals("Service")) {
    	    		  openNextScene("/ServiceDashboard/ServiceDashboard.fxml",last_event);
    	    	  }
    	    	  else if(permission.equals("Park_Manager")) {
    	    		  openNextScene("/ParkManagerDashboard/ParkManagerDashboard.fxml",last_event);
    	    	  }
    	    	  else if(permission.equals("Devision_Manager")) {
    	    		  openNextScene("/DevisionMangerDashboard/DevisionMangerDashboard.fxml",last_event);
    	    	  }
    	    	  else {
    	    		  error_message_text.setText("ERROR: No such perrmision");
    	    	  }
    	      }
    	  });
    }
    public void wrong_worker_login() {
    	 error_message_text.setText("ERROR: Wrong username or password");
    }
    
    private void openNextScene(String location ,ActionEvent event) {
	      try {
	          FXMLLoader loader = new FXMLLoader(getClass().getResource(location));
	          Scene scene = new Scene(loader.load());
	          Stage newStage = new Stage();
	          newStage.setScene(scene);
	          newStage.show();
	          
	          //WorkerDashboardControler controller = loader.getController();
	          //controller.setStage(stage); 

		      ((Node)event.getSource()).getScene().getWindow().hide();
	      } 
	      catch (IOException e) {
	          e.printStackTrace();
	          // Handle the exception if unable to load or show the next scene
	      }
	 }
    
	 private void check_user_worker(ActionEvent event) {
		 this.last_event = event;
		 String username = usernameTextbox.getText();
		 String password = passwordTextbox.getText();
			 TransferrableData message = new TransferrableData(MessageType.WorkerLogin, new String[] {username,password});
			 ConnectionScreenController.client.handleMessageFromClientUI(message);
			 ConnectionScreenController.client.setWorkerLoginPageControler(this);
	 }

}
