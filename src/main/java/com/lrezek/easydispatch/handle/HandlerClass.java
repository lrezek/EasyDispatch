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
package com.lrezek.easydispatch.handle;

import com.lrezek.easydispatch.annotation.Handles;
import com.lrezek.easydispatch.exception.EasyDispatchException;
import com.lrezek.easydispatch.stategy.DispatchStrategy;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Contains a mapping of handlers inside a class.
 * 
 * @author Lukas Rezek
 */
public class HandlerClass 
{                
    /** The handler methods in this class. */
    private final Map<Class, Handler> handlers = new HashMap<>();
        
    /** The default dispatch strategy to use. */
    private final Class<? extends DispatchStrategy> defaultDispatchStrategy;
    
    /** The default method name to use. */
    private final String defaultMethodName;
    
    /** The object to use as the handler. */
    private final Object handlerObject;
    
    /**
     * Constructs the entry with an annotation.
     * 
     * @param handlerObject The handler object.
     * @param defaultDispatchStrategy The default dispatch strategy to use.
     * @param defaultMethodName The default method name to use.
     * @throws EasyDispatchException When no Handles annotation is found.
     */
    public HandlerClass(Object handlerObject, Class<? extends DispatchStrategy> defaultDispatchStrategy, String defaultMethodName) throws EasyDispatchException
    {                
        // Store defaults
        this.defaultDispatchStrategy = defaultDispatchStrategy;
        this.defaultMethodName = defaultMethodName;
        this.handlerObject = handlerObject;
        
        // Handle class annotations
        this.handleClassAnnotations();
        
        // Handle method annotations
        this.handleMethodAnnotations();
        
        // If we have no annotations, throw an exception
        if(this.handlers.isEmpty())
        {
            throw new EasyDispatchException(handlerObject.getClass() + " does not contain a valid @Handles annotation.");
        }
    }
        
    /**
     * Handles all the class annotations on the object.
     */
    private void handleClassAnnotations()
    {
        // Get all the class annotations
        Handles[] annotations = this.handlerObject.getClass().getAnnotationsByType(Handles.class);
        
        // Handle all the class annotations
        if(annotations != null && annotations.length != 0)
        {
            for(Handles annotation : annotations)
            {
                this.handleClassAnnotation(annotation);
            }
        }
    }
    
    /**
     * Handles a class annotation.
     * 
     * @param annotation The class annotation.
     */
    private void handleClassAnnotation(Handles annotation)
    {
        Class[] handledClasses = annotation.value();
        
        if(handledClasses != null && handledClasses.length != 0)
        {
            // Get the method name and dispatch strategy from the annotation
            String methodName = annotation.method().isEmpty() ? this.defaultMethodName : annotation.method();
            Class dispatchStrategy = annotation.dispatchStrategy() == DispatchStrategy.class ? this.defaultDispatchStrategy : annotation.dispatchStrategy();
            
            // Loop over the handled classes
            for(Class handledClass : handledClasses)
            {
                try
                {
                    // Attempt to construct the handlerMethod and put it on the map
                    this.handlers.put(handledClass, new Handler(this.handlerObject, handledClass, methodName, dispatchStrategy));
                }
                catch(EasyDispatchException e)
                {
                    // Do nothing, ignore the missing method
                }
            }
        }
    }
    
    /**
     * Handles all the method annotations on the object.
     */
    private void handleMethodAnnotations()
    {
        // Get all the methods
        Method[] methods = this.handlerObject.getClass().getDeclaredMethods();
        
        if(methods != null && methods.length != 0)
        {
            // Loop over all the methods
            for(Method method : methods)
            {
                // Get all the methods annotations
                Handles[] annotations = method.getAnnotationsByType(Handles.class);
                
                // Handle all the method annotations
                if(annotations != null && annotations.length != 0)
                {
                    for(Handles annotation : annotations)
                    {
                        try
                        {
                            this.handleMethodAnnotation(annotation, method);
                        }
                        catch(EasyDispatchException e)
                        {
                            // Do nothing, ignore the annotation
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Handles a method annotation.
     * 
     * @param annotation The method annotation.
     * @param method The method.
     */
    private void handleMethodAnnotation(Handles annotation, Method method) throws EasyDispatchException
    {
        Class[] handledClasses = annotation.value();
        
        if(handledClasses == null || handledClasses.length != 1)
        {
            throw new EasyDispatchException("A method-level @Handles must have a single class as its value");
        }
        
        // Prepapre the dispatch strategy
        Class dispatchStrategy = annotation.dispatchStrategy() == DispatchStrategy.class ? this.defaultDispatchStrategy : annotation.dispatchStrategy();
            
        // Attempt to construct the handlerMethod and put it on the map
        this.handlers.put(handledClasses[0], new Handler(this.handlerObject, handledClasses[0], method, dispatchStrategy));
    }
    
    /**
     * Gets the handler method for the dispatched object.
     * 
     * @param object The object.
     * @return The handler method.
     */
    public Handler getHandler(Object object)
    {
        return this.handlers.get(object.getClass());
    }
    
    /**
     * Gets all the classes handled by this handler class.
     * 
     * @return The collection of handled classes.
     */
    public Collection<Class> getHandledClasses()
    {
        return this.handlers.keySet();
    }
}
