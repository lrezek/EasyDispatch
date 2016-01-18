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

import com.lrezek.easydispatch.dispatch.Dispatcher;
import com.lrezek.easydispatch.dispatch.result.DispatchResults;
import com.lrezek.easydispatch.dispatch.strategy.DispatchStrategyRegistry;
import com.lrezek.easydispatch.handle.HandlerRegistry;
import com.lrezek.easydispatch.dispatch.strategy.DispatchStrategy;
import com.lrezek.easydispatch.dispatch.strategy.SynchronousDispatchStrategy;

/**
 * Main entry point for EasyDispatch.
 * 
 * @author Lukas Rezek
 */
public class EasyDispatch 
{           
    /** Handler registry. */
    private final HandlerRegistry handlers = new HandlerRegistry();
    
    /** The dispatcher to use. */
    private final Dispatcher dispatcher = new Dispatcher();
    
    /**
     * Dispatches an object with the default dispatch strategy.
     * 
     * Basic dispatch method. The default dispatch strategy will only be used if 
     * the handler annotation does not contain a dispatch strategy parameter.
     * 
     * @param object The object to dispatch.
     * @return The dispatch results.
     */
    public DispatchResults dispatch(Object object)
    {
        // Dispatch the object with the configured default dispatch strategy
        return this.dispatcher.dispatch(object, this.handlers.get(object));
    }
    
    /**
     * Dispatches an object, overriding the default dispatch strategy.
     * 
     * Dispatches an object with the specified default dispatch strategy. The 
     * specified default strategy will be used if the annotation does not specify
     * a strategy of its own.
     * 
     * @param object The object to dispatch.
     * @param defaultDispatchStrategyClass The default dispatch strategy class to use.
     * @return The dispatch results.
     */
    public DispatchResults dispatch(Object object, Class<? extends DispatchStrategy> defaultDispatchStrategyClass)
    {         
        return this.dispatcher.dispatch(object, this.handlers.get(object), defaultDispatchStrategyClass);
    }
    
    /**
     * Dispatches an object, overriding the default dispatch strategy.
     * 
     * Dispatches an object with the specified default dispatch strategy. The 
     * specified default strategy will be used if the annotation does not specify
     * a strategy of its own.
     * 
     * @param object The object to dispatch.
     * @param defaultDispatchStrategy The default dispatch strategy to use.
     * @return The dispatch results.
     */
    public DispatchResults dispatch(Object object, DispatchStrategy defaultDispatchStrategy)
    {
        return this.dispatcher.dispatch(object, this.handlers.get(object), defaultDispatchStrategy);
    }
    
    /**
     * Dispatches an object, using the specified dispatch strategy class.
     * 
     * Dispatches an object with the specified dispatch strategy class. This
     * dispatch strategy will be used, regardless of the annotations specified
     * dispatch strategy.
     * 
     * @param object The object to dispatch.
     * @param dispatchStrategyClass The dispatch strategy class to use.
     * @return The dispatch results.
     */
    public DispatchResults dispatchWith(Object object, Class<? extends DispatchStrategy> dispatchStrategyClass)
    {        
        return this.dispatcher.dispatchWith(object, this.handlers.get(object), dispatchStrategyClass);
    }
    
    /**
     * Dispatches an object, using the specified dispatch strategy.
     * 
     * Dispatches an object with the specified dispatch strategy class. This
     * dispatch strategy will be used, regardless of the annotations specified
     * dispatch strategy.
     * 
     * @param object The object to dispatch.
     * @param dispatchStrategy The dispatch strategy class.
     * @return The dispatch results.
     */
    public DispatchResults dispatchWith(Object object, DispatchStrategy dispatchStrategy)
    {   
        return this.dispatcher.dispatchWith(object, this.handlers.get(object), dispatchStrategy);
    }
    
    /**
     * Shortcut for a fully synchronous dispatch.
     * 
     * @param object The object to dispatch.
     * @return The dispatch results.
     */        
    public DispatchResults synchronousDispatch(Object object)
    {
        return this.dispatchWith(object, SynchronousDispatchStrategy.class);
    }
   
    public Dispatcher dispatcher()
    {
        return this.dispatcher;
    }
    
    /**
     * Gets the dispatch strategies registry.
     * 
     * @return The dispatch strategies registry.
     */
    public DispatchStrategyRegistry dispatchStrategies()
    {
        return this.dispatcher.dispatchStrategies();
    }
    
    /**
     * Gets the handler registry.
     * 
     * @return The dispatch strategies registry.
     */
    public HandlerRegistry handlers()
    {
        return this.handlers;
    }
}