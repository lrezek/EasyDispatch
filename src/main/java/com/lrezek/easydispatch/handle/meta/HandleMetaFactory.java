/*
 * The MIT License
 *
 * Copyright 2016 Lukas Rezek.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.lrezek.easydispatch.handle.meta;

import com.lrezek.easydispatch.annotation.Handles;
import com.lrezek.easydispatch.annotation.HandlesContainer;
import com.lrezek.easydispatch.exception.EasyDispatchReflectionException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;

/**
 * Defines the handle meta factory. This class is responsible for creating Handle
 * meta objects for a given class of Handler.
 * 
 * @author Lukas Rezek
 */
public class HandleMetaFactory 
{
    /** The default method name when one isn't specified in the annotation. */
    private String defaultMethodName = "handle";
    
    /**
     * Creates a collection of handle meta objects for a specified class.
     * 
     * @param cls The class to read annotation information on.
     * @return The collection of handle meta objects.
     * @throws EasyDispatchReflectionException If there is an annotation error in the class.
     */
    public Collection<HandleMeta> createHandleMetas(Class cls) throws EasyDispatchReflectionException
    {
        Collection<HandleMeta> annotationEntries = new LinkedList<>();
        
        // Populate the annotation information
        annotationEntries.addAll(this.createHandleMetasFromClassAnnotations(cls));
        annotationEntries.addAll(this.createHandleMetasFromMethodAnnotations(cls));
        
        return annotationEntries;
    }
        
    /**
     * Creates a collection of handle meta objects from the class annotations on 
     * the specified class.
     * 
     * @param cls The class to read annotation information from.
     * @return The collection of handle meta objects.
     * @throws EasyDispatchReflectionException If there is an annotation error in the class-level annotations.
     */
    private Collection<HandleMeta> createHandleMetasFromClassAnnotations(Class cls) throws EasyDispatchReflectionException
    {
        // Prepare collection to return
        Collection<HandleMeta> toReturn = new LinkedList<>();
        
        // Read in @Handles annotations on the class
        Collection<Handles> annotations = Arrays.asList((Handles[]) cls.getAnnotationsByType(Handles.class));
        
        // Loop over all the annotations
        for(Handles annotation : annotations)
        {
            // Get the defined handled classes
            Class[] handledClasses = annotation.value();
            
            // Make sure there's at least one handled class
            if(handledClasses == null || handledClasses.length == 0)
            {
                throw new EasyDispatchReflectionException("Empty @Handles annotation on " + cls.getName());
            }
            
            // Pick the right method name
            String methodName = annotation.method();
            if(methodName == null || methodName.isEmpty())
            {
                methodName = this.defaultMethodName;
            }
            
            // Loop over all the handled classes
            for(Class handledClass : handledClasses)
            {
                try
                {
                    // Get the best method
                    Method method = cls.getMethod(methodName, handledClass);
                    
                    // Construct the AnnotationEntry
                    toReturn.add(new HandleMeta(method, handledClass, annotation.dispatchStrategy()));                    
                }
                catch(NoSuchMethodException e)
                {
                    throw new EasyDispatchReflectionException("Failed to find method with name " + methodName + " with exactly 1 parameter of type " + handledClass + " in " + cls.getName(), e);
                }
            }
        }
        
        return toReturn;
    }
            
    /**
     * Creates a collection of handle meta objects from the method annotations
     * on the specified class.
     * 
     * @param cls The class to read annotation information from.
     * @return The collection of handle meta objects.
     * @throws EasyDispatchReflectionException If there is an annotation error in the method-level annotations.
     */
    private Collection<HandleMeta> createHandleMetasFromMethodAnnotations(Class cls) throws EasyDispatchReflectionException
    {
        // Prepare collection to return
        Collection<HandleMeta> toReturn = new LinkedList<>();

        // Read in all the appropriate methodss
        Collection<Method> methods = Arrays.stream(cls.getMethods())
                .filter(method -> !method.isBridge())
                .filter(method -> method.isAnnotationPresent(Handles.class) || method.isAnnotationPresent(HandlesContainer.class))
                .collect(Collectors.toList());
               
        // Loop over all the methods
        for(Method method : methods)
        {
            // Must have 1 paramter total
            if(method.getParameterCount() != 1)
            {
                throw new EasyDispatchReflectionException("Method with name " + method.getName() + " has multiple parameters in " + cls.getName());
            }
            
            // Loop over the annotations
            for(Handles annotation : method.getAnnotationsByType(Handles.class))
            {
                // Get the handled classes
                Class[] handledClasses = annotation.value();

                // None specified, try the parameter type
                if(handledClasses == null || handledClasses.length == 0)
                {
                    handledClasses = new Class[1];
                    handledClasses[0] = method.getParameterTypes()[0];
                }

                // Loop over them all and make this this method can actually accept the parameters
                for(Class handledClass : handledClasses)
                {
                    // If the method can't handle this parameter type, error
                    if(!method.getParameterTypes()[0].isAssignableFrom(handledClass))
                    {
                        throw new EasyDispatchReflectionException("Method annotated with name " + method.getName() + " cannot accept a parameter of type " + handledClass + " in " + cls.getName());
                    }

                    toReturn.add(new HandleMeta(method, handledClass, annotation.dispatchStrategy()));
                }
            }
        }
               
        return toReturn;
    }
    
    /**
     * Sets the default method name to use for class level annotations that don't
     * specify one.
     * 
     * @param defaultMethodName Default method name to use.
     */
    public void setDefaultMethodName(String defaultMethodName)
    {
        this.defaultMethodName = defaultMethodName;
    }
}