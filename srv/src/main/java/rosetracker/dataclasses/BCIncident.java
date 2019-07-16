package rosetracker.dataclasses;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BCIncident {
	
	public String PackageID = "";
	public Timestamp TimeStamp = null;
	public double CurrentLatitude = 0.0;
	public double CurrentLongitude = 0.0;
	public double MinTemperature = 0.0;
	public double MaxTemperature = 0.0;
	public double MinHumidity = 0.0;
	public double MaxHumidity = 0.0;
	public String Description = "";
	
	public String getPackageID(){
		return this.PackageID;
	}
	
	public Map<String,Object> ToMap(){
		
		Map<String,Object> back = new HashMap<String,Object>();
		
		back.put("PackageID" , PackageID);
		back.put("TimeStamp", TimeStamp);
		back.put("CurrentLatitude", CurrentLatitude);
		back.put("CurrentLongitude", CurrentLongitude);
		back.put("MinTemperature", MinTemperature);
		back.put("MaxTemperature", MaxTemperature);
		back.put("MinHumidity", MinHumidity);
		back.put("MaxHumidity", MaxHumidity);
		back.put("Description", Description);
		
		return back;
	}
	
	public static List<String> GetKeys() {
		
		List<String> back = new LinkedList<String>();
		
		back.add("PackageID");
		back.add("TimeStamp");
		back.add("CurrentLatitude");
		back.add("CurrentLongitude");
		back.add("MinTemperature");
		back.add("MaxTemperature");
		back.add("MinHumidity");
		back.add("MaxHumidity");
		back.add("Description");
		
		return back;
		
	}
	
	public Boolean equals(BCIncident other) {
		
		return	PackageID.equals(other.PackageID) &&
				TimeStamp.equals(other.TimeStamp) &&
				CurrentLatitude == other.CurrentLatitude &&
				CurrentLongitude == other.CurrentLongitude &&
				MinTemperature == other.MinTemperature &&
				MaxTemperature == other.MaxTemperature &&
				MinHumidity == other.MinHumidity &&
				MaxHumidity == other.MaxHumidity &&
				Description == other.Description;
		
	}
	
	public int attrCompareTo(Object toCompare, String attr) {
		
		if(attr.equals("PackageID")) {
			String sToCompare = (String) toCompare;
			return PackageID.compareTo(sToCompare);
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
		if(attr.equals("MinTemperature")) {
			
			BigDecimal bdToComare = (BigDecimal) toCompare;
			double dToCompare = bdToComare.doubleValue();
			
			if(MinTemperature == dToCompare) {
				return 0;
			}
			if(MinTemperature < dToCompare) {
				return -1;
			}
			if(MinTemperature > dToCompare) {
				return 1;
			}
		}
		if(attr.equals("MaxTemperature")) {
			
			BigDecimal bdToComare = (BigDecimal) toCompare;
			double dToCompare = bdToComare.doubleValue();
			
			if(MaxTemperature == dToCompare) {
				return 0;
			}
			if(MaxTemperature < dToCompare) {
				return -1;
			}
			if(MaxTemperature > dToCompare) {
				return 1;
			}
		}
		if(attr.equals("MinHumidity")) {
			
			BigDecimal bdToComare = (BigDecimal) toCompare;
			double dToCompare = bdToComare.doubleValue();
			
			if(MinHumidity == dToCompare) {
				return 0;
			}
			if(MinHumidity < dToCompare) {
				return -1;
			}
			if(MinHumidity > dToCompare) {
				return 1;
			}
		}
		if(attr.equals("MaxHumidity")) {
			
			BigDecimal bdToComare = (BigDecimal) toCompare;
			double dToCompare = bdToComare.doubleValue();
			
			if(MaxHumidity == dToCompare) {
				return 0;
			}
			if(MaxHumidity < dToCompare) {
				return -1;
			}
			if(MaxHumidity > dToCompare) {
				return 1;
			}
		}
		if(attr.equals("Description")) {
			String sToCompare = (String) toCompare;
			return Description.compareTo(sToCompare);
		}
		
		return 0;
		
	}
	
	public int compareTo(BCIncident toCompare, String attr) {
		
		if(attr.equals("PackageID")) {
			
			return PackageID.compareTo(toCompare.PackageID);
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
		if(attr.equals("MinTemperature")) {
			
			if(MinTemperature == toCompare.MinTemperature) {
				return 0;
			}
			if(MinTemperature < toCompare.MinTemperature) {
				return -1;
			}
			if(MinTemperature > toCompare.MinTemperature) {
				return 1;
			}
		}
		if(attr.equals("MaxTemperature")) {
			
			if(MaxTemperature == toCompare.MaxTemperature) {
				return 0;
			}
			if(MaxTemperature < toCompare.MaxTemperature) {
				return -1;
			}
			if(MaxTemperature > toCompare.MaxTemperature) {
				return 1;
			}
		}
		if(attr.equals("MinHumidity")) {
			
			if(MinHumidity == toCompare.MinHumidity) {
				return 0;
			}
			if(MinHumidity < toCompare.MinHumidity) {
				return -1;
			}
			if(MinHumidity > toCompare.MinHumidity) {
				return 1;
			}
		}
		if(attr.equals("MaxHumidity")) {
			
			if(MaxHumidity == toCompare.MaxHumidity) {
				return 0;
			}
			if(MaxHumidity < toCompare.MaxHumidity) {
				return -1;
			}
			if(MaxHumidity > toCompare.MaxHumidity) {
				return 1;
			}
		}
		if(attr.equals("Description")) {
			
			return Description.compareTo(toCompare.Description);
		}
		
		return 0;
		
	}
		

}