package com.github.tommyettinger.bitnucleus.annotations;

import com.github.tommyettinger.bitnucleus.annotations.Annotations.Struct;
import com.github.tommyettinger.bitnucleus.annotations.Annotations.StructField;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic.Kind;
import java.util.List;
import java.util.Set;

/**Generates ""value types"" classes that are packed into integer primitives of the most aproppriate size.
 * It would be nice if Java didn't make crazy hacks like this necessary.*/
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({
    "com.github.tommyettinger.bitnucleus.annotations.Annotations.Struct"
})
public class StructAnnotationProcessor extends AbstractProcessor{
    private int round;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv){
        super.init(processingEnv);
        //put all relevant utils into utils class
        Utils.typeUtils = processingEnv.getTypeUtils();
        Utils.elementUtils = processingEnv.getElementUtils();
        Utils.filer = processingEnv.getFiler();
        Utils.messager = processingEnv.getMessager();
    }

    private String left(int offset, int total)
    {
        return offset == 0
                ? total > 32 ? "1L" : "1"
                : total > 32 ? "(1L << " + offset + ")" : "(1 << " + offset + ")";
    }
    private String left(String item, int offset)
    {
        return offset == 0
                ? item
                : "(" + item + " << " + offset + ")";
    }

//    private String right(int offset, int total)
//    {
//        return offset == 0
//                ? total > 32 ? "1L" : "1"
//                : total > 32 ? "(1L >>> " + offset + ")" : "(1 >>> " + offset + ")";
//    }
    private String right(String item, int offset)
    {
        return offset == 0
                ? item
                : "(" + item + " >>> " + offset + ")";
    }
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv){
        if(round++ != 0) return false; //only process 1 round

        try{
            Set<TypeElement> elements = ElementFilter.typesIn(roundEnv.getElementsAnnotatedWith(Struct.class));

            for(TypeElement elem : elements){

                if(!elem.getSimpleName().toString().endsWith("Struct")){
                    Utils.messager.printMessage(Kind.ERROR, "All classes annotated with @Struct must have their class names end in 'Struct'.", elem);
                    continue;
                }

                String structName = elem.getSimpleName().toString().substring(0, elem.getSimpleName().toString().length() - "Struct".length());
                String structParam = structName.toLowerCase();

                TypeSpec.Builder classBuilder = TypeSpec.classBuilder(structName)
                    .addModifiers(Modifier.FINAL, Modifier.PUBLIC);

                try{
                    List<VariableElement> variables = ElementFilter.fieldsIn(elem.getEnclosedElements());
                    int structSize = variables.stream().mapToInt(StructAnnotationProcessor::varSize).sum();
                    int structTotalSize = (structSize <= 8 ? 8 : structSize <= 16 ? 16 : structSize <= 32 ? 32 : 64);

                    if(variables.size() == 0){
                        Utils.messager.printMessage(Kind.ERROR, "making a struct with no fields is utterly pointless.", elem);
                        continue;
                    }

                    //obtain type which will be stored
                    Class<?> structType = typeForSize(structSize);

                    //[constructor] get(fields...) : structType
                    MethodSpec.Builder constructor = MethodSpec.methodBuilder("get")
                    .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                    .returns(structType);

                    StringBuilder cons = new StringBuilder();
                    StringBuilder doc = new StringBuilder();
                    doc.append("Bits used: ").append(structSize).append(" / ").append(structTotalSize).append("\n");

                    int offset = 0;
                    for(VariableElement var : variables){
                        int size = varSize(var);
                        TypeName varType = TypeName.get(var.asType());
                        String varName = var.getSimpleName().toString();

                        //add val param to constructor
                        constructor.addParameter(varType, varName);

                        //[get] field(structType) : fieldType
                        MethodSpec.Builder getter = MethodSpec.methodBuilder(var.getSimpleName().toString())
                            .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                            .returns(varType)
                            .addParameter(structType, structParam);
                        //[set] field(structType, fieldType) : structType
                        MethodSpec.Builder setter = MethodSpec.methodBuilder(var.getSimpleName().toString())
                            .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                            .returns(structType)
                            .addParameter(structType, structParam).addParameter(varType, "value");

                        //[getter]
                        if(varType == TypeName.BOOLEAN){
                            //bools: single bit, is simplified
                            getter.addStatement("return ($L & $L) != 0", structParam, left(offset, structSize));
                        }else if(varType == TypeName.FLOAT){
                            //floats: need conversion
                            getter.addStatement("return Float.intBitsToFloat((int)($L & $L))", right(structParam, offset), bitString(size, structTotalSize));
                        }else{
                            //bytes, shorts, chars, ints
                            getter.addStatement("return ($T)($L & $L)", varType, right(structParam, offset), bitString(size, structTotalSize));
                        }

                        //[setter] + [constructor building]
                        if(varType == TypeName.BOOLEAN){
                            cons.append(" | (").append(varName).append(" ? ").append(left(offset, structSize)).append(" : 0)");

                            //bools: single bit, needs special case to clear things
                            setter.addStatement("return value ? ($T)(($L | $L)) : ($T)($L & ~$L)",
                                    structType, structParam, left(offset, structSize), structType, structParam, left(offset, structSize));
//                            setter.beginControlFlow("if(value)");
//                            setter.addStatement("return ($T)(($L & ~(1L << $L)))", structType, structParam, offset);
//                            setter.nextControlFlow("else");
//                            setter.addStatement("return ($T)(($L | (1L << $L)))", structType, structParam, offset);
//                            setter.endControlFlow();
                        }else if(varType == TypeName.FLOAT){
                            cons.append(" | (").append("(").append(structType).append(")")
                                    .append("Float.floatToIntBits(").append(varName).append(offset == 0 ? ")" : ") << " + offset).append(')');

                            //floats: need conversion
                            setter.addStatement("return ($T)(($L & $L) | (($T)Float.floatToIntBits(value)$L))",
                                    structType, structParam, bitString(offset, size, structTotalSize), structType, 
                                    offset == 0 ? "" : " << " + offset);
                        }else{
                            cons.append(" | (").append("(").append(structType).append(")").append(varName).append(offset == 0 ? "" : " << " + offset).append(')');

                            //bytes, shorts, chars, ints
                            setter.addStatement("return ($T)(($L & $L) | (($T)value$L))", structType, structParam, bitString(offset, size, structTotalSize), structType, offset == 0 ? "" : " << " + offset);
                        }

                        doc.append("<br>  ").append(varName).append(" [").append(offset).append("..").append(size + offset).append("]\n");

                        //add finished methods
                        classBuilder.addMethod(getter.build());
                        classBuilder.addMethod(setter.build());

                        offset += size;
                    }

                    classBuilder.addJavadoc(doc.toString());

                    //add constructor final statement + add to class and build
                    constructor.addStatement("return ($T)($L)", structType, cons.substring(3));
                    classBuilder.addMethod(constructor.build());
                    PackageElement pe = Utils.elementUtils.getPackageOf(elem);
                    if(pe.isUnnamed())
                    {
                        JavaFile.builder("structs", classBuilder.build()).build().writeTo(Utils.filer);
                    }
                    else
                    {
                        JavaFile.builder(pe.getQualifiedName().toString() + ".structs", classBuilder.build()).build().writeTo(Utils.filer);
                    }
                }catch(IllegalArgumentException e){
                    e.printStackTrace();
                    Utils.messager.printMessage(Kind.ERROR, e.getMessage(), elem);
                }
            }

            return true;
        }catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    static String bitString(int offset, int size, int totalSize){
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < offset; i++) builder.append('0');
        for(int i = 0; i < size; i++) builder.append('1');
        for(int i = 0; i < totalSize - size - offset; i++) builder.append('0');
        if(totalSize > 32)
            return builder.append("b0").reverse().append('L').toString();
        else
            return builder.append("b0").reverse().toString();
    }

    static String bitString(int size, int totalSize){
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < size; i++) builder.append('1');
        for(int i = 0; i < totalSize - size; i++) builder.append('0');
        if(totalSize > 32)
            return builder.append("b0").reverse().append('L').toString();
        else
            return builder.append("b0").reverse().toString();
    }

    static int varSize(VariableElement var) throws IllegalArgumentException{
        if(!var.asType().getKind().isPrimitive()){
            throw new IllegalArgumentException("All struct fields must be primitives: " + var);
        }

        StructField an = var.getAnnotation(StructField.class);
        if(var.asType().getKind() == TypeKind.BOOLEAN && an != null && an.value() != 1){
            throw new IllegalArgumentException("Booleans can only be one bit long... why would you do this?");
        }

        if(var.asType().getKind() == TypeKind.FLOAT && an != null && an.value() != 32){
            throw new IllegalArgumentException("Float size can't be changed. Very sad.");
        }

        return an == null ? typeSize(var.asType().getKind()) : an.value();
    }

    static Class<?> typeForSize(int size) throws IllegalArgumentException{
        if(size <= 8){
            return byte.class;
        }else if(size <= 16){
            return short.class;
        }else if(size <= 32){
            return int.class;
        }else if(size <= 64){
            return long.class;
        }
        throw new IllegalArgumentException("Too many fields, must fit in 64 bits. Curent size: " + size);
    }

    /**returns a type's element size in bits.*/
    static int typeSize(TypeKind kind) throws IllegalArgumentException{
        switch(kind){
            case BOOLEAN:
                return 1;
            case BYTE:
                return 8;
            case SHORT:
            case CHAR:
                return 16;
            case FLOAT:
            case INT:
                return 32;
            default:
                throw new IllegalArgumentException("Invalid type kind: " + kind + ". Note that doubles and longs are not supported.");
        }
    }
}
