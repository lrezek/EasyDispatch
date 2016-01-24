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
import java.util.LinkedList;
import java.util.List;
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
    public List<HandleMeta> createHandleMetas(Class cls) throws EasyDispatchReflectionException
    {
        List<HandleMeta> annotationEntries = new LinkedList<>();
        
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
    private List<HandleMeta> createHandleMetasFromClassAnnotations(Class cls) throws EasyDispatchReflectionException
    {
        // Prepare collection to return
        List<HandleMeta> toReturn = new LinkedList<>();
        
        // Read in @Handles annotations on the class
        List<Handles> annotations = Arrays.asList((Handles[]) cls.getAnnotationsByType(Handles.class));
        
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
                // Initialize method
                Method method = null;
                
                try
                {
                    // Get the best method with the parameter type
                    method = cls.getMethod(methodName, handledClass);                   
                }
                catch(NoSuchMethodException ignored) {}
                
                // Could not find method, try one without parameters
                if(method == null)
                {
                    try
                    {
                        // Try to get a method with no parameters
                        method = cls.getMethod(methodName); 
                    }
                    catch(NoSuchMethodException ignored) {}
                }
                
                // Still no matching method, exception
                if(method == null)
                {
                    throw new EasyDispatchReflectionException("Failed to find method with name " + methodName + " with 0 parameters or exactly 1 parameter of type " + handledClass + " in " + cls.getName());
                }
                
                // Construct the AnnotationEntry
                toReturn.add(new HandleMeta(method, handledClass, annotation.dispatchStrategy())); 
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
    private List<HandleMeta> createHandleMetasFromMethodAnnotations(Class cls) throws EasyDispatchReflectionException
    {
        // Prepare collection to return
        List<HandleMeta> toReturn = new LinkedList<>();

        // Read in all the appropriate methodss
        List<Method> methods = Arrays.stream(cls.getMethods())
                .filter(method -> !method.isBridge())
                .filter(method -> method.isAnnotationPresent(Handles.class) || method.isAnnotationPresent(HandlesContainer.class))
                .collect(Collectors.toList());
               
        // Loop over all the methods
        for(Method method : methods)
        {
            // Must have 1 parameter or 0 parameters
            if(method.getParameterCount() > 1)
            {
                throw new EasyDispatchReflectionException("Method with name " + method.getName() + " has multiple parameters in " + cls.getName());
            }
            
            // Loop over the annotations
            for(Handles annotation : method.getAnnotationsByType(Handles.class))
            {
                // Get the handled classes
                Class[] handledClasses = annotation.value();
                
                // If the method has no parameters, just add it for every handled class
                if(method.getParameterCount() == 0)
                {
                    for(Class handledClass : handledClasses)
                    {
                        toReturn.add(new HandleMeta(method, handledClass, annotation.dispatchStrategy()));
                    }
                }
                
                // The method has 1 parameter, make sure it's assignable before adding
                else
                {                   
                    // No handled class specified, try the parameter type
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