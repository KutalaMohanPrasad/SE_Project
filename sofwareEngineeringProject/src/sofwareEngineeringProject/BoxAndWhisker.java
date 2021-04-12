package sofwareEngineeringProject;

import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.BoxAndWhiskerToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.data.statistics.BoxAndWhiskerCategoryDataset;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class BoxAndWhisker extends ApplicationFrame  {

	public BoxAndWhisker(final String title,ArrayList<Integer> fileLengths) {

        super(title);
        
        final BoxAndWhiskerCategoryDataset dataset = createSampleDataset(fileLengths);

        final CategoryAxis xAxis = new CategoryAxis("");
        final NumberAxis yAxis = new NumberAxis("Length");
        yAxis.setAutoRangeIncludesZero(false);
        final BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();
        renderer.setFillBox(false);
        renderer.setToolTipGenerator(new BoxAndWhiskerToolTipGenerator());
        final CategoryPlot plot = new CategoryPlot(dataset, xAxis, yAxis, renderer);

        final JFreeChart chart = new JFreeChart(
            "File Names",
            plot
        );
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(450, 570));
        setContentPane(chartPanel);

    }

    
    private BoxAndWhiskerCategoryDataset createSampleDataset(ArrayList<Integer> fileLengths) {
        
        final int seriesCount = 1;
        final int categoryCount = 1;
       // final int entityCount = 22;
        
        final DefaultBoxAndWhiskerCategoryDataset dataset 
            = new DefaultBoxAndWhiskerCategoryDataset();
        for (int i = 0; i < seriesCount; i++) {
            for (int j = 0; j < categoryCount; j++) {
                final List list = new ArrayList();
                // add some values...
                for (int k = 0; k < fileLengths.size(); k++) {
                   // final double value1 = 10.0 + Math.random() * 3;
                    list.add(new Double(fileLengths.get(k)));
//                    final double value2 = 11.25 + Math.random(); // concentrate values in the middle
//                    list.add(new Double(value2));
                }
               
                dataset.add(list, "Markdown File Names", "");
            }
            
        }

        return dataset;
    }


}
