module com.example.app {
    // require ArcGIS Runtime module
    requires com.esri.arcgisruntime;

    // requires JavaFX modules that the application uses
    requires javafx.graphics;

    // requires SLF4j module
    requires org.slf4j.nop;

    requires org.apache.commons.io;


    exports com.example.app;
}