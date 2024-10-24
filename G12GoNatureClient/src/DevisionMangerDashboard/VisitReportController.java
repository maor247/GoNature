package DevisionMangerDashboard;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import common.Park;
import common.Worker;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.text.Text;

public class VisitReportController {

    @FXML
    private PieChart Family_enteryTime;
    @FXML
    private Text report_dateText;
    @FXML
    private PieChart Organized_enteryTime;

    @FXML
    private PieChart private_enteryTime;
    
    @FXML
    private PieChart total_duration;
    
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
    private int[][][] Visitors;
    private List<PieChart.Data> privateList = new ArrayList<>();
    private List<PieChart.Data> familyList = new ArrayList<>();
    private List<PieChart.Data> organizedList = new ArrayList<>();
    private List<PieChart.Data> totalDurationlist = new ArrayList<>();
//    // Constructor to receive the park
//    VisitReportController(Park park ,Worker worker,int[][][] visitors) {
//        this.park = park;
//        this.worker = worker;
//        this.Visitors = visitors;
//        
//    }
//    public VisitReportController() {//for checking
//        // Set default values for testing
//        this.park = new Park(0, "Fake Park", 0, 0, 0);
//        this.worker = new Worker("8446531", "lev@gmail.com", "555-5555", null, null, "lev", "leyfer", "123456789");
//        Random random = new Random();
//        this.Visitors = new int[2][3][31]; // Initialize the class field
//
//        // Populate the array with random numbers up to 120
//        for (int i = 0; i < 3; i++) {
//            for (int j = 0; j < 31; j++) {
//                this.Visitors[0][i][j] = random.nextInt(121); // Generates random numbers from 0 to 120
//                this.Visitors[1][i][j] = random.nextInt(121);
//            }
//        } 
//    }

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

	public void setTotalNumberVisitors(int[][][] Visitors) {
		this.Visitors=Visitors;	
//		// Add data to the chart
//        XYChart.Series series1 = new XYChart.Series();
//        XYChart.Series series2 = new XYChart.Series();
//        XYChart.Series series3 = new XYChart.Series();
//        XYChart.Series series11 = new XYChart.Series();
//        XYChart.Series series22 = new XYChart.Series();
//        XYChart.Series series33 = new XYChart.Series();
//
//        // Add data to the chart
//        for (int i = 0; i < 24; i++) {
//            series1.getData().add(new XYChart.Data(String.valueOf(i + 1), Visitors[0][0][i]));
//            series2.getData().add(new XYChart.Data(String.valueOf(i + 1), Visitors[0][1][i]));
//            series3.getData().add(new XYChart.Data(String.valueOf(i + 1), Visitors[0][2][i])); 
//            series11.getData().add(new XYChart.Data(String.valueOf(i + 1), Visitors[1][0][i]));
//            series22.getData().add(new XYChart.Data(String.valueOf(i + 1), Visitors[1][1][i]));
//            series33.getData().add(new XYChart.Data(String.valueOf(i + 1), Visitors[1][2][i])); 
//        }
//        //chart.getData().addAll(series1,series11,series2,series22,series3,series33);
		
        
        for(int i = 0 ; i < Visitors[0][0].length ; i++) {
        	if(Visitors[0][0][i] != 0) {
        		privateList.add(new PieChart.Data(String.valueOf(i) + ":00", Visitors[0][0][i]));
        	}
        }
        this.private_enteryTime.getData().addAll(privateList);
        
        for(int i = 0 ; i < Visitors[0][1].length ; i++) {
        	if(Visitors[0][1][i] != 0) {
        		this.familyList.add(new PieChart.Data(String.valueOf(i) + ":00", Visitors[0][1][i]));
        	}
        }
        this.Family_enteryTime.getData().addAll(familyList);
        
        for(int i = 0 ; i < Visitors[0][2].length ; i++) {
        	if(Visitors[0][2][i] != 0) {
        		this.organizedList.add(new PieChart.Data(String.valueOf(i) + ":00", Visitors[0][2][i]));
        	}
        }
       this.Organized_enteryTime.getData().addAll(organizedList);
       
       
       total_duration.getData().addAll(
       		new PieChart.Data("Private", Visitors[1][0][0]),
               new PieChart.Data("Family", Visitors[1][1][1]),
               new PieChart.Data("Organized", Visitors[1][2][2])
       ); 
	}
}
