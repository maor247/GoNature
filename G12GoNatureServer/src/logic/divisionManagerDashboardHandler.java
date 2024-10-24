package logic;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import ServerGUI.Server_GUI;
import common.ChangeValueRequest;
import common.Park;
import common.Report;
import common.TransferrableData;
import common.VisitReservation;
import server.DBController;

public class divisionManagerDashboardHandler {
	public TransferrableData showUserDetails(TransferrableData transferrableData) {
		// TODO Auto-generated method stub
		String id = (String)transferrableData.getMessage();
		List<Park> parks = new ArrayList<Park>();
		List<Park> return_park = new ArrayList<Park>();
		parks = DBController.getParks();
		for(Park park: parks) {
			if(id.equals(String.valueOf(park.getDevisionManagerID()))) {
				int takenSpace = Server_GUI.serverInstance.getWorkerDashboardHandler().getAvailableSpaceForPark(park.getParkid(), LocalDateTime.now(), DBController.getAllReservations());
				park.setVisitor_gap(park.getVisitor_gap() + takenSpace);
				return_park.add(park);
			}
		}
		return new TransferrableData(transferrableData.getMessageType(), return_park);
	}

	public TransferrableData showRequestsUpdateTableRequest(TransferrableData transferrableData) {
		Park park = (Park)transferrableData.getMessage();
		List<ChangeValueRequest> allRequests = DBController.getPendingRequestsForChangingValues();
		List<ChangeValueRequest> returnRequests = new ArrayList<ChangeValueRequest>();
		for(ChangeValueRequest rq: allRequests) {
			if(rq.getPark().getParkid() == park.getParkid()) {
				returnRequests.add(rq);
			}
		}
		
		return new TransferrableData(transferrableData.getMessageType(), returnRequests);
	}

	public TransferrableData AcceptRequest(TransferrableData transferrableData) {
		ChangeValueRequest request = (ChangeValueRequest)transferrableData.getMessage();
		List<Park> parks = new ArrayList<Park>();
		Park choosen_park = null ;
		parks = DBController.getParks();
		for(Park park: parks) {
			if(request.getPark().getParkid() == park.getParkid()) {
				choosen_park = park;
				break;
			}
		}
		if(request.getValueRequestedString().equals("MaxCapacity")) {
			choosen_park.setVisitor_quota(request.getNewValue());
			DBController.updatePark(choosen_park);
			DBController.deleteARequest(request);
			return new TransferrableData(transferrableData.getMessageType(), "Visitor Quota Saved");
		}
		else if(request.getValueRequestedString().equals("GapToMaxCapacity")) {
			choosen_park.setVisitor_gap(request.getNewValue());
			DBController.updatePark(choosen_park);
			DBController.deleteARequest(request);
			return new TransferrableData(transferrableData.getMessageType(), "Visitor Gap Saved");
		}
		else if(request.getValueRequestedString().equals("MaxStayDurationHours")) {
			choosen_park.setVisitorTimeInMinut(request.getNewValue());
			DBController.updatePark(choosen_park);
			DBController.deleteARequest(request);
			return new TransferrableData(transferrableData.getMessageType(), "Visitor Max stay time Saved");
		}
		 return new TransferrableData(null,null);
	}

	public TransferrableData DenyRequest(TransferrableData transferrableData) {
		// TODO Auto-generated method stub
		ChangeValueRequest request = (ChangeValueRequest)transferrableData.getMessage();
		DBController.deleteARequest(request);
		return new TransferrableData(transferrableData.getMessageType(), null);
	}

	public TransferrableData GetAllReservesionMD(TransferrableData transferrableData) {
		// TODO Auto-generated method stub
		return new TransferrableData(transferrableData.getMessageType(), DBController.getAllReservations());
	}

	public TransferrableData getAllReports(TransferrableData transferrableData) {
		// TODO Auto-generated method stub
		List<Park> parks = (List<Park>)transferrableData.getMessage();
		List<VisitReservation> canceledReservesions = DBController.getCanceledReservations();
		List<VisitReservation> return_canceledReservesions = new ArrayList<VisitReservation>();
		
		List<VisitReservation> notVisitedReservesions = DBController.getAllNotVisitedReservations();
		List<VisitReservation> return_notVisitedReservesions = new ArrayList<VisitReservation>();
		
		List<VisitReservation> allValidReservesions = DBController.getAllReservations();
		List<VisitReservation> return_allValidReservesions = new ArrayList<VisitReservation>();
		for(Park pr: parks) {
			for(VisitReservation crv: canceledReservesions) {
				if(crv.getPark().getParkid() == pr.getParkid()) {
					return_canceledReservesions.add(crv);
				}
			}
			for(VisitReservation nrv: notVisitedReservesions) {
				if(nrv.getPark().getParkid() == pr.getParkid()) {
					return_notVisitedReservesions.add(nrv);
				}
			}
			for(VisitReservation rv: allValidReservesions) {
				if(rv.getPark().getParkid() == pr.getParkid()) {
					return_allValidReservesions.add(rv);
				}
			}
			
		}
		
		return new TransferrableData(transferrableData.getMessageType(), new Object[] {return_canceledReservesions , return_notVisitedReservesions , return_allValidReservesions});
	}

	public TransferrableData getAllReportsFromAll(TransferrableData transferrableData) {
		// TODO Auto-generated method stub
		
		List<Park> parks = (List<Park>)transferrableData.getMessage();
		List<Report> reports = DBController.getReports();
		List<Report> return_reports = new ArrayList<Report>();
		for(Park park: parks)
			for(Report rp : reports) {
				if(rp.getPark().getParkid() == park.getParkid()) {
					return_reports.add(rp);
				}
			}
		return new TransferrableData(transferrableData.getMessageType(),return_reports);
	}

}