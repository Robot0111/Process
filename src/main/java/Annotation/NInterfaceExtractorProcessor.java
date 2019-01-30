package Annotation;

import static javax.lang.model.util.ElementFilter.methodsIn;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.JavaFileObject;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("Annotation.ExtractInterface")
public class NInterfaceExtractorProcessor extends AbstractProcessor{
	private  ProcessingEnvironment env;
	private ArrayList<ExecutableElement> interfaceMethods = new ArrayList<ExecutableElement>();
	   @Override
	    public synchronized void init(ProcessingEnvironment processingEnv) {
	        super.init(processingEnv);
	        this.env = processingEnv;
	        System.out.println("AnnotationProcessor注解处理器初始化完成.........111.....");
	    }
	   
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		System.out.println("process注解处理器初始化完成.........111.....");
		for(TypeElement m: annotations) {
			for(Element j:roundEnv.getElementsAnnotatedWith(m)) {
				ExtractInterface annot = j.getAnnotation(ExtractInterface.class);
				if(annot == null)
					break;
				
				for(ExecutableElement k : methodsIn(j.getEnclosedElements())) 
if(k.getModifiers().contains(Modifier.PUBLIC)&&!k.getKind().equals(ElementKind.CONSTRUCTOR)&&!(k.getModifiers().contains(Modifier.STATIC))) 
					interfaceMethods.add(k);
				if(interfaceMethods.size() > 0) {
					try {
						JavaFileObject sourceFile =  env.getFiler().createSourceFile(annot.value());
						Writer  writer = sourceFile.openWriter(); 
						//PackageElement pk = packagesIn(j.getEnclosedElements()).get(0);
							writer.write("package com.think.注解;\n");
							writer.write("public interface " + annot.value() + "{\n");
							for(ExecutableElement n :interfaceMethods) {
								writer.write (Modifier.PUBLIC.name().toLowerCase()+" ");
								writer.write(n.getReturnType()+" ");
								writer.write(n.getSimpleName() + "(");
								int i = 0;
								for(VariableElement parm:n.getParameters()) {
									System.out.println("parm类型："+parm.asType());
									writer.write(parm.asType() + " " + parm.getSimpleName());
									if(++i < n.getParameters().size())
										writer.write(",");
								}
								writer.write(");\n");
							}
						
						writer.write("}");
						writer.flush();
						writer.close();
					}catch(IOException ioe) {
						throw new RuntimeException(ioe);
					}
				}
			}
			
		}
		return true;
	}
	/** 
     * 这里必须指定，这个注解处理器是注册给哪个注解的。注意，它的返回值是一个字符串的集合，包含本处理器想要处理的注解类型的合法全称 
     * @return  注解器所支持的注解类型集合，如果没有这样的类型，则返回一个空集合 
     */  
    @Override  
    public Set<String> getSupportedAnnotationTypes() {
    	return Collections.singleton(ExtractInterface.class.getCanonicalName());
    }  
  
    /** 
     * 指定使用的Java版本，通常这里返回SourceVersion.latestSupported()，默认返回SourceVersion.RELEASE_6 
     * @return  使用的Java版本 
     */
    @Override  
    public SourceVersion getSupportedSourceVersion() {  
        return SourceVersion.latestSupported();  
    }  
}
