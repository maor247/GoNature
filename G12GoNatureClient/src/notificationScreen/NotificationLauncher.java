package notificationScreen;

import java.io.IOException;
import java.util.List;

import common.TransferrableData;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class NotificationLauncher{
	private static Object lock = new Object();
	
	private void showNotification(List<String> details) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				try {
					FXMLLoader loader = new FXMLLoader(getClass().getResource("/notificationScreen/NotificationScreen.fxml"));
					Scene scene = new Scene(loader.load());
					Stage stage = new Stage();
			        
			        NotificationController notificationController = loader.getController();
			        notificationController.setUserPhoneNumber(details.get(0));
			        notificationController.setUserSMSMessage(details.get(1));
			        
			        stage.setTitle("Notification Screen");
			        stage.setScene(scene);
			        stage.show();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
			
		});
	}
	
	@SuppressWarnings("unchecked")
	public void launchAvailableNotifications(TransferrableData transferrableData) {
		synchronized(lock) {
			List<List<String>> allMessages = (List<List<String>>) transferrableData.getMessage();
			for(List<String> messageDetails : allMessages) {
				showNotification(messageDetails);
			}
		}
	}

}
