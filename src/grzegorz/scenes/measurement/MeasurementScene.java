package grzegorz.scenes.measurement;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;

public class MeasurementScene {

    @FXML
    private Tab chartTab;

    @FXML
    private AnchorPane chartPane;

    @FXML
    private LineChart<Integer, Double> chart;


//    @FXML
//    public void initialize() {
//        loadMeasurementChartData(1000.0, 10);
//    }

    public void start() {
        loadMeasurementChartData(1000.0, 10);
    }

    private void loadMeasurementChartData(double keyLength, int distance) {
        XYChart.Series<Integer, Double> series = new LineChart.Series<>();

        series.getData().add(new LineChart.Data<>(1, Math.log(1 / keyLength)));
        for (int i = distance; i <= keyLength; i += distance) {
            series.getData().add(new LineChart.Data<>(i, Math.log(i / keyLength)));
        }

        chart.getData().add(series);
        chart.setCreateSymbols(false);
        chart.setTitle("Measure of security");
    }


    // TODO use entropy chart
    private void loadEntropyChartData(double keyLength, int distance) {
        XYChart.Series<Integer, Double> series = new LineChart.Series<>();

        series.getData().add(new LineChart.Data<>(1, 1 / keyLength * Math.log(1 / keyLength)));
        for (int i = distance; i <= keyLength; i += distance) {
            series.getData().add(new LineChart.Data<>(i, -1 * i / keyLength * Math.log(i / keyLength)));
        }

        chart.getData().add(series);
        chart.setCreateSymbols(false);
        chart.setTitle("Entropy of security");
    }
}
