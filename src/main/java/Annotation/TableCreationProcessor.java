package Annotation;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.SimpleElementVisitor8;
import javax.tools.Diagnostic;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class TableCreationProcessor extends AbstractProcessor {
	private ProcessingEnvironment env;
	private StringBuilder sql = new StringBuilder();

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		this.env = processingEnv;
		System.out.println("TableCreationProcessor注解处理器初始化完成.............");
	}
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		TypeElement[] ty = new TypeElement[3];
		for (TypeElement anno : annotations) {
			switch (anno.getSimpleName().toString()) {
			case "DBTable":
				ty[0] = anno;
				break;
			case "SQLString":
				ty[1] = anno;
				break;
			case "SQLInteger":
				ty[2] = anno;
			default:
				annotations.toArray(new TypeElement[] {});
			}
		}
		for (TypeElement anno : ty) {
			for (Element round : roundEnv.getElementsAnnotatedWith(anno)) {
				// accept方法会判断Element类型 对应执行具体的类型方法
				// eg：Element是TypeElement那么此次循环会调用visitType()方法;
				// 如果需要返回值 只需要修改对应的反型参数即可,泛型参数不能是基本数据类型。
				round.accept(new TableCreationVisitor(), 0b1010);
				if (round.getAnnotation(DBTable.class) != null)
					env.getMessager().printMessage(Diagnostic.Kind.WARNING, "警告：此次循环的注解是DBTable");
				else {
					System.out.println("creation SQL is :\n" + sql.toString());
				}
			}
		}
		return true;
	}

	private class TableCreationVisitor extends SimpleElementVisitor8<Element, Integer> {
		@Override
		public Element visitType(TypeElement e, Integer p) {
			DBTable dbTable = e.getAnnotation(DBTable.class);
			if (dbTable != null) {
				sql.append("CREATE TABLE ").append(
						(dbTable.name().length() < 1) ? e.getSimpleName().toString().toUpperCase() : dbTable.name())
						.append(" (");

			}
			return e;
		}

		/*
		 * @Override public Void visitPackage(PackageElement e, Void p) {
		 * System.err.println(111); return defaultAction(e, p); }
		 */

		@Override
		public Element visitVariable(VariableElement e, Integer p) {
			
			String columnName = "";
			if (e.getAnnotation(SQLInteger.class) != null) {
				sql = new StringBuilder(sql.substring(0, sql.length() - 2)).append(",");
				SQLInteger sInt = e.getAnnotation(SQLInteger.class);
				if (sInt.name().length() < 1)
					columnName = e.getSimpleName().toString().toUpperCase();
				else
					columnName = sInt.name();
				sql.append("\n " + columnName + " INT" + getConstraints(sInt.constraints())).append(");");

			}

			if (e.getAnnotation(SQLString.class) != null) {
				SQLString sString = e.getAnnotation(SQLString.class);
				if(getConstraints(sString.constraints())==null||getConstraints(sString.constraints()).equals(""))
					sql = new StringBuilder(sql.substring(0, sql.length() - 2)).append(",");
				if (sString.name().length() < 1)
					columnName = e.getSimpleName().toString().toUpperCase();
				else
					columnName = sString.name();
				sql.append("\n " + columnName + " VARCHAR(" + sString.value() + ")"
						+ getConstraints(sString.constraints()) + ");");
			}
			return e;
		}

		/*
		 * @Override public Void visitExecutable(ExecutableElement e, Void p) {
		 * System.err.println(111); return defaultAction(e, p); }
		 */

		/*
		 * @Override public Void visitTypeParameter(TypeParameterElement e, Void p) {
		 * System.err.println(111); return defaultAction(e, p); }
		 */
	}

	private static String getConstraints(Constraints con) {
		String constraints = "";
		if (!con.allowNull())
			constraints += "NOT NULL";
		if (con.primaryKey())
			constraints += "PRIMARY KEY";
		if (con.unique())
			constraints += "UNIQUE";
		return constraints;
	}

	/*
	 * ,
	 * 
	 */
	@Override
	public Set<String> getSupportedAnnotationTypes() {
		Set<String> a = new HashSet<String>();
		Collections.addAll(a, new String[] { DBTable.class.getCanonicalName(), Constraints.class.getCanonicalName(),
				SQLString.class.getCanonicalName(), SQLInteger.class.getCanonicalName() });
		return a;
	}
}
