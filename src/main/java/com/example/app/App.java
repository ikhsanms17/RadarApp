package com.example.app;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.geometry.*;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.*;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;

import com.esri.arcgisruntime.symbology.TextSymbol;
import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.text.DecimalFormat;

public class App extends Application {

    private MapView mapView;
    private Group group;
    private StackPane root;
    private GraphicsOverlay graphicsOverlay;
    private Graphic scanningArea;
    private Point point, point2, posisi, centerPoint, pointCircle;
    private Graphic graphic, graphText, graphText1, graphText2, graphText3;
    private Graphic numGraphic, numGraphic1, numGraphic2, numGraphic3;
    private TextSymbol radText, radText1, radText2, radText3;
    private TextSymbol numText, numText1, numText2, numText3, numText4;
    private SimpleMarkerSymbol symbol, circle2, circle3, circle4, circle5, circle6;
    private SimpleLineSymbol stroke;
    private AnimationTimer animationTimer;
    private Label label, labelLat, labelLong, labelDistance;

    private static final int WIDTH = 1000;   // Width of the radar display
    private static final int HEIGHT = 700;  // Height of the radar display
    private double cenX = -6.8743094530729225;
    private double cenY = 107.58553101717864;
    private double progress = 0;
    private double scale = 91000;
    private double radius = 0.0535;
    private double rotationAngle = 0;

    private int numLines = 72; // Jumlah garis vertikal 1
    private int numLines1 = 24; // Jumlah garis vertikal 2
    private int numLines2 = 360; // Jumlah garis vertikal kecil
    private int numLines3 = 8; // garis +
    private double centerX = cenY - 10; // Koordinat x pusat
    private double centerY = cenX - 5; // Koordinat y pusat
    private double radLine = 265;
    private double radLine4 = 270;
    private double radLine1 = 210;
    private double radLine2 = 140;
    private double radLine3 = 70;
    private double angle = 360.0 / numLines;
    private double angle1 = 360.0 / numLines1;
    private double angle2 = 360.0 / numLines2;
    private double angle3 = 360.0 / numLines3;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // API ArcGIS for Maps
        String yourApiKey = "AAPK262c106448034a9e982fbc8b2d2cf950oXHKnb3EJwvrPGU-uBFNwy-p76PSsnT6BI0UYGyakqd5tHEa3sCXUhVv5q3g_qGm";
        ArcGISRuntimeEnvironment.setApiKey(yourApiKey);

        // create a JavaFX scene with a stack pane as the root node, and add it to the scene
        root = new StackPane();
        Scene scene = new Scene(root, WIDTH, HEIGHT, Color.TRANSPARENT);

        // create a map view to display the map and add it to the stack pane
        mapView = new MapView();
        root.getChildren().add(mapView);
        ArcGISMap map = new ArcGISMap(BasemapStyle.ARCGIS_DARK_GRAY);

        // create node group for radar display
        group = new Group();

        // add node group into node root
        root.getChildren().add(group);

        // set the map on the map view
        mapView.setMap(map);
        mapView.setViewpoint(new Viewpoint(cenX, cenY, scale));
        mapView.setOnScroll(null);
        mapView.setOnMousePressed(null);
        mapView.setOnMouseDragged(null);

        // set title, scene and show for scene
        primaryStage.setTitle("Prototype Radar App");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Create a graphics overlay to display the location A and B
        graphicsOverlay = new GraphicsOverlay();
        mapView.getGraphicsOverlays().add(graphicsOverlay);

        // add location A and B
        addObject( -6.989816573099767, 107.68786246812824, -6.838321160281938, 107.48785712513322);
        addCircle( -6.8743094530729225, 107.58553101717864);
        addNumRadar();
        addNumber();

        // Create a Label to display latitude and longitude
        label = new Label();
        label.setText("Speed Rotation: ");
        label.setTextFill(Color.WHITE);

        // Create a Label to display latitude and longitude
        labelLat = new Label();
        labelLat.setStyle("-fx-padding: 5px;");
        labelLat.setTextFill(Color.WHITE);

        labelLong = new Label();
        labelLong.setStyle("-fx-padding: 5px;");
        labelLong.setTextFill(Color.WHITE);

        labelDistance = new Label();
        labelDistance.setStyle("-fx-padding: 5px;");
        labelDistance.setTextFill(Color.WHITE);

