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
import com.lrezek.easydispatch.strategy.DispatchStrategy;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Object wrapper for an object with @Handles annotations.
 * 
 * @author Lukas Rezek
 */
public class HandlerObject 
{                
    /** Map of handled classes to Handler methods. */
    private final Map<Class, HandlerMethod> handlers = new HashMap<>();
        
    /** The default dispatch strategy to use. */
    private final Class<? extends DispatchStrategy> defaultDispatchStrategy;
    
    /** The default method name to use. */
    private final String defaultMethodName;
    
    /** The underlying object. */
    private final Object object;
    
    /**
     * Constructs a HandlerObject from an object, a default dispatch strategy, 
     * and a default method name.
     * 
     * @param object The object to wrap.
     * @param defaultDispatchStrategy The default dispatch strategy to use.
     * @param defaultMethodName The default method name to use.
     * @throws EasyDispatchException When no valid @Handles annotations are found.
     */
    public HandlerObject(Object object, Class<? extends DispatchStrategy> defaultDispatchStrategy, String defaultMethodName) throws EasyDispatchException
    {                
        // Store defaults
        this.defaultDispatchStrategy = defaultDispatchStrategy;
        this.defaultMethodName = defaultMethodName;
        
        // Store the underlying object
        this.object = object;
        
        // Handle class annotations
        this.handleClassAnnotations();
        
        // Handle method annotations
        this.handleMethodAnnotations();
        
        // If we have no annotations, throw an exception
        if(this.handlers.isEmpty())
        {
            throw new EasyDispatchException(object.getClass() + " does not contain a valid @Handles annotation.");
        }
    }
        
    /**
     * Handles all the class annotations on the object.
     */
    private void handleClassAnnotations()
    {
        // Get all the class annotations
        Handles[] annotations = this.object.getClass().getAnnotationsByType(Handles.class);
        
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
            // Loop over the handled classes
            for(Class handledClass : handledClasses)
            {
                try
                {
                    // Attempt to construct the handlerMethod and put it on the map
                    this.handlers.put(handledClass, new HandlerMethod(this, handledClass, annotation));
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
        Method[] methods = this.object.getClass().getDeclaredMethods();
        
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
        // Attempt to construct the handlerMethod
        HandlerMethod handlerMethod = new HandlerMethod(this, method, annotation);
        
        // Put it on the map
        this.handlers.put(handlerMethod.getHandledClass(), handlerMethod);
    }
    
    /**
     * Gets the handler method for an object.
     * 
     * @param object The object.
     * @return The handler method, if there is one (Null otherwise).
     */
    public HandlerMethod getHandler(Object object)
    {
        return this.handlers.get(object.getClass());
    }
    
    /**
     * Gets all the classes handled by this HandlerObject.
     * 
     * @return The collection of handled classes.
     */
    public Collection<Class> getHandledClasses()
    {
        return this.handlers.keySet();
    }
    
    /**
     * Gets the underlying object.
     * 
     * @return The object.
     */
    public Object getObject()
    {
        return this.object;
    }
    
    /**
     * Gets the default dispatch strategy.
     * 
     * @return The dispatch strategy.
     */
    public Class<? extends DispatchStrategy> getDefaultDispatchStrategy()
    {
        return this.defaultDispatchStrategy;
    }
    
    /**
     * Gets the default method name.
     * 
     * @return The method name.
     */
    public String getDefaultMethodName()
    {
        return this.defaultMethodName;
    }
}
