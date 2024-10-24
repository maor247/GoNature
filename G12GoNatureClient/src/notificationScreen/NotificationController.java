package notificationScreen;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class NotificationController{

		@FXML
	    private TextArea messagetext;

	    @FXML
	    private TextField phonenumber;

		/**
		 * @param userPhoneNumber the userPhoneNumber to set
		 */
		public void setUserPhoneNumber(String userPhoneNumber) {
			phonenumber.setText(userPhoneNumber);
		}

		/**
		 * @param userSMSMessage the userSMSMessage to set
		 */
		public void setUserSMSMessage(String userSMSMessage) {
			messagetext.setText(userSMSMessage);
		}

	}


