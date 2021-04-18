package sofwareEngineeringProject;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JFrame;  
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;  
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;  
  
public class LineChart extends JFrame {  
  
  private static final long serialVersionUID = 1L;  
  
  public LineChart(String title,HashMap<String, Integer> exceptionList1, String count) {  
    super(title);  
    // Create dataset  
    DefaultCategoryDataset dataset = createDataset(exceptionList1,count);  
    // Create chart  
    JFreeChart chart = ChartFactory.createLineChart(  
            "literate programming features", // Chart title  
            "Distribution of cell types", // X-Axis Label  
            "code cells", // Y-Axis Label  
            dataset ,
            PlotOrientation.VERTICAL,
            false,
            false,
            false
            );   
  
    ChartPanel panel = new ChartPanel(chart);  
    setContentPane(panel);  
  }  
  
  private DefaultCategoryDataset createDataset(HashMap<String, Integer> exceptionList, String count) {  
  
    String series1 = "Visitor";    
  
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();  
  
    TreeMap<String, Integer> sorted = new TreeMap<>();
    sorted.putAll(exceptionList);
    for (Map.Entry<String, Integer> entry : sorted.entrySet()) {
  	  if(entry.getKey().equalsIgnoreCase("beginning"))
  		  dataset.addValue( (Integer)entry.getValue()*10 , count , entry.getKey() );
  	  if(entry.getKey().equalsIgnoreCase("Middle"))
  		  dataset.addValue( (Integer)entry.getValue()*5 , count , entry.getKey() );
  	  else
  		  dataset.addValue( (Integer)entry.getValue()/5 , count , entry.getKey() );
    }


  
    return dataset;  
  }  
  
//  public static void main(String[] args) {  
//    SwingUtilities.invokeLater(() -> {  
//      LineChart example = new LineChart("literate programming features");  
//      example.setAlwaysOnTop(true);  
//      example.pack();  
//      example.setSize(600, 400);  
//      example.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);  
//      example.setVisible(true);  
//    });  
//  }  
}  