package server;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.*;

public class DBController {
	//Default values for the database:
	public static final String DBName = "jdbc:mysql://localhost/gonaturedb?serverTimezone=IST";
	public static final String DBRoot = "root";
	public static final String DBPassword = "Aa123456";
	
	private String databaseName = DBName;
	private String databaseRoot = DBRoot;
	private String databasePassword = DBPassword;
	private boolean databaseIsConnected;
	private static Connection connection;
	
	/**
	 * @return the connection
	 */
	public static Connection getConnection() {
		return connection;
	}

	public DBController(String databaseName, String databaseRoot, String databasePassword) {
		setValuesForDB(databaseName, databaseRoot, databasePassword);
		databaseIsConnected = false;
	}
	
	private void setValuesForDB(String databaseName, String databaseRoot, String databasePassword) {
		this.databaseName = databaseName;
		this.databaseRoot = databaseRoot;
		this.databasePassword = databasePassword;
	}
	
	public String getName() {
		return databaseName;
	}
	
	public Connection connectToDB() 
	{
		try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            System.out.println("Driver definition succeed");
        } catch (Exception ex) {
        	/* handle the error*/
        	 System.out.println("Driver definition failed");
        }
        
        try {
            connection = DriverManager.getConnection(databaseName, databaseRoot, databasePassword);
            System.out.println("SQL connection succeed");
            databaseIsConnected = true;
            return connection;
     	} catch (SQLException ex) 
     	    {/* handle any errors*/
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        return null;
   	}

	public boolean isConnected() {
		return databaseIsConnected;
	}
	
	public static synchronized List<VisitReservation> getReservationsForTraveler(Traveler traveler) {
		try {
			PreparedStatement preparedStatement = connection.prepareStatement("SELECT PP.ParkName, VR.* FROM VisitReservations VR, ParkParameters PP WHERE VR.UserID = ? AND VR.ParkID = PP.ParkID ORDER BY VR.ReservationID;");
			preparedStatement.setString(1, traveler.getId_number());
			ResultSet resultSet = preparedStatement.executeQuery();
			List<VisitReservation> resultOfQuery = new ArrayList<>();
			while(resultSet.next()) {
				Park park = new Park();
				park.setParkid(resultSet.getInt(4));
				park.setParkname(resultSet.getString(1));
				Timestamp timeOfVisit = Timestamp.valueOf(resultSet.getString(5));
				int numberOfVisitors = resultSet.getInt(7);
				float priceDiscount = resultSet.getFloat(8);
				boolean approvedByTheTraveler = (resultSet.getString(12).equals("1"))? true : false;
				long reservationID = resultSet.getLong(2);
				String status = resultSet.getString(9);
				String reservationType = resultSet.getString(10);
				String groupType = resultSet.getString(11);
				VisitReservation visitReservation = new VisitReservation(reservationID, traveler, park, timeOfVisit, numberOfVisitors, priceDiscount, -1, status, reservationType, groupType);
				visitReservation.setApprovedbythetraveler(approvedByTheTraveler);
				visitReservation.setMaxDurationInHours(resultSet.getInt(12));
				visitReservation.setExitTime(Timestamp.valueOf(resultSet.getString(6)));
				resultOfQuery.add(visitReservation);
			}
			return resultOfQuery;
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			return null;
		}
		return null;
	}
	
	public static synchronized Traveler getUserDetailsViaUserID(String userID) {
		try {
			PreparedStatement preparedStatement = connection.prepareStatement("SELECT T.* FROM Travelers T WHERE T.TravelerID = ?;");
			preparedStatement.setString(1, userID);
			ResultSet resultSet = preparedStatement.executeQuery();
			if(resultSet.next()) {
				String emailAddress = resultSet.getString(4);
				String phoneNumber = resultSet.getString(5);
				Traveler traveler = new Traveler(userID, emailAddress, phoneNumber);
				PreparedStatement preparedStatementGuides = connection.prepareStatement("SELECT U.* FROM Users U WHERE U.TravelerID = ?;");
				preparedStatementGuides.setString(1, userID);
				ResultSet resultSetGuides = preparedStatementGuides.executeQuery();
				if(resultSetGuides.next()) {
					String permission = resultSetGuides.getString(4);
					traveler.setIsguide(permission.toLowerCase().equals("guide"));
				}
				return traveler;
			}
			return null;
		} catch (SQLException e) {
			return null;
		}
	}
	
