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

import com.lrezek.easydispatch.exception.EasyDispatchException;
import com.lrezek.easydispatch.strategy.DispatchStrategy;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Defines a handler method.
 * 
 * @author Lukas Rezek
 */
public class Handler 
{
    /** The method to invoke to dispatch. */
    private Method method;
    
    /** The handler object. */
    private final Object object;
    
    /** The dispatch strategy to use. */
    private final Class<? extends DispatchStrategy> dispatchStrategy;
    
    /** The class this handler handles. */
    private final Class handledClass;
    
    /**
     * Constructs a handler from a handler object, method name, dispatch class,
     * and dispatch strategy.
     * 
     * @param handlerObject The handler object.
     * @param handledClass The class to handle.
     * @param methodName The method name to use.
     * @param dispatchStrategy The dispatch strategy. 
     * @throws EasyDispatchException When the method is missing.
     */
    public Handler(Object handlerObject, Class handledClass, String methodName, Class<? extends DispatchStrategy> dispatchStrategy) throws EasyDispatchException
    {
        try
        {
            // Try to get the specified method
            this.method = handlerObject.getClass().getDeclaredMethod(methodName, handledClass);
        }
        catch(NoSuchMethodException e)
        {
            throw new EasyDispatchException(e.getCause());
        }
        
        this.handledClass = handledClass;
        this.dispatchStrategy = dispatchStrategy;
        this.object = handlerObject;
    }
    
    /**
     * Constructs a handler method from a handler object, method, and dispatch class.
     * 
     * @param handlerObject The handler object.
     * @param handledClass The class to handle.
     * @param method The method name to use.
     * @param dispatchStrategy The dispatch strategy. 
     */
    public Handler(Object handlerObject, Class handledClass, Method method, Class<? extends DispatchStrategy> dispatchStrategy)
    {
        this.handledClass = handledClass;
        this.method = method;
        this.dispatchStrategy = dispatchStrategy;
        this.object = handlerObject;
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
            this.method.invoke(this.object, object);
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
        return this.object;
    }
}
