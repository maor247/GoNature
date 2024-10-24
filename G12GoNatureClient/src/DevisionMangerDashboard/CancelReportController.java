package DevisionMangerDashboard;

import java.util.Calendar;

import common.Park;
import common.Worker;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.text.Text;

public class CancelReportController {

    @FXML
    private PieChart CancelReportPieChart;
    @FXML
    private Text report_dateText;

    @FXML
    private Text avarage_cancel_numText;

    @FXML
    private LineChart<?, ?> cancelReportChart;

    @FXML
    private Text emailText;

    @FXML
    private Text idText;

    @FXML
    private Text lastNameText;

    @FXML
    private Text nameText;

    @FXML
    private Text phoneText;
    
    private Park park;
    private Worker worker;
    private int[] Visitors;
    
//    // Constructor to receive the park
//    CancelReportController(Park park ,Worker worker,int[][][] visitors) {
//        this.park = park;
//        this.worker = worker;
//        this.Visitors = visitors;
//        
//    }
//    public CancelReportController() {//for checking
//        // Set default values for testing
//        this.park = new Park(0, "Fake Park", 0, 0, 0);
//        this.worker = new Worker("8446531", "lev@gmail.com", "555-5555", null, null, "lev", "leyfer", "123456789");
//        Random random = new Random();
//        this.Visitors = new int[2][3][31]; // Initialize the class field
//
//        // Populate the array with random numbers up to 120
//        for (int i = 0; i < 2; i++) {
//            for (int j = 0; j < 31; j++) {
//                this.Visitors[i][0][j] = random.nextInt(121); // Generates random numbers from 0 to 120
//            }
//        } 
//    }

//	public void setPark(Park park) {
//		this.park = park;
//        ParkNameText.setText(park.getParkname());
//	}

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

	public void PieNumOfCanceledVisitors(int[] Visitors) {
		this.Visitors=Visitors;	
        // Add data to the chart
		CancelReportPieChart.getData().addAll(
	       		new PieChart.Data("Canceled", Visitors[0]),
	               new PieChart.Data("Not Visited", Visitors[1])
	       ); 
		
	}
	
	public void setAvarageOfCanceled(int num) {
		this.avarage_cancel_numText.setText(String.valueOf(num));
	}
	
	public void setChartOfXY(int [][] array) {
		
        // Add data to the chart
        XYChart.Series series1 = new XYChart.Series();
        XYChart.Series series2 = new XYChart.Series();
        
		 for (int i = 0; i < 31; i++) {
			 series1.getData().add(new XYChart.Data(String.valueOf(i + 1), array[0][i])); 
			 series2.getData().add(new XYChart.Data(String.valueOf(i + 1), array[1][i]));
	        }
		 cancelReportChart.getData().addAll(series1,series2);
	}
}
