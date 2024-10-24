package logic;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import ServerGUI.Server_GUI;
import server.DBController;
import common.Reminder;
import common.VisitReservation;

public class RequestsForComingVisits implements Runnable{
	private Thread thread;
	
	@Override
	public void run() {
		while(Server_GUI.serverInstance.isListening()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
			List<VisitReservation> allReservations = DBController.getAllReservations();
			for(VisitReservation visitReservation : allReservations) {
				LocalDateTime timeOfVisit = visitReservation.getTimeofvisit().toLocalDateTime();
				if(!DBController.getReminderByReservationID(visitReservation) && LocalDateTime.now().plusDays(1).isAfter(timeOfVisit)
						&& !visitReservation.isApprovedbythetraveler()) {
					Reminder reminder = new Reminder();
					reminder.setUserID(Integer.parseInt(visitReservation.getTraveler().getId_number()));
					reminder.setReservationID(visitReservation.getReservationID());
					reminder.setReminderType("Approve Visit");
					reminder.setDateTimeSent(Timestamp.valueOf(LocalDateTime.now()));
					DBController.addReminder(reminder);
				}
			}
		}
		thread.interrupt();
	}


	public void setThread(Thread thread) {
		this.thread = thread;
	}

}
