package example;

import java.util.*;
import parser.SimpleCParser;
import parser.SimpleCParser.VarDeclContext;
import parser.SimpleCBaseVisitor;
import example.declVisitor;

public class assignStmtVisitor extends SimpleCBaseVisitor<Void>{
	
	private ArrayList<String> preSeq = new ArrayList<>();
	private String assignStmt;
	private Map<String,Integer> varIndex = new HashMap<String,Integer>();
	private String declaration;
	private ArrayList<String> varList = new ArrayList<>();
	private String num = "0123456789";
	
	@Override
	public Void visitVarDecl(VarDeclContext ctx){
		String s = ctx.ID().getText();
		//System.out.println(s);
		varList.add(s);
		return null;
		
	}
	public String getassignStmt(){
		return assignStmt;
	}
	@Override 
	public Void visitAssignStmt(SimpleCParser.AssignStmtContext ctx){
		//System.out.println(ctx.expr().getChild(0).getText());
		//midToPre(ctx);
		//ArrayList<String> test= new ArrayList<String>();
		//test.add("x");
		String left = ctx.ID().getText();
		String right = ctx.expr().getText();
		midToPre(right);
		if(assignStmt == null){
			assignStmt = setAssignStmt(varList,left) + "\n";
		}
		else 
			assignStmt = assignStmt + setAssignStmt(varList,left) + "\n";
		return null;
	}
	public String getdecla(){
		return declaration;
	}
	public ArrayList getPreSeq(){
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
		for(int i = 0;i < preSeq.size();i++){
			String s = preSeq.get(i);
			if(idArray.contains(s)){
				s = getLastIndexId(s);
			}
			if(num.contains(s)){
				for(int j = i+1;j < preSeq.size() && num.contains(preSeq.get(j));j++){
					s = s + String.valueOf(preSeq.get(j));
					i = j;
				}
			}
			if(s.equals("/")){
				s = "div";
				String divisor = getLastIndexId(preSeq.get(i+2));
				//System.out.println(divisor);
				String value_true = getLastIndexId(preSeq.get(i+1));
				//System.out.println(divir);
				String value_false = getLastIndexId(id);
				assignStmtOne = "(ite(= "+divisor+" 0) " + value_true +" "+ value_false +")";
				
			}
			if(s.equals("%"))
				s = "mod";
			if(assignStmtOne == null)
				assignStmtOne = s;
			else if(assignStmtOne.contains("ite")){
				assignStmtOne = assignStmtOne;
			}
			else
				assignStmtOne = assignStmtOne + " " + s;
			
		}//right side
		increaseVarIndex(id);
		if(assignStmtOne.contains("ite") || !assignStmtOne.contains(" ")){
			assignStmtOne = "(assert (= " + getLastIndexId(id) + " " + assignStmtOne + "))";
		}
/*		if(!assignStmtOne.contains(" ")){
			assignStmtOne = "(assert (= " + getLastIndexId(id) + " " + assignStmtOne + "))";
		}*/
		else{
			assignStmtOne = "(assert (= " + getLastIndexId(id) + " (" + assignStmtOne + ")))";
		}
			
		return assignStmtOne;
	}
	
	public void midToPre(String midSeq){
		preSeq.clear();
		Stack<String> op = new Stack<>();
		Stack<String> result = new Stack<>();
		//String midSeq = ctx.expr().getText();
		for(int i = midSeq.length()-1;i>=0;i--){
			String s = String.valueOf(midSeq.charAt(i));
			String next = null;
			if(i>0){
				next = String.valueOf(midSeq.charAt(i-1));
			}
			switch(s){
			case ")":
				op.push(s);
				result.push(s);
				//System.out.println(op.peek());
				break;
			case "(":
				while(!op.peek().equals(")")){
					result.push(op.pop());
				}
				result.push("(");
				op.pop();
				break;
			case "/":
			case "*":
			case "%":
				op.push(s);
				break;
			case "+":
			case "-":
				while(!op.isEmpty() && (op.peek().equals("/") || op.peek().equals("*") || op.peek().equals("%"))){
					result.push(op.pop());
				}
				op.push(s);
				break;
			case ">":
			case "<":
				if(next.equals(">") || next.equals("<")){
					System.out.println("?");
					i--;
					s = next + s;
					while(!op.isEmpty() && (op.peek().equals("/") || op.peek().equals("*") || op.peek().equals("%") || 
							op.peek().equals("+") || op.peek().equals("-"))){
						result.push(op.pop());
					}
				}
				else{
					while(!op.isEmpty() && (op.peek() == "/" || op.peek() == "*" || op.peek() == "%" || 
							op.peek() == "+" || op.peek() == "-" || op.peek() == ">>" || op.peek() == "<<")){
						result.push(op.pop());
					}					
				}
				op.push(s);
				break;
			case "=":
				i--;
				s = next + s;
				if(next == ">" || next == "<"){      //>=,<=
					while(!op.isEmpty() && (op.peek() == "/" || op.peek() == "*" || op.peek() == "%" || 
							op.peek() == "+" || op.peek() == "-" || op.peek() == ">>" || op.peek() == "<<")){
						result.push(op.pop());
					}
				}
				else{ // !=,==
					while(!op.isEmpty() && (op.peek() == "/" || op.peek() == "*" || op.peek() == "%" || 
							op.peek() == "+" || op.peek() == "-" || op.peek() == ">>" || op.peek() == "<<"
							|| op.peek() == "<" || op.peek() == ">" || op.peek() == "<=" || op.peek() == ">=")){
						result.push(op.pop());
					}					
				}
				op.push(s);
				break;
			case "&":
			case "|":
			case "^":
				if(next.equals("|") || next.equals("&")){
					i--;
					s = next + s;
				}
				while(!op.isEmpty() && (op.peek() == "/" || op.peek() == "*" || op.peek() == "%" || 
						op.peek() == "+" || op.peek() == "-" || op.peek() == ">>" || op.peek() == "<<"
						|| op.peek() == "<" || op.peek() == ">" || op.peek() == "<=" || op.peek() == ">="
						|| op.peek() == "==" || op.peek() == "!=")){
					result.push(op.pop());
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
		while(!result.isEmpty()){
			//System.out.println(result.peek());
			preSeq.add(result.pop());	
		}
		//return pre;
		
	}

}
