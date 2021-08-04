package analyzer;

import java.util.Comparator;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

public class Case implements Comparator<Case>, Comparable<Case> {

	private ClassOrInterfaceDeclaration classDecl;
	private String useCase;
	private boolean isDAO;

	public Case(ClassOrInterfaceDeclaration classDecl, String useCase) {
		this.classDecl = classDecl;
		this.useCase = useCase;
	}

	public String getId() {
		return this.classDecl.resolve().getQualifiedName() + "." + this.useCase;
	}

	/**
	 * @return the classDecl
	 */
	public ClassOrInterfaceDeclaration getClassDecl() {
		return this.classDecl;
	}

	/**
	 * @param classDecl the classDecl to set
	 */
	public void setClassDecl(ClassOrInterfaceDeclaration classDecl) {
		this.classDecl = classDecl;
	}

	/**
	 * @return the useCase
	 */
	public String getUseCase() {
		return this.useCase;
	}

	/**
	 * @param useCase the useCase to set
	 */
	public void setUseCase(String useCase) {
		this.useCase = useCase;
	}

	/**
	 * @return the isDAO
	 */
	public boolean isDAO() {
		return this.isDAO;
	}

	/**
	 * @param isDAO the isDAO to set
	 */
	public void setDAO(boolean isDAO) {
		this.isDAO = isDAO;
	}

	@Override
	public boolean equals(Object obj) {
		if ((obj == null) || (obj.getClass() != this.getClass())) {
			return false;
		}
		final Case other = (Case) obj;
		if ((getId() == null) ? (other.getId() != null) : !getId().equals(other.getId())) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = (53 * hash) + (getId() != null ? getId().hashCode() : 0);
		return hash;
	}

	@Override
	public int compareTo(Case o) {
		return getId().compareTo(o.getId());
	}

	@Override
	public int compare(Case arg0, Case arg1) {
		return arg0.getId().compareTo(arg1.getId());
	}
}
