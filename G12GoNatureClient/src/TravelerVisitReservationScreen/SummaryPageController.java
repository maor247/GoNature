package TravelerVisitReservationScreen;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;

import common.MessageType;
import common.TransferrableData;
import common.VisitReservation;
import connectionscreen.ConnectionScreenController;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class SummaryPageController implements Initializable{
	
	private boolean updatedDetails = false;
	private String phoneNumber;
	private String emailAddress;
	private TravelerVisitReservationScreenControler travelerVisitReservationScreenControler;
	
    @FXML
    private ImageView imageExitButton;
	
    @FXML
    private TextField amountOfVisitorsTextField;

    @FXML
    private TextField emailAddressTextField;

    @FXML
    private Button exitSummaryPage;

    @FXML
    private TextField groupTypeTextField;

    @FXML
    private TextField parkTextField;

    @FXML
    private TextField phoneNumberTextField;

    @FXML
    private TextField reservationIDTextField;

    @FXML
    private Button setPhoneAndEmail;

    @FXML
    private Label setPhoneAndEmailLabel;

    @FXML
    private TextField sumToPayTextField;

    @FXML
    private TextField visitTimeTextField;
    
    @FXML
    private Button pdfButton;
    
    @FXML
    private Label pdfCreationLabel;
	
    @FXML
    void pressExit(ActionEvent event) {
    	if(!updatedDetails) {
    		setPhoneAndEmailLabel.setTextFill(Color.RED);
    		setPhoneAndEmailLabel.setText("Please provide values to both fields \nbefore exiting.");
    	}
    	else {
    		((Node)event.getSource()).getScene().getWindow().hide();
    		travelerVisitReservationScreenControler.setDefaultValues();
    		travelerVisitReservationScreenControler.setDefaultChoiceOfGroupType();
    		travelerVisitReservationScreenControler.continueVisitReservationProcess(true);
    	}
    }
    
    @FXML
    void pressSet(ActionEvent event) {
    	setPhoneAndEmailLabel.setTextFill(Color.RED);
    	phoneNumber = phoneNumberTextField.getText();
    	emailAddress = emailAddressTextField.getText();
    	if(phoneNumber.isEmpty() || emailAddress.isEmpty()) {
    		setPhoneAndEmailLabel.setText("Please provide values to both fields.");
    	}
    	else {
    		if(!phoneNumber.startsWith("+")) {
    			setPhoneAndEmailLabel.setText("Phone number should start with a\'+\'");
    			return;
    		}
    		if(phoneNumber.length() != 13) {
    			setPhoneAndEmailLabel.setText("Phone number should be 12 digits");
    			return;
    		}
    		if(!phoneNumber.substring(1, 13).chars().allMatch(Character::isDigit)) {
    			setPhoneAndEmailLabel.setText("Phone number should include only numbers after \'+\'");
    			return;
    		}
    		if(!emailAddress.endsWith(".com")) {
    			setPhoneAndEmailLabel.setText("Email address should have .com");
    			return;
    		}
    		if(!emailAddress.contains("@")) {
    			setPhoneAndEmailLabel.setText("Email address should contain \'@\'");
    			return;
    		}
    		ConnectionScreenController.client.get_traveler().setPhone_number(phoneNumber);
    		ConnectionScreenController.client.get_traveler().setEmail_addres(emailAddress);
    		ConnectionScreenController.client.handleMessageFromClientUI(new TransferrableData(MessageType.SummaryPageUpdateEmailANdPhoneNumber, ConnectionScreenController.client.get_traveler()));
    	}
    }


	public void setVisitReservation(VisitReservation visitReservation) {
		setPhoneAndEmail.setVisible(false);
		boolean phoneNumberIsSet = false, emailAddressIsSet = false;
		String messageToUpdate = "";
		String phoneNumber = visitReservation.getTraveler().getPhone_number();
		if(phoneNumber == null || phoneNumber.isEmpty()) {
			messageToUpdate += "Please set the following: \nYour phone number.";
			phoneNumberTextField.setEditable(true);
		}
		else {
			phoneNumberTextField.setText(phoneNumber);
			phoneNumberTextField.setEditable(false);
			phoneNumberIsSet = true;
		}
		String emailAddress = visitReservation.getTraveler().getEmail_addres();
		if(emailAddress == null || emailAddress.isEmpty()) {
			messageToUpdate += "\nYour email address.";
			emailAddressTextField.setEditable(true);
		}
		else {
			emailAddressTextField.setText(emailAddress);
			emailAddressTextField.setEditable(false);
			emailAddressIsSet = true;
		}
		updatedDetails = phoneNumberIsSet && emailAddressIsSet;
		reservationIDTextField.setText(String.valueOf(visitReservation.getReservationID()));
		parkTextField.setText(visitReservation.getPark().getParkname());
		amountOfVisitorsTextField.setText(String.valueOf(visitReservation.getNumofvisitors()));
		groupTypeTextField.setText(visitReservation.getGroupType());
		visitTimeTextField.setText(visitReservation.getTimeofvisit().toString());
		sumToPayTextField.setText(String.format("%.2f₪", visitReservation.getPrice()));
		if(!messageToUpdate.isEmpty()) {
			setPhoneAndEmail.setVisible(true);
			setPhoneAndEmailLabel.setText(messageToUpdate);
			pdfButton.setVisible(false);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ConnectionScreenController.client.setSummaryPageController(this);
		imageExitButton.setImage(new Image(getClass().getResourceAsStream("/TravelerVisitReservationScreen/WindowsXPExit.jpeg")));
		exitSummaryPage.setGraphic(imageExitButton);
	}

	public void showUpdatedMessage(TransferrableData transferrableData) {
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				setPhoneAndEmailLabel.setTextFill(Color.GREEN);
				setPhoneAndEmailLabel.setText("Email and Phone Number are updated");
				updatedDetails = true;
				travelerVisitReservationScreenControler.getMakeNotification().start();
				ConnectionScreenController.client.get_traveler().setPhone_number(phoneNumber);
				ConnectionScreenController.client.get_traveler().setEmail_addres(emailAddress);
				setPhoneAndEmail.setVisible(false);
				phoneNumberTextField.setEditable(false);
				emailAddressTextField.setEditable(false);
				pdfButton.setVisible(true);
			}
		});
	}
	
    @FXML
    void generatePDF(ActionEvent event) {
    	 setPhoneAndEmailLabel.setText("");
    	 try {
             // Capture the current scene as an image
             WritableImage snapshot = new WritableImage((int) ((Node) event.getSource()).getScene().getWidth() + 100,
                     (int) ((Node) event.getSource()).getScene().getHeight() + 100);
             ((Node) event.getSource()).getScene().snapshot(snapshot);
    		 
             // Create a new PDF document
             try (PDDocument document = new PDDocument()) {
                 PDPage page = new PDPage();
                 document.addPage(page);
                 
                 // Add the captured image to the PDF document
                 try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                     contentStream.drawImage(LosslessFactory.createFromImage(document, SwingFXUtils.fromFXImage(snapshot, null)), 0, 0);
                 }
                 
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                 // Save the PDF document
//                 File file = new File("output.pdf");
                 document.save(outputStream);
//                 System.out.println("PDF created successfully at: " + file.getAbsolutePath());
                 openSaveFileDialog(outputStream.toByteArray());
             }
         } catch (IOException e) {
             e.printStackTrace();
         }
    }

	private void openSaveFileDialog(byte[] pdfBytes) {
		FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("output.pdf");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null) {
            try {
                Files.write(file.toPath(), pdfBytes);
                pdfCreationLabel.setText("PDF saved successfully at: " + file.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
	}

	public void setTravelerVisitReservationScreenControler(TravelerVisitReservationScreenControler travelerVisitReservationScreenControler) {
		this.travelerVisitReservationScreenControler = travelerVisitReservationScreenControler;
	}

}
