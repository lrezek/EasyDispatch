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
package com.lrezek.easydispatch;

import com.lrezek.easydispatch.exception.EasyDispatchException;
import com.lrezek.easydispatch.handle.HandlerObject;
import com.lrezek.easydispatch.handle.HandlerMethod;
import com.lrezek.easydispatch.strategy.DispatchStrategy;
import com.lrezek.easydispatch.strategy.SynchronousDispatchStrategy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Main dispatcher class.
 * 
 * @author Lukas Rezek
 */
public class EasyDispatch 
{   
    /** The dispatch strategy to use if it's not specified in the annotation. */
    private Class<? extends DispatchStrategy> defaultDispatchStrategyClass;
    
    /** The default method name. */
    private String defaultMethodName = "handle";
    
    /** Map of available dispatch strategies. */
    private final Map<Class<? extends DispatchStrategy>, DispatchStrategy> dispatchStategies = new HashMap<>();
    
    /** Map of handler objects. */
    private final Map<Class, Collection<HandlerObject>> handlerClasses = new HashMap();
    
    /** 
     * Constructor for the Dispatcher.
     */
    public EasyDispatch()
    {
        // Add the built in dispatch strategies
        this.addDispatchStrategy(new SynchronousDispatchStrategy());
        
        // Set the default dispatch strategy
        this.setDefaultDispatchStrategy(SynchronousDispatchStrategy.class);
    }
    
    /**
     * Dispatches an object.
     * 
     * @param object The object.
     */
    public void dispatch(Object object)
    {
        Collection<HandlerObject> handlers = this.handlerClasses.get(object.getClass());
        
        if(handlers != null && !handlers.isEmpty())
        {
            for(HandlerObject handlerClass : handlers)
            {
                HandlerMethod h = handlerClass.getHandler(object);
                
                this.dispatchStategies.get(h.getDispatchStrategy()).dispatch(object, h);
            }
        }
    }
    
    /**
     * Sets the default dispatch strategy from the set of built in ones.
     * 
     * @param defaultDispatchStrategyClass The default strategy to use.
     */
    public void setDefaultDispatchStrategy(Class<? extends DispatchStrategy> defaultDispatchStrategyClass)
    {
        this.defaultDispatchStrategyClass = defaultDispatchStrategyClass;
    }
    
    /**
     * Sets the default method name to use.
     * 
     * @param defaultMethodName The default method name.
     */
    public void setDefaultMethodName(String defaultMethodName)
    {
        this.defaultMethodName = defaultMethodName;
    }
    
    /**
     * Adds a dispatch strategy to the dispatcher so it can be used in annotations.
     * 
     * @param dispatchStrategy The strategy to add.
     */
    public void addDispatchStrategy(DispatchStrategy dispatchStrategy)
    {
        this.dispatchStategies.put(dispatchStrategy.getClass(), dispatchStrategy);
    }
    
    /**
     * Adds a handler.
     * 
     * @param handler The handler object.
     * @throws EasyDispatchException If the handler has no valid @Handles annotations.
     */
    public void addHandler(Object handler) throws EasyDispatchException
    {
        // Create a handler class object
        HandlerObject handlerClass = new HandlerObject(handler, this.defaultDispatchStrategyClass, this.defaultMethodName);
        
        // Add the handler class mapping for everything it handles
        for(Class handledClass : handlerClass.getHandledClasses())
        {
            if(!this.handlerClasses.containsKey(handledClass))
            {
                this.handlerClasses.put(handledClass, new ArrayList<>());
            }
            
            this.handlerClasses.get(handledClass).add(handlerClass);
        }
    }
}
