package com.example.app;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.*;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.*;
import com.esri.arcgisruntime.mapping.view.*;
import com.esri.arcgisruntime.symbology.*;

import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class App extends Application {

    private MapView mapView;
    private ArcGISMap map;
    private Scene scene;
    private Group group;
    private StackPane root;
    private GraphicsOverlay overlayMap, overlayObject, overlay2;
    private Graphic scanningArea, graphic, graphic2, updateObject, updateObject2;
    private Point point, point2, point3,  point4, posisi, posisi2, centerPoint, range, range1, range2, range3;
    private SimpleMarkerSymbol symbol, warningSymbol, dangerSymbol, symbol2;
    private SimpleLineSymbol stroke, lineSymbol, lineRad;
    private SimpleFillSymbol fillRadar, fillRange, fillRange1;
    private VBox displayInfo, displayButton;
    private AnimationTimer animationTimer;
    private Label label, labelLat, labelLong, labelDistance, labelSudut;
    private PointCollection points, pointCircle, pointCircle1, pointCircle2, pointCircle3;
    private DecimalFormat df, df2;
    private Polyline polyCircle, polyCircle1, polyCircle2, polyCircle3;
    private Point tA, tA1, tA2, tA3, tA4, tA5, tA6, tA7, tA8, tA9, tA10, titikAwal;
    private Graphic graphText, graphText1, graphText2, graphText3, graphRange, graphRange1;
    private Graphic graphCircle, graphCircle1, graphCircle2, graphCircle3;
    private Graphic numGraphic, numGraphic1, numGraphic2, numGraphic3, numGraphic4, numGraphic5,
            numGraphic6, numGraphic7, numGraphic8, numGraphic9, numGraphic10, numGraphic11;
    private TextSymbol radText, radText1, radText2, radText3;
    private TextSymbol numText, numText1, numText2, numText3, numText4,
            numText5, numText6, numText7, numText8, numText9, numText10, numText11;

    private static final int WIDTH = 1000;   // Width of the radar display
    private static final int HEIGHT = 700;  // Height of the radar display
    private final double cenX = -6.8743094530729225;
    private final double cenY = 107.58553101717864;
    private double progress = 0;
    private final double scale = 91000;
    private final double radius = 0.0535; // 6 / 111
    private double rotationAngle = 0;
    private double distance, lat, lon, sudut, degrees, sudutRad;
    private double distance2, lat2, lon2, sudut2, degrees2, sudutRad2;
    private boolean logic, logic2;

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
        scene = new Scene(root, WIDTH, HEIGHT, Color.TRANSPARENT);

        // create a map view to display the map and add it to the stack pane
        mapView = new MapView();
        root.getChildren().add(mapView);
        map = new ArcGISMap(BasemapStyle.ARCGIS_DARK_GRAY);

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
        overlayObject = new GraphicsOverlay();
        overlay2 = new GraphicsOverlay();
        overlayMap = new GraphicsOverlay();
        mapView.getGraphicsOverlays().add(overlayMap);
        mapView.getGraphicsOverlays().add(overlay2);
        mapView.getGraphicsOverlays().add(overlayObject);

        // Create a Label to display latitude and longitude
        label = new Label();
        label.setText("Speed Rotation: ");
        label.setTextFill(Color.WHITE);

        // Create a Label to display latitude and longitude
        labelSudut = new Label("Sudut        : ");
        labelSudut.setStyle("-fx-padding: 5px;");
        labelSudut.setTextFill(Color.WHITE);

        labelDistance = new Label("Distance   : ");
        labelDistance.setStyle("-fx-padding: 5px;");
        labelDistance.setTextFill(Color.WHITE);

        // Create a Label to display latitude and longitude
        labelLat = new Label("Latitude    : " );
        labelLat.setStyle("-fx-padding: 5px;");
        labelLat.setTextFill(Color.WHITE);

        labelLong = new Label("Longitude : ");
        labelLong.setStyle("-fx-padding: 5px;");
        labelLong.setTextFill(Color.WHITE);

        // create a control panel
        displayInfo = new VBox(10);
        displayInfo.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0, 0, 0, 0.3)"), CornerRadii.EMPTY, Insets.EMPTY)));
        displayInfo.setPadding(new Insets(10.0));
        displayInfo.setMaxSize(200, 80);
        displayInfo.getStyleClass().add("panel-region");

        // add radio buttons to the control panel
        displayInfo.getChildren().addAll(labelSudut, labelDistance, labelLat, labelLong);

        // add scene view, label and control panel to the stack pane
        root.getChildren().add(displayInfo);
        root.setAlignment(displayInfo, Pos.TOP_LEFT);
        root.setMargin(displayInfo, new Insets(60, 0, 0, 20));

        // create a control panel
        displayButton = new VBox(10);
        displayButton.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0, 0, 0, 0.3)"), CornerRadii.EMPTY, Insets.EMPTY)));
        displayButton.setPadding(new Insets(10.0));
        displayButton.setMaxSize(200, 80);
        displayButton.getStyleClass().add("panel-region");

        // add scene view, label and control panel to the stack pane
        root.getChildren().add(displayButton);
        root.setAlignment(displayButton, Pos.TOP_RIGHT);
        root.setMargin(displayButton, new Insets(60, 20, 0, 0));

        // add location A and B for movement object
