package ServiceDashboard;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import common.MessageType;
import common.TransferrableData;
import connectionscreen.ConnectionScreenController;

public class ServiceDashboardControler implements Initializable {

    @FXML
    private Button BackButton;

    @FXML
    private Text IDText;

    @FXML
    private Button addButton;

    @FXML
    private Text guidAproved;

    @FXML
    private TextField guidIdTextBox;

    @FXML
    void addClicked(ActionEvent event) {
    	String idNumber = guidIdTextBox.getText();
    	if(idNumber.length() != 9) {
    		guidAproved.setFill(Color.RED);
    		guidAproved.setText("ID Number should have 9 digits.");
    	}
    	else {
    		ConnectionScreenController.client.handleMessageFromClientUI(new TransferrableData(MessageType.ServiceDashboardRegisterAGuide, idNumber));
    	}
    }
    
    @FXML
    void pressReturn(ActionEvent event) {
    	openNextScene("/TravelerGUi/TravelerGui.fxml", event); // Open scene
    }
    
    private void openNextScene(String loaction,ActionEvent event) { //return
	      try {
	          FXMLLoader loader = new FXMLLoader(getClass().getResource(loaction));
	          Scene scene = new Scene(loader.load());
	          Stage newStage = new Stage();
	          ((Node)event.getSource()).getScene().getWindow().hide();
	          newStage.setScene(scene);
	          newStage.show();

	      } 
	      catch (IOException e) {
	          e.printStackTrace();
	          // Handle the exception if unable to load or show the next scene
	      }
	 }

	@Override
    public void initialize(URL location, ResourceBundle resources) {
		ConnectionScreenController.client.setServiceDashboardController(this);
        IDText.setText(ConnectionScreenController.client.get_worker().getFirstName() + " " + ConnectionScreenController.client.get_worker().getLastName());
	}

	public void showMessageGuideIsRegistered(TransferrableData transferrableData) {
		boolean guideAdded = (Boolean) transferrableData.getMessage();
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				if(guideAdded) {
					guidAproved.setFill(Color.GREEN);
					guidAproved.setText("The user has been added as a guide.");
				}
				else {
					guidAproved.setFill(Color.RED);
					guidAproved.setText("The user hasn't been added as a guide.");
				}
			}
		});
	}
    
}

