package haplous.rest.services;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TestDriver {
    public Method method[];

    public static void perform(String className, String methodName, String[] argList) throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException, SecurityException {
        Class classObj = null;
        try {
            classObj = Class.forName("haplous.rest." + className);
        } catch (ClassNotFoundException e1) {
            System.out.println(className +"."+ methodName +" Keyword does not exists - Class not Found exception occurred");
            e1.printStackTrace();
        }

        Object obj = classObj.newInstance();

        Class[] classArr = null;

        ArrayList<Class> classList = new ArrayList<Class>();
        ArrayList<String> args = new ArrayList<String>();

        for (int i=0; i<argList.length; i++)
        {
            if (argList[i]!= null && !argList[i].equals("") ) {
                classList.add(String.class);
                args.add(argList[i]);
            }
        }
        classArr =  new Class[classList.size()];
        classArr = (Class[])classList.toArray(classArr);
        Method m = classObj.getMethod(methodName, classArr);
        m.invoke(obj,args.toArray());

    }
}

