package logic;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import ServerGUI.Server_GUI;
import common.Reminder;
import common.VisitReservation;
import server.DBController;

public class ReminderForApprovingVisits implements Runnable {
	private Thread thread;
	
	@Override
	public void run() {
//		while(Server_GUI.serverInstance.isListening()) {
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {}
//			List<VisitReservation> allReservations = DBController.getAllReservations();
//			List<Reminder> allReminders = DBController.getAllReminders();
//			for(VisitReservation visitReservation : allReservations) {
//				Reminder reminder = hasReminderForReservation(visitReservation, allReminders);
//				if(reminder != null) {
//					LocalDateTime timeOfReminder = reminder.getDateTimeSent().toLocalDateTime();
//					if(LocalDateTime.now().isAfter(timeOfReminder.plusHours(2)) && !visitReservation.isApprovedbythetraveler()) {
//						reminder.setStatus("Canceled");
//						DBController.updateReminderStatus(reminder);
//						if(DBController.deleteReservation(visitReservation)) {
//							DBController.addReservationIntoCanceledReservations(visitReservation);
//							Reminder newReminder = new Reminder();
//							newReminder.setReservationID(visitReservation.getReservationID());
//							newReminder.setUserID(Integer.parseInt(visitReservation.getTraveler().getId_number()));
//							newReminder.setReminderType("Canceled Visit");
//							newReminder.setDateTimeSent(Timestamp.valueOf(LocalDateTime.now()));
//							DBController.addReminder(newReminder);
//						}
//					}
//				}
//			}
//		}
//		thread.interrupt();
	}
	
	private Reminder hasReminderForReservation(VisitReservation visitReservations, List<Reminder> reminders) {
		for(Reminder reminder : reminders) {
			if(reminder.getReservationID() == visitReservations.getReservationID() && reminder.getReminderType().equals("Approve Visit")) {
				return reminder;
			}
		}
		return null;
	}
	
	public void setThread(Thread thread) {
		this.thread = thread;
	}
	
}
