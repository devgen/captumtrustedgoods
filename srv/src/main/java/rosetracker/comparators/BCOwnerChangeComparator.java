package rosetracker.comparators;

import java.util.Comparator;
import java.util.List;

import com.sap.cloud.sdk.service.prov.api.request.OrderByExpression;

import rosetracker.dataclasses.BCOwnerChange;

public class BCOwnerChangeComparator implements Comparator<BCOwnerChange> {
	
	private List<OrderByExpression> expressions = null;
	
	public void setOrderByExpression(List<OrderByExpression> expressions) {
		
		this.expressions = expressions;
		
	}

	@Override
	public int compare(BCOwnerChange o1, BCOwnerChange o2) {
		
		for(OrderByExpression exp : expressions) {
			
			int comp = o1.compareTo(o2, exp.getOrderByProperty());
			if(comp != 0) {
				if(exp.isDescending()) {
					
					return (comp * -1);
					
				} else {
					
					return comp;
					
				}
			}
			
		}
		
		return 0;
	}

}
