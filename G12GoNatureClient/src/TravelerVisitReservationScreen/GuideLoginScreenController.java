package TravelerVisitReservationScreen;

import common.MessageType;
import common.TransferrableData;
import common.Worker;
import connectionscreen.ConnectionScreenController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class GuideLoginScreenController {
	private TravelerVisitReservationScreenControler travelerVisitReservationScreenControler;
	private Stage stage;
	
    @FXML
    private Button CancelButton;

    @FXML
    private Button loginButton;

    @FXML
    private Label loginErrorMessage;

    @FXML
    private TextField passwordTextField;

    @FXML
    private TextField usernameTextField;

    @FXML
    void cancelButtonClicked(ActionEvent event) {
    	((Node)event.getSource()).getScene().getWindow().hide();
    	travelerVisitReservationScreenControler.setDefaultChoiceOfGroupType();
    }

    @FXML
    void loginButtonClicked(ActionEvent event) {
    	loginErrorMessage.setText("");
    	String username = usernameTextField.getText();
    	String password = passwordTextField.getText();
    	if(username.isEmpty() || password.isEmpty())
    		loginErrorMessage.setText("Please fill the fields.");
    	else {
    		Worker worker = new Worker(ConnectionScreenController.client.get_traveler().getId_number(), null);
    		worker.setUsername(username);
    		worker.setPassword(password);
    		worker.setIsguide(ConnectionScreenController.client.get_traveler().isIsguide());
    		ConnectionScreenController.client.handleMessageFromClientUI(new TransferrableData(MessageType.GuideLoginScreenLogin, worker));
    	}	
    }
    
    public void loginGuide(TransferrableData transferrableData) {
    	loginErrorMessage.setText("");
		boolean loginSuccess = (Boolean) transferrableData.getMessage();
		if(loginSuccess) {
			Platform.runLater(new Runnable() {
				
				@Override
				public void run() {
					stage.hide();
				}
			});
			travelerVisitReservationScreenControler.setGuideIsLoggedIn(true);
			travelerVisitReservationScreenControler.continueVisitReservationProcess(false);
		}
		else {
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					loginErrorMessage.setText("Username or Password are not valid.");
				}
				
			});
		}
	}

	/**
	 * @param travelerVisitReservationScreenControler the travelerVisitReservationScreenControler to set
	 */
	public void setTravelerVisitReservationScreenControler(
			TravelerVisitReservationScreenControler travelerVisitReservationScreenControler) {
		this.travelerVisitReservationScreenControler = travelerVisitReservationScreenControler;
	}

	@FXML
	void initialize() {
		ConnectionScreenController.client.setGuideLoginScreenController(this);
	}
    
    public void setStage(Stage stage) {
    	this.stage = stage;
    }

}
