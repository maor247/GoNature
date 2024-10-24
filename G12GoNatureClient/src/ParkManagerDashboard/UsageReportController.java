package ParkManagerDashboard;

import common.Park;
import common.Worker;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.text.Text;

import java.util.Calendar;
import java.util.Random;



public class UsageReportController {
	
    @FXML
    private LineChart<?, ?> chart;
    @FXML
    private Text report_dateText;
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
    
    private Park park;
    private Worker worker;
    private int[][] totalNumberVisitors;
    
//    // Constructor to receive the park
//    UsageReportController(Park park ,Worker worker,int[][] visitorCounts) {
//        this.park = park;
//        this.worker = worker;
//        this.totalNumberVisitors = visitorCounts;
//        
//    }
//    public UsageReportController() {//for checking
//        // Set default values for testing
////        this.park = new Park(0, "Fake Park", 0, 0, 0);
////        this.worker = new Worker("8446531", "lev@gmail.com", "555-5555", null, null, "lev", "leyfer", "123456789");
////        Random random = new Random();
////        this.totalNumberVisitors = new int[3][31]; // Initialize the class field
////
////        // Populate the array with random numbers up to 120
////        for (int i = 0; i < 3; i++) {
////            for (int j = 0; j < 31; j++) {
////                this.totalNumberVisitors[i][j] = random.nextInt(121); // Generates random numbers from 0 to 120
////            }
////        } 
//    }
    
    @FXML
    public void initialize() {
    	// Set the text values using the park and worker informatio
    	
        
    }

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
        
        
        idText.setText(worker.getId_number());
        lastNameText.setText(worker.getLastName());
        nameText.setText(worker.getFirstName());
        phoneText.setText(worker.getPhone_number());
        emailText.setText(worker.getEmail_addres());
	}

	public void setTotalNumberVisitors(int[][] totalNumberVisitors2) {
		this.totalNumberVisitors=totalNumberVisitors2;
		
        // Add data to the chart
        XYChart.Series series1 = new XYChart.Series();
        XYChart.Series series2 = new XYChart.Series();
        XYChart.Series series3 = new XYChart.Series();

        // Add data to the chart
        for (int i = 0; i < 31; i++) {
            series1.getData().add(new XYChart.Data(String.valueOf(i + 1), totalNumberVisitors[0][i]));
            series2.getData().add(new XYChart.Data(String.valueOf(i + 1), totalNumberVisitors[1][i]));
            series3.getData().add(new XYChart.Data(String.valueOf(i + 1), totalNumberVisitors[2][i]));   
        }
        chart.getData().addAll(series1,series2,series3);
		
	}


}
