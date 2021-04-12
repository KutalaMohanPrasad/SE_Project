package sofwareEngineeringProject;

import java.util.HashMap;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities; 

public class BarChart extends ApplicationFrame {
	
	public BarChart(String chartTitle, String y_axis_title, String x_axis_title,HashMap<String, Integer> exceptionList1, String count) {
		super(chartTitle);
		HashMap<String, Integer> exceptionList=exceptionList1;
		JFreeChart barChart = ChartFactory.createBarChart(
		         chartTitle,           
		         x_axis_title,            
		         y_axis_title,            
		         createDataset(exceptionList,count),          
		         PlotOrientation.VERTICAL,           
		         true, true, false);
		         
		      ChartPanel chartPanel = new ChartPanel( barChart );        
		      chartPanel.setPreferredSize(new java.awt.Dimension( 560 , 367 ) );        
		      setContentPane( chartPanel );
	}

	private CategoryDataset createDataset(HashMap<String, Integer> exceptionList, String count) {
		
		
	     // final String fiat = "Total Files Executed:"+count;          
	      final DefaultCategoryDataset dataset = 
	      new DefaultCategoryDataset( );  
	      for (Map.Entry<String, Integer> entry : exceptionList.entrySet()) {
	      dataset.addValue( (Integer)entry.getValue() , count , entry.getKey() );
	      }
	      return dataset; 
	   }
	   
	  // public static void main( String[ ] args ) {
//		   BarChart chart = new BarChart("Execution Results Chart","Count","Exceptions and Errors");
//	      chart.pack( );        
//	      RefineryUtilities.centerFrameOnScreen( chart );        
//	      chart.setVisible( true ); 
	  // }

}
