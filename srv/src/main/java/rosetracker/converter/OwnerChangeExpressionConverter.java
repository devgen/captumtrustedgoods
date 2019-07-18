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

	private static void WriteToConsole(String msg) {
		
		System.out.print(" ++++ " + msg + " ++++ ");
		
	}
	
	public static Boolean CheckIfFilterIsSingle(ExpressionNode node) {
	
		if(node instanceof BinaryExpressionNode) {
			
			BinaryExpressionNode biNode = (BinaryExpressionNode) node;
			
			if(biNode.getOperator().equals(OPERATOR.EQ.toString())) {
			
				return true;
			
			}
		}
		
		return false;
	}
	
	public static String ExecuteSingleFilterID(ExpressionNode node) {
		
		if(node instanceof BinaryExpressionNode) {
			
			BinaryExpressionNode biNode = (BinaryExpressionNode) node;
			
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
			
			//if(property == "PackageID") {
				
				return (String) literal;
			//} 
		}
		return null;
	}
	
	public static void dummy() {
		
		WriteToConsole("Operator eq : " + OPERATOR.EQ);
		
		WriteToConsole("Operator eq to String: " + OPERATOR.EQ.toString());
		
		WriteToConsole("Operator eq name : " + OPERATOR.EQ.name());
		
		WriteToConsole("Operator eq value of: " + OPERATOR.valueOf(OPERATOR.EQ.name()));
		
		for(OPERATOR o : OPERATOR.values()) {
			
			WriteToConsole("Through the operators: " + o);
			
		}
		
	}
	
	
	public static List<BCOwnerChange> executeExpressionNodeOnData(ExpressionNode node, List<BCOwnerChange> dataToExecute) {
		
		WriteToConsole("---- Start Filter Expression ----");
		
		WriteToConsole("With node: " + node.toString());
		
		List<BCOwnerChange> data = new LinkedList<BCOwnerChange>();
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
	
	
	private static List<BCOwnerChange> executeAnd(List<BCOwnerChange> firstList, List<BCOwnerChange> secondList) {
		
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
		List<BCOwnerChange> back = firstList.stream()
								  .distinct()
								  .filter(secondList::contains)
								  .collect(Collectors.toList());
		
		return back;
		
	}
	
	
	private static List<BCOwnerChange> executeOr(List<BCOwnerChange> firstList, List<BCOwnerChange> secondList) {
		
		firstList.addAll(secondList);
		return firstList.stream()
						.distinct()
						.collect(Collectors.toList());
						
	}
	
	
	private static List<BCOwnerChange> executeComperator(BinaryExpressionNode biNode, List<BCOwnerChange> data) {
		
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
		
		
		
		List<BCOwnerChange> back = new LinkedList<BCOwnerChange>();
		
		for(BCOwnerChange c : data) {
			
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