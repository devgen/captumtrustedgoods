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

import rosetracker.dataclasses.BCIncident;

public class IncidentExpressionConverter {

	private static void WriteToConsole(String msg) {
		
		System.out.print(" ++++ " + msg + " ++++ ");
		
	}
	
	public static List<BCIncident> executeExpressionNodeOnData(ExpressionNode node, List<BCIncident> dataToExecute) {
		
		WriteToConsole("---- Start Filter Expression ----");
		
		WriteToConsole("With node: " + node.toString());
		
		List<BCIncident> data = new LinkedList<BCIncident>();
		data.addAll(dataToExecute);
		
		if(node instanceof BinaryExpressionNode) {
			
			BinaryExpressionNode biNode = (BinaryExpressionNode) node;
			
			// --- Operators --- 
			if(biNode.getOperator().equals(OPERATOR.AND.toString())) {
				
				WriteToConsole(" Its an AND ! ");
				
				return executeAnd(
					executeExpressionNodeOnData(biNode.getFirstChild(), data), 
					executeExpressionNodeOnData(biNode.getSecondChild(), data)
				);
			}
			if(biNode.getOperator().equals(OPERATOR.OR.toString())) {
				
				WriteToConsole(" Its an OR ! ");
				
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
				
				WriteToConsole(" Its a comparator ! ");
				
				
				return executeComperator(biNode, data);
				
			}
			
		}
		
		// ---- contains ---- 
		// --> how to test this? 
		
		if(node instanceof FunctionNode) {
			
			WriteToConsole(" node is function node ");
			
			FunctionNode fuNode = (FunctionNode) node;
			
			WriteToConsole(" node func name: " + fuNode.getFunctionName());
			
			if(fuNode.getFunctionName().equals("contains")) {
				
				WriteToConsole(" function node is contains ");
				
				List<ExpressionNode> params = fuNode.getParameters();
				
				for(ExpressionNode n : params) {
					
					WriteToConsole(" expression node in params: " + n);
					
				}
				
			}
			
		}
		
		return data;
		
	}
	
	
	private static List<BCIncident> executeAnd(List<BCIncident> firstList, List<BCIncident> secondList) {
		
		// WriteToConsole(" It's an AND ! ");
		// WriteToConsole(" with first List count " + firstList.size());
		// WriteToConsole(" elems of first List ");
		// for(BCOwnerChange c : firstList) {
		// 	WriteToConsole(" elem of 1.list " + c.PackageID);
		// }
		// WriteToConsole(" with second List count " + secondList.size());
		// WriteToConsole(" elems of second List ");
		// for(BCOwnerChange c : secondList) {
		// 	WriteToConsole(" elem of 2.list " + c.PackageID);
		// }
		
		// // both lists are correct!
		
		
		// error here?
		List<BCIncident> back = firstList.stream()
								  .distinct()
								  .filter(secondList::contains)
								  .collect(Collectors.toList());
		
		return back;
		
	}
	
	
	private static List<BCIncident> executeOr(List<BCIncident> firstList, List<BCIncident> secondList) {
		
		firstList.addAll(secondList);
		return firstList.stream()
						.distinct()
						.collect(Collectors.toList());
						
	}
	
	
	private static List<BCIncident> executeComperator(BinaryExpressionNode biNode, List<BCIncident> data) {
		
		PropertyNode poNode = null;
		LiteralNode liNode = null;;
		
		if(biNode.getFirstChild() instanceof PropertyNode) {
			
			//WriteToConsole(" First Child is a Property ! ");
				
			poNode = (PropertyNode) biNode.getFirstChild();
			
		} 
		if(biNode.getSecondChild() instanceof PropertyNode) {
			
			//WriteToConsole(" Second Child is a Property ! ");
				
			poNode = (PropertyNode) biNode.getSecondChild();
			
		} 
		if(biNode.getFirstChild() instanceof LiteralNode) {
			
			//WriteToConsole(" First Child is a Literal ! ");
			
			liNode = (LiteralNode) biNode.getFirstChild();
			
		} 
		if(biNode.getSecondChild() instanceof LiteralNode) {
			
			//WriteToConsole(" Second Child is a Literal ! ");
			
			liNode = (LiteralNode) biNode.getSecondChild();
			
		} 
		
		if(poNode == null) {
			
			//WriteToConsole("Property Node was null.");
			return null;
			
		}
		
		if(liNode == null) {
			
			//WriteToConsole("Literal Node was null.");
			return null;
			
		}
		
		String property = poNode.getPath();
		Object literal = liNode.getValue();
		
		WriteToConsole(" property: " + property);
		
		WriteToConsole(" literal: " + literal);
		
		
		
		List<BCIncident> back = new LinkedList<BCIncident>();
		
		for(BCIncident c : data) {
			
			if(biNode.getOperator().equals(OPERATOR.EQ.toString())) {
			
				//WriteToConsole(" Its a EQ ! ");
				
				if(c.attrCompareTo(literal, property) == 0) {
					
					//WriteToConsole(" Its a match with id " + c.PackageID);
					
					back.add(c);
					
				}
			
			}
			
			if(biNode.getOperator().equals(OPERATOR.NE.toString())) {
				
				//WriteToConsole(" Its a NE ! ");
				
				if(c.attrCompareTo(literal, property) != 0) {
					
					back.add(c);
					
				}
				
			}
			
			if(biNode.getOperator().equals(OPERATOR.LE.toString())) {
				
				//WriteToConsole(" Its a LE ! ");
				
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