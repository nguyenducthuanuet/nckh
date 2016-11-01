package formula2;

import java.util.ArrayList;
import java.util.List;

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtWhile;

import util.Helper;
import util.StringBox;
import util.Variable;

public class WhileFormularization extends Formularization {
	
	public WhileFormularization(	CtWhile loop, 
				List<Variable> listVariables,
				List<Variable> flagVariables,
				StringBox lastReturnFlag) 
	{
		this.loop = loop;
		assign(listVariables, flagVariables, lastReturnFlag);
		
		flagVariables.forEach(p -> System.out.println(p));
		
		whileFlag = Variable.getVariable("while", flagVariables);
		if (whileFlag == null) {
			System.out.println("null");
			System.exit(-1);
		}
	}

	@Override
	public List<String> getFormula() {
		if(formula == null) {
			formula = new ArrayList<>();
			formularize();
		}
		
		return formula;
	}
	
	public void formularize() {
		
		List<Variable> sync = Helper.copyList(listVariables);

		int nLoop = defaultNumOfLoop;
		
		List<String> updatedVarsList = new ArrayList<>();
		
		List<String> aLoop = null;
		for(int i = 0; i < nLoop; i++) {
			aLoop = formularizeALoop( updatedVarsList, sync);
			formula.addAll(aLoop);
			sync = null;
			
			if (lastReturn.getString() != null)
				lastReturnFlag.setString( lastReturn.getString() );
		}

	}
	
	private List<String> formularizeALoop( 	List<String> syncedVarsList,
											List<Variable> sync) {
		List<String> f = new ArrayList<>();
		
		if(sync == null)
			sync = Helper.copyList(listVariables);
		
		String conditionAssignment = getConditionAssignment();
		
		List<String> body;
		
		CtBlock block = (CtBlock) loop.getBody();
		body = formularize(block, lastReturn, 
							lastBreak, lastContinue);
		
		String loopBody = wrapAll(body, "and");
		
		if(conditionAssignment == null) {	// for loop doesn't have loop condition
			f.add(loopBody);
		}
		else {
			f.add(conditionAssignment);
			String temp = wrap(whileFlagVal, "=>", loopBody);
			f.add(temp);
			String syncExp = sync(sync, syncedVarsList);
			if (syncExp != null)
				f.add( syncExp );
		}
		
		return f;
	}
	
	private String getConditionAssignment() {
		String condition = null;
		String conditionAssign = null;
		condition = formularize(loop.getLoopingExpression());
		
		String preCondition = getPreCondition(lastReturn, lastBreak, null);
		if(preCondition != null) {
			if(condition != null)
				condition = wrap(preCondition, "and", condition);
			else
				condition = preCondition;
		}
		
		if(condition != null) {
			
			if(whileFlagVal != null)
				condition = wrap(whileFlagVal, "and", condition);
			
			if( !whileFlag.hasInitialized())
				whileFlag.initialize();
			else
				whileFlag.increase();
			
			conditionAssign = wrap(whileFlag.getValue(), "=", condition);
			whileFlagVal = whileFlag.getValue();
		}
		
		return conditionAssign;
	}
	
	private String sync(List<Variable> sync, List<String> syncedVarsList) {
		String f = null;
		
		List<String> syncVarsExp = syncVariable(sync, listVariables, syncedVarsList);
		
		if ( lastReturn.getString() != null && lastReturnFlag.getString() != null) {
			String syncReturnFlag = wrap(lastReturn.getString(), "=", 
									lastReturnFlag.getString());
			syncVarsExp.add(syncReturnFlag);
		}
		
		String syncVarsExpS = wrapAll(syncVarsExp, "and");
		
		if(syncVarsExp != null)
			f = wrap( wrap("not", whileFlagVal), "=>", syncVarsExpS );
		
		return f;
	}
	
	private CtWhile loop;
	
	protected int defaultNumOfLoop = 10;
	
	String whileFlagVal;
	
	private StringBox lastReturn = new StringBox();
	private StringBox lastBreak = new StringBox();
	private StringBox lastContinue = new StringBox();
}