//        object( -6.932581890834847, 107.64582546015241, -6.812463080435314, 107.55275371743387);
        object( -6.857902970144943, 107.56870633355435, -6.857804514931573, 107.63053552961236);
        addObject( -6.938633031473232, 107.55341813907535,
                -6.820136022973438, 107.60990126818064);


        centerPoint = new Point ( cenY, cenX, SpatialReferences.getWgs84());
        addCircle();
        addNumRadar();
        addNumber();

        // create the scanning area graphic
        scanningArea = createScanningArea(centerPoint, radius, rotationAngle);

        // create a symbol for the scanning area graphic (line symbol)
        fillRadar = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, 0x30FFFFFF, null);

        // set the symbol to the scanning area graphic
        scanningArea.setSymbol(fillRadar);

        // add the scanning area graphic to the graphics overlay
        overlayMap.getGraphics().add(scanningArea);

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

        lineRad = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.WHITE, 2);

        for (int i = 0; i < 360; i+=2){
            // create the start point of the scanning area line
            double startX = centerPoint.getX() + 0.0536 * Math.cos(Math.toRadians(i));
            double startY = centerPoint.getY() + 0.0536 * Math.sin(Math.toRadians(i));
            Point startPoint = new Point(startX, startY, SpatialReferences.getWgs84());

            // calculate the end point of the scanning area line based on the rotation angle
            double endX = centerPoint.getX() + 0.0518 * Math.cos(Math.toRadians(i));
            double endY = centerPoint.getY() + 0.0518 * Math.sin(Math.toRadians(i));
            Point endPoint = new Point(endX, endY, SpatialReferences.getWgs84());

            // create a polyline from the start and end points
            PointCollection points = new PointCollection(SpatialReferences.getWgs84());
            points.add(startPoint);
            points.add(endPoint);
            Polyline polyline = new Polyline(points);
            Graphic lineGraph = new Graphic(polyline, lineRad);
            overlayMap.getGraphics().add(lineGraph);
        }

        for (int i = 0; i < 360; i+=10){
            // create the start point of the scanning area line
            double startX1 = centerPoint.getX() + 0.0553 * Math.cos(Math.toRadians(i));
            double startY1 = centerPoint.getY() + 0.0553 * Math.sin(Math.toRadians(i));
            Point startPoint1 = new Point(startX1, startY1, SpatialReferences.getWgs84());

            // calculate the end point of the scanning area line based on the rotation angle
            double endX1 = centerPoint.getX() + 0.051 * Math.cos(Math.toRadians(i));
            double endY1 = centerPoint.getY() + 0.051 * Math.sin(Math.toRadians(i));
            Point endPoint1 = new Point(endX1, endY1, SpatialReferences.getWgs84());

            // create a polyline from the start and end points
            PointCollection points1 = new PointCollection(SpatialReferences.getWgs84());
            points1.add(startPoint1);
            points1.add(endPoint1);
            Polyline polyline1 = new Polyline(points1);
            Graphic lineGraph1 = new Graphic(polyline1, lineRad);
            overlayMap.getGraphics().add(lineGraph1);
        }

        for (int i = 0; i < 360; i+=30){
            // create the start point of the scanning area line
            double startX2 = centerPoint.getX() + 0.0405 * Math.cos(Math.toRadians(i));
            double startY2 = centerPoint.getY() + 0.0405 * Math.sin(Math.toRadians(i));
            Point startPoint2 = new Point(startX2, startY2, SpatialReferences.getWgs84());

            // calculate the end point of the scanning area line based on the rotation angle
            double endX2 = centerPoint.getX() + 0.043 * Math.cos(Math.toRadians(i));
            double endY2 = centerPoint.getY() + 0.043 * Math.sin(Math.toRadians(i));
            Point endPoint2 = new Point(endX2, endY2, SpatialReferences.getWgs84());

            // create a polyline from the start and end points
            PointCollection points2 = new PointCollection(SpatialReferences.getWgs84());
            points2.add(startPoint2);
            points2.add(endPoint2);
            Polyline polyline2 = new Polyline(points2);
            Graphic lineGraph2 = new Graphic(polyline2, lineRad);
            overlayMap.getGraphics().add(lineGraph2);
        }

        for (int i = 0; i < 360; i+=30){
            // create the start point of the scanning area line
            double startX3 = centerPoint.getX() + 0.027 * Math.cos(Math.toRadians(i));
            double startY3 = centerPoint.getY() + 0.027 * Math.sin(Math.toRadians(i));
            Point startPoint3 = new Point(startX3, startY3, SpatialReferences.getWgs84());

            // calculate the end point of the scanning area line based on the rotation angle
            double endX3 = centerPoint.getX() + 0.0295 * Math.cos(Math.toRadians(i));
            double endY3 = centerPoint.getY() + 0.0295 * Math.sin(Math.toRadians(i));
            Point endPoint3 = new Point(endX3, endY3, SpatialReferences.getWgs84());

            // create a polyline from the start and end points
            PointCollection points3 = new PointCollection(SpatialReferences.getWgs84());
            points3.add(startPoint3);
            points3.add(endPoint3);
            Polyline polyline3 = new Polyline(points3);
            Graphic lineGraph3 = new Graphic(polyline3, lineRad);
            overlayMap.getGraphics().add(lineGraph3);
        }

        for (int i = 0; i < 360; i+=30){
            // create the start point of the scanning area line
            double startX4 = centerPoint.getX() + 0.0138 * Math.cos(Math.toRadians(i));
            double startY4 = centerPoint.getY() + 0.0138 * Math.sin(Math.toRadians(i));
            Point startPoint4 = new Point(startX4, startY4, SpatialReferences.getWgs84());

            // calculate the end point of the scanning area line based on the rotation angle
            double endX4 = centerPoint.getX() + 0.0163 * Math.cos(Math.toRadians(i));
            double endY4 = centerPoint.getY() + 0.0163 * Math.sin(Math.toRadians(i));
            Point endPoint4 = new Point(endX4, endY4, SpatialReferences.getWgs84());

            // create a polyline from the start and end points
            PointCollection points4 = new PointCollection(SpatialReferences.getWgs84());
            points4.add(startPoint4);
            points4.add(endPoint4);
            Polyline polyline4 = new Polyline(points4);
            Graphic lineGraph4 = new Graphic(polyline4, lineRad);
            overlayMap.getGraphics().add(lineGraph4);
        }

        for (int i = 0; i < 360; i+=90){
            // create the start point of the scanning area line
            double startX5 = centerPoint.getX() + 0.0000 * Math.cos(Math.toRadians(i));
            double startY5 = centerPoint.getY() + 0.0000 * Math.sin(Math.toRadians(i));
            Point startPoint5 = new Point(startX5, startY5, SpatialReferences.getWgs84());

            // calculate the end point of the scanning area line based on the rotation angle
            double endX5 = centerPoint.getX() + 0.0015 * Math.cos(Math.toRadians(i));
            double endY5 = centerPoint.getY() + 0.0015 * Math.sin(Math.toRadians(i));
            Point endPoint5 = new Point(endX5, endY5, SpatialReferences.getWgs84());

            // create a polyline from the start and end points
            PointCollection points5 = new PointCollection(SpatialReferences.getWgs84());
            points5.add(startPoint5);
            points5.add(endPoint5);
            Polyline polyline5 = new Polyline(points5);
            Graphic lineGraph5 = new Graphic(polyline5, lineRad);
            overlayMap.getGraphics().add(lineGraph5);
        }

        mapView.setOnMouseClicked(event -> {
            if (event.isStillSincePress()) {
                // Hanya tangani event saat tombol mouse dilepas setelah diklik
                Point2D clickLocation = new Point2D(event.getX(), event.getY());

                // Melakukan identifikasi terhadap grafik yang diklik
                ListenableFuture<IdentifyGraphicsOverlayResult> identifyResult = mapView.identifyGraphicsOverlayAsync(
                        overlayObject, clickLocation, 10, false);
                identifyResult.addDoneListener(() -> {
                    try {
                        IdentifyGraphicsOverlayResult graphicsOverlayResult = identifyResult.get();
                        List<Graphic> graphics = graphicsOverlayResult.getGraphics();

                        if (!graphics.isEmpty()) {
                            Graphic selectedGraphic = graphics.get(0);
                            // Proses pemilihan grafik yang diklik
                            showObjectInfo(selectedGraphic);
                        }
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    } catch (ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                });

                // Melakukan identifikasi terhadap grafik yang diklik
                ListenableFuture<IdentifyGraphicsOverlayResult> result = mapView.identifyGraphicsOverlayAsync(
                        overlay2, clickLocation, 10, false);
                identifyResult.addDoneListener(() -> {
                    try {
                        IdentifyGraphicsOverlayResult graphicsOverlayResult = result.get();
                        List<Graphic> graphics = graphicsOverlayResult.getGraphics();

                        if (!graphics.isEmpty()) {
                            Graphic selectedGraphic = graphics.get(0);
                            // Proses pemilihan grafik yang diklik
                            showObjectInfo2(selectedGraphic);
                        }
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    } catch (ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        });
    }

    // overlay map and radar
    private void addCircle() {
        // Buat simbol untuk garis tepi (stroke)
        stroke = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.WHITE, 2.0f);
        fillRange = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, 0x01FFFF00, null);
        fillRange1 = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, 0x01FF0000, null);

        // warning symbol with yellow
        warningSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, 0x40FFFF00, 250);
        // Create the graphic with the start point and symbol
        graphRange = new Graphic(centerPoint, warningSymbol);
        graphRange.setVisible(false);
        // Add the graphic to the graphics overlay
        overlayMap.getGraphics().add(graphRange);

        // danger symbol with red
        dangerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, 0x40FF0000, 125);
        // Create the graphic with the start point and symbol
        graphRange1 = new Graphic(centerPoint, dangerSymbol);
        graphRange1.setVisible(false);
        // Add the graphic to the graphics overlay
        overlayMap.getGraphics().add(graphRange1);

        for(int i = 0 ; i <= 360 ; i++){
            double cX = centerPoint.getX() + 0.0536 * Math.cos(Math.toRadians(i));
            double cY = centerPoint.getY() + 0.0536 * Math.sin(Math.toRadians(i));
            Point circle = new Point(cX, cY, SpatialReferences.getWgs84());

            double cenX = centerPoint.getX() + 0.0536 * Math.cos(Math.toRadians(1 + i));
            double cenY = centerPoint.getY() + 0.0536 * Math.sin(Math.toRadians(1 + i));
            Point circle2 = new Point(cenX, cenY, SpatialReferences.getWgs84());

            pointCircle = new PointCollection(SpatialReferences.getWgs84());
            pointCircle.add(circle);
            pointCircle.add(circle2);
            polyCircle= new Polyline(pointCircle);
            graphCircle = new Graphic(polyCircle, stroke);
            overlayMap.getGraphics().add(graphCircle);
        }

        for(int i = 0 ; i <= 360 ; i++){
            double cX = centerPoint.getX() + 0.0405 * Math.cos(Math.toRadians(i));
            double cY = centerPoint.getY() + 0.0405 * Math.sin(Math.toRadians(i));
            Point circle = new Point(cX, cY, SpatialReferences.getWgs84());

            double cenX = centerPoint.getX() + 0.0405 * Math.cos(Math.toRadians(1 + i));
            double cenY = centerPoint.getY() + 0.0405 * Math.sin(Math.toRadians(1 + i));
            Point circle2 = new Point(cenX, cenY, SpatialReferences.getWgs84());

            pointCircle1 = new PointCollection(SpatialReferences.getWgs84());
            pointCircle1.add(circle);
            pointCircle1.add(circle2);
            polyCircle1= new Polyline(pointCircle1);
            graphCircle1 = new Graphic(polyCircle1, stroke);
            overlayMap.getGraphics().add(graphCircle1);
        }

        for(int i = 0 ; i <= 360 ; i++){
            double cX = centerPoint.getX() + 0.027 * Math.cos(Math.toRadians(i));
            double cY = centerPoint.getY() + 0.027 * Math.sin(Math.toRadians(i));
            range = new Point(cX, cY, SpatialReferences.getWgs84());

            double cenX = centerPoint.getX() + 0.027 * Math.cos(Math.toRadians(1 + i));
            double cenY = centerPoint.getY() + 0.027 * Math.sin(Math.toRadians(1 + i));
            range1 = new Point(cenX, cenY, SpatialReferences.getWgs84());

            pointCircle2 = new PointCollection(SpatialReferences.getWgs84());
            pointCircle2.add(range);
            pointCircle2.add(range1);
            polyCircle2 = new Polyline(pointCircle2);
            graphCircle2 = new Graphic(polyCircle2, stroke);
            overlayMap.getGraphics().add(graphCircle2);
        }

        for(int i = 0 ; i <= 360 ; i++){
            double cX = centerPoint.getX() + 0.0138 * Math.cos(Math.toRadians(i));
            double cY = centerPoint.getY() + 0.0138 * Math.sin(Math.toRadians(i));
            range2 = new Point(cX, cY, SpatialReferences.getWgs84());

            double cenX = centerPoint.getX() + 0.0138 * Math.cos(Math.toRadians(1 + i));
            double cenY = centerPoint.getY() + 0.0138 * Math.sin(Math.toRadians(1 + i));
            range3 = new Point(cenX, cenY, SpatialReferences.getWgs84());

            pointCircle3 = new PointCollection(SpatialReferences.getWgs84());
            pointCircle3.add(range2);
            pointCircle3.add(range3);
            polyCircle3 = new Polyline(pointCircle3);
            graphCircle3 = new Graphic(polyCircle3, stroke);
            overlayMap.getGraphics().add(graphCircle3);
        }
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
        radText3.setOffsetX(205);
        radText3.setOffsetY(-5);

        graphText = new Graphic(centerPoint, radText);
        graphText1 = new Graphic(centerPoint, radText1);
        graphText2 = new Graphic(centerPoint, radText2);
        graphText3 = new Graphic(centerPoint, radText3);

        overlayMap.getGraphics().add(graphText);
        overlayMap.getGraphics().add(graphText1);
        overlayMap.getGraphics().add(graphText2);
        overlayMap.getGraphics().add(graphText3);
    }

    private void addNumber() {

        numText = new TextSymbol(10, "0", Color.WHITE,
                TextSymbol.HorizontalAlignment.CENTER, TextSymbol.VerticalAlignment.MIDDLE);
        numText.setOffsetY(270);
        numText.setAngle(0);

        numText1 = new TextSymbol(10, "30", Color.WHITE,
                TextSymbol.HorizontalAlignment.CENTER, TextSymbol.VerticalAlignment.MIDDLE);
        numText1.setOffsetY(270);
        numText1.setAngle(30);

        numText2 = new TextSymbol(10, "60", Color.WHITE,
                TextSymbol.HorizontalAlignment.CENTER, TextSymbol.VerticalAlignment.MIDDLE);
        numText2.setOffsetY(270);
        numText2.setAngle(60);

        numText3 = new TextSymbol(10, "90", Color.WHITE,
                TextSymbol.HorizontalAlignment.CENTER, TextSymbol.VerticalAlignment.MIDDLE);
        numText3.setOffsetY(270);
        numText3.setAngle(90);

        numText4 = new TextSymbol(10, "120", Color.WHITE,
                TextSymbol.HorizontalAlignment.CENTER, TextSymbol.VerticalAlignment.MIDDLE);
        numText4.setOffsetY(270);
        numText4.setAngle(120);

        numText5 = new TextSymbol(10, "150", Color.WHITE,
                TextSymbol.HorizontalAlignment.CENTER, TextSymbol.VerticalAlignment.MIDDLE);
        numText5.setOffsetY(270);
        numText5.setAngle(150);

        numText6 = new TextSymbol(10, "180", Color.WHITE,
                TextSymbol.HorizontalAlignment.CENTER, TextSymbol.VerticalAlignment.MIDDLE);
        numText6.setOffsetY(270);
        numText6.setAngle(180);

        numText7 = new TextSymbol(10, "210", Color.WHITE,
                TextSymbol.HorizontalAlignment.CENTER, TextSymbol.VerticalAlignment.MIDDLE);
        numText7.setOffsetY(270);
        numText7.setAngle(210);

        numText8 = new TextSymbol(10, "240", Color.WHITE,
                TextSymbol.HorizontalAlignment.CENTER, TextSymbol.VerticalAlignment.MIDDLE);
        numText8.setOffsetY(270);
        numText8.setAngle(240);

        numText9 = new TextSymbol(10, "270", Color.WHITE,
                TextSymbol.HorizontalAlignment.CENTER, TextSymbol.VerticalAlignment.MIDDLE);
        numText9.setOffsetY(270);
        numText9.setAngle(270);

        numText10 = new TextSymbol(10, "300", Color.WHITE,
                TextSymbol.HorizontalAlignment.CENTER, TextSymbol.VerticalAlignment.MIDDLE);
        numText10.setOffsetY(270);
        numText10.setAngle(300);

        numText11 = new TextSymbol(10, "330", Color.WHITE,
                TextSymbol.HorizontalAlignment.CENTER, TextSymbol.VerticalAlignment.MIDDLE);
        numText11.setOffsetY(270);
        numText11.setAngle(330);

        numGraphic = new Graphic(centerPoint, numText);
        numGraphic1 = new Graphic(centerPoint, numText1);
        numGraphic2 = new Graphic(centerPoint, numText2);
        numGraphic3 = new Graphic(centerPoint, numText3);
        numGraphic4 = new Graphic(centerPoint, numText4);
        numGraphic5 = new Graphic(centerPoint, numText5);
        numGraphic6 = new Graphic(centerPoint, numText6);
        numGraphic7 = new Graphic(centerPoint, numText7);
        numGraphic8 = new Graphic(centerPoint, numText8);
        numGraphic9 = new Graphic(centerPoint, numText9);
        numGraphic10 = new Graphic(centerPoint, numText10);
        numGraphic11 = new Graphic(centerPoint, numText11);

        overlayMap.getGraphics().add(numGraphic);
        overlayMap.getGraphics().add(numGraphic1);
        overlayMap.getGraphics().add(numGraphic2);
        overlayMap.getGraphics().add(numGraphic3);
        overlayMap.getGraphics().add(numGraphic4);
        overlayMap.getGraphics().add(numGraphic5);
        overlayMap.getGraphics().add(numGraphic6);
        overlayMap.getGraphics().add(numGraphic7);
        overlayMap.getGraphics().add(numGraphic8);
        overlayMap.getGraphics().add(numGraphic9);
        overlayMap.getGraphics().add(numGraphic10);
        overlayMap.getGraphics().add(numGraphic11);

    }

    private void updateScanningArea(Point centerPoint, double radius, double rotationAngle) {
        // remove the old scanning area graphic
        overlayMap.getGraphics().remove(scanningArea);

        // create the new scanning area graphic
        scanningArea = createScanningArea(centerPoint, radius, rotationAngle);

        // create a symbol for the scanning area graphic (line symbol)
        lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.WHITE, 2);

        // set the symbol to the new scanning area graphic
        scanningArea.setSymbol(fillRadar);

        // add the new scanning area graphic to the graphics overlay
        overlayMap.getGraphics().add(scanningArea);
    }

    private Graphic createScanningArea(Point centerPoint, double radius, double rotationAngle) {

        // create the start point of the scanning area line
        titikAwal = new Point(centerPoint.getX(), centerPoint.getY(), SpatialReferences.getWgs84());

        double eX = centerPoint.getX() + radius * Math.cos(Math.toRadians(rotationAngle));
        double eY = centerPoint.getY() + radius * Math.sin(Math.toRadians(rotationAngle));
        tA = new Point(eX, eY, SpatialReferences.getWgs84());

        double eX1 = centerPoint.getX() + radius * Math.cos(Math.toRadians(rotationAngle + 3));
        double eY1 = centerPoint.getY() + radius * Math.sin(Math.toRadians(rotationAngle + 3));
        tA1 = new Point(eX1, eY1, SpatialReferences.getWgs84());

        double eX2 = centerPoint.getX() + radius * Math.cos(Math.toRadians(rotationAngle + 6));
        double eY2 = centerPoint.getY() + radius * Math.sin(Math.toRadians(rotationAngle + 6));
        tA2 = new Point(eX2, eY2, SpatialReferences.getWgs84());

        double eX3 = centerPoint.getX() + radius * Math.cos(Math.toRadians(rotationAngle + 9));
        double eY3 = centerPoint.getY() + radius * Math.sin(Math.toRadians(rotationAngle + 9));
        tA3 = new Point(eX3, eY3, SpatialReferences.getWgs84());

        double eX4 = centerPoint.getX() + radius * Math.cos(Math.toRadians(rotationAngle + 12));
        double eY4 = centerPoint.getY() + radius * Math.sin(Math.toRadians(rotationAngle + 12));
        tA4 = new Point(eX4, eY4, SpatialReferences.getWgs84());

        double eX5 = centerPoint.getX() + radius * Math.cos(Math.toRadians(rotationAngle + 15));
        double eY5 = centerPoint.getY() + radius * Math.sin(Math.toRadians(rotationAngle + 15));
        tA5 = new Point(eX5, eY5, SpatialReferences.getWgs84());

        double eX6 = centerPoint.getX() + radius * Math.cos(Math.toRadians(rotationAngle + 18));
        double eY6 = centerPoint.getY() + radius * Math.sin(Math.toRadians(rotationAngle + 18));
        tA6 = new Point(eX6, eY6, SpatialReferences.getWgs84());

        double eX7 = centerPoint.getX() + radius * Math.cos(Math.toRadians(rotationAngle + 21));
        double eY7 = centerPoint.getY() + radius * Math.sin(Math.toRadians(rotationAngle + 21));
        tA7 = new Point(eX7, eY7, SpatialReferences.getWgs84());

        double eX8 = centerPoint.getX() + radius * Math.cos(Math.toRadians(rotationAngle + 24));
        double eY8 = centerPoint.getY() + radius * Math.sin(Math.toRadians(rotationAngle + 24));
        tA8 = new Point(eX8, eY8, SpatialReferences.getWgs84());

        double eX9 = centerPoint.getX() + radius * Math.cos(Math.toRadians(rotationAngle + 27));
        double eY9 = centerPoint.getY() + radius * Math.sin(Math.toRadians(rotationAngle + 27));
        tA9 = new Point(eX9, eY9, SpatialReferences.getWgs84());

        double eX10 = centerPoint.getX() + radius * Math.cos(Math.toRadians(rotationAngle + 30));
        double eY10 = centerPoint.getY() + radius * Math.sin(Math.toRadians(rotationAngle + 30));
        tA10 = new Point(eX10, eY10, SpatialReferences.getWgs84());

        // create a polyline from the start and end points
        points = new PointCollection(SpatialReferences.getWgs84());
        points.add(titikAwal);
        points.add(tA);
        points.add(tA1);
        points.add(tA2);
        points.add(tA3);
        points.add(tA4);
        points.add(tA5);
        points.add(tA6);
        points.add(tA7);
        points.add(tA8);
        points.add(tA9);
        points.add(tA10);
        Polygon polygon= new Polygon(points);
        return new Graphic(polygon);
    }

    // logic object 1
    private void addObject(double x, double y, double x1, double y1) {
        // Create a point graphic at the specified location
        point = new Point(y, x, SpatialReferences.getWgs84());
        point2 = new Point(y1, x1, SpatialReferences.getWgs84());

        // Create a symbol for the moving object (a simple red circle)
        symbol = new SimpleMarkerSymbol();
        symbol.setStyle(SimpleMarkerSymbol.Style.CIRCLE);
        symbol.setColor(Color.BLUE);
        symbol.setSize(10);

        // Create the graphic with the start point and symbol
        graphic = new Graphic(point, symbol);
        graphic.setVisible(false);

        // Add the graphic to the graphics overlay
        overlayObject.getGraphics().add(graphic);
        overlayObject.getOpacity();

        // Start animation
        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Update progress (value between 0 and 1)
                progress += 0.0001; // Change this value to adjust animation speed
                if (progress >= 1) {
                    progress = 0;
                }

                // Calculate current location based on progress
                double currentX = point.getX() + (point2.getX() - point.getX()) * progress;
                double currentY = point.getY() + (point2.getY() - point.getY()) * progress;
                posisi = new Point(currentX, currentY, SpatialReferences.getWgs84());

                // Set the current location for the graphic
                graphic.setGeometry(posisi);

                logic();
            }
        };
        animationTimer.start();
    }

    private void logic() {
        // get distance object
        distance = GeometryEngine.distanceGeodetic(posisi, centerPoint,
                new LinearUnit(LinearUnitId.KILOMETERS),
                new AngularUnit(AngularUnitId.DEGREES),
                GeodeticCurveType.GEODESIC).getDistance();

        degrees = GeometryEngine.distanceGeodetic(posisi, centerPoint,
                new LinearUnit(LinearUnitId.KILOMETERS),
                new AngularUnit(AngularUnitId.DEGREES),
                GeodeticCurveType.GEODESIC).getAzimuth2();

        sudut = degrees;
        if (sudut < 0) {
            sudut += 360;
        }

        //   set lat lon object
        sudutRad = Math.toRadians(degrees);
        lat = centerPoint.getY() + ((distance * Math.sin(sudutRad) / 111));
        lon = centerPoint.getX() + ((distance * Math.cos(sudutRad) / 111));

        df = new DecimalFormat("0.000000");
        df2 = new DecimalFormat("0.000");

        // logic for object
        logic = GeometryEngine.intersects(graphic.getGeometry(), scanningArea.getGeometry());
        if (logic) {
            graphic.setVisible(true);
            updateObject();
        } else {
            graphic.setVisible(false);
        }
        logicDist();
    }

    private void updateObject(){
        // remove the old scanning area graphic
        overlayObject.getGraphics().remove(updateObject);
        // create the new scanning area graphic
        updateObject =  new Graphic(graphic.getGeometry(), symbol);
        // add the new scanning area graphic to the graphics overlay
        overlayObject.getGraphics().add(updateObject);
    }

    private void showObjectInfo(Graphic clickedObject) {
        // Hapus konten sebelumnya dari VBox
        displayButton.getChildren().clear();

        if (clickedObject != null) {
            // Tampilkan informasi objek dalam Label atau komponen lain
            Label infoLabel = new Label(clickedObject.toString());
            infoLabel.setText("Choose Object 1");
            infoLabel.setTextFill(Color.WHITE);
            Button hostile = new Button("Hostile");
            hostile.setOnAction(event ->{
                symbol.setColor(Color.RED);
            });
            Button friend = new Button("Friend");
            friend.setOnAction(event -> {
                symbol.setColor(Color.GREEN);
            });
            Button netral = new Button("Netral");
            netral.setOnAction(event -> {
                symbol.setColor(Color.BLUE);
            });
            displayButton.getChildren().addAll(infoLabel, hostile, friend, netral);

            labelSudut.setText("Sudut        : " + df2.format(sudut) + "°");
            labelDistance.setText("Distance   : " + df2.format(distance) + " km");
            labelLat.setText("Latitude    : " + df.format(lat));
            labelLong.setText("Longitude : " + df.format(lon));
        }
    }

    // logic object 2
    private void object(double x, double y, double x1, double y1) {
        // Create a point graphic at the specified location
        point3 = new Point(y, x, SpatialReferences.getWgs84());
        point4 = new Point(y1, x1, SpatialReferences.getWgs84());

        // Create a symbol for the moving object (a simple red circle)
        symbol2 = new SimpleMarkerSymbol();
        symbol2.setStyle(SimpleMarkerSymbol.Style.CIRCLE);
        symbol2.setColor(Color.BLUE);
        symbol2.setSize(10);

        // Create the graphic with the start point and symbol
        graphic2 = new Graphic(point3, symbol2);
        graphic2.setVisible(false);

        // Add the graphic to the graphics overlay
        overlay2.getGraphics().add(graphic2);
        overlay2.getOpacity();

        // Start animation
        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Update progress (value between 0 and 1)
                progress += 0.0001; // Change this value to adjust animation speed
                if (progress >= 1) {
                    progress = 0;
                }

                // Calculate current location based on progress
                double currentX = point3.getX() + (point4.getX() - point3.getX()) * progress;
                double currentY = point3.getY() + (point4.getY() - point3.getY()) * progress;
                posisi2 = new Point(currentX, currentY, SpatialReferences.getWgs84());

                // Set the current location for the graphic
                graphic2.setGeometry(posisi2);

                logic2();
            }
        };
        animationTimer.start();
    }

    private void logic2() {
        // get distance object
        distance2 = GeometryEngine.distanceGeodetic(posisi2, centerPoint,
                new LinearUnit(LinearUnitId.KILOMETERS),
                new AngularUnit(AngularUnitId.DEGREES),
                GeodeticCurveType.GEODESIC).getDistance();

        degrees2 = GeometryEngine.distanceGeodetic(posisi2, centerPoint,
                new LinearUnit(LinearUnitId.KILOMETERS),
                new AngularUnit(AngularUnitId.DEGREES),
                GeodeticCurveType.GEODESIC).getAzimuth2();

        sudut2 = degrees2;
        if (sudut2 < 0) {
            sudut2 += 360;
        }

        //   set lat lon object
        sudutRad2 = Math.toRadians(degrees2);
        lat2 = centerPoint.getY() + ((distance2 * Math.sin(sudutRad2) / 111));
        lon2 = centerPoint.getX() + ((distance2 * Math.cos(sudutRad2) / 111));

        // logic for object
        logic2 = GeometryEngine.intersects(graphic2.getGeometry(), scanningArea.getGeometry());
        if (logic2) {
            graphic2.setVisible(true);
            updateObject2();
        } else {
            graphic2.setVisible(false);
        }
    }

    private void updateObject2(){
        // remove the old scanning area graphic
        overlay2.getGraphics().remove(updateObject2);
        // create the new scanning area graphic
        updateObject2 =  new Graphic(graphic2.getGeometry(), symbol2);
        // add the new scanning area graphic to the graphics overlay
        overlay2.getGraphics().add(updateObject2);
    }

    private void showObjectInfo2(Graphic clickedObject) {
        // Hapus konten sebelumnya dari VBox
        displayButton.getChildren().clear();

        if (clickedObject != null) {
            // Tampilkan informasi objek dalam Label atau komponen lain
            Label infoLabel = new Label(clickedObject.toString());
            infoLabel.setText("Choose Object 2");
            infoLabel.setTextFill(Color.WHITE);
            Button hostile = new Button("Hostile");
            hostile.setOnAction(event ->{
                symbol2.setColor(Color.RED);
            });
            Button friend = new Button("Friend");
            friend.setOnAction(event -> {
                symbol2.setColor(Color.GREEN);
            });
            Button netral = new Button("Netral");
            netral.setOnAction(event -> {
                symbol2.setColor(Color.BLUE);
            });
            displayButton.getChildren().addAll(infoLabel, hostile, friend, netral);

            labelSudut.setText("Sudut        : " + df2.format(sudut2) + "°");
            labelDistance.setText("Distance   : " + df2.format(distance2) + " km");
            labelLat.setText("Latitude    : " + df.format(lat2));
            labelLong.setText("Longitude : " + df.format(lon2));
        }
    }

    // logic pelengkap
    private void logicDist(){
        if (distance <= 3.000){
            graphRange.setVisible(true);
            graphRange1.setVisible(true);
        } else if (distance2 <= 3.000){
            graphRange.setVisible(true);
            graphRange1.setVisible(true);
        } else {
            graphRange.setVisible(false);
            graphRange1.setVisible(false);
        }
    }

    @Override
    public void stop() {
        if (mapView != null) {
            mapView.dispose();
        }
    }
}