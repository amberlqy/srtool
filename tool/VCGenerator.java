package tool;
import parser.SimpleCParser.ProcedureDeclContext;
import example.declVisitor;
import java.util.*;

public class VCGenerator {

	private ProcedureDeclContext proc;
	private declVisitor declVis; 
	
	public VCGenerator(ProcedureDeclContext proc) {
		this.proc = proc;
		// TODO: You will probably find it useful to add more fields and constructor arguments
		declVis = new declVisitor();
	
		
	}
	
	public StringBuilder generateVC() {
		
		declVis.visit(proc);
		StringBuilder result = new StringBuilder("(set-logic QF_LIA)\n");

		for(int i = 0 ; i<declVis.getIDArray().size() ; i++){
			String s = "(declare-fun " + declVis.getIDArray().get(i) +" () Int)" + "\n";
			result.append(s);
		}
		// TODO: generate the meat of the VC
		result.append("\n(check-sat)\n");
		return result;
	}

}
