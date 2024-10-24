package TravelerGUi;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.scene.text.Text;


import ApprovalScreen.ApprovalScreenController;
import TravelerDashboard.TravelerDashboardControler;
import TravelerVisitReservationScreen.TravelerVisitReservationScreenControler;
import WorkerLoginPage.WorkerLoginPageControler;
import common.MessageType;
import common.TransferrableData;
import common.Traveler;
import connectionscreen.ConnectionScreenController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class TravelerGuiControler implements Initializable {
	 private Stage stage; // Reference to the stage of the current scene
	 @FXML
	 private TextField IdTextField;
	 @FXML
	 private Button approvalscreenButoon;
	 @FXML
	 private Button reservevisitButoon;
	 @FXML
	 private Button workerloginButton;
	 @FXML
	 private Text error_message_text;
	 @FXML
	 private Button logInButton;
	 
	 private String last_button = "";
	 private ActionEvent last_button_event;
	 private String id;
//	 private TravelerGuiControler entity;
	 // Setter method to set the stage reference
	 public void setStage(Stage stage) {
	      this.stage = stage;
	 }
	 
	 private boolean check_user_id(String last_button , ActionEvent event) {
		 this.last_button = last_button;
		 this.last_button_event = event;
		 id = IdTextField.getText();
		 if(hasExactly9Digits(id)) {
			 TransferrableData message = new TransferrableData(MessageType.TravlerGUILogin, id);
			 ConnectionScreenController.client.handleMessageFromClientUI(message);
			 ConnectionScreenController.client.setTravelerGuiControler(this);
		 }
		 else {
			 error_message_text.setText("ERROR: Wrong ID Format");
		 }
		 return false;
	 }
	 
	 public void last_button_selection(boolean status) {
		 if(status == true) {
		 Platform.runLater(new Runnable() {
		      @Override
		      public void run() {
		    	  if(last_button.equals("pressApprovalScreen")) {
		 			 openNextScene("/ApprovalScreen/ApprovalScreen.fxml",last_button_event); // Open scene
		 		 }
		 		 else if (last_button.equals("pressReserveScreen")) {
		 			 openNextScene("/TravelerVisitReservationScreen/TravelerVisitReservationScreen.fxml" ,last_button_event); // Open scene
		 		 }
		 		 else if(last_button.equals("pressLogIn")) {
		 			 openNextScene("/TravelerDashboard/TravelerDashboard.fxml" ,last_button_event); // Open scene
		 		 }
		 		 else {
		 			 error_message_text.setText("ERROR: User not found");
		 		 }
		      }
		  });
		 }
		 else {
			 if (last_button.equals("pressReserveScreen")) {
				 TransferrableData message = new TransferrableData(MessageType.TravlerAddNew, new Traveler(id,"",""));
				 ConnectionScreenController.client.handleMessageFromClientUI(message);
				 ConnectionScreenController.client.setTravelerGuiControler(this);
			 }
			 else {
	 			 error_message_text.setText("ERROR: User not found");
	 		 }
		 }
	 }
	 
	 public void new_traveler_reservetion(boolean status) {
		 if(status == true) {
			 Platform.runLater(new Runnable() {
			      @Override
			      public void run() {
			    	  openNextScene("/TravelerVisitReservationScreen/TravelerVisitReservationScreen.fxml" ,last_button_event);
			      }
			 });
		 }
		 else {
			 error_message_text.setText("ERROR: Error creating new traveler");
		 }
	 }
	 
	 public void Error_traveler_is_loggedIn() {
		 error_message_text.setText("ERROR: Traveler is already logged in");
	 }
	 
	 @FXML
	 void pressWorkerButton(ActionEvent event) {
	      openNextScene("/WorkerLoginPage/WorkerLoginPage.fxml",event); // Open scene
	 }
	 
	 @FXML
	 void pressApprovalScreen(ActionEvent event) {
		   
//		      String id = "";
//		      id = IdTextField.getSelectedText();
//		      System.out.print(id);
//
//		      // Check if the ID exists in the database (andy need to implement this logic)
//		      boolean idExistsInDatabase = checkIfIdExistsInDatabase(id);
		 	  check_user_id("pressApprovalScreen",event);
//		      if (check_user_id("pressApprovalScreen",event)) {
//		          openNextScene("/ApprovalScreen/ApprovalScreen.fxml",event); // Open scene
//		      } 
//		      else {
//		          // Handle the case when the ID doesn't exist in the database
//	              System.out.println("ID does not exist in the database.");
//		      }
	 }
	 
	 @FXML
	    void pressReserveScreen(ActionEvent event) {
//		 String id = "";
//	      id = IdTextField.getSelectedText();
//	      System.out.print(id);
//		
//	      // Check if the ID exists in the database (andy need to implement this logic)
//	      boolean idExistsInDatabase = checkIfIdExistsInDatabase(id);
		 check_user_id("pressReserveScreen",event);
//	      if (check_user_id("pressReserveScreen",event)) {
//	          openNextScene("/TravelerVisitReservationScreen/TravelerVisitReservationScreen.fxml" ,event); // Open scene
//	      } 
//	      else {
//	          // Handle the case when the ID doesn't exist in the database
//             System.out.println("ID does not exist in the database.");
//	      }

	 }
	 
	 @FXML
	 void pressLogIn(ActionEvent event) {
		 check_user_id("pressLogIn",event);
//	          openNextScene("/TravelerDashboard/TravelerDashboard.fxml" ,event); // Open scene
//	      } 
//	      else {
//	          // Handle the case when the ID doesn't exist in the database
//            System.out.println("ID does not exist in the database.");
//	      }
	 }
	 
	 
	 private void openNextScene(String location, ActionEvent event) {
	     try {
	    	  FXMLLoader loader = new FXMLLoader(getClass().getResource(location));
	    	  Parent root = loader.load();
	    	  
	          if (location.equals("/ApprovalScreen/ApprovalScreen.fxml")) {
	            ApprovalScreenController controller = loader.getController();
	            controller.setStage(stage); 
	          }
	          if (location.equals("/TravelerVisitReservationScreen/TravelerVisitReservationScreen.fxml")) {
	        	 TravelerVisitReservationScreenControler controller = loader.getController();
		         controller.setStage(stage);   
		         controller.setReservationType("Online");
		         controller.setLocationToReturn("/TravelerGUi/TravelerGui.fxml");
		      }
	          if (location.equals("/WorkerLoginPage/WorkerLoginPage.fxml")) {
	        	 WorkerLoginPageControler controller = loader.getController();
		         controller.setStage(stage); 
		      }
	          if (location.equals("/TravelerDashboard/TravelerDashboard.fxml")) {
	        	 TravelerDashboardControler controller = loader.getController();
	        	 controller.setStage(stage);
//	        	 ConnectionScreenController.client.setTravelerDashboardController(controller);
		      }
	          Scene scene = new Scene(root);
	          Stage newStage = new Stage();
	          newStage.setScene(scene);
	          
	          newStage.show();
	          // Hide the current stage
		      ((Node)event.getSource()).getScene().getWindow().hide();
	      } 
	      catch (IOException e) {
	          e.printStackTrace();
	          // Handle the exception if unable to load or show the next scene
	      }
	 }
	 

//	  private boolean checkIfIdExistsInDatabase(String id) {
//	      // Return true if the ID exists, false otherwise
//       return true; // Dummy implementation, replace with actual database check
//	  }
	  
	  public static boolean hasExactly9Digits(String str) {
	        // Check if the string contains only numerical digits and has exactly 9 digits
	        return str.matches("[0-9]{9}");
	    }

@Override
public void initialize(URL arg0, ResourceBundle arg1) {
	// TODO Auto-generated method stub
	//this.entity = this;
}


}
