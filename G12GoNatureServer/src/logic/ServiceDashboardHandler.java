package logic;

import java.util.List;

import common.TransferrableData;
import common.Worker;
import server.DBController;

public class ServiceDashboardHandler {

	public TransferrableData registerAGuide(TransferrableData transferrableData) {
		// check if roll == guide if true return false
		//check if the roll == none if true assighn to guide than return true
		//return false
		String idNumber = (String) transferrableData.getMessage();
		List<Worker> workers = DBController.getWorkersDetails();
		Worker worker = null;
		for(Worker w : workers) {
			if(w.getId_number().equals(idNumber)) {
				worker = w;
				break;
			}
		}
		if(worker == null || worker.isIsguide()) {
			return new TransferrableData(transferrableData.getMessageType(), false);
		}
		if(worker.get_permmision().toLowerCase().equals("none")) {
			worker.setIsguide(true);
			DBController.addGuide(worker);
			return new TransferrableData(transferrableData.getMessageType(), true);
		}
		return new TransferrableData(transferrableData.getMessageType(), false);
	}
	
}
