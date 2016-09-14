package formula2;

import java.util.ArrayList;
import java.util.List;

import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtStatement;

import util.Helper;
import util.StringBox;
import util.Variable;

public class IfFormularization extends Formularization {

	public IfFormularization(	CtIf ifs, 
								List<Variable> listVariables,
								List<Variable> flagVariables,
								StringBox lastReturnFlag,
								StringBox lastBreakFlag,
								StringBox lastContinueFlag) 
	{
		this.ifs = ifs;
		thenStatement = ifs.getThenStatement();
		elseStatement = ifs.getElseStatement();
		
		assign(listVariables, flagVariables, lastReturnFlag);
		
		this.lastBreakFlag = lastBreakFlag;
		this.lastContinueFlag = lastContinueFlag;
		
		if( !ifFlag.hasInitialized())
			ifFlag.initialize();
		else 
			ifFlag.increase();
		
	}
	
	@Override
	public List<String> getFormula() {
		
		if(formula == null) {
			formula = new ArrayList<>();
			formularize();
		}
		
		return formula;
	}
	
	// assign condition of if statement to ifFlag
	private void assignCondition() {
		CtExpression<Boolean> conditionExp = ifs.getCondition();
		String condition = formularize(conditionExp);
		ifFlagStr = ifFlag.getValue();
		String assignment = wrap(ifFlagStr, "=", condition);
		formula.add(assignment);
	}
	
	// formularize then statement
	private void formularizeThenStatement() {
		
		List<Variable> lvBackup = listVariables;
		thenListVars = Helper.copyList(listVariables);
		listVariables = thenListVars;
		
		List<Variable> flagsBackup = flagVariables;
		thenFlags = Helper.copyList(flagVariables);
		flagVariables = thenFlags;
		
		
		fThen = formularize(thenStatement, thenReturnFlagVal, thenBreakFlagVal, null);
		
		listVariables = lvBackup; 
		
		flagVariables = flagsBackup;
	}
	
	// formularize else statement
	private void formularizeElseStatement() {
		List<Variable> lvBackup = listVariables;
		elseListVars = Helper.copyList(listVariables);
		listVariables = elseListVars;
		
		List<Variable> flagsBackup = flagVariables;
		elseFlags = Helper.copyList(flagVariables);
		flagVariables = elseFlags;
		
		
		fElse = formularize(elseStatement, elseReturnFlagVal, elseBreakFlagVal, null);
		
		listVariables = lvBackup;
		
		flagVariables = flagsBackup;
	}
	
	
	private void syncIndex() {
		syncIndex(listVariables, thenListVars, elseListVars);
		
		syncIndex(flagVariables, thenFlags, elseFlags);
	}
	
	// cap nhat lai index cua danh sach cac bien
	private void syncIndex(List<Variable> list, List<Variable> thenList,
							List<Variable> elseList) {
		Variable v1, v2;
		int oldIndex;
		for(Variable v: list) {
			oldIndex = v.getIndex();
			v1 = Variable.getVariable(v.getName(), thenList);
			v2 = Variable.getVariable(v.getName(), elseList);
			if( v1.getIndex() > v2.getIndex()) 
				v.setIndex(v1.getIndex());
			else
				v.setIndex(v2.getIndex());
			if (oldIndex < v.getIndex())
				v.initialize();
		}
	}
	
	
	// tim phan them vao
	private void sync() {
//		syncReturnFlag();
		syncFlag(returnFlag, thenReturnFlagVal, elseReturnFlagVal, lastReturnFlag);
		
		syncFlag(breakFlag, thenBreakFlagVal, elseBreakFlagVal, lastBreakFlag);
		
		syncFlag(continueFlag, thenContinueFlagVal, elseContinueFlagVal, lastContinueFlag);
		
		
		List<String> syncVariable = null;
		
		syncVariable = syncVariable(thenListVars, listVariables);
		thenAddition.addAll( syncVariable );
		
		syncVariable = syncVariable(elseListVars, listVariables);
		elseAddition.addAll( syncVariable );
	}
	
