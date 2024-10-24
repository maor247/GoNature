package logic;

import java.time.LocalDateTime;
import java.util.List;

import ServerGUI.Server_GUI;
import common.VisitReservation;
import server.DBController;

public class WaitingListChecker implements Runnable{
	private Thread thread;
	
	@Override
	public void run() {
		while(Server_GUI.serverInstance.isListening()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
			List<VisitReservation> waitingLists = DBController.getWaitingList();
			for(VisitReservation visitReservation : waitingLists) {
				LocalDateTime timeOfVisit = visitReservation.getTimeofvisit().toLocalDateTime();
				if(timeOfVisit.isBefore(LocalDateTime.now())) {
					DBController.deleteReservationFromWaitingList(visitReservation);
				}
			}
		}
		thread.interrupt();
	}

	/**
	 * @param thread the thread to set
	 */
	public void setThread(Thread thread) {
		this.thread = thread;
	}
	

}
