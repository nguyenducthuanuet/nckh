package formula2;

import java.util.ArrayList;
import java.util.List;

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtStatement;

import util.Helper;
import util.StringBox;
import util.Variable;

public class ForFormularization extends Formularization {
	
	public ForFormularization(	CtFor forLoop, 
								List<Variable> listVariables,
								List<Variable> flagVariables,
								StringBox lastReturnFlag) 
	{
		loop = forLoop;
		assign(listVariables, flagVariables, lastReturnFlag);
	}

	@Override
	public List<String> getFormula() {
		if(formula == null) {
			formula = new ArrayList<>();
//			formularize();
			formularize2();
		}
		
		return formula;
	}
	
	// cho lap so lan mac dinh 
	public void formularize() {
		
		List<Variable> sync = Helper.copyList(listVariables);
//		sync = null;
		
		List<CtStatement> forInit = loop.getForInit();
		List<String> init = formularize(forInit, null, null, null);
		formula.addAll(init);
		
		List<CtStatement> forUpdate = loop.getForUpdate();
		CtStatement body = loop.getBody();

		
		String loopBody = null;
		String aLoop = null;
		int nLoop = defaultNumOfLoop;
		
		String condition = null;
		List<String> updateVarsExp = null;
		
		List<String> temp;
		
		List<String> updatedVarsList = new ArrayList<>();
		String conditionAssign = null;
		String forFlagVal = null;
		
		for(int i = 0; i < nLoop; i++) {

			condition = formularize(loop.getExpression());
			if(condition != null) {
				if(forFlagVal != null)
					condition = wrap(forFlagVal, "and", condition);
				conditionAssign = wrap(forFlag.getValue(), "=", condition);
				formula.add(conditionAssign);
				forFlagVal = forFlag.getValue();
				forFlag.increase();
			}
			
			temp = formularize(body, lastReturn, lastBreak, lastContinue);

			List<String> update = formularize(forUpdate, null, null, null);
			temp.addAll(update);
			
			loopBody = wrapAll(temp, "and");
			if(condition == null) {
				formula.add(loopBody);
			}
			else {
				aLoop = wrap(forFlagVal, "=>", loopBody);
				formula.add(aLoop);
				updateVarsExp = syncVariable(sync, listVariables, updatedVarsList);
				if(updateVarsExp != null)
					formula.add(wrap(wrap("not", forFlagVal), "=>", 
								wrapAll(updateVarsExp, "and") ));
			}
			sync = Helper.copyList(listVariables);
		}
	}
	
	public void formularize2() {
		
		List<Variable> sync = Helper.copyList(listVariables);
		
		List<CtStatement> forInit = loop.getForInit();
		List<String> init = formularize(forInit, null, null, null);
		formula.addAll(init);
		
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
		if (lastContinue.getString() != null) {
			System.out.println("last continue: " + lastContinue);
		}
	
	
		String update = getForUpdate();
		if (update != null)
			body.add(update);
		
		String loopBody = wrapAll(body, "and");
		
		if(conditionAssignment == null) {	// for loop doesn't have loop condition
			f.add(loopBody);
		}
		else {
			f.add(conditionAssignment);
			String temp = wrap(forFlagVal, "=>", loopBody);
			f.add(temp);
			String syncExp = sync(sync, syncedVarsList);
			if (syncExp != null)
				f.add( syncExp );
			
//			List<String> updateVarsExp = syncVariable(sync, listVariables, syncedVarsList);
//			if(updateVarsExp != null)
//				f.add(wrap(wrap("not", forFlagVal), "=>", wrapAll(updateVarsExp, "and") ));
		}
		
		return f;
	}
	
	private String getConditionAssignment() {
		String condition = null;
		String conditionAssign = null;
		condition = formularize(loop.getExpression());
		
		String preCondition = getPreCondition(lastReturn, lastBreak, null);
		if(preCondition != null) {
			if(condition != null)
				condition = wrap(preCondition, "and", condition);
			else
				condition = preCondition;
		}
		
		if(condition != null) {
			if(forFlagVal != null)
				condition = wrap(forFlagVal, "and", condition);
			
			if( !forFlag.hasInitialized())
				forFlag.initialize();
			else
				forFlag.increase();
			
			conditionAssign = wrap(forFlag.getValue(), "=", condition);
			forFlagVal = forFlag.getValue();
		}
		
		return conditionAssign;
	}
	
	private String getForUpdate() {
		List<String> update = formularize(loop.getForUpdate(), null, null, null);
		if (update.isEmpty())
			return null;
	
		String preConOfUpdate = getPreCondition(lastReturn, lastBreak, null);
		
		String updateStr = wrapAll(update, "and");
		String f = null;
		if (preConOfUpdate != null)
			f = wrap(preConOfUpdate, "=>", updateStr);
		else 
			f = updateStr;
		
		return f;
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
			f = wrap( wrap("not", forFlagVal), "=>", syncVarsExpS );
		
		return f;
	}
	
	private void setLastFlagToNull () {
		lastReturn.setString(null);
		lastBreak.setString(null);
		lastContinue.setString(null);
	}
	
	private CtFor loop;
	
	protected int defaultNumOfLoop = 10;
	
	String forFlagVal;
	
	private StringBox lastReturn = new StringBox();
	private StringBox lastBreak = new StringBox();
	private StringBox lastContinue = new StringBox();
}
