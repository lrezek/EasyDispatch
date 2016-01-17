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
    /** Dispatch strategy registry. */
    private final DispatchStrategyRegistry dispatchStrategies = new DispatchStrategyRegistry();
    
    /** Handler registry. */
    private final HandlerRegistry handlers = new HandlerRegistry();
    
    /** 
     * Constructor for the dispatcher with default settings.
     */
    public EasyDispatch()
    {
        // Add the built in dispatch strategies
        this.dispatchStrategies.add(new SynchronousDispatchStrategy())
            .setDefault(SynchronousDispatchStrategy.class); // Set the default dispatch strategy
    }
    
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
        return this.dispatch(object, this.dispatchStrategies.getDefault());
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
        return this.dispatch(object, this.dispatchStrategies.get(defaultDispatchStrategyClass));
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
        // Prepare dispatch results
        DispatchResults results = new DispatchResults();
        
        // Loop over all the handlers for the object
        this.handlers.get(object).forEach(handler -> handler.getHandles(object).forEach(handle -> {
            
            // Get the annotation specified dispatch strategy, fall back to the default specified
            DispatchStrategy dispatchStrategy = this.dispatchStrategies.get(handle.getDispatchStrategy(), defaultDispatchStrategy);

            // Dispatch the object and store the result
            results.add(dispatchStrategy.dispatch(handle, object, results));
            
        }));
        
        return results;
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
        return this.dispatchWith(object, this.dispatchStrategies.get(dispatchStrategyClass));
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
        // Prepare dispatch results
        DispatchResults results = new DispatchResults();
        
        // Loop over all handles
        this.handlers.get(object).forEach(handler -> handler.getHandles(object).forEach(handle -> {
            
            // Dispatch the object and store the result
            results.add(dispatchStrategy.dispatch(handle, object, null));      
            
        }));
        
        return results;
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
   
    /**
     * Gets the dispatch strategies registry.
     * 
     * @return The dispatch strategies registry.
     */
    public DispatchStrategyRegistry dispatchStrategies()
    {
        return this.dispatchStrategies;
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