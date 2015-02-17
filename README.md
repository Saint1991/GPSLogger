GeoLogger
=========

##Introduction
GeoLogger is an application that was developed to collect user's trajectory data for the purpose of academic research.  
This application collects the data below.

* The pairs of latitude and logitude with timestamp
* The place where user checked in with timestamp
* Companions for each trajectory

User ID is issued at the time when the user's first boot of the application.  
** User ID is created by using UUID and timestamp so it doesn't identify individuals. **  

##Functions

###Record
In this activity, you can start and stop logging, and map view shows you how logging is being taken place.  
During logging, you can additionary record your check-in place.  
Click "CHECK IN" button and POI confirmation activity starts and you will be shown nearby POIs on FourSquare.  
You can choose one from the list and it will be recorded and displayed by "Flag Marker".  
If you can't find appropriate POI on the list, you can choose "free-form input" by clicking the button "Input By Free Form" that is placed on the bottom of the view.  

###Log
In this activity, you can look back your logs that you recorded.  
By using player control, you can play your trajectory data.  

###POI Search
In this activity, you can find your nearby POIs.  
You not necessarily need to input any words.  
In that case, the result will be only based on your current place.  
 
###Settings
You can set some parameters for logging and positioning and about checkin result.  
You can alse confirm your userID here.  

