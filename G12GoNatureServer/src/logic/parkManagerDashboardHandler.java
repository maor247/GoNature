package logic;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import ServerGUI.Server_GUI;
import common.ChangeValueRequest;
import common.Park;
import common.Report;
import common.TransferrableData;
import common.Traveler;
import server.DBController;

public class parkManagerDashboardHandler {

	public TransferrableData showUserDetails(TransferrableData transferrableData) {
		// TODO Auto-generated method stub
		String id = (String)transferrableData.getMessage();
		List<Park> parks = new ArrayList<Park>();
		List<Park> return_park = new ArrayList<Park>() ;
		parks = DBController.getParks();
		for(Park park: parks) {
			if(id.equals(String.valueOf(park.getParkManagerId()))) {
				int takenSpace = Server_GUI.serverInstance.getWorkerDashboardHandler().getAvailableSpaceForPark(park.getParkid(), LocalDateTime.now(), DBController.getAllReservations());
				park.setVisitor_gap(park.getVisitor_gap() + takenSpace);
				return_park.add(park);
			}
		}
		return new TransferrableData(transferrableData.getMessageType(), return_park);
	}

	public TransferrableData requestVisitorChange(TransferrableData transferrableData) {
		// TODO Auto-generated method stub
		boolean status;
		status = DBController.addChangeValueRequest((ChangeValueRequest)transferrableData.getMessage());
		return new TransferrableData(transferrableData.getMessageType(),new Object [] {((ChangeValueRequest)transferrableData.getMessage()).getValueRequestedString() ,status});
	}

	public TransferrableData GetAllReportsDetails(TransferrableData transferrableData) {
		Park park = (Park)transferrableData.getMessage();
		List<Report> reports = DBController.getReports();
		List<Report> return_reports = new ArrayList<Report>();
		for(Report rp : reports) {
			if(rp.getPark().getParkid() == park.getParkid()) {
				return_reports.add(rp);
			}
		}
		
		return new TransferrableData(transferrableData.getMessageType(),return_reports);
	}

	public TransferrableData createAReport(TransferrableData transferrableData) {
		// TODO Auto-generated method stub
		Report report = (Report)transferrableData.getMessage();
		boolean flag = DBController.addReport(report);
		return new TransferrableData(transferrableData.getMessageType(),flag);
	}
	

}
