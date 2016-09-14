package formula2;

import java.util.ArrayList;
import java.util.List;

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtSwitch;
import spoon.support.reflect.code.CtBlockImpl;

import util.Helper;
import util.StringBox;
import util.Variable;

public class SwitchFormularization extends Formularization {
	
	public SwitchFormularization(	CtSwitch switchCase, 
									List<Variable> listVariables,
									List<Variable> flagVariables,
									StringBox lastReturnFlag,
									StringBox lastContinueFlag) 
	{
		this.switchCase = switchCase;
		
		assign(listVariables, flagVariables, lastReturnFlag);
	}

	@Override
	public List<String> getFormula() {
		if (formula == null) {
			formula = new ArrayList<>();
			formularize();
		//	formularize2();
		}
		
		return formula;
	}
	
	public void formularize() {
		
		caseExpList = new ArrayList<>();
		
		List<CtCase> caseList = switchCase.getCases();
		selector = formularize(switchCase.getSelector());
		
		List<String> fCase;
		String breakDefault = null, lastBreakBeforeDefault = null;
		CtCase defaultCase = null;
		String fDefaultCase = null; 	// formula of default case
		int positionOfDefault = -1;
		List<Variable> defalutCaseBackup = null;
		for(CtCase c: caseList) {
			if (c.getCaseExpression() == null) {		// default clause
				lastBreakBeforeDefault = lastBreak.getString();
				defalutCaseBackup = Helper.copyList(listVariables);
				fDefaultCase = formulaCaseStatement(c);
				positionOfDefault = formula.size();
				defaultCase = c;
			} 
			else {
			//	fCase = formularize(c);
				fCase = formularizeCase(c);
				formula.addAll(fCase);
			}
		}
		
		if (defaultCase != null) {	// Switch has default clause
			String condition = getDefaultCaseCondition(lastBreakBeforeDefault);
			fCase = makeCaseFormula(condition, fDefaultCase, defalutCaseBackup);
			formula.addAll(positionOfDefault, fCase);
		}
		
	}
	
	private List<String> formularizeCase(CtCase c) {
		List<Variable> backup = Helper.copyList(listVariables);
		
		String condition = getCaseCondition(c);
		String fCaseStatements = formulaCaseStatement(c);
		
		List<String> f = makeCaseFormula(condition, fCaseStatements, backup);
	
		return f;
	}
	
	private String getCaseCondition(CtCase c) {
		String caseExpStr = formularize( c.getCaseExpression() );
		String selectionExp = wrap(selector, "=", caseExpStr);
		caseExpList.add(selectionExp);
		String condition;
		if (lastBreak.getString() == null) 
			condition = selectionExp;
		else
			condition = wrap(wrap("not", lastBreak.getString()), "or", selectionExp);
		
		return condition;
	}
	
	private String getDefaultCaseCondition(String lastBreakBeforeDefault) {
		String selectionExp = wrapAll(caseExpList, "or");
		caseExpList.add(selectionExp);
		String condition;
		
		if (lastBreakBeforeDefault == null) 
			condition = selectionExp;
		else
			condition = wrap(wrap("not", lastBreakBeforeDefault), "or", selectionExp);
		
		return condition;
	}
	
	private String formulaCaseStatement(CtCase c) {
		
		List<CtStatement> statements = c.getStatements();
		
		List<String> fStatements = formularize(statements, null, lastBreak, null);
	
		String fCaseState = wrapAll( fStatements, "and");
		if (fCaseState == null) {	// list statement is empty
			if (breakFlag.hasInitialized())
				breakFlag.increase();
			else 
				breakFlag.initialize();
			fCaseState = wrap(breakFlag.getValue(), "=", "false");
			lastBreak.setString(breakFlag.getValue());
		}
		
		return fCaseState;
	}
	
	private List<String> makeCaseFormula(	String condition, 
									String fCaseStatements,
									List<Variable> backup ) {
		
		List<String> f = new ArrayList<>();
		
		f.add( wrap(condition, "=>", fCaseStatements) );
		
		List<String> syncVariables = syncVariable(backup, listVariables);
		String syncFormula = wrapAll(syncVariables, "and");
		
		if (syncFormula == null)
			f.add( wrap(wrap("not", condition), "=>", wrap(lastBreak.getString(), "=", "true")) );
		else
			f.add( wrap(wrap("not", condition), "=>", wrap(wrap(lastBreak.getString(), "=", "true"), "and", syncFormula)));
		
		return f;
	}
	

	private CtSwitch switchCase;
	
	private String selector;
	
	private List<String> caseExpList;
	
	StringBox lastBreak = new StringBox();

}
