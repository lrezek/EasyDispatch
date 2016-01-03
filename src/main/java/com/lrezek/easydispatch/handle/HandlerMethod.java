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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Method wrapper for methods annotated with @Handles, or for "handles" methods
 * in a class with a @Handles class annotation.
 * 
 * @author Lukas Rezek
 */
public class HandlerMethod 
{
    /** The underlying reflection method. */
    private final Method method;
    
    /** The object the method is in. */
    private final HandlerObject handlerObject;
    
    /** The dispatch strategy to use. */
    private Class<? extends DispatchStrategy> dispatchStrategy;
    
    /** The class this handlerMethod handles. */
    private final Class handledClass;
    
    /**
     * Constructs a handler method from a handler object, method, and dispatch class.
     * 
     * @param object The parent HandlerObject.
     * @param method The method name to use.
     * @param annotation The annotation. 
     * @throws EasyDispatchException If there is more than 1 class in the value of the @Handles.
     */
    public HandlerMethod(HandlerObject object, Method method, Handles annotation) throws EasyDispatchException
    {
        // If there is more than 1 class to handle, error
        if(annotation.value() == null || annotation.value().length != 1)
        {
            throw new EasyDispatchException("A method-level @Handles must have a single class as its value");
        }
        
        this.handledClass = annotation.value()[0];
        this.method = method;
        this.handlerObject = object;
        this.setDispatchStrategy(annotation.dispatchStrategy());
    }
    
    /**
     * Constructs a handler from a handler object and @Handles annotation.
     * 
     * @param object The parent HandlerObject.
     * @param handledClass The handled class 
     * @param annotation The @Handles annotation.
     * @throws EasyDispatchException When the method is missing.
     */
    public HandlerMethod(HandlerObject object, Class handledClass, Handles annotation) throws EasyDispatchException
    {
        
        // Get the method name or default if none defined
        String methodName = annotation.method().isEmpty() ? object.getDefaultMethodName() : annotation.method();
        
        try
        {
            // Try to get the specified method
            this.method = object.getObject().getClass().getDeclaredMethod(methodName, handledClass);
        }
        catch(NoSuchMethodException e)
        {
            throw new EasyDispatchException(e.getCause());
        }
        
        this.handledClass = handledClass;
        this.handlerObject = object;
        this.setDispatchStrategy(annotation.dispatchStrategy());
    }
    
    /**
     * Sets the dispatch strategy, defaults if required.
     * 
     * @param dispatchStrategy The dispatch strategy.
     */
    private void setDispatchStrategy(Class<? extends DispatchStrategy> dispatchStrategy)
    {
        if(dispatchStrategy == DispatchStrategy.class)
        {
            this.dispatchStrategy = this.handlerObject.getDefaultDispatchStrategy();
        }
        else
        {
            this.dispatchStrategy = dispatchStrategy;
        }
    }
    
    /**
     * Dispatches the object by invoking the method.
     * 
     * @param object The object to dispatch.
     * @throws EasyDispatchException Wrapper exception for exceptions in the dispatcher.
     */
    public void dispatch(Object object) throws EasyDispatchException
    {
        try
        {
            this.method.invoke(this.handlerObject.getObject(), object);
        }
        catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
        {
            throw new EasyDispatchException(e.getCause());
        }
    }
    
    /**
     * Gets the dispatch strategy associated with this handler.
     * 
     * @return The dispatch strategy class.
     */
    public Class<? extends DispatchStrategy> getDispatchStrategy()
    {
        return this.dispatchStrategy;
    }
    
    /**
     * Gets the handler method.
     * 
     * @return The handler method.
     */
    public Method getMethod()
    {
        return this.method;
    }
    
    /**
     * Gets the class this handler handles.
     * 
     * @return The handled class.
     */
    public Class getHandledClass()
    {
        return this.handledClass;
    }
    
    /**
     * Gets the object containing this handler method.
     * 
     * @return The handler object.
     */
    public Object getHandlerObject()
    {
        return this.handlerObject;
    }
}
