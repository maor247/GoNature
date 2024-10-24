
package DevisionMangerDashboard;

import java.util.Calendar;
import java.awt.Desktop;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import java.util.Iterator;
import common.VisitReservation;
import common.Worker;
import common.ChangeValueRequest;
import common.MessageType;
import common.Park;
import common.Report;
import common.TransferrableData;
import connectionscreen.ConnectionScreenController;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;


public class DevisionMangerDashboardControler implements Initializable {

    @FXML
    private Button BackButton;

    @FXML
    private Button approveButton;

    @FXML
    private TextField avalibleSpaceTextBox;

    @FXML
    private Button denyButton;
    
    @FXML
    private Text error_request_type;
    
    @FXML
    private ChoiceBox<String> idChoose;

    @FXML
    private ChoiceBox<String> parkChoose;

    @FXML
    private ChoiceBox<String> reportTypeChoiceBox;

    @FXML
    private TableView<String[]> table;

    @FXML
    private Text userNameText;

    @FXML
    private Button viewButton;

    @FXML
    private ChoiceBox<String> viewReportDateChoose;
    
    @FXML
    private TableColumn<String[], String> Date;

    @FXML
    private TableColumn<String[], String> FromUser;

    @FXML
    private TableColumn<String[], String> ID;

    @FXML
    private TableColumn<String[], String> Park;

    @FXML
    private TableColumn<String[], String> Status;
    
    @FXML
    private TableColumn<String[], String> currentValue;


    @FXML
    private TableColumn<String[], String> requestedValue;
    
    @FXML
    private TableColumn<String[], String> valueType;
    
    @FXML
    private Text aprrove_button_message;
    
    @FXML
    private TextField ParkName_TextField;
    
    @FXML
    private TextField Date_TextField;
    
    @FXML
    private Text Error_choose_typeMassege;
    
    
    private Park choosen_park;
    
    private Worker worker; // lev aded worker witout asking for
    
    private int totalNumberVisitors[][][] = new int [2][3][24];
    private int canceledNubmervisitors[] = new int [3];
    private int canceledNuMmervisitorsPerDay[][] = new int [3][31];
    private int totalOfCanceledVisits = 0 , avarageOfCanceledVisits = 0;
    List<VisitReservation> allreservesions;
    List<Report> reports;
    Report local_report;
//    @FXML
//	private TableColumn<String[], String> ID, FromUser, Park,Date,Status;
//    
    
    private ObservableList<String[]> tableData = FXCollections.observableArrayList();
    ChangeValueRequest SelectedRequest;
    private List<Park> parks;
    List<ChangeValueRequest> allRequests;
    @FXML
    void approveRequest(ActionEvent event) {
    	if(idChoose.getValue() != null) {
    	ChangeValueRequest request = getRequestById(Integer.parseInt(idChoose.getValue()));
    	TransferrableData message = new TransferrableData(MessageType.DivisionManagerDashboardAcceptRequest,  request);
		ConnectionScreenController.client.handleMessageFromClientUI(message);
    	}
    	else {
    		approve_deny_message("");
    		error_request_type.setText("Error: need to choose request ID");
    	}
    }

    @FXML
    void denyRequest(ActionEvent event) {
    	if(idChoose.getValue() != null) {
        	ChangeValueRequest request = getRequestById(Integer.parseInt(idChoose.getValue()));
        	TransferrableData message = new TransferrableData(MessageType.DivisionManagerDashboardDenyRequest,  request);
    		ConnectionScreenController.client.handleMessageFromClientUI(message);
        	}
        	else {
        		approve_deny_message("");
        		error_request_type.setText("Error: need to choose request ID");
        	}
    }

    @FXML
    void pressReturn(ActionEvent event) {
  	  ConnectionScreenController.client.handleMessageFromClientUI(new TransferrableData(MessageType.TravlerLogOut, ConnectionScreenController.client.get_worker()));
	  openNextScene("/WorkerLoginPage/WorkerLoginPage.fxml",event); // Open scene
    }
    
