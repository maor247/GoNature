package ParkManagerDashboard;
 
import java.awt.Desktop;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import common.ChangeValueRequest;
import common.MessageType;
import common.Park;
import common.Report;
import common.TransferrableData;
import common.VisitReservation;
import common.Worker;
import connectionscreen.ConnectionScreenController;
import javafx.animation.PauseTransition;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ParkManagerDashboardControler implements Initializable {
	
	private List<Park> parks;
    @FXML
    private Button BackButton;

    @FXML
    private TextField QuotaTextField;

    @FXML
    private TextField avalibleSpaceTextField;

    @FXML
    private Button createButton;

    @FXML
    private ComboBox<String> datePeaker_combobox;

    @FXML
    private Text nameText;

    @FXML
    private Button previewButton;

    @FXML
    private Button saveQuata;

    @FXML
    private Button saveVisitTime;

    @FXML
    private Button saveVisitorGap;

    @FXML
    private ChoiceBox<String> typeChoose;

    @FXML
    private ComboBox<String> choosePark;

    @FXML
    private TextField visitTimeTextField;
    @FXML
    private Label NewReport_Text;
    @FXML
    private TextField visitorGapTextField;
    @FXML
    private Text Error_Quota_in_park_message;

    @FXML
    private Text Error_Visitor_Gap_message;

    @FXML
    private Text Error_visit_time_message;
    
    @FXML
    private Text access_Quota_in_park_massege;

    @FXML
    private Text access_Visitor_Gap_massege;
    
    @FXML
    private Text Error_send_new_report;
    
    @FXML
    private Text successuly_created_report;

    @FXML
    private Text access_visit_time_massege;
    
    private String last_press;
    private Park park;
    private Worker worker; //adddd
    private List<Report> reports;
    private int visitorCounts[] = new int [3];
    private int totalNumberVisitors[][] = new int [3][31];
    Report local_report;
//    private GoNatureClient client;
    
// // this method is called by the previous stage's controller, after 'loader.load();' and before 'stage.show();'
//    public void setSettings(Park park) {
//    	try {
//			client.sendToServer(new TransferrableData(MessageType.GetAllReservations, null));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//    }


    @FXML
    void pressCreate(ActionEvent event) {
    	if ("total number report".equals(typeChoose.getValue())) {
    		if(check_if_report_created("total number report")) {
    			this.Error_send_new_report.setText("Error: Report for this month already excited");
    		}
    		else {
        	try {//cheack pleas
        		FXMLLoader loader = new FXMLLoader(getClass().getResource("totalNumberVisitorsReport.fxml"));
                //Scene scene = new Scene(loader.load());
        		Parent root = loader.load();
        		
                // Pass the current park to the next controller
                TotalNumberVisitorsReportControler nextController = loader.getController();
                nextController.setPark(park); // Assuming you have a setter for the park in the next controller
                nextController.setVisitorCounts(visitorCounts);
                nextController.setWorker(ConnectionScreenController.client.get_worker());

                Stage newStage = new Stage();
               // ((Node) event.getSource()).getScene().getWindow().hide();

                newStage.setScene(new Scene(root));
                newStage.show();
                // Capture image of the JavaFX scene
                WritableImage image = newStage.getScene().snapshot(null);
                // Convert image to a format suitable for saving (such as PNG)
                try {
                	
                    File imageFile = new File("total_number_report.png");
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
                    TransferrableData message = new TransferrableData(MessageType.ParkManagerDashboardCreateReport, new Report(this.park,month,year,mybytearray , "total number report"));
                    ConnectionScreenController.client.handleMessageFromClientUI(message);
                    message = new TransferrableData(MessageType.ParkManagerDashboardGetAllReportsForPark, this.park);
			    	ConnectionScreenController.client.handleMessageFromClientUI(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
               // newStage.show();

            } catch (IOException e) {
                e.printStackTrace();
                // Handle the exception if unable to load or show the next scene
            }
    		}
    	}
        else if ("usage report".equals(typeChoose.getValue())) {
        	if(check_if_report_created("usage report")) {
    			this.Error_send_new_report.setText("Error: Report for this month already excited");
    		}
        	else {
        	try {
        		FXMLLoader loader = new FXMLLoader(getClass().getResource("UsageReport.fxml"));
                Scene scene;
				scene = new Scene(loader.load());

                // Pass the current park to the next controller
                UsageReportController nextController = loader.getController();
                nextController.setPark(park); // Assuming you have a setter for the park in the next controller
                nextController.setTotalNumberVisitors(totalNumberVisitors);
                nextController.setWorker(ConnectionScreenController.client.get_worker());

                Stage newStage = new Stage();
               // ((Node) event.getSource()).getScene().getWindow().hide();
                newStage.setScene(scene);
                newStage.show();
                
                // Use a PauseTransition to give time for the scene to render
                PauseTransition pause = new PauseTransition(Duration.seconds(1));
                pause.setOnFinished(finishEvent -> {
                    WritableImage image = scene.snapshot(null);
                    try {
                        File imageFile = new File("usage_report.png");
                        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", imageFile);
                        System.out.println("Image saved successfully!");

                        byte[] mybytearray = convertToByteArray(imageFile);
                        int year, month; // need calendar
                        Calendar calendar = Calendar.getInstance();
                        year = calendar.get(Calendar.YEAR);
                        month = calendar.get(Calendar.MONTH) + 1;
                        TransferrableData message = new TransferrableData(MessageType.ParkManagerDashboardCreateReport, new Report(this.park,month,year,mybytearray , "usage report"));
                        ConnectionScreenController.client.handleMessageFromClientUI(message);
                        message = new TransferrableData(MessageType.ParkManagerDashboardGetAllReportsForPark, this.park);
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
    
    public void setReportStatusMassege(boolean status) {
    	if(status) {
    		successuly_created_report.setText("Report Saved");
    	}
    	else {
    		Error_send_new_report.setText("Error: DB do no saved report");
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

    @FXML
    void pressPreview(ActionEvent event) {//add check if create pressed and what report
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
    void pressReturn(ActionEvent event) {
    	  ConnectionScreenController.client.handleMessageFromClientUI(new TransferrableData(MessageType.TravlerLogOut, ConnectionScreenController.client.get_worker()));
	      openNextScene("/WorkerLoginPage/WorkerLoginPage.fxml",event); // Open scene
    }
    

    @FXML
    void pressSaveQuata(ActionEvent event) {
    	try {
    		send_Requeste_server("MaxCapacity", park.getVisitor_quota(),Integer.parseInt(QuotaTextField.getText()));
        } catch (NumberFormatException e) {
        	answer_pressSaveQuata(false);
        }
    }

    @FXML
    void pressSaveVisitTime(ActionEvent event) {
    	try {
    		send_Requeste_server("MaxStayDurationHours", park.getVisitorTimeInMinut(),Integer.parseInt(visitTimeTextField.getText()));
        } catch (NumberFormatException e) {
        	answer_pressSaveVisitTime(false);
        }
    }

    @FXML
    void pressSaveVistorGap(ActionEvent event) {
    	try {
    		send_Requeste_server("GapToMaxCapacity", park.getVisitor_gap(),Integer.parseInt(visitorGapTextField.getText()));
        } catch (NumberFormatException e) {
        	answer_pressSaveVistorGap(false);
        }
    }
    
    public void choose_answer(String option , boolean status) {
    	if(option.equals("MaxCapacity")) {
    		if(status) {
    			answer_pressSaveQuata(true);
    		}
    		else {
    			answer_pressSaveQuata(false);
    		}
    	}
    	if(option.equals("MaxStayDurationHours")) {
    		if(status) {
    			answer_pressSaveVisitTime(true);
    		}
    		else {
    			answer_pressSaveVisitTime(false);
    		}
    	}
    	if(option.equals("GapToMaxCapacity")) {
    		if(status) {
    			answer_pressSaveVistorGap(true);
    		}
    		else {
    			answer_pressSaveVistorGap(false);
    		}
    	}
    	
    }
    
    void answer_pressSaveQuata(boolean flag) {
    	if(flag) {
    		access_Quota_in_park_massege.setText("Saved");
    	}
    	else {
    		Error_Quota_in_park_message.setText("Error: Wrong format");
    	}
    	
    }
    
    void answer_pressSaveVisitTime(boolean flag) {
    	if(flag) {
    		access_visit_time_massege.setText("Saved");
    	}
    	else {
    		Error_visit_time_message.setText("Error: Wrong format");
    	}
    }
    void answer_pressSaveVistorGap(boolean flag) {
    	if(flag) {
    		access_Visitor_Gap_massege.setText("Saved");
    	}
    	else {
    		Error_Visitor_Gap_message.setText("Error: Wrong format");
    	}
    }
    
    public void getAllReservations(Object obj) {
    	@SuppressWarnings("unchecked")
		List<VisitReservation> list = (List<VisitReservation>) obj;
    	Iterator <VisitReservation> iterator = list.iterator();
    	VisitReservation res ;
//    	int numOfVisitorsPrivate = 0, numOfVisitorsFamily = 0 ,numOfVisitorsOrganized = 0;
//    	int openHour = 8,closeHour = 17;
    	int today , month;// need calendar
    	Calendar calendar = Calendar.getInstance();
        today = calendar.get(Calendar.DAY_OF_WEEK);
        month = calendar.get(Calendar.MONTH) + 1;
        
    	while (iterator.hasNext()) { //if types are right family private and organized 
      	  res = iterator.next();
      	  calendar = Calendar.getInstance();
      	  calendar.setTimeInMillis(res.getTimeofvisit().getTime());
      	  if (calendar.get(Calendar.MONTH) + 1 == month && res.getPark().getParkid() == park.getParkid()) {
      	    if(res.getGroupType().equals("Private"))
      	    	visitorCounts[0] += res.getNumofvisitors();
      	    else if(res.getGroupType().equals("Family"))
      	    	visitorCounts[1]+= res.getNumofvisitors();
      	    else if(res.getGroupType().equals("Organized"))
      	    	visitorCounts[2]+= res.getNumofvisitors();
      	  }
      	}	 
    	
//        while (iterator.hasNext()) {
//        	res = iterator.next();
//    	for (int day = 1; day < today ; day++) {
//    		for (int hour = openHour; hour < closeHour-1; hour++) {	
//    		    calendar = Calendar.getInstance();//    	 
//
//    		    calendar.setTimeInMillis(res.getTimeofvisit().getTime());
//    		    if(res.getPark().getParkid() == park.getParkid()&& calendar.get(Calendar.MONTH)== month&& calendar.get(Calendar.DAY_OF_WEEK)==day &&calendar.get(Calendar.HOUR_OF_DAY)==hour) {
//    		    	if(res.getGroupType().equals("Private"))
//    			      numOfVisitorsPrivate+= res.getNumofvisitors();
//    		    	else if(res.getGroupType().equals("Family"))
//      			      numOfVisitorsFamily+= res.getNumofvisitors();
//    		    	else if(res.getGroupType().equals("Organized"))
//        			  numOfVisitorsOrganized+= res.getNumofvisitors();
//    		    }
//    	    }
//    	   }
//        }
//    	visitorCounts[0] = numOfVisitorsPrivate;
//        visitorCounts[1] = numOfVisitorsFamily;
//        visitorCounts[2] = numOfVisitorsOrganized;
//        
        iterator = list.iterator();
        	
    	while (iterator.hasNext()) { //if types are right family private and organized 
    	  res = iterator.next();
    	  calendar = Calendar.getInstance();
    	  calendar.setTimeInMillis(res.getTimeofvisit().getTime());
    	  if (calendar.get(Calendar.MONTH) + 1 == month) {
    	    if(res.getGroupType().equals("Private"))
    	      totalNumberVisitors[0][calendar.get(Calendar.DAY_OF_MONTH)]+=res.getNumofvisitors();   
    	    else if(res.getGroupType().equals("Family"))
      	      totalNumberVisitors[1][calendar.get(Calendar.DAY_OF_MONTH)]+=res.getNumofvisitors(); 
    	    else if(res.getGroupType().equals("Organized"))
      	      totalNumberVisitors[2][calendar.get(Calendar.DAY_OF_MONTH)]+=res.getNumofvisitors(); 
    	  }
    	}	           
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
    
    public void UpdrateReporstDetails(List<Report> reports) {
    	this.reports = reports;
    	
    	ObservableList<String> datePeakercombobox = FXCollections.observableArrayList();
		for(Report rp : reports) {
			datePeakercombobox.add(String.valueOf(rp.getMounth()) +" / "+ String.valueOf(rp.getYear() + " " + rp.getReportType()));
		}
		datePeaker_combobox.setItems(datePeakercombobox);
		datePeaker_combobox.getSelectionModel().clearSelection();
		
		datePeaker_combobox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if(newValue != null) {
				for(Report rp : reports) {
					if(newValue.equals(String.valueOf(rp.getMounth()) +" / "+ String.valueOf(rp.getYear() + " " + rp.getReportType()))) {
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
    
    public void send_Requeste_server(
		String valueRequested, int currentValue, int newValue) {
    	ChangeValueRequest req = new ChangeValueRequest( ConnectionScreenController.client.get_worker(),  this.park, valueRequested,  currentValue,  newValue);
    	TransferrableData message = new TransferrableData(MessageType.ParkManagerDashboardRequestVisitorChange, req);
		 ConnectionScreenController.client.handleMessageFromClientUI(message);
		 ConnectionScreenController.client.setParkManagerDashboardController(this);
    }
    
    @SuppressWarnings("unchecked")
	public void showParks(TransferrableData data) {
    	parks = (List<Park>) data.getMessage();
		ObservableList<String> parkNames = FXCollections.observableArrayList();
		for(Park park : parks) {
			parkNames.add(park.getParkname());
		}
		choosePark.setItems(parkNames);
		choosePark.getSelectionModel().clearSelection();
		choosePark.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if(newValue != null) {
				for(Park park : parks) {
					if(newValue.equals(park.getParkname())) {
						try {
							this.park = park;
							avalibleSpaceTextField.setText(String.valueOf(park.getVisitor_quota()-park.getVisitor_gap()));
							visitorGapTextField.setText(String.valueOf(park.getVisitor_gap()));
							QuotaTextField.setText(String.valueOf(park.getVisitor_quota()));
							visitTimeTextField.setText(String.valueOf(park.getVisitorTimeInMinut()));
					    	TransferrableData message = new TransferrableData(MessageType.GetAllReservationsParkManagerDashboard, null);
					    	ConnectionScreenController.client.handleMessageFromClientUI(message);
					    	message = new TransferrableData(MessageType.ParkManagerDashboardGetAllReportsForPark, this.park);
					    	ConnectionScreenController.client.handleMessageFromClientUI(message);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		});
    }

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// Initialize ChoiceBox with options
    	ConnectionScreenController.client.setParkManagerDashboardController(this);
    	TransferrableData message = new TransferrableData(MessageType.ParkManagerDashboardShowDetails,  ConnectionScreenController.client.get_worker().getWorkerId());
		 ConnectionScreenController.client.handleMessageFromClientUI(message);
		 nameText.setText(ConnectionScreenController.client.get_worker().getId_number()); 
		 Calendar calendar = Calendar.getInstance();
		 int year , month;// need calendar
         year = calendar.get(Calendar.YEAR);
         month = calendar.get(Calendar.MONTH) + 1;
         NewReport_Text.setText(month + " / " + year);
         ObservableList<String> reportOptions = FXCollections.observableArrayList("total number report", "usage report");
         typeChoose.setItems(reportOptions);
	}
    

}





