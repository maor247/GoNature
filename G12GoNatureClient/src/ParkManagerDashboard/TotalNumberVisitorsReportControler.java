package ParkManagerDashboard;

import java.net.URL;
import java.util.Calendar;
import java.util.ResourceBundle;

import common.Park;
import common.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.text.Text;

public class TotalNumberVisitorsReportControler implements Initializable {
	
    @FXML
    private Text organizedText;
    
    @FXML
    private Text report_dateText;
    
    @FXML
    private Text privateText;
    
    @FXML
    private Text familyText;

    @FXML
    private Text ParkNameText;

    @FXML
    private Text idText;

    @FXML
    private Text lastNameText;

    @FXML
    private Text nameText;

    @FXML
    private Text phoneText;
    
    @FXML
    private Text emailText;

    @FXML
    private PieChart chart;
    
    private Park park;
    private Worker worker;
    private int[] visitorCounts;
    
//    // Constructor to receive the park
//    public TotalNumberVisitorsReportControler(Park park ,Worker worker,int[] visitorCounts) {
//        this.park = park;
//        this.worker = worker;
//        this.visitorCounts = visitorCounts;
//    }
//    c TotalNumberVisitorsReportControler() {//for checking
//        // Set default values for testing
//        this.park = new Park(0, "Fake Park", 0, 0, 0);
//        this.worker = new Worker("8446531", "lev@gmail.com", "555-5555", null, null, "lev", "leyfer", "123456789");
//        this.visitorCounts = new int[]{10, 20, 30}; // Fake visitor counts
//    }
//    

	public void setPark(Park park) {
		this.park = park;
		 ParkNameText.setText(park.getParkname());
	}

	public void setWorker(Worker worker) {
		
		Calendar calendar = Calendar.getInstance();
		int year , month;// need calendar
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        report_dateText.setText(month + " / " + year);
        
        
		this.worker = worker;
		 idText.setText(worker.getId_number());
	     lastNameText.setText(worker.getLastName());
	     nameText.setText(worker.getFirstName());
	     phoneText.setText(worker.getPhone_number());
	     emailText.setText(worker.getEmail_addres());
	}

	public void setVisitorCounts(int[] visitorCounts) {
		this.visitorCounts = visitorCounts;
        privateText.setText(""+visitorCounts[0]);
        familyText.setText(""+visitorCounts[1]);
        organizedText.setText(""+visitorCounts[2]);
        
        chart.getData().addAll(
        		new PieChart.Data("Private", visitorCounts[0]),
                new PieChart.Data("Family", visitorCounts[1]),
                new PieChart.Data("Organized", visitorCounts[2])
        ); 
	}
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
	}
}
