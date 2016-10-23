package example;

import java.util.*;
import parser.SimpleCParser;
import parser.SimpleCParser.VarDeclContext;
import parser.SimpleCBaseVisitor;

public class declVisitor extends SimpleCBaseVisitor<Void>{
	
	private  ArrayList <String> IDArray = new ArrayList<>();
	
	@Override
	public Void visitVarDecl(VarDeclContext ctx) { 
		String s = ctx.ID().getText() + "0";
		//System.out.println(s);
		IDArray.add(s);
		return null;
	}
	
	public ArrayList getIDArray(){
		return IDArray;
	}

}