	private void syncReturnFlag() {
		if(thenReturnFlagVal.getString() == null) {
			if(elseReturnFlagVal.getString() != null) {
				thenAddition.add( wrap(elseReturnFlagVal.getString(), "=", "false") );
			}
		} else if(elseReturnFlagVal.getString() == null) {
			if(thenReturnFlagVal.getString() != null) {
				elseAddition.add( wrap(thenReturnFlagVal.getString(), "=", "false") );
			}
		} else if(thenReturnFlagVal.getString() != null && elseReturnFlagVal.getString() != null) {
			
			String temp;
			temp = syncAVar(thenFlags, flagVariables, "return");
			if(temp != null) {
				lastReturnFlag.setString(temp);
			}
				
			
			temp = syncAVar(elseFlags, flagVariables, "return");
			if(temp != null) {
				lastReturnFlag.setString(temp);
			}
		}
		
		lastReturnFlag.setString(returnFlag.getValue());
	}
	
	private void syncFlag(	Variable flag, StringBox thenFlag, 
							StringBox elseFlag, StringBox lastFlag) {
		if(thenFlag.getString() == null) {
			if(elseFlag.getString() != null) {
				thenAddition.add( wrap(elseFlag.getString(), "=", "false") );
			}
		} else if(elseFlag.getString() == null) {
			if(thenFlag.getString() != null) {
				elseAddition.add( wrap(thenFlag.getString(), "=", "false") );
			}
		} else if(thenFlag.getString() != null && elseFlag.getString() != null) {
			String temp;
			temp = syncAVar(thenFlags, flagVariables, flag.getName());
			if(temp != null) {
				lastFlag.setString(temp);
			}
				
			temp = syncAVar(elseFlags, flagVariables, flag.getName());
			if(temp != null) {
				lastFlag.setString(temp);
			}
		}
		
		// thenFlag.getString() != null || elseFlag.getString() != null
		
		if(flag.hasInitialized() && 
				(thenFlag.getString() != null || elseFlag.getString() != null))
			lastFlag.setString(flag.getValue());
	}

	
	private void makeFormula() {
		String f;
		
		if(thenAddition != null)
			fThen.addAll(thenAddition);
		f = wrapAll(fThen, "and");
		if(f != null)
			formula.add( wrap(ifFlagStr, "=>", f) );
		
		if(elseAddition != null)
			fElse.addAll(elseAddition);
		f = wrapAll(fElse, "and");
		if(f != null)
			formula.add( wrap(wrap("not", ifFlagStr), "=>", f) );
		
	}
	
	private void formularize() {
		
		assignCondition();
		
		formularizeThenStatement();
		formularizeElseStatement();
	
		syncIndex();
		
		sync();
		
		Variable.addVariable(listVariables, thenListVars);
		Variable.addVariable(listVariables, elseListVars);
		
		makeFormula();
	
	}
	
	
	private CtIf ifs;
	
	String ifFlagStr;
	
	String returnFlagValue;
	
	StringBox ownReturnFlag;
	
	StringBox thenReturnFlagVal = new StringBox();
	StringBox elseReturnFlagVal = new StringBox();
	
	CtStatement thenStatement;
	CtStatement	elseStatement;

	private List<String> fThen;	// formula of then statement
	private List<String> fElse;  	// formula of else statement
	
	private List<String> thenAddition = new ArrayList<>();	// phan them vao bieu thuc then nhu: return, break, continue hay dong bo gia tri cac bien
	private List<String> elseAddition = new ArrayList<>();	// phan them vao bieu thuc else nhu: return, break, continue hay dong bo gia tri cac bien
	
	private List<Variable> thenListVars;	// danh sach cac bien sau khi da cong thuc then
	private List<Variable> elseListVars;	// danh sach cac bien sau khi da cong thuc else
	
	private List<Variable> thenFlags;
	private List<Variable> elseFlags;
	
	private StringBox thenBreakFlagVal = new StringBox();
	private StringBox elseBreakFlagVal = new StringBox();
	private StringBox lastBreakFlag = new StringBox();
	
	
	private StringBox thenContinueFlagVal = new StringBox();
	private StringBox elseContinueFlagVal = new StringBox();
	private StringBox lastContinueFlag = new StringBox();
	
}
