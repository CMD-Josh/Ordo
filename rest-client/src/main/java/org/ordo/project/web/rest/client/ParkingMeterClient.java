/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ordo.project.web.rest.client;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javax.ws.rs.core.MediaType;
import org.ordo.project.model.Entity;



/**
 *
 * @author Josh
 */
public class ParkingMeterClient extends Application{
    
    // 
    String host;
    Integer groupScheduleId;
    RestClientJerseyImpl restClient;
    Calendar date;
    SimpleDateFormat df;
    Timer tick; // Used to keep track of the time
    List<Entity> scheduleList;
    
    // not intened for the actual project, we are to assume card transactions are successful
    // Assignment breif mensions to not worry about implementing bank api but does want a lunn check at least
    boolean isPaying = false;
    int counter = 0;
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        df = new SimpleDateFormat("HH:mm:ss - EE");
        tick = new Timer("clock");
        
        Stage window = primaryStage;
        window.setTitle("Parking Meter Interface");
        
        // Node instanciations 
        
        Text txtSetupServ = new Text("Input server address:");
        Text txtSetupId = new Text("Input Group Schedule Id:");
        Text txtTime = new Text();
        Text txtSelectStayLength = new Text("Intended stay duration: ");
        Text txtHours = new Text();
        Text txtPrice = new Text();
        Text txtPriceFinal = new Text();
        Text txtPaymentNotice = new Text("Please scan your card\n total price of ticket:");
        
        Button btnSetup = new Button("Accept");
        Button btnPurchase = new Button("Purchase Ticket");
        Button btnRefresh = new Button("Refresh");
        Button btnBack = new Button("Return");
        
        TextField itxtSetupServ = new TextField();
        TextField itxtSetupId = new TextField();
        
        Slider slDurr = new Slider(1,24,1);
        
        ObservableList<String> schedules = FXCollections.observableArrayList();
        
        ListView<String> lsSchedule = new ListView<>(schedules); 
        lsSchedule.setFocusTraversable(false);
        
        // Layout configuration
        GridPane setupLayout = new GridPane();
        GridPane scheduleLayout = new GridPane();
        GridPane paymentLayout = new GridPane();
        
        setupLayout.setAlignment(Pos.CENTER);
        scheduleLayout.setAlignment(Pos.CENTER);
        paymentLayout.setAlignment(Pos.CENTER);
        
        setupLayout.setMinSize(800,600);
        scheduleLayout.setMinSize(800, 600);
        paymentLayout.setMinSize(800, 600);
        
        setupLayout.setVgap(5); 
        setupLayout.setHgap(5); 
        scheduleLayout.setVgap(5); 
        scheduleLayout.setHgap(5); 
        paymentLayout.setVgap(5); 
        paymentLayout.setHgap(5); 
        
        // Add nodes to layout
        setupLayout.add(txtSetupServ, 0, 0);
        setupLayout.add(txtSetupId, 1, 0);
        setupLayout.add(itxtSetupServ, 0, 1);
        setupLayout.add(itxtSetupId, 1, 1);
        setupLayout.add(btnSetup, 2, 0);
        scheduleLayout.add(txtTime, 0, 0,1,1);
        scheduleLayout.add(lsSchedule, 0, 1,2,1);
        scheduleLayout.add(txtSelectStayLength, 0, 2,1,1);
        scheduleLayout.add(slDurr, 1, 2,1,1);
        scheduleLayout.add(txtHours, 0, 3,1,1);
        scheduleLayout.add(txtPrice, 1, 3,1,1);
        scheduleLayout.add(btnPurchase, 0, 4,1,1);
        scheduleLayout.add(btnRefresh, 1, 4,1,1);
        
        paymentLayout.add(txtPaymentNotice,0,0);
        paymentLayout.add(txtPriceFinal,0,1);
        paymentLayout.add(btnBack,0,2);
        
        // Scene building
        Scene scene = new Scene(setupLayout);
        
        window.setResizable(false);
        window.setScene(scene);
        window.show();
        
        // Node event listeners
        
