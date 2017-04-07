package com.twiceyuan.retrofitmockresult.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.twiceyuan.retrofitmockresult.core.Constants;
import com.twiceyuan.retrofitmockresult.core.Generated;
import com.twiceyuan.retrofitmockresult.core.MockApi;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

@AutoService(Processor.class)
public class MockApiProcessor extends AbstractProcessor {

    private Filer    filer;
    private Elements elements;

    private SimpleDateFormat mDateFormat = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
            Locale.getDefault()
    );

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnvironment.getFiler();
        elements = processingEnvironment.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        Set<? extends Element> mockApis = roundEnvironment.getElementsAnnotatedWith(MockApi.class);

        for (Element mockApi : mockApis) {
            MockApi annotation = mockApi.getAnnotation(MockApi.class);
            generateClass(mockApi, annotation.enable());
        }

        return true;
    }

    /**
     * 生成 Class 文件
     *
     * @param mockApi  mock Api Class
     * @param isEnable 是否删除该文件
     */
    private void generateClass(Element mockApi, boolean isEnable) {
        Name qualifiedName = ((TypeElement) mockApi).getQualifiedName();
        String nameString = qualifiedName.toString();
        ClassName className = ClassName.bestGuess(nameString);

        TypeSpec.Builder generateClassBuilder = TypeSpec.classBuilder(className.simpleName() + Constants.SUFFIX)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .superclass(className);

        appendEmptyMethod(generateClassBuilder, mockApi);

        generateClassBuilder.addField(FieldSpec
                .builder(boolean.class, "ENABLE",
                        Modifier.FINAL,
                        Modifier.PUBLIC,
                        Modifier.STATIC)
                .initializer("$L", isEnable).build());

        generateClassBuilder.addAnnotation(AnnotationSpec.get(new Generated() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return Generated.class;
            }

            @Override
            public String value() {
                return MockApiProcessor.class.getCanonicalName();
            }

            @Override
            public String date() {
                return mDateFormat.format(new Date());
            }

            @Override
            public String comment() {
                return "";
            }
        }));
        TypeSpec generateClass = generateClassBuilder.build();

        JavaFile javaFile = JavaFile.builder(className.packageName(), generateClass).build();

        try {
            javaFile.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 给 Mock 实现类动态追加空方法
     */
    private void appendEmptyMethod(TypeSpec.Builder builder, Element mockApiClass) {

        List<? extends Element> methods = this.elements.getAllMembers((TypeElement) mockApiClass);

        for (Element method : methods) {
            if (!(method instanceof ExecutableElement)) {
                continue;
            }
            if (!method.getModifiers().contains(Modifier.ABSTRACT)) {
                continue;
            }
            TypeMirror returnType = ((ExecutableElement) method).getReturnType();

            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(method.getSimpleName().toString())
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(TypeName.get(returnType));

            methodBuilder.addStatement("throw new $T($S)", IllegalStateException.class,
                    method.getSimpleName() + "() is an abstract method!");

            appendParameters(methodBuilder, (ExecutableElement) method);
            builder.addMethod(methodBuilder.build());
        }
    }

    /**
     * 追加空方法的参数
     */
    private void appendParameters(MethodSpec.Builder builder, ExecutableElement method) {
        List<? extends VariableElement> parameters = method.getParameters();
        if (parameters == null || parameters.size() == 0) {
            return;
        }

        for (int i = 0; i < parameters.size(); i++) {
            VariableElement element = parameters.get(i);
            builder.addParameter(TypeName.get(element.asType()), element.getSimpleName().toString());
        }
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<>();
        types.add(MockApi.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
