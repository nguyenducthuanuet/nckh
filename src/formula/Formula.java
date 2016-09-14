package formula;

import java.util.ArrayList;
import java.util.List;

public class Formula {

	public Formula() {
		listExpressions = new ArrayList<>();
	}

	public Formula(String condition) {
		this();
		this.condition = condition;
	}

	public Formula(Formula other) {
		condition = other.condition;
		if ( other.hasChild() ) {
			for(Formula f: other.listExpressions) {
				listExpressions.add(f.clone());
			}
		}
	}

	public void setCondition(String f) {
		condition = f;
	}

	public String getCondition() {
		return condition;
	}

	public void add(String f) {
		listExpressions.add(new Formula(f));
	}

	public void add(Formula child) {
		if (child == null)
			return;

		if (child.condition == null)
			listExpressions.addAll(child.listExpressions);
		else
			listExpressions.add(child);
	}

	public void add(List<String> childList) {
		if (childList == null || childList.isEmpty())
			return;

		for(String str: childList) {
			add(str);
		}
	}

	public void add(int index, Formula child) {
		if (child == null)
			return;

		if (child.condition == null)
			listExpressions.addAll(index, child.listExpressions);
		else
			listExpressions.add(index, child);
	}

	public void addChild(Formula child) {
		if (child == null)
			return;

		listExpressions.add(child);
	}



	public void addChild(String child) {
		if (child == null)
			return;

		listExpressions.add(new Formula(child));
	}


	public String getFormula() {
		return condition;
	}

	public List<Formula> getChilds() {
		return listExpressions;
	}

	public Formula clone() {
		return new Formula(this);
	}

	public boolean hasChild() {
		return listExpressions.size() != 0;
	}

	public boolean isEmpty () {
		return listExpressions.isEmpty();
	}

	public int size() {
		return listExpressions.size();
	}

	public void print(int nSpaces) {

/*		System.out.println("");
        
	    String spaces = (nSpaces==0)?"":String.format("%"+ nSpaces+"s", "");
	    int nSpacesOfChild = 0;
	    int n = 0;
	    if (condition != null) {
            if ( listExpressions.isEmpty() ) {
                System.out.print(spaces + "( and" + condition);
            } else {
                System.out.print(spaces + "( =>" + condition);
                nSpacesOfChild = nSpaces + 4;
            }
            n++;
	    } else {
            nSpacesOfChild = nSpaces;
	    }


	    for ( int i = 0; i < listExpressions.size(); i++) {
            System.out.println("");
            listExpressions.get(i).print(nSpacesOfChild);
            n++;
	    }

 //       String brackets = (n==0?"":String.format("%"+nOfSpace+"s", ")");
        String brackets = "";
        for (int i = 0; i < n; i++) {
            brackets += ")";
        }

        System.out.print(brackets);
*/

		int nOfSpaceOfChild;
		if (condition != null) {
			String spaces = (nSpaces==0)?"":String.format("%"+nSpaces+"s", "");
			System.out.println(spaces + condition);
			nOfSpaceOfChild = nSpaces + 4;
		} else {
			nOfSpaceOfChild = nSpaces;
		}
	//	System.out.println(root);
		for (Formula f: listExpressions) {
			f.print(nOfSpaceOfChild);
		}

	}



	public static void main(String[] args) {
		Formula f = new Formula();
//		f.set("root");
		Formula child1 = new Formula("1");
		child1.add("2");
		child1.add("2");
		child1.add("2");
		f.add(new Formula("1"));
		f.add(child1);
		f.add(new Formula("1"));
		f.add(new Formula("1"));
		Formula child2 = new Formula();
		child2.add("2");
		child2.add("2");
		child2.add("2");
		f.add(child2);



		f.print(0);

		System.out.println("done");
	}

	private List<Formula> listExpressions;
	private String condition = null;
}
