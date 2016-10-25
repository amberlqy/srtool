package example;

import java.util.*;
import parser.SimpleCParser;
import parser.SimpleCParser.VarDeclContext;
import parser.SimpleCBaseVisitor;
import example.declVisitor;

public class assignStmtVisitor extends SimpleCBaseVisitor<Void>{
	
	private String preSeq;
	private String assignStmt;
	private Map<String,Integer> varIndex = new HashMap<String,Integer>();
	private String declaration;
	
	public String getassignStmt(){
		return assignStmt;
	}
	@Override 
	public Void visitAssignStmt(SimpleCParser.AssignStmtContext ctx){
		//System.out.println(ctx.expr().getChild(0).getText());
		//midToPre(ctx);
		ArrayList<String> test= new ArrayList<String>();
		test.add("x");
		String left = ctx.ID().getText();
		String right = ctx.expr().getText();
		midToPre(right);
		if(assignStmt == null){
			assignStmt = setAssignStmt(test,left) + "\n";
		}
		else 
			assignStmt = assignStmt + setAssignStmt(test,left) + "\n";
		return null;
	}
	public String getdecla(){
		return declaration;
	}
	public String getPreSeq(){
		return preSeq;
	}
	
	public void increaseVarIndex(String id){
		if(varIndex.containsKey(id)){
			varIndex.put(id, varIndex.get(id)+1);
		}
		else{
			varIndex.put(id, 1);
		}
		if(declaration == null){
			declaration = "(declare-fun " + getLastIndexId(id) + " () Int)" + "\n";
		}
		else{
			declaration = declaration + "(declare-fun " + getLastIndexId(id) + " () Int)" + "\n";
		}
	}
	
	public String getLastIndexId(String id){
		String s;
		if(varIndex.containsKey(id)){
			int i = varIndex.get(id);
			s = id + String.valueOf(i);
		}
		else
			s = id + "0";
		return s;
	}
	public String setAssignStmt(ArrayList<String> idArray,String id ){
		//String preSeq = midToPre(right);
		String assignStmtOne = null ;//one row
		//System.out.println(preSeq);
		for(int i = 0;i < preSeq.length();i++){
			String s = String.valueOf(preSeq.charAt(i));
			if(idArray.contains(s)){
				s = getLastIndexId(s);
			}
			if(assignStmtOne == null)
				assignStmtOne = s;
			else
				assignStmtOne = assignStmtOne + " " + s;
			
		}//right side
		increaseVarIndex(id);
		if(assignStmtOne.length() == 1){
			assignStmtOne = "(assert (= " + getLastIndexId(id) + " " + assignStmtOne + "))";
		}
		else
			assignStmtOne = "(assert (= " + getLastIndexId(id) + " (" + assignStmtOne + ")))";
		return assignStmtOne;
	}
	
	public void midToPre(String midSeq){
		preSeq = null;
		Stack<String> op = new Stack<>();
		Stack<String> result = new Stack<>();
		//String midSeq = ctx.expr().getText();
		for(int i = midSeq.length()-1;i>=0;i--){
			String s = String.valueOf(midSeq.charAt(i));
			switch(s){
			case ")":
				op.push(s);
				//System.out.println(op.peek());
				break;
			case "(":
				while(!op.peek().equals(")")){
					result.push(op.pop());
				}
				op.pop();
				break;
			case "/":
			case "*":
			case "%":
				op.push(s);
				break;
			case "+":
			case "-":
				while(!op.isEmpty() && (op.peek() == "/" || op.peek() == "*" || op.peek() == "%")){
					result.push(op.pop());
				}
				op.push(s);
				break;
			case ">":
			case "<":
				i--;
				s = s + String.valueOf(midSeq.charAt(i));
				while(!op.isEmpty() && (op.peek() == "/" || op.peek() == "*" || op.peek() == "%" || 
						op.peek() == "+" || op.peek() == "-")){
					result.push(s);
				}
				op.push(s);
				break;
			default:
				result.push(s);	
			}//end of switch
		}//end of for
		while(!op.isEmpty()){
			result.push(op.pop());
		}
		while(!result.isEmpty()){ //add x0 and assertion
			if(preSeq == null){
				preSeq = result.pop();
			}
			else
				preSeq = preSeq + result.pop();
		}
		//return pre;
		
	}

}