    public void approve_deny_message(String str) {
    	aprrove_button_message.setText(str);
    	TransferrableData message = new TransferrableData(MessageType.DivisionManagerChoosePark,  ConnectionScreenController.client.get_worker().getWorkerId());
		ConnectionScreenController.client.handleMessageFromClientUI(message);
    	TransferrableData message2 = new TransferrableData(MessageType.DivisionManagerDashboardShowDetails,  this.choosen_park);
		ConnectionScreenController.client.handleMessageFromClientUI(message2);
		avalibleSpaceTextBox.setText(String.valueOf(choosen_park.getVisitor_quota() - choosen_park.getVisitor_gap()));
    }
    
    
    private void openNextScene(String location ,ActionEvent event) {
	      try {
	          FXMLLoader loader = new FXMLLoader(getClass().getResource(location));
	          Scene scene = new Scene(loader.load());
	          Stage newStage = new Stage();
	          newStage.setScene(scene);
	          newStage.show();
	          
	          //WorkerDashboardControler controller = loader.getController();
	          //controller.setStage(stage); 

		      ((Node)event.getSource()).getScene().getWindow().hide();
	      } 
	      catch (IOException e) {
	          e.printStackTrace();
	          // Handle the exception if unable to load or show the next scene
	      }
	 }

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		ConnectionScreenController.client.setDivisionManagerDashboardController(this);
		userNameText.setText(ConnectionScreenController.client.get_worker().getId_number());
    	TransferrableData message = new TransferrableData(MessageType.DivisionManagerChoosePark,  ConnectionScreenController.client.get_worker().getWorkerId());
		 ConnectionScreenController.client.handleMessageFromClientUI(message);
		 ObservableList<String> reportOptions = FXCollections.observableArrayList("Visitors report", "Canceling report");
		 reportTypeChoiceBox.setItems(reportOptions);
		 Calendar calendar = Calendar.getInstance();
		 int year , month;// need calendar
         year = calendar.get(Calendar.YEAR);
         month = calendar.get(Calendar.MONTH) + 1;
         Date_TextField.setText(month + " / " + year);
		 
