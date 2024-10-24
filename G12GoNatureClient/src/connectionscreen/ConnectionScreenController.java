package connectionscreen;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import client.ClientMain;
import client.GoNatureClient;
import common.MessageType;
import common.TransferrableData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ConnectionScreenController  {
	private boolean flag= false;
	@SuppressWarnings("unused")
	private Stage stage;
	public static GoNatureClient client;
	
	public void setStage(Stage stage) {
		this.stage= stage;
		
	}
    @FXML
    private Text error_text;

    @FXML
    private TextField ip_text_input;

    @FXML
    private Text ip_title;

    @FXML
    private Text main_title;

    @FXML
    private TextField port_text_input;

    @FXML
    private Text port_title;

    @FXML
    void connect_button(ActionEvent event) throws UnknownHostException {
    	TransferrableData message = new TransferrableData(MessageType.NewClientConnected,new String[] {InetAddress.getLocalHost().getHostAddress(),port_text_input.getText(),"connected"});
		try {
			client = new GoNatureClient(ip_text_input.getText(),Integer.parseInt(port_text_input.getText()));
			client.setCr(this);
			ClientMain.setConnectioClient(client);
	    	client.handleMessageFromClientUI(message);
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	if(flag == true) {
    		this.flag = false;
    	 	openNextScene("/TravelerGUi/TravelerGui.fxml",event); // Open scene
    	}
    	else {
    		error_text.setText("ERROR: Cannot connect to server");
    	}
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
    
    public void set_connectionstatus(boolean flag) {
    	this.flag= flag;
    }
   

}
