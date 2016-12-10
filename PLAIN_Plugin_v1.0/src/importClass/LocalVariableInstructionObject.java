package importClass;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;

public class LocalVariableInstructionObject {
	private TypeObject type;
    private String name;
    //private SimpleName simpleName;
    private ASTInformation simpleName;
    private volatile int hashCode = 0;

    public LocalVariableInstructionObject(TypeObject type, String name) {
        this.type = type;
        this.name = name;
    }

    public TypeObject getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public void setSimpleName(SimpleName simpleName) {
    	//this.simpleName = simpleName;
    	this.simpleName = ASTInformationGenerator.generateASTInformation(simpleName);
    }

    public SimpleName getSimpleName() {
    	//return this.simpleName;
    	ASTNode node = this.simpleName.recoverASTNode();
    	if(node instanceof QualifiedName) {
    		return ((QualifiedName)node).getName();
    	}
    	else {
    		return (SimpleName)node;
    	}
    }

    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }

        if (o instanceof LocalVariableInstructionObject) {
        	LocalVariableInstructionObject lvio = (LocalVariableInstructionObject)o;
            return this.name.equals(lvio.name) && this.type.equals(lvio.type);
        }
        return false;
    }

    public boolean equals(LocalVariableDeclarationObject lvdo) {
    	return this.name.equals(lvdo.getName()) && this.type.equals(lvdo.getType());
    }

    public int hashCode() {
    	if(hashCode == 0) {
    		int result = 17;
    		result = 37*result + type.hashCode();
    		result = 37*result + name.hashCode();
    		hashCode = result;
    	}
    	return hashCode;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(type).append(" ");
        sb.append(name);
        return sb.toString();
    }
}