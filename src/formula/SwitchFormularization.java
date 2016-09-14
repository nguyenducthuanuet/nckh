package formula;

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
	public Formula getFormula() {
		if (formula == null) {
			formula = new Formula();
			formularize();
		}
		
		return formula;
	}
	
	public void formularize() {
		
		caseExpList = new ArrayList<>();
		
		List<CtCase> caseList = switchCase.getCases();
		selector = formularize(switchCase.getSelector());
		
		Formula fCase;
		String breakDefault = null, lastBreakBeforeDefault = null;
		CtCase defaultCase = null;
		Formula fDefaultCase = null; 	// formula of default case
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
				formula.add(fCase);
			}
		}
		
		if (defaultCase != null) {	// Switch has default clause
			String condition = getDefaultCaseCondition(lastBreakBeforeDefault);
			fCase = makeCaseFormula(condition, fDefaultCase, defalutCaseBackup);
			formula.add(positionOfDefault, fCase);
		}
		
	}
	
	private Formula formularizeCase(CtCase c) {
		List<Variable> backup = Helper.copyList(listVariables);
		
		String condition = getCaseCondition(c);
		Formula fCaseStatements = formulaCaseStatement(c);
		
		Formula f = makeCaseFormula(condition, fCaseStatements, backup);
	
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
	
	private Formula formulaCaseStatement(CtCase c) {
		
		List<CtStatement> statements = c.getStatements();
		
		Formula fStatements = formularize(statements, null, lastBreak, null);
	
		if ( fStatements.isEmpty() ) {	// list statement is empty
			if (breakFlag.hasInitialized())
				breakFlag.increase();
			else 
				breakFlag.initialize();
			fStatements.add( wrap(breakFlag.getValue(), "=", "false") );
			lastBreak.setString(breakFlag.getValue());
		}
		
		return fStatements;
	}
	
	private Formula makeCaseFormula(	String condition, 
									Formula fCaseStatements,
									List<Variable> backup ) {
		
		Formula f = new Formula();
		
		fCaseStatements.setCondition(condition);
		f.add( fCaseStatements );
		
		Formula syncFormula = syncVariable(backup, listVariables);
		syncFormula.add( wrap(lastBreak.getString(), "=", "true"));	
		syncFormula.setCondition( wrap("not", condition) );
		f.add(syncFormula);

		return f;
	}
	

	private CtSwitch switchCase;
	
	private String selector;
	
	private List<String> caseExpList;
	
	StringBox lastBreak = new StringBox();

}