        slDurr.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                txtHours.textProperty().set("Hours: " + String.valueOf((int) slDurr.getValue()));
                txtPrice.textProperty().set("£" + String.valueOf(calcPrice((int) slDurr.getValue())));
            }
        });
        
        btnSetup.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                restClient = new RestClientJerseyImpl(itxtSetupServ.getText(), MediaType.APPLICATION_XML_TYPE);
                groupScheduleId = Integer.valueOf(itxtSetupId.getText());
                scene.setRoot(scheduleLayout);
            }
            
        });
        
        btnPurchase.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                txtPriceFinal.textProperty().set(txtPrice.textProperty().get());
                isPaying = true;
                scene.setRoot(paymentLayout);
            }
            
        });
        
        btnRefresh.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                try{
                    scheduleList = restClient.retrieveAllEntities(Integer.SIZE).getEntityList().getEntities();
                    schedules.clear();
                    for(Entity e : scheduleList){
                        schedules.add(e.getTimeStart()+ " : "+e.getTimeEnd() + " = £" + e.getPrice());
                    }
                } catch(Exception e){ // Implement logging system to log error
                    txtTime.setText("Error, contact manager"); // TODO create a new scene displaying error message
                }
            }
            
        });
        
        btnBack.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                isPaying = false;
                scene.setRoot(scheduleLayout);
            }
            
        });
        
        
        tick.scheduleAtFixedRate(new TimerTask() { // For keeping track of time
            @Override
            public void run() {
                counter += 1;
                date = Calendar.getInstance();
                txtTime.setText("Current time: " + df.format(date.getTime()));
                if(isPaying){
                    System.out.println("Awaiting card..."); // Would keep running until contactless card is detected
                    luhnCheck("00000000000"); // Checks for lunn
                }
                
                if(counter >= 1200){ // Update the schedule every hour
                    try{
                        scheduleList = restClient.retrieveAllEntities(Integer.SIZE).getEntityList().getEntities();
                        schedules.clear();
                        for(Entity e : scheduleList){
                            schedules.add(e.getTimeStart()+ " : "+e.getTimeEnd() + " = £" + e.getPrice());
                        }
                    } catch(Exception e){ // Implement logging system to log error
                        txtTime.setText("Error, contact manager"); // TODO create a new scene displaying error message
                    }
                }
              }
          }, 1000L, 1000L);
    }
    
    private double calcPrice(Integer duration){ // Duration is in hours
        double retPrice = 0.0;
        SimpleDateFormat ldf = new SimpleDateFormat("E");
        DecimalFormat ldecf = new DecimalFormat(".00");
        
        for(Entity e: scheduleList){
            if(ldf.format(new Date()).equals(e.getTimeStart().substring(8,11))){
                int startScheduleHours = Integer.parseInt(e.getTimeStart().substring(0,2)) * 60 * 60 * 1000;
                int startScheduleMinuets = Integer.parseInt(e.getTimeStart().substring(3,5)) * 60 * 1000;
                
                int endScheduleHours = Integer.parseInt(e.getTimeEnd().substring(0,2)) * 60 * 60 * 1000;
                int endScheduleMinuets = Integer.parseInt(e.getTimeEnd().substring(3,5)) * 60 * 1000;
                
                int hours = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) * 60 * 60 * 1000;
                int minuets = Calendar.getInstance().get(Calendar.MINUTE)  * 60 * 1000;
                
                int endHours = (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + duration) * 60 * 60 * 1000;
                
                //System.out.println("ID: "+e.getId()+" Start: "+startScheduleHours + startScheduleMinuets+" End: " + endScheduleHours + endScheduleMinuets
                 //       + "           Start: "+hours + minuets+"End: "+endHours + minuets);
                
                if(startScheduleHours + startScheduleMinuets < hours + minuets && endScheduleHours + endScheduleMinuets > hours + minuets ||
                   startScheduleHours + startScheduleMinuets < endHours + minuets && endScheduleHours + endScheduleMinuets > endHours + minuets ||
                   startScheduleHours + startScheduleMinuets > hours + minuets && endScheduleHours + endScheduleMinuets < endHours + minuets){
                    retPrice += Double.valueOf(e.getPrice());
                }
            }
        }
        ldecf.setRoundingMode(RoundingMode.CEILING);
        return Double.valueOf(ldecf.format(retPrice));
    }
    
    private boolean luhnCheck(String cardNumber){
        String cn = cardNumber;
        int checksum = 0;
        for(int i = 1; i < cn.length(); i++){
            if(i%2 == 0){
                if(((int) cn.charAt(i)) * 2 > 9){
                    checksum += (((int) cn.charAt(i)) * 2) / 9;
                }else{
                    checksum += (int) cn.charAt(i) * 2;
                }
            }else{
                checksum += (int) cn.charAt(i);
            }
        }
        if(checksum%10 == 0){
            return true; // Bank API would be implemented here to handle transactions
        }else{
            return false;
        }
    }
    
    @Override
    public void stop(){
        tick.cancel();
        System.out.println("Closing Application...");
    }
    
    public static void main(String[] args){
        launch(args);
    }
    
}
