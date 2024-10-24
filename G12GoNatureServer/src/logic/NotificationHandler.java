package logic;

import java.util.ArrayList;
import java.util.List;

import ServerGUI.Server_GUI;
import common.MessageType;
import common.Reminder;
import common.TransferrableData;
import common.Traveler;
import common.VisitReservation;
import server.DBController;

public class NotificationHandler implements Runnable{
	
	private String buildMessage(Reminder reminder, VisitReservation visitReservation) {
		StringBuilder message = new StringBuilder();
		message.append("Dear traveler, \nReservation number ");
		message.append(reminder.getReservationID());
		message.append(" in the park ");
		message.append(visitReservation.getPark().getParkname());
		switch(reminder.getReminderType()) {
		case "Approve Visit":
			message.append(" has to be approved in 2 hours.\nPlease approve it via the "
					+ "Approve Screen from the Login Screen. \n");
			break;
		case "Reservation Added":
			message.append(" has been removed from the waiting list and created. \n");
			break;
		case "Canceled Visit":
			message.append(" has been canceled since you didn't approve it. \n");
			break;
		default: break;
		}
		message.append("With regards, Go Nature team.");
		return message.toString();
	}
	
	private VisitReservation findReservationForReminder(Reminder reminder) {
		List<VisitReservation> reservations;
		if(reminder.getReminderType().equals("Canceled Visit")) {
			reservations = DBController.getCanceledReservations();
		}
		else {
			reservations = DBController.getAllReservations();
		}
		for(VisitReservation visitReservation : reservations) {
			if(visitReservation.getReservationID() == reminder.getReservationID()) {
				return visitReservation;
			}
		}
		return null;
	}


	@Override
	public void run() {
		while(Server_GUI.serverInstance.isListening()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
			List<List<String>> allMessages = new ArrayList<>();
			List<Reminder> reminders = DBController.getAllReminders();
			for(Reminder reminder : reminders) {
				if(reminder.getStatus().equals("Active")) {
					VisitReservation visitReservation = findReservationForReminder(reminder);
					if(visitReservation != null) {
						Traveler traveler = DBController.getUserDetailsViaUserID(String.valueOf(reminder.getUserID()));
						if(traveler != null && !traveler.getPhone_number().isEmpty()) {
							reminder.setStatus("Sent");
							DBController.updateReminderStatus(reminder);
							String message = buildMessage(reminder, visitReservation);
							List<String> messageDetails = new ArrayList<>();
							messageDetails.add(traveler.getPhone_number());
							messageDetails.add(message);
							allMessages.add(messageDetails);
						}
					}
				}
			}
			Server_GUI.serverInstance.sendToAllClients(new TransferrableData(MessageType.NotificationScreenShowMessage, allMessages));
		}
	}
}

	
