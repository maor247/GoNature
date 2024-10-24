package logic;

import java.util.List;

import common.*;
import server.DBController;

public class GuideLoginHandler {

	public TransferrableData loginGuide(TransferrableData transferrableData) {
		boolean loginSuccess = false;
		List<Worker> workers = DBController.getWorkersDetails();
		Worker guide = (Worker) transferrableData.getMessage();
		for(Worker worker : workers) {
			if(worker.getId_number().equals(guide.getId_number())) {
				if(worker.isIsguide() == guide.isIsguide() && worker.getUsername().equals(guide.getUsername()) && worker.getPassword().equals(guide.getPassword()))
					loginSuccess = true;
			}
		}
		return new TransferrableData(transferrableData.getMessageType(), loginSuccess);
	}

}
