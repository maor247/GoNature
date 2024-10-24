package WorkerDashboard;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class receiptController {

	private static Long invoiceNum = 0L;
	private String park, reservationID, paid;
	
	@FXML
	private Text invoiceText;
	
	@FXML
	private Text parkText;
	
	@FXML
	private Text reservationIDText;
	
	@FXML
	private Text paidText;
	
	public void setValues(String[] values) {
		park = values[0];
		reservationID = values[1];
		paid = values[2];
		
		synchronized(invoiceNum) {
			invoiceText.setText("" + invoiceNum++);
		}
		parkText.setText(park);
		reservationIDText.setText(reservationID);
		paidText.setText(paid);
	}
	
	public void closeWindow(ActionEvent event) {
		((Stage)(((Node)event.getSource()).getScene().getWindow())).close();
	}
}
