package rosetracker.dataclasses;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class BCOwnerChange {
	
	public String PackageID = "";
	public String CurrentOwner = "";
	public Timestamp TimeStamp = null;
	public double CurrentLatitude = 0.0;
	public double CurrentLongitude = 0.0;
	public String RouteToNextOwner = "";
	
	// converts the data object to a map. Necessary for the conversion to an EntityData.
	public Map<String,Object> ToMap(){
		
		Map<String,Object> back = new HashMap<String,Object>();
		
		back.put("PackageID" , PackageID);
		back.put("TimeStamp", TimeStamp);
		back.put("OwnerID", CurrentOwner);
		back.put("CurrentLatitude", CurrentLatitude);
		back.put("CurrentLongitude", CurrentLongitude);
		back.put("RouteToNextOwner", RouteToNextOwner);
		
		return back;
	}
	
	// returns the properties of the data object. Necessary for the conversion to an EntityData.
	public static List<String> GetKeys() {
		
		List<String> back = new LinkedList<String>();
		
		back.add("PackageID");
		back.add("TimeStamp");
		back.add("OwnerID");
		back.add("CurrentLatitude");
		back.add("CurrentLongitude");
		back.add("RouteToNextOwner");
		
		return back;
		
	}
	
	// override of equals. Necessary for the comparator 
	public Boolean equals(BCOwnerChange other) {
		
		return	PackageID.equals(other.PackageID) &&
				TimeStamp.equals(other.TimeStamp) &&
				CurrentOwner.equals(other.CurrentOwner) &&
				CurrentLatitude == other.CurrentLatitude &&
				CurrentLongitude == other.CurrentLongitude;
		
	}
	
	// compares a attribute of this elements with the given object. 
	// used to implement the filter expression of the query function
	public int attrCompareTo(Object toCompare, String attr) {
		
		if(attr.equals("PackageID")) {
			String sToCompare = (String) toCompare;
			return PackageID.compareTo(sToCompare);
		}
		if(attr.equals("OwnerID")) {
			String sToCompare = (String) toCompare;
			return CurrentOwner.compareTo(sToCompare);
		}
		if(attr.equals("TimeStamp")) {
			GregorianCalendar gcToCompare = (GregorianCalendar) toCompare;
			Timestamp tToCompare = new Timestamp(gcToCompare.getTimeInMillis());
			return TimeStamp.compareTo(tToCompare);
		}
		if(attr.equals("CurrentLatitude")) {
			
			BigDecimal bdToComare = (BigDecimal) toCompare;
			double dToCompare = bdToComare.doubleValue();
			
			if(CurrentLatitude == dToCompare) {
				return 0;
			}
			if(CurrentLatitude < dToCompare) {
				return -1;
			}
			if(CurrentLatitude > dToCompare) {
				return 1;
			}
		}
		if(attr.equals("CurrentLongitude")) {
			
			BigDecimal bdToComare = (BigDecimal) toCompare;
			double dToCompare = bdToComare.doubleValue();
			
			if(CurrentLongitude == dToCompare) {
				return 0;
			}
			if(CurrentLongitude < dToCompare) {
				return -1;
			}
			if(CurrentLongitude > dToCompare) {
				return 1;
			}
		}
		
		return 0;
	}
	
	// compares to another BCOwnerChange on a given attribute
	public int compareTo(BCOwnerChange toCompare, String attr) {
		
		if(attr.equals("PackageID")) {
			
			return PackageID.compareTo(toCompare.PackageID);
		}
		if(attr.equals("OwnerID")) {
			
			return CurrentOwner.compareTo(toCompare.CurrentOwner);
		}
		if(attr.equals("TimeStamp")) {
			
			return TimeStamp.compareTo(toCompare.TimeStamp);
		}
		if(attr.equals("CurrentLatitude")) {
			
			if(CurrentLatitude == toCompare.CurrentLatitude) {
				return 0;
			}
			if(CurrentLatitude < toCompare.CurrentLatitude) {
				return -1;
			}
			if(CurrentLatitude > toCompare.CurrentLatitude) {
				return 1;
			}
		}
		if(attr.equals("CurrentLongitude")) {
			
			if(CurrentLongitude == toCompare.CurrentLongitude) {
				return 0;
			}
			if(CurrentLongitude < toCompare.CurrentLongitude) {
				return -1;
			}
			if(CurrentLongitude > toCompare.CurrentLongitude) {
				return 1;
			}
		}
		
		return 0;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