        // create a control panel
        VBox controlsVBox = new VBox(10);
        controlsVBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0, 0, 0, 0.3)"), CornerRadii.EMPTY, Insets.EMPTY)));
        controlsVBox.setPadding(new Insets(10.0));
        controlsVBox.setMaxSize(200, 80);
        controlsVBox.getStyleClass().add("panel-region");

        // add radio buttons to the control panel
        controlsVBox.getChildren().addAll(labelLat, labelLong, labelDistance);

        // add scene view, label and control panel to the stack pane
        root.getChildren().add(controlsVBox);
        root.setAlignment(controlsVBox, Pos.TOP_LEFT);
        root.setMargin(controlsVBox, new Insets(60, 0, 0, 20));

        centerPoint = new Point ( cenY, cenX, SpatialReferences.getWgs84());

        // create the scanning area graphic
        scanningArea = createScanningArea(centerPoint, radius, rotationAngle);

        // create a symbol for the scanning area graphic (line symbol)
        SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.WHITE, 2);

        // set the symbol to the scanning area graphic
        scanningArea.setSymbol(lineSymbol);

        // add the scanning area graphic to the graphics overlay
        graphicsOverlay.getGraphics().add(scanningArea);

        // create an AnimationTimer for rotating the scanning area graphic
        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // update the rotation angle
                rotationAngle += -1;
                if (rotationAngle >= 360) {
                    rotationAngle = 0;
                }

                // update the scanning area graphic
                updateScanningArea(centerPoint, radius, rotationAngle);
            }
        };

        // start the animation
        animationTimer.start();
    }

    // Metode untuk membuat grafik overlay dan menambahkan objek poin ke dalamnya
    private void addObject(double x, double y, double x1, double y1) {
        // Create a point graphic at the specified location
        point = new Point(y, x, SpatialReferences.getWgs84());
        point2 = new Point(y1, x1, SpatialReferences.getWgs84());

        // Create a symbol for the moving object (a simple red circle)
        symbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.BLUE, 10);

        // Create the graphic with the start point and symbol
        graphic = new Graphic(point, symbol);

        // Start animation
        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Update progress (value between 0 and 1)
                progress += 0.001; // Change this value to adjust animation speed
                if (progress >= 1) {
                    progress = 0;
                }

                // Calculate current location based on progress
                double currentX = point.getX() + (point2.getX() - point.getX()) * progress;
                double currentY = point.getY() + (point2.getY() - point.getY()) * progress;
                posisi = new Point(currentX, currentY, SpatialReferences.getWgs84());

                // Set the current location for the graphic
                graphic.setGeometry(posisi);

                double distance = GeometryEngine.distanceGeodetic(posisi, centerPoint,
                        new LinearUnit(LinearUnitId.METERS),
                        new AngularUnit(AngularUnitId.DEGREES),
                        GeodeticCurveType.GEODESIC).getDistance();

                // display lat long
                DecimalFormat df = new DecimalFormat("0.000000");
                DecimalFormat df2 = new DecimalFormat("0.000");
                labelLat.setText("Latitude    : " + df.format(currentY));
                labelLong.setText("Longitude : " + df.format(currentX));
                labelDistance.setText("Distance   : " + df2.format(distance) + " m");
            }
        };

        animationTimer.start();

        // Add the graphic to the graphics overlay
        graphicsOverlay.getGraphics().add(graphic);
        graphicsOverlay.getOpacity();
    }

    private void addCircle(double cX, double cY) {
        // Create a point graphic at the specified location
        pointCircle = new Point(cY, cX, SpatialReferences.getWgs84());

        // Buat simbol untuk garis tepi (stroke)
        stroke = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.WHITE, 2.0f);

        // Create a symbol for the moving object (a simple red circle)
        circle2 = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.TRANSPARENT, 500);
        circle2.setOutline(stroke);

        circle3 = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.TRANSPARENT, 375);
        circle3.setOutline(stroke);

        circle4 = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.TRANSPARENT, 250);
        circle4.setOutline(stroke);

        circle5 = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.TRANSPARENT, 125);
        circle5.setOutline(stroke);

        circle6 = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.WHITE, 5);

        // Create the graphic with the start point and symbol
        Graphic graphic2 = new Graphic(pointCircle, circle2);
        Graphic graphic3 = new Graphic(pointCircle, circle3);
        Graphic graphic4 = new Graphic(pointCircle, circle4);
        Graphic graphic5 = new Graphic(pointCircle, circle5);
        Graphic graphic6 = new Graphic(pointCircle, circle6);

        // Add the graphic to the graphics overlay
        graphicsOverlay.getGraphics().add(graphic2);
        graphicsOverlay.getGraphics().add(graphic3);
        graphicsOverlay.getGraphics().add(graphic4);
        graphicsOverlay.getGraphics().add(graphic5);
        graphicsOverlay.getGraphics().add(graphic6);
    }

    private void addNumRadar() {
        radText = new TextSymbol(8, "1500 m", Color.WHITE,
                TextSymbol.HorizontalAlignment.LEFT, TextSymbol.VerticalAlignment.TOP);
        radText.setOffsetX(28);
        radText.setOffsetY(-5);

        radText1 = new TextSymbol(8, "3000 m", Color.WHITE,
                TextSymbol.HorizontalAlignment.LEFT, TextSymbol.VerticalAlignment.TOP);
        radText1.setOffsetX(90);
        radText1.setOffsetY(-5);

        radText2 = new TextSymbol(8, "4500 m", Color.WHITE,
                TextSymbol.HorizontalAlignment.LEFT, TextSymbol.VerticalAlignment.TOP);
        radText2.setOffsetX(152);
        radText2.setOffsetY(-5);

        radText3 = new TextSymbol(8, "6000 m", Color.WHITE,
                TextSymbol.HorizontalAlignment.LEFT, TextSymbol.VerticalAlignment.TOP);
        radText3.setOffsetX(214);
        radText3.setOffsetY(-5);

        graphText = new Graphic(pointCircle, radText);
        graphText1 = new Graphic(pointCircle, radText1);
        graphText2 = new Graphic(pointCircle, radText2);
        graphText3 = new Graphic(pointCircle, radText3);

        graphicsOverlay.getGraphics().add(graphText);
        graphicsOverlay.getGraphics().add(graphText1);
        graphicsOverlay.getGraphics().add(graphText2);
        graphicsOverlay.getGraphics().add(graphText3);
    }

    private void addNumber() {
        numText = new TextSymbol(12, "0", Color.WHITE,
                TextSymbol.HorizontalAlignment.CENTER, TextSymbol.VerticalAlignment.MIDDLE);
        numText.setOffsetX(0);
        numText.setOffsetY(265);

        numText1 = new TextSymbol(12, "90", Color.WHITE,
                TextSymbol.HorizontalAlignment.CENTER, TextSymbol.VerticalAlignment.MIDDLE);
        numText1.setOffsetX(265);
        numText1.setOffsetY(0);

        numText2 = new TextSymbol(12, "180", Color.WHITE,
                TextSymbol.HorizontalAlignment.CENTER, TextSymbol.VerticalAlignment.MIDDLE);
        numText2.setOffsetX(0);
        numText2.setOffsetY(-265);

        numText3 = new TextSymbol(12, "270", Color.WHITE,
                TextSymbol.HorizontalAlignment.CENTER, TextSymbol.VerticalAlignment.MIDDLE);
        numText3.setOffsetX(-268);
        numText3.setOffsetY(0);

        numGraphic = new Graphic(pointCircle, numText);
        numGraphic1 = new Graphic(pointCircle, numText1);
        numGraphic2 = new Graphic(pointCircle, numText2);
        numGraphic3 = new Graphic(pointCircle, numText3);

        graphicsOverlay.getGraphics().add(numGraphic);
        graphicsOverlay.getGraphics().add(numGraphic1);
        graphicsOverlay.getGraphics().add(numGraphic2);
        graphicsOverlay.getGraphics().add(numGraphic3);

    }

    private void updateScanningArea(Point centerPoint, double radius, double rotationAngle) {
        // remove the old scanning area graphic
        graphicsOverlay.getGraphics().remove(scanningArea);

        // create the new scanning area graphic
        scanningArea = createScanningArea(centerPoint, radius, rotationAngle);

        // create a symbol for the scanning area graphic (line symbol)
        SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.WHITE, 2);

        // set the symbol to the new scanning area graphic
        scanningArea.setSymbol(lineSymbol);

        // add the new scanning area graphic to the graphics overlay
        graphicsOverlay.getGraphics().add(scanningArea);
    }

    private Graphic createScanningArea(Point centerPoint, double radius, double rotationAngle) {
        // create the start point of the scanning area line
        Point startPoint = new Point(centerPoint.getX(), centerPoint.getY(), SpatialReferences.getWgs84());

        // calculate the end point of the scanning area line based on the rotation angle
        double endPointX = centerPoint.getX() + radius * Math.cos(Math.toRadians(rotationAngle));
        double endPointY = centerPoint.getY() + radius * Math.sin(Math.toRadians(rotationAngle));
        Point endPoint = new Point(endPointX, endPointY, SpatialReferences.getWgs84());

        // create a polyline from the start and end points
        PointCollection points = new PointCollection(SpatialReferences.getWgs84());
        points.add(startPoint);
        points.add(endPoint);
        Polyline polyline = new Polyline(points);

        return new Graphic(polyline);
    }

    @Override
    public void stop() {
        if (mapView != null) {
            mapView.dispose();
        }
    }
}