	public static synchronized boolean addTraveler(Traveler traveler) { //checked
		PreparedStatement preparedStatement;
		try {
			preparedStatement = connection.prepareStatement("INSERT INTO Travelers (TravelerID, Email, PhoneNumber)\r\n"
					+ "VALUES (?, ?, ?);");
			preparedStatement.setString(1, traveler.getId_number());
			preparedStatement.setString(2, traveler.getEmail_addres());
			preparedStatement.setString(3, traveler.getPhone_number());
			preparedStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			return false;
		}
	}
	
	public static synchronized void updateReservation(VisitReservation visitReservation) {
		try {
			PreparedStatement preparedStatement = connection.prepareStatement("UPDATE VisitReservations SET UserID = ?, ParkID = ?, TimeOfVisit = ?, ExitTime = ?, NumberOfVisitors = ?, PriceDiscount = ?, Status = ?, ReservationType = ?, GroupType = ?, Approved = ?, MaxDuration = ? WHERE ReservationID = ?;");
			preparedStatement.setString(1, visitReservation.getTraveler().getId_number());
			preparedStatement.setString(2, new Integer(visitReservation.getPark().getParkid()).toString());
			preparedStatement.setString(3, visitReservation.getTimeofvisit().toString());
			preparedStatement.setString(4, visitReservation.getExitTime().toString());
			preparedStatement.setString(5, new Integer(visitReservation.getNumofvisitors()).toString());
			preparedStatement.setString(6, new Float(visitReservation.getPricediscountforreservation()).toString());
			preparedStatement.setString(7, visitReservation.getStatus());
			preparedStatement.setString(8, visitReservation.getReservationType());
			preparedStatement.setString(9, visitReservation.getGroupType());
			int approved = (visitReservation.isApprovedbythetraveler())? 1 : 0;
			preparedStatement.setString(10, String.valueOf(approved));
			preparedStatement.setInt(11, visitReservation.getMaxDurationInHours());
			preparedStatement.setString(12, new Long(visitReservation.getReservationID()).toString());
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static synchronized void updateStatusOfARequest(ChangeValueRequest changeValueRequest) {
		try {
			PreparedStatement preparedStatement = connection.prepareStatement("UPDATE ChangeValueRequests SET status = ? WHERE requestID = ?;");
			preparedStatement.setString(1, (changeValueRequest.isRequestApproved())? "Approved" : "Pending");
			preparedStatement.setString(2, new Integer(changeValueRequest.getRequestID()).toString());
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static synchronized void deleteARequest(ChangeValueRequest changeValueRequest) {
		try {
			PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM ChangeValueRequests WHERE RequestID = ?;");
			preparedStatement.setString(1, new Integer(changeValueRequest.getRequestID()).toString());
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static synchronized List<ChangeValueRequest> getPendingRequestsForChangingValues() {
		try {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT U.UserID, CVR.* FROM ChangeValueRequests CVR, Users U WHERE CVR.RequestStatus = \"Pending\" AND CVR.WorkerID = U.UserID;");
			List<ChangeValueRequest> pendingRequests = new ArrayList<>();
			while(resultSet.next()) {
				int requestID = resultSet.getInt(2);
				Worker worker = new Worker(new Integer(resultSet.getInt(1)).toString(), new Integer(resultSet.getInt(3)).toString());
				Park park = new Park();
				park.setParkid(resultSet.getInt(4));
				LocalDateTime requestDate = Timestamp.valueOf(resultSet.getString(5)).toLocalDateTime();
				String valueRequested = resultSet.getString(7);
				int currentValue = resultSet.getInt(8);
				int newValue = resultSet.getInt(9);
				ChangeValueRequest changeValueRequest = new ChangeValueRequest(worker, park, valueRequested, currentValue, newValue);
				changeValueRequest.setRequestDate(requestDate);
				changeValueRequest.setRequestID(requestID);
				pendingRequests.add(changeValueRequest);
			}
			return pendingRequests;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static synchronized List<Report> getReports(){
		try {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT PP.*, R.* FROM Reports R, ParkParameters PP WHERE R.ParkID = PP.ParkID;");
			List<Report> reports = new ArrayList<>();
			while(resultSet.next()) {
				Park park = new Park();
				park.setParkid(resultSet.getInt(1));
				park.setParkname(resultSet.getString(2));
				park.setVisitor_quota(resultSet.getInt(3));
				park.setVisitor_gap(resultSet.getInt(4));
				park.setVisitorTimeInMinut(resultSet.getInt(5));
				park.setParkManagerId(resultSet.getInt(6));
				park.setParkManagerId(resultSet.getInt(7));
				String reportId = String.valueOf(resultSet.getInt(8));
				int month = resultSet.getInt(10);
				int year = resultSet.getInt(11);
				Blob fileBlob = resultSet.getBlob(13);
				Report report = new Report(reportId, month, year);
				report.setReportType(resultSet.getString(12));
				report.setFileByteArray(fileBlob.getBytes(1, (int) fileBlob.length()));
				report.setPark(park);
				reports.add(report);
			}
			return reports;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static synchronized boolean addReport(Report report) {
		try {
			PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Reports (ParkID, Month, Year, ReportType, Report)"
					+ " VALUES (?, ?, ?, ?, ?);");
			preparedStatement.setString(1, String.valueOf(report.getPark().getParkid()));
			preparedStatement.setString(2, String.valueOf(report.getMounth()));
			preparedStatement.setString(3, String.valueOf(report.getYear()));
			preparedStatement.setString(4, report.getReportType());
			preparedStatement.setBytes(5, report.getFileByteArray());
			int rowsAffected = preparedStatement.executeUpdate();
			return rowsAffected > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static synchronized List<VisitReservation> getWaitingList(Traveler traveler) {
		try {
			PreparedStatement preparedStatement = connection.prepareStatement("SELECT PP.ParkName, WR.*, PP.MaxCapacity, PP.GapToMaxCapacity, PP.MaxStayDurationHours FROM WaitingReservations WR, ParkParameters PP WHERE WR.UserID = ? AND PP.ParkID = WR.ParkID;");
			preparedStatement.setString(1, traveler.getId_number());
			ResultSet resultSet = preparedStatement.executeQuery();
			List<VisitReservation> resultOfQuery = new ArrayList<>();
			while(resultSet.next()) {
				String emptyString = "";
				long reservationID = resultSet.getLong(2);
				Park park = new Park();
				park.setParkid(resultSet.getInt(4));
				park.setParkname(resultSet.getString(1));
				Timestamp preferredDate = Timestamp.valueOf(resultSet.getString(5));
				int numberOfVisitors = resultSet.getInt(6);
				String reservationType = resultSet.getString(7);
				String groupType = resultSet.getString(8);
				park.setVisitorTimeInMinut(resultSet.getInt(11));
				park.setVisitor_quota(resultSet.getInt(9));
				park.setVisitor_gap(resultSet.getInt(10));
				VisitReservation visitReservation = new VisitReservation(reservationID, traveler, park, preferredDate, numberOfVisitors, -1, -1, emptyString, reservationType, groupType);
				visitReservation.setMaxDurationInHours(resultSet.getInt(11));
				resultOfQuery.add(visitReservation);
			}
			return resultOfQuery;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static synchronized List<VisitReservation> getWaitingList(){
		try {
			PreparedStatement preparedStatement = connection.prepareStatement("SELECT PP.ParkName, WR.*, PP.MaxCapacity, PP.GapToMaxCapacity, PP.MaxStayDurationHours FROM WaitingReservations WR, ParkParameters PP WHERE PP.ParkID = WR.ParkID ORDER BY WaitingListID;");
			ResultSet resultSet = preparedStatement.executeQuery();
			List<VisitReservation> resultOfQuery = new ArrayList<>();
			while(resultSet.next()) {
				String emptyString = "";
				long reservationID = resultSet.getLong(2);
				Park park = new Park();
				park.setParkid(resultSet.getInt(4));
				park.setParkname(resultSet.getString(1));
				Timestamp preferredDate = Timestamp.valueOf(resultSet.getString(5));
				int numberOfVisitors = resultSet.getInt(6);
				Traveler traveler = new Traveler(resultSet.getString(3));
				String reservationType = resultSet.getString(7);
				String groupType = resultSet.getString(8);
				park.setVisitorTimeInMinut(resultSet.getInt(11));
				park.setVisitor_quota(resultSet.getInt(9));
				park.setVisitor_gap(resultSet.getInt(10));
				VisitReservation visitReservation = new VisitReservation(reservationID, traveler, park, preferredDate, numberOfVisitors, -1, -1, emptyString, reservationType, groupType);
				visitReservation.setMaxDurationInHours(resultSet.getInt(11));
				resultOfQuery.add(visitReservation);
			}
			return resultOfQuery;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static synchronized boolean deleteReservation(VisitReservation visitReservation) {
		try {
			PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM VisitReservations WHERE (ReservationID = ?);");
			preparedStatement.setString(1, new Long(visitReservation.getReservationID()).toString());
			int rowsAffected = preparedStatement.executeUpdate();
			return rowsAffected == 1;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static synchronized void addReservationIntoCanceledReservations(VisitReservation visitReservation) {
		try {
			PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO CanceledReservations (ReservationID, ParkID, TimeOfVisit, NumberOfVisitors, Status)\r\n"
					+ "VALUES (?, ?, ?, ?, ?);");
			preparedStatement.setString(1, new Long(visitReservation.getReservationID()).toString());
			preparedStatement.setString(2, new Integer(visitReservation.getPark().getParkid()).toString());
			preparedStatement.setString(3, visitReservation.getTimeofvisit().toString());
			preparedStatement.setString(4, new Integer(visitReservation.getNumofvisitors()).toString());
			preparedStatement.setString(5, visitReservation.getStatus());
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static synchronized List<VisitReservation> getCanceledReservations() {
		try {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT PP.ParkName, CVR.* FROM CanceledReservations CVR, ParkParameters PP WHERE CVR.ParkID = PP.ParkID;");
			List<VisitReservation> resultOfQuery = new ArrayList<>();
			while(resultSet.next()) {
				Park park = new Park();
				park.setParkid(resultSet.getInt(3));
				park.setParkname(resultSet.getString(1));
				Timestamp timeOfVisit = Timestamp.valueOf(resultSet.getString(4));
				int numberOfVisitors = resultSet.getInt(5);
				long reservationID = resultSet.getLong(2);
				String status = resultSet.getString(6);
				VisitReservation visitReservation = new VisitReservation(reservationID, null, park, timeOfVisit, numberOfVisitors, -1, -1, status, null, null);;
				resultOfQuery.add(visitReservation);
			}
			return resultOfQuery;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static synchronized boolean deleteReservationFromWaitingList(VisitReservation visitReservation) {
		try {
			PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM WaitingReservations WHERE (WaitingListID = ?);");
			preparedStatement.setString(1, new Long(visitReservation.getReservationID()).toString());
			int rowsAffected = preparedStatement.executeUpdate();
			return rowsAffected == 1;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static synchronized void updateUserDetails(Traveler traveler) { //checked
		try {
			PreparedStatement preparedStatement = connection.prepareStatement("UPDATE Travelers SET Email = ?, PhoneNumber = ? WHERE TravelerID = ?;");
			preparedStatement.setString(1, traveler.getEmail_addres());
			preparedStatement.setString(2, traveler.getPhone_number());
			preparedStatement.setString(3, traveler.getId_number());
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static synchronized List<Worker> getWorkersDetails() {
		try {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT U.UserID, U.Username, U.Password, U.Role, T.* FROM Users U, Travelers T WHERE U.TravelerID = T.TravelerID;");
			List<Worker> resultOfQuery = new ArrayList<>();
			while(resultSet.next()) {
				int workerID = resultSet.getInt(1);
				String username = resultSet.getString(2);
				String password = resultSet.getString(3);
				String role = resultSet.getString(4);
				int userID = resultSet.getInt(5);
				String firstName = resultSet.getString(6);
				String lastName = resultSet.getString(7);
				String emailAddress = resultSet.getString(8);
				String phoneNumber = resultSet.getString(9);
				Worker worker = new Worker(new Integer(userID).toString(), emailAddress, phoneNumber, username, password, firstName, lastName, new Integer(workerID).toString());
				switch(role) {
				case "Department Manager":
					worker.setDevision_ManagerPermission();
					break;
				case "Park Manager":
					worker.setPark_ManagerPermission();
					break;
				case "Service Agent":
					worker.setServicePermission();
					break;
				case "General Worker":
					worker.setRegularPermission();
					break;
				case "Guide":
					worker.setIsguide(true);
					break;
				case "None":
					worker.setPermissionAsNone();
					break;
				default: break;
				}
				resultOfQuery.add(worker);
			}
			return resultOfQuery;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static synchronized List<Park> getParks() { //checked
		try {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT PP.* FROM ParkParameters PP;");
			List<Park> resultOfQuery = new ArrayList<>();
			while(resultSet.next()) {
				int parkID = resultSet.getInt(1);
				String parkName = resultSet.getString(2);
				int maxCapacity = resultSet.getInt(3);
				int gapToMaxCapacity = resultSet.getInt(4);
				int maxStayDurationHours = resultSet.getInt(5);
				int parkManagerID = resultSet.getInt(6);
				int divisionManagerID = resultSet.getInt(7);
				Park park = new Park(parkID, parkName, maxCapacity, gapToMaxCapacity, maxStayDurationHours);
				park.setParkManagerId(parkManagerID);
				park.setDevisionManagerID(divisionManagerID);
				resultOfQuery.add(park);
			}
			return resultOfQuery;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static synchronized boolean updatePark(Park park) {
		try {
			PreparedStatement preparedStatement = connection.prepareStatement("UPDATE ParkParameters SET ParkName = ?, MaxCapacity = ?, GapToMaxCapacity = ?, MaxStayDurationHours = ?, ParkManagerID = ?, DivisionManagerID = ? WHERE ParkID = ?");
			preparedStatement.setString(1, park.getParkname());
			preparedStatement.setString(2, new Integer(park.getVisitor_quota()).toString());
			preparedStatement.setString(3, new Integer(park.getVisitor_gap()).toString());
			preparedStatement.setString(4, new Integer(park.getVisitorTimeInMinut()).toString());
			preparedStatement.setString(5, new Integer(park.getParkManagerId()).toString());
			preparedStatement.setString(6, new Integer(park.getDevisionManagerID()).toString());
			preparedStatement.setString(7, String.valueOf(park.getParkid()));
			int rowsAffected = preparedStatement.executeUpdate();
			return rowsAffected > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static synchronized List<VisitReservation> getAllReservations() {
		try {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT PP.ParkName, VR.* FROM VisitReservations VR, ParkParameters PP WHERE VR.ParkID = PP.ParkID;");
			List<VisitReservation> resultOfQuery = new ArrayList<>();
			while(resultSet.next()) {
				Park park = new Park();
				park.setParkid(resultSet.getInt(4));
				park.setParkname(resultSet.getString(1));
				Timestamp timeOfVisit = Timestamp.valueOf(resultSet.getString(5));
				int numberOfVisitors = resultSet.getInt(7);
				float priceDiscount = resultSet.getFloat(8);
				boolean approvedByTheTraveler = (resultSet.getString(12).equals("1"))? true : false;
				long reservationID = resultSet.getLong(2);
				String status = resultSet.getString(9);
				String reservationType = resultSet.getString(10);
				String groupType = resultSet.getString(11);
				Traveler traveler = new Traveler(new Integer(resultSet.getInt(3)).toString());
				VisitReservation visitReservation = new VisitReservation(reservationID, traveler, park, timeOfVisit, numberOfVisitors, priceDiscount, -1, status, reservationType, groupType);
				visitReservation.setApprovedbythetraveler(approvedByTheTraveler);
				visitReservation.setMaxDurationInHours(resultSet.getInt(13));
				visitReservation.setExitTime(Timestamp.valueOf(resultSet.getString(6)));
				resultOfQuery.add(visitReservation);
			}
			return resultOfQuery;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static synchronized long addNewReservation(VisitReservation visitReservation) {
		try {
			PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO VisitReservations (UserID, ParkID, TimeOfVisit, ExitTime, NumberOfVisitors, PriceDiscount, Status, ReservationType, GroupType, Approved, MaxDuration)\r\n"
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);", PreparedStatement.RETURN_GENERATED_KEYS);
			preparedStatement.setString(1, visitReservation.getTraveler().getId_number());
			preparedStatement.setString(2, new Integer(visitReservation.getPark().getParkid()).toString());
			preparedStatement.setString(3, visitReservation.getTimeofvisit().toString());
			preparedStatement.setString(4, visitReservation.getExitTime().toString());
			preparedStatement.setString(5, new Integer(visitReservation.getNumofvisitors()).toString());
			preparedStatement.setString(6, new Float(visitReservation.getPricediscountforreservation()).toString());
			preparedStatement.setString(7, visitReservation.getStatus());
			preparedStatement.setString(8, visitReservation.getReservationType());
			preparedStatement.setString(9, visitReservation.getGroupType());
			int isApproved = (visitReservation.isApprovedbythetraveler())? 1 : 0;
			preparedStatement.setString(10, String.valueOf(isApproved));
			PreparedStatement preparedStatementDuration = connection.prepareStatement("SELECT PP.MaxStayDUrationHours FROM ParkParameters PP WHERE ParkID = ?");
			preparedStatementDuration.setInt(1, visitReservation.getPark().getParkid());
			ResultSet resultSet = preparedStatementDuration.executeQuery();
			resultSet.next();
			int maxDuartion = resultSet.getInt(1);
			preparedStatement.setInt(11, maxDuartion);
			int rowsAffected = preparedStatement.executeUpdate();
			if(rowsAffected > 0) {
				ResultSet resultSetKey = preparedStatement.getGeneratedKeys();
				if(resultSetKey.next()) {
					return resultSetKey.getLong(1);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public static synchronized boolean addReservationIntoWaitingList(VisitReservation visitReservation) {
		try {
			PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO WaitingReservations (UserID, ParkID, PreferredTime, NumberOfVisitors, ReservationType, GroupType)\r\n"
					+ "VALUES (?, ?, ?, ?, ?, ?);");
			preparedStatement.setString(1, visitReservation.getTraveler().getId_number());
			preparedStatement.setString(2, new Integer(visitReservation.getPark().getParkid()).toString());
			preparedStatement.setString(3, visitReservation.getTimeofvisit().toString());
			preparedStatement.setString(4, new Integer(visitReservation.getNumofvisitors()).toString());
			preparedStatement.setString(5, visitReservation.getReservationType());
			preparedStatement.setString(6, visitReservation.getGroupType());
			int rowsAffected = preparedStatement.executeUpdate();
			return rowsAffected == 1;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static synchronized void addGuide(Traveler traveler) {
		try {
			PreparedStatement preparedStatement = connection.prepareStatement("UPDATE Users SET Role = \'Guide\' WHERE TravelerID = ?;");
			preparedStatement.setString(1, traveler.getId_number());
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static synchronized void updatePricingModelDefaultPrice(float defaultPrice) {
		try {
			PreparedStatement preparedStatement = connection.prepareStatement("UPDATE PricingModel SET DefaultPrice = ? WHERE ServiceType like 'P%' OR ServiceType like 'G%';");
			preparedStatement.setFloat(1, defaultPrice);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static synchronized List<Map<String, Object>> getPricingModel(){
		try {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT PM.* FROM PricingModel PM;");
			List<Map<String, Object>> resultOfQuery = new ArrayList<>();
			while(resultSet.next()) {
				Map<String, Object> pricingModelTuple = new HashMap<>();
				pricingModelTuple.put("ServiceType", resultSet.getString(1));
				pricingModelTuple.put("DefaultPrice", resultSet.getFloat(2));
				pricingModelTuple.put("PaymentMethod", resultSet.getString(3));
				pricingModelTuple.put("DiscountPercentage", resultSet.getFloat(4));
				resultOfQuery.add(pricingModelTuple);
			}
			return resultOfQuery;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static synchronized void addReminder(Reminder reminder) {
		try {
			PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Reminders (ReservationID, UserID, ReminderType, DateTimeSent)\r\n"
					+ "VALUES (?, ?, ?, ?);");
			preparedStatement.setLong(1, reminder.getReservationID());
			preparedStatement.setString(2, String.valueOf(reminder.getUserID()));
			preparedStatement.setString(3, reminder.getReminderType());
			preparedStatement.setString(4, reminder.getDateTimeSent().toString());
			preparedStatement.executeUpdate();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static synchronized void updateReminderStatus(Reminder reminder) {
		try {
			PreparedStatement preparedStatement = connection.prepareStatement("UPDATE Reminders SET Status = ? WHERE ReminderID = ?;");
			preparedStatement.setString(1, reminder.getStatus());
			preparedStatement.setLong(2, reminder.getReminderID());
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static synchronized boolean getReminderByReservationID(VisitReservation visitReservation) {
		try {
			PreparedStatement preparedStatement = connection.prepareStatement("SELECT R.* FROM Reminders R WHERE R.ReservationID = ?;");
			preparedStatement.setString(1, String.valueOf(visitReservation.getReservationID()));
			ResultSet resultSet = preparedStatement.executeQuery();
			return resultSet.next();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static synchronized List<Reminder> getAllReminders(){
		try {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT R.* FROM Reminders R;");
			List<Reminder> reminders = new ArrayList<>();
			while(resultSet.next()) {
				Reminder reminder = new Reminder();
				reminder.setReminderID(resultSet.getInt(1));
				reminder.setReservationID(resultSet.getLong(2));
				reminder.setUserID(resultSet.getInt(3));
				reminder.setReminderType(resultSet.getString(4));
				reminder.setDateTimeSent(Timestamp.valueOf(resultSet.getString(5)));
				reminder.setStatus(resultSet.getString(6));
				reminders.add(reminder);
			}
			return reminders;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static synchronized boolean addChangeValueRequest(ChangeValueRequest changeValueRequest) {
		try {
			PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO ChangeValueRequests (WorkerID, ParkID, RequestDate, ValueToChange, CurrentValue, NewValue)\r\n"
					+ "VALUES (?, ?, ?, ?, ?, ?);");
			preparedStatement.setString(1, changeValueRequest.getWorker().getWorkerId());
			preparedStatement.setInt(2, changeValueRequest.getPark().getParkid());
			LocalDateTime requestTime = changeValueRequest.getRequestDate();
			String requestTimeInString = Timestamp.valueOf(requestTime).toString();
			preparedStatement.setString(3, requestTimeInString);
			preparedStatement.setString(4, changeValueRequest.getValueRequestedString());
			preparedStatement.setInt(5, changeValueRequest.getCurrentValue());
			preparedStatement.setInt(6, changeValueRequest.getNewValue());
			preparedStatement.executeUpdate();
			return true;
		} catch(SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static synchronized void logInTraveler(Traveler traveler) {
		try {
			PreparedStatement preparedStatement = connection.prepareStatement("UPDATE Travelers SET LoggedIn = true WHERE TravelerID = ?;");
			preparedStatement.setString(1, traveler.getId_number());
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static synchronized void logOutTraveler(Traveler traveler) {
		try {
			PreparedStatement preparedStatement = connection.prepareStatement("UPDATE Travelers SET LoggedIn = false WHERE TravelerID = ?;");
			preparedStatement.setString(1, traveler.getId_number());
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static synchronized boolean isTravelerLoggedIn(Traveler traveler) throws Exception {
		try {
			PreparedStatement preparedStatement = connection.prepareStatement("SELECT T.LoggedIn FROM Travelers T WHERE T.TravelerID = ?");
			preparedStatement.setString(1, traveler.getId_number());
			ResultSet resultSet = preparedStatement.executeQuery();
			if(!resultSet.next())
				throw new Exception("Traveler doesn't exist.");
			return resultSet.getBoolean(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static synchronized void logOutEveryone() {
		Statement statement;
		try {
			statement = connection.createStatement();
			statement.execute("UPDATE Travelers SET LoggedIn = false WHERE TravelerID > 0;");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static synchronized boolean addReservationIntoNotVisited(VisitReservation visitReservation) {
		try {
			PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO NotVisitedReservations (ParkID, TimeOfVisit, NumberOfVisitors, ReservationType, GroupType)\r\n"
					+ "VALUES (?, ?, ?, ?, ?);");
			preparedStatement.setString(1, String.valueOf(visitReservation.getPark().getParkid()));
			preparedStatement.setString(2, String.valueOf(visitReservation.getTimeofvisit()));
			preparedStatement.setString(3, String.valueOf(visitReservation.getNumofvisitors()));
			preparedStatement.setString(4, visitReservation.getReservationType());
			preparedStatement.setString(5, visitReservation.getGroupType());
			int rowsAffected = preparedStatement.executeUpdate();
			return rowsAffected > 0;
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static synchronized List<VisitReservation> getAllNotVisitedReservations() {
		try {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT PP.ParkName, NVR.* FROM NotVisitedReservations NVR, ParkParameters PP WHERE NVR.ParkID = PP.ParkID;");
			List<VisitReservation> resultOfQuery = new ArrayList<>();
			while(resultSet.next()) {
				Park park = new Park();
				park.setParkid(resultSet.getInt(3));
				park.setParkname(resultSet.getString(1));
				Timestamp timeOfVisit = Timestamp.valueOf(resultSet.getString(4));
				int numberOfVisitors = resultSet.getInt(5);
				long reservationID = resultSet.getLong(2);
				String reservationType = resultSet.getString(6);
				String groupType = resultSet.getString(7);
				VisitReservation visitReservation = new VisitReservation(reservationID, null, park, timeOfVisit, numberOfVisitors, -1, -1, null, reservationType, groupType);
				resultOfQuery.add(visitReservation);
			}
			return resultOfQuery;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
