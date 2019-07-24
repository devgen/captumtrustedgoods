package rosetracker.converter;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.sap.cloud.sdk.service.prov.api.filter.BinaryExpressionNode;
import com.sap.cloud.sdk.service.prov.api.filter.ExpressionNode;
import com.sap.cloud.sdk.service.prov.api.filter.FunctionNode;
import com.sap.cloud.sdk.service.prov.api.filter.LiteralNode;
import com.sap.cloud.sdk.service.prov.api.filter.PropertyNode;
import com.sap.cloud.sdk.service.prov.api.filter.ExpressionOperatorTypes.OPERATOR;

import rosetracker.dataclasses.BCOwnerChange;

public class OwnerChangeExpressionConverter {
	
	// checks if the given filter node is a single equals comparator
	public static Boolean CheckIfFilterIsSingle(ExpressionNode node) {
	
		if(node instanceof BinaryExpressionNode) {
			
			BinaryExpressionNode biNode = (BinaryExpressionNode) node;
			
			if(biNode.getOperator().equals(OPERATOR.EQ.toString())) {
			
				return true;
			
			}
		}
		
		return false;
	}
	
	// executes a single equlals comparator
	public static String ExecuteSingleFilterID(ExpressionNode node) {
		
		if(node instanceof BinaryExpressionNode) {
			
			BinaryExpressionNode biNode = (BinaryExpressionNode) node;
			
			PropertyNode poNode = null;
			LiteralNode liNode = null;;
			
			if(biNode.getFirstChild() instanceof PropertyNode) {
				
				poNode = (PropertyNode) biNode.getFirstChild();
				
			} 
			if(biNode.getSecondChild() instanceof PropertyNode) {
					
				poNode = (PropertyNode) biNode.getSecondChild();
				
			} 
			if(biNode.getFirstChild() instanceof LiteralNode) {
				
				liNode = (LiteralNode) biNode.getFirstChild();
				
			} 
			if(biNode.getSecondChild() instanceof LiteralNode) {
				
				liNode = (LiteralNode) biNode.getSecondChild();
				
			} 
			
			if(poNode == null) {
				
				return null;
				
			}
			
			if(liNode == null) {
				
				return null;
				
			}
			
			Object literal = liNode.getValue();
			
			return (String) literal;
		}
		return null;
	}
	
	// applices a filter expression tree on a list of BC Incident data
	// if futher filter expressions are added they must be added here. Currently just the two operators and the six comparators are supported.
	public static List<BCOwnerChange> executeExpressionNodeOnData(ExpressionNode node, List<BCOwnerChange> dataToExecute) {
		
		List<BCOwnerChange> data = new LinkedList<BCOwnerChange>();
		data.addAll(dataToExecute);
		
		if(node instanceof BinaryExpressionNode) {
			
			BinaryExpressionNode biNode = (BinaryExpressionNode) node;
			
			// --- Operators --- 
			if(biNode.getOperator().equals(OPERATOR.AND.toString())) {
				
				return executeAnd(
					executeExpressionNodeOnData(biNode.getFirstChild(), data), 
					executeExpressionNodeOnData(biNode.getSecondChild(), data)
				);
			}
			if(biNode.getOperator().equals(OPERATOR.OR.toString())) {
				
				return executeOr(
					executeExpressionNodeOnData(biNode.getFirstChild(), data), 
					executeExpressionNodeOnData(biNode.getSecondChild(), data)
				);
			}
			
			// --- Comperators --- 
			if(	biNode.getOperator().equals(OPERATOR.EQ.toString()) || 
				biNode.getOperator().equals(OPERATOR.NE.toString()) ||
				biNode.getOperator().equals(OPERATOR.LE.toString()) ||
				biNode.getOperator().equals(OPERATOR.LT.toString()) ||
				biNode.getOperator().equals(OPERATOR.GE.toString()) ||
				biNode.getOperator().equals(OPERATOR.GT.toString())) {
				
				return executeComperator(biNode, data);
				
			}
			
		}
		
		return data;
		
	}
	
	
	// executes the and operator on two lists. 
	// returns items that are in both lists
	private static List<BCOwnerChange> executeAnd(List<BCOwnerChange> firstList, List<BCOwnerChange> secondList) {
		
		List<BCOwnerChange> back = firstList.stream()
								  .distinct()
								  .filter(secondList::contains)
								  .collect(Collectors.toList());
		
		return back;
		
	}
	
	
	// executes the or operator on two lists.
	// returns all elements of both lists
	private static List<BCOwnerChange> executeOr(List<BCOwnerChange> firstList, List<BCOwnerChange> secondList) {
		
		firstList.addAll(secondList);
		return firstList.stream()
						.distinct()
						.collect(Collectors.toList());
						
	}
	
	
	// executes the comparators on both lists
	private static List<BCOwnerChange> executeComperator(BinaryExpressionNode biNode, List<BCOwnerChange> data) {
		
		// gets property and literal values	
		
		PropertyNode poNode = null;
		LiteralNode liNode = null;;
		
		if(biNode.getFirstChild() instanceof PropertyNode) {
			
			poNode = (PropertyNode) biNode.getFirstChild();
			
		} 
		if(biNode.getSecondChild() instanceof PropertyNode) {
				
			poNode = (PropertyNode) biNode.getSecondChild();
			
		} 
		if(biNode.getFirstChild() instanceof LiteralNode) {
			
			liNode = (LiteralNode) biNode.getFirstChild();
			
		} 
		if(biNode.getSecondChild() instanceof LiteralNode) {
			
			liNode = (LiteralNode) biNode.getSecondChild();
			
		} 
		
		if(poNode == null) {
			
			return null;
			
		}
		
		if(liNode == null) {
			
			return null;
			
		}
		
		// the name of the porperty
		String property = poNode.getPath();
		// the value of the porperty
		Object literal = liNode.getValue();
		
		
		
		List<BCOwnerChange> back = new LinkedList<BCOwnerChange>();
		
		// maps the odata operators to java understanable operations (example EQ --> ==)
		for(BCOwnerChange c : data) {
			
			if(biNode.getOperator().equals(OPERATOR.EQ.toString())) {
			
				if(c.attrCompareTo(literal, property) == 0) {
					
					back.add(c);
					
				}
			
			}
			
			if(biNode.getOperator().equals(OPERATOR.NE.toString())) {
				
				if(c.attrCompareTo(literal, property) != 0) {
					
					back.add(c);
					
				}
				
			}
			
			if(biNode.getOperator().equals(OPERATOR.LE.toString())) {
				
				if(c.attrCompareTo(literal, property) <= 0) {
					
					back.add(c);
					
				}
				
			}
			
			if(biNode.getOperator().equals(OPERATOR.LT.toString())) {
				
				if(c.attrCompareTo(literal, property) < 0) {
					
					back.add(c);
					
				}
				
			}
			
			if(biNode.getOperator().equals(OPERATOR.GE.toString())) {
				
				if(c.attrCompareTo(literal, property) >= 0) {
					
					back.add(c);
					
				}
				
			}
			
			if(biNode.getOperator().equals(OPERATOR.GT.toString())) {
				
				if(c.attrCompareTo(literal, property) > 0) {
					
					back.add(c);
					
				}
				
			}
			
		}
		
		return back;
		
	}
	


}