		 // sets cell value factories for table columns
	        ID.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[0]));
	        FromUser.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[1]));
	        Park.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[2]));
	        currentValue.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[3]));
	        requestedValue.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[4]));
	        valueType.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[5]));
	        Date.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[6]));
	        Status.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[7]));
	        
	        // sets items for table
	        table.setItems(tableData);
	}
	
    @SuppressWarnings("unchecked")
	public void showParks(TransferrableData data) {
    	parks = (List<Park>) data.getMessage();
		ObservableList<String> parkNames = FXCollections.observableArrayList();
		for(Park park : parks) {
			parkNames.add(park.getParkname());
		}
		parkChoose.setItems(parkNames);
		parkChoose.getSelectionModel().clearSelection();
		TransferrableData message = new TransferrableData(MessageType.GetAllReportsDivisionManager,  parks);
		ConnectionScreenController.client.handleMessageFromClientUI(message);
		parkChoose.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if(newValue != null) {
				for(Park park : parks) {
					if(newValue.equals(park.getParkname())) {
						try {
							this.choosen_park =park;
							avalibleSpaceTextBox.setText(String.valueOf(park.getVisitor_quota() - park.getVisitor_gap()));
							ConnectionScreenController.client.setDivisionManagerDashboardController(this);
							TransferrableData message2 = new TransferrableData(MessageType.DivisionManagerDashboardShowDetails,  park);
							ConnectionScreenController.client.handleMessageFromClientUI(message2);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		});
    }
    
    public void updateTable(TransferrableData transferrableData) {
    	this.allRequests = (List<ChangeValueRequest>) transferrableData.getMessage();
    	if(allRequests != null) {
    		ClearTable();
	    	for(ChangeValueRequest data: allRequests) {
	    		updateTable(data);
	    	}
	    	updateRequestIdComboBox(allRequests);
    	}
    }
    public void updateRequestIdComboBox(List<ChangeValueRequest> allRequests) {
    	Platform.runLater(new Runnable() {
		      @Override
		      public void run() {
				ObservableList<String> RequestId = FXCollections.observableArrayList();
				for(ChangeValueRequest rq : allRequests) {
					RequestId.add(String.valueOf(rq.getRequestID()));
				}
				idChoose.setItems(RequestId);
				idChoose.getSelectionModel().clearSelection();
//				idChoose.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
//					if(newValue != null) {
//						for(ChangeValueRequest rq : allRequests) {
//							if(newValue.equals(String.valueOf(rq.getRequestID()))) {
//								try {		
//									
//								} catch (Exception e) {
//									e.printStackTrace();
//								}
//							}
//						}
//					}
//				});
		      }
    	});
    }
    
	// adds a new row to table
	public void updateTable(ChangeValueRequest data) {
		tableData.add(new String[] {String.valueOf(data.getRequestID()), data.getWorker().getId_number(), String.valueOf(data.getPark().getParkid()),String.valueOf(data.getCurrentValue()),String.valueOf(data.getNewValue()),data.getValueRequestedString(),data.getRequestDate().toString(),"Pending"});
		table.refresh();
	}
	public void ClearTable() {
		ObservableList<String[]> newData = FXCollections.observableArrayList();
		 tableData.setAll(newData);
	}
	
	public ChangeValueRequest getRequestById(int num) {
		for(ChangeValueRequest rq : this.allRequests) {
			if(rq.getRequestID() == num) {
				return rq;
			}
		}
		return null;
		
	}
	
	@FXML
    void viewReport(ActionEvent event) { //add listen to time and ask leonid why no make and view seperatly;
    	try {
    		File out_f=new File("report_formdb.png");
			restorePngFromByteArray(this.local_report.getFileByteArray(),out_f);
			openFile(out_f);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
	
    @FXML
    void createReport(ActionEvent event) {
    	if ("Visitors report".equals(reportTypeChoiceBox.getValue())) {
    		if(check_if_report_created("Visitors report")) {
    			this.Error_choose_typeMassege.setText("Error: Report for this month already excited");
    		}
    		else {
        	try {//cheack pleas
        		FXMLLoader loader = new FXMLLoader(getClass().getResource("visitReport.fxml"));
                //Scene scene = new Scene(loader.load());
        		Parent root = loader.load();
        		
                // Pass the current park to the next controller
                VisitReportController nextController = loader.getController();
                nextController.setWorker(ConnectionScreenController.client.get_worker());
                nextController.setTotalNumberVisitors(totalNumberVisitors);

                Stage newStage = new Stage();
               // ((Node) event.getSource()).getScene().getWindow().hide();

                newStage.setScene(new Scene(root));
                newStage.show();
//                 Capture image of the JavaFX scene
                WritableImage image = newStage.getScene().snapshot(null);
                // Convert image to a format suitable for saving (such as PNG)
                try {
                	
                    File imageFile = new File("visitReport.png");
                    ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", imageFile);
                    System.out.println("Image saved successfully!");

                    // Create HTML content with the embedded image
//                    String htmlContent = "<!DOCTYPE html>\n" +
//                            "<html lang=\"en\">\n" +
//                            "<head>\n" +
//                            "    <meta charset=\"UTF-8\">\n" +
//                            "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
//                            "    <title>Embedded Image</title>\n" +
//                            "</head>\n" +
//                            "<body>\n" +
//                            "    <h1></h1>\n" +
//                            "    <img src=\"scene.png\" alt=\"Scene Image\">\n" +
//                            "</body>\n" +
//                            "</html>";
//
//                    // Write HTML content to a file
//                    try (FileWriter fileWriter = new FileWriter("total_number_report.html")) {
//                        fileWriter.write(htmlContent);
//                        System.out.println("HTML file saved successfully!");
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    FileInputStream fis = new FileInputStream(newFile);
                    byte [] mybytearray  = convertToByteArray(imageFile);
                    int year , month;// need calendar
                	Calendar calendar = Calendar.getInstance();
                    year = calendar.get(Calendar.YEAR);
                    month = calendar.get(Calendar.MONTH) + 1;
                    TransferrableData message = new TransferrableData(MessageType.DivisionManagerDashboardCreateReport, new Report(parks.get(0),month,year,mybytearray , "Visitors report"));
                    ConnectionScreenController.client.handleMessageFromClientUI(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
               newStage.show();

            } catch (IOException e) {
                e.printStackTrace();
                // Handle the exception if unable to load or show the next scene
            }
    		}
    	}
        else if ("Canceling report".equals(reportTypeChoiceBox.getValue())) {
        	if(check_if_report_created("Canceling report")) {
    			this.Error_choose_typeMassege.setText("Error: Report for this month already excited");
    		}
        	else {
        	try {
        		FXMLLoader loader = new FXMLLoader(getClass().getResource("cancelReport.fxml"));
                Scene scene;
				scene = new Scene(loader.load());

                // Pass the current park to the next controller
                CancelReportController nextController = loader.getController();
                nextController.setWorker(ConnectionScreenController.client.get_worker());
                nextController.setChartOfXY(canceledNuMmervisitorsPerDay);
                nextController.setAvarageOfCanceled(avarageOfCanceledVisits);
                nextController.PieNumOfCanceledVisitors(canceledNubmervisitors);

                Stage newStage = new Stage();
               // ((Node) event.getSource()).getScene().getWindow().hide();
                newStage.setScene(scene);
                newStage.show();
                
                // Use a PauseTransition to give time for the scene to render
                PauseTransition pause = new PauseTransition(Duration.seconds(1));
                pause.setOnFinished(finishEvent -> {
                    WritableImage image = scene.snapshot(null);
                    try {
                        File imageFile = new File("cancelReport.png");
                        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", imageFile);
                        System.out.println("Image saved successfully!");

                        byte[] mybytearray = convertToByteArray(imageFile);
                        int year, month; // need calendar
                        Calendar calendar = Calendar.getInstance();
                        year = calendar.get(Calendar.YEAR);
                        month = calendar.get(Calendar.MONTH) + 1;
                        TransferrableData message = new TransferrableData(MessageType.DivisionManagerDashboardCreateReport, new Report(parks.get(0),month,year,mybytearray , "Canceling report"));
                        ConnectionScreenController.client.handleMessageFromClientUI(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                pause.play();
        } catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        }
        }
    }
    
    public void getAllCanceledReservations(Object canceled,Object notvisited) {
    	@SuppressWarnings("unchecked")
		List<VisitReservation> canceledList = (List<VisitReservation>) canceled;
    	Iterator <VisitReservation> canceledList_iterator1 = canceledList.iterator();
    	VisitReservation Cres ;
    	List<VisitReservation> notvisitedList = (List<VisitReservation>) notvisited;
    	Iterator <VisitReservation> notvisitedList_iterator2 = notvisitedList.iterator();
    	VisitReservation Nres ;
    	totalOfCanceledVisits = 0;

    	int year , month;// need calendar
    	Calendar calendar = Calendar.getInstance();
        year = Calendar.getInstance().get(Calendar.YEAR);
        month = Calendar.getInstance().get(Calendar.MONTH) + 1;
    			
    	while (canceledList_iterator1.hasNext()) { //if types are private family private and organized 
    	  Cres = canceledList_iterator1.next();
    	  calendar = Calendar.getInstance();
    	  calendar.setTimeInMillis(Cres.getTimeofvisit().getTime());
    	  if (calendar.get(Calendar.MONTH) + 1 == month && calendar.get(Calendar.YEAR) == year) {
    		  this.totalOfCanceledVisits +=Cres.getNumofvisitors();
    		  canceledNubmervisitors[0]+=Cres.getNumofvisitors();
    		  canceledNuMmervisitorsPerDay[0][calendar.get(Calendar.DAY_OF_MONTH)]+=Cres.getNumofvisitors();
    	  }
    	}
    	while (notvisitedList_iterator2.hasNext()) { //if types are private family private and organized 
    		Nres = notvisitedList_iterator2.next();
      	  calendar = Calendar.getInstance();
      	  calendar.setTimeInMillis(Nres.getTimeofvisit().getTime());
      	if (calendar.get(Calendar.MONTH)+ 1 == month && calendar.get(Calendar.YEAR) == year) {
      		 this.totalOfCanceledVisits += Nres.getNumofvisitors();
      		 canceledNubmervisitors[1]+=Nres.getNumofvisitors();
      		canceledNuMmervisitorsPerDay[1][calendar.get(Calendar.DAY_OF_MONTH)]+=Nres.getNumofvisitors();
      	  }
      	}
    	int total = 0;
    	for(VisitReservation res: allreservesions) {
    		total += res.getNumofvisitors();
    	}
    	this.avarageOfCanceledVisits = totalOfCanceledVisits/total;
    }
   
    
    public void getAllReservations(Object obj) {
    	@SuppressWarnings("unchecked")
		List<VisitReservation> list = (List<VisitReservation>) obj;
    	this.allreservesions = list;
    	Iterator <VisitReservation> iterator = list.iterator();
    	VisitReservation res ;
    	int numOfVisitorsPrivate = 0, numOfVisitorsFamily = 0 ,numOfVisitorsOrganized = 0;
    	int openHour = 8,closeHour = 17;
    	int year , month;// need calendar
    	Calendar calendar = Calendar.getInstance();
        year = Calendar.getInstance().get(Calendar.YEAR);
        month = Calendar.getInstance().get(Calendar.MONTH) + 1;
    			
    	while (iterator.hasNext()) { //if types are private family private and organized 
    	  res = iterator.next();
    	  calendar = Calendar.getInstance();
    	  calendar.setTimeInMillis(res.getTimeofvisit().getTime());
    	  if (calendar.get(Calendar.MONTH)+ 1 == month && calendar.get(Calendar.YEAR) == year) {
    	    if(res.getGroupType().equals("Private")) {
    	      totalNumberVisitors[0][0][calendar.get(Calendar.HOUR_OF_DAY)]+=res.getNumofvisitors();
    	      // if max duration is for only one ;
    	      totalNumberVisitors[1][0][0]+=res.getMaxDurationInHours() * res.getNumofvisitors();
    	    }
    	    else if(res.getGroupType().equals("Family")) {
      	      totalNumberVisitors[0][1][calendar.get(Calendar.HOUR_OF_DAY)]+=res.getNumofvisitors();
      	      totalNumberVisitors[1][1][1]+=res.getMaxDurationInHours()*res.getNumofvisitors();
    	    }
    	    else if(res.getGroupType().equals("Organized")) {
      	      totalNumberVisitors[0][2][calendar.get(Calendar.HOUR_OF_DAY)]+=res.getNumofvisitors(); 
      	      totalNumberVisitors[1][2][2]+=res.getMaxDurationInHours()*res.getNumofvisitors(); 
    	    }
    	  }
    	}
    }
    
    public static byte[] convertToByteArray(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = fis.read(buffer)) != -1) {
            bos.write(buffer, 0, bytesRead);
        }
        fis.close();
        return bos.toByteArray();
    }
    
    public static void restorePngFromByteArray(byte[] byteArray, File outputFile) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(outputFile);
             ByteArrayInputStream bis = new ByteArrayInputStream(byteArray)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        }
    }
//	public void removeFromTable(String ip, String host) {
//		boolean flag = true;
//		ObservableList<String[]> newData = FXCollections.observableArrayList();
//	    for (String[] row : tableData) {
//	    	if(!row[0].equals(ip) && !row[1].equals(host) || flag == false) {
//	    		newData.add(row);
//	    	}
//	    	else {
//	    		flag = false;
//	    	}
////	        if (!row[0].equals(ip) && !row[1].equals(host)&& flag==false) {
////	            newData.add(row);
////	            flag = true;
////	        }
//	    }
//	    tableData.setAll(newData);
//	}

	public void UpdateReportDentails(List<Report> reports) {
	    	this.reports = reports;
	    	
	    	ObservableList<String> datePeakercombobox = FXCollections.observableArrayList();
			for(Report rp : reports) {
				if(!rp.getReportType().equals("Canceling report") && !rp.getReportType().equals("Visitors report"))
				datePeakercombobox.add("{ id:"+rp.getPark().getParkid()+ "}  " +String.valueOf(rp.getMounth()) +" / "+ String.valueOf(rp.getYear() + " " + rp.getReportType()));
				else {
					datePeakercombobox.add(String.valueOf(rp.getMounth()) +" / "+ String.valueOf(rp.getYear() + " " + rp.getReportType()));
				}
			}
			Platform.runLater(()->{
				viewReportDateChoose.setItems(datePeakercombobox);
				viewReportDateChoose.getSelectionModel().clearSelection();
			});
			TransferrableData message = new TransferrableData(MessageType.DivisionManagerViewReport,  parks);
			ConnectionScreenController.client.handleMessageFromClientUI(message);
			viewReportDateChoose.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
				if(newValue != null) {
					for(Report rp : reports) {
						if(newValue.equals( "{ id:"+rp.getPark().getParkid()+ "}  " +String.valueOf(rp.getMounth()) +" / "+ String.valueOf(rp.getYear() + " " + rp.getReportType())) ||
							newValue.equals(String.valueOf(rp.getMounth()) +" / "+ String.valueOf(rp.getYear() + " " + rp.getReportType()))){
							try {
								//show report
								this.local_report = rp;
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			});
		
	}
	
    public static void openFile(File file) throws IOException {
        if (!Desktop.isDesktopSupported()) {
            System.out.println("Desktop is not supported");
            return;
        }

        Desktop desktop = Desktop.getDesktop();
        if (file.exists() && desktop.isSupported(Desktop.Action.OPEN)) {
            desktop.open(file);
        } else {
            System.out.println("File does not exist or cannot be opened");
        }
    }
    
    public boolean check_if_report_created(String name) {
    	boolean flag = false;
    	Calendar calendar = Calendar.getInstance();
    	int year, month; // need calendar
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
    	for(Report rp: this.reports) {
    		if(rp.getReportType().equals(name) && rp.getMounth() == month && rp.getYear() == year) {
    			flag = true;
    		}
    	}
    	return flag;
    }

	public void updateComboBoxReport() {
		// TODO Auto-generated method stub
		TransferrableData message = new TransferrableData(MessageType.GetAllReportsDivisionManager,  parks);
		ConnectionScreenController.client.handleMessageFromClientUI(message);
	}

}
