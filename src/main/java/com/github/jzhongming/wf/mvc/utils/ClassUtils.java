package com.github.jzhongming.wf.mvc.utils;

import java.lang.reflect.Method;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;


public class ClassUtils {

    /**
     * 得到方法参数名称数组
     * 由于java没有提供获得参数名称的api，利用了javassist来实现
     *
     * @return 参数名称数组
     */
    public static String[] getMethodParamNames(Class<?> clazz, Method method) {
        Class<?>[] paramTypes = method.getParameterTypes();

        try {
            ClassPool pool = ClassPool.getDefault();

            pool.insertClassPath(new ClassClassPath(clazz));

            CtClass cc = pool.get(clazz.getName());

            String[] paramTypeNames = new String[method.getParameterTypes().length];

            for (int i = 0; i < paramTypes.length; i++)
                paramTypeNames[i] = paramTypes[i].getName();

            CtMethod cm = cc.getDeclaredMethod(method.getName(), pool.get(paramTypeNames));

            // 使用javaassist的反射方法获取方法的参数名
            MethodInfo methodInfo = cm.getMethodInfo();

            CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
            LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute
                    .getAttribute(LocalVariableAttribute.tag);
            if (attr == null) {
                throw new RuntimeException("class:" + clazz.getName()
                        + ", have no LocalVariableTable, please use javac -g:{vars} to compile the source file");
            }

            int startIndex = getStartIndex(attr);
            String[] paramNames = new String[cm.getParameterTypes().length];
            int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;

            for (int i = 0; i < paramNames.length; i++)
                paramNames[i] = attr.variableName(startIndex + i + pos);

            return paramNames;

        } catch (NotFoundException e) {
            e.printStackTrace();
            return new String[0];
        }
    }

    private static int getStartIndex(LocalVariableAttribute attr) {

        int startIndex = 0;
        for (int i = 0; i < attr.length(); i++) {
            if ("this".equals(attr.variableName(i))) {
                startIndex = i;
                break;
            }
        }
        return startIndex;
    }
}
