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
package com.lrezek.easydispatch.dispatch;

import com.lrezek.easydispatch.dispatch.result.DispatchResult;
import com.lrezek.easydispatch.dispatch.result.DispatchResults;
import com.lrezek.easydispatch.dispatch.strategy.AsynchronousDispatchStrategy;
import com.lrezek.easydispatch.dispatch.strategy.DispatchStrategy;
import com.lrezek.easydispatch.dispatch.strategy.DispatchStrategyRegistry;
import com.lrezek.easydispatch.dispatch.strategy.SynchronousDispatchStrategy;
import com.lrezek.easydispatch.handle.Handle;
import com.lrezek.easydispatch.handle.Handler;
import java.util.List;
import java.util.ListIterator;

/**
 * Defines the dispatcher.
 * 
 * @author Lukas Rezek
 */
public class Dispatcher 
{    
    /** Dispatch strategy registry. */
    private final DispatchStrategyRegistry dispatchStrategies = new DispatchStrategyRegistry();
    
    /**
     * Constructs the dispatcher with default settings.
     */
    public Dispatcher()
    {
        this.dispatchStrategies
                .add(new SynchronousDispatchStrategy())         // Add the built in dispatch strategies
                .add(new AsynchronousDispatchStrategy())
                .setDefault(SynchronousDispatchStrategy.class); // Set the default dispatch strategy
    }
    
    /**
     * Dispatches the given object to the handlers, with the specified default dispatch strategy.
     * 
     * @param object The object to dispatch.
     * @param handlers The handlers to dispatch to.
     * @param defaultDispatchStrategy The default dispatch strategy to use if the annotation doesn't specify one.
     * @return The dispatch results.
     */
    public DispatchResults dispatch(Object object, List<Handler> handlers, DispatchStrategy defaultDispatchStrategy)
    {
        // Prepare the results
        DispatchResults results = new DispatchResults();
        
        // Get the iterator for the handlers
        ListIterator<Handler> handlerIterator = handlers.listIterator();
        
        // Loop over the handlers
        while(handlerIterator.hasNext())
        {
            // Get the handler
            Handler handler = handlerIterator.next();
            
            // Get the handlers handles iterator
            ListIterator<Handle> handleIterator = handler.getHandles(object).listIterator();
            
            // Loop over the handlers handles
            while(handleIterator.hasNext())
            {
                // Ge the handle
                Handle handle = handleIterator.next();
                
                // Get the annotation specified dispatch strategy, fall back to the default specified
                DispatchStrategy dispatchStrategy = this.dispatchStrategies.get(handle.getDispatchStrategy(), defaultDispatchStrategy);

                // Dispatch the object and store the result
                DispatchResult dispatchResult = dispatchStrategy.dispatch(handle, object, results);
                                
                // Add the result to the return object
                results.add(dispatchResult);
                
                // Run the dispatch flow control in the result if required
                if(dispatchResult.getDispatchFlowControl() != null)
                {
                    dispatchResult.getDispatchFlowControl().execute(handlerIterator, handleIterator);
                }
            }
        }
        
        // Return the results object
        return results;
    }
    
    /**
     * Dispatches the given object to the handlers, with the specified dispatch strategy.
     * 
     * @param object The object to dispatch.
     * @param handlers The handlers to dispatch to.
     * @param dispatchStrategy  The dispatch strategy to use.
     * @return The dispatch results.
     */
    public DispatchResults dispatchWith(Object object, List<Handler> handlers, DispatchStrategy dispatchStrategy)
    {
        // Prepare the results
        DispatchResults results = new DispatchResults();
        
        // Get the iterator for the handlers
        ListIterator<Handler> handlerIterator = handlers.listIterator();
        
        // Loop over the handlers
        while(handlerIterator.hasNext())
        {
            // Get the handler
            Handler handler = handlerIterator.next();
            
            // Get the handlers handles iterator
            ListIterator<Handle> handleIterator = handler.getHandles(object).listIterator();
            
            // Loop over the handlers handles
            while(handleIterator.hasNext())
            {
                // Ge the handle
                Handle handle = handleIterator.next();
                
                // Dispatch the object and store the result
                DispatchResult dispatchResult = dispatchStrategy.dispatch(handle, object, results);
                                
                // Add the result to the return object
                results.add(dispatchResult);
                
                // Run the dispatch flow control in the result if required
                if(dispatchResult.getDispatchFlowControl() != null)
                {
                    dispatchResult.getDispatchFlowControl().execute(handlerIterator, handleIterator);
                }
            }
        }
        
        // Return the results object
        return results;
    }
    
    /**
     * Dispatches the given object to the handlers, with the configured default dispatch strategy.
     * 
     * @param object The object to dispatch.
     * @param handlers The handlers to dispatch to.
     * @return The dispatch results.
     */
    public DispatchResults dispatch(Object object, List<Handler> handlers)
    {
        // Dispatch the object with the configured default dispatch strategy
        return this.dispatch(object, handlers, this.dispatchStrategies.getDefault());
    }
    
    /**
     * Dispatches the given object to the handlers, with the specified default dispatch strategy class.
     * 
     * @param object The object to dispatch.
     * @param handlers The handlers to dispatch to.
     * @param defaultDispatchStrategyClass  The default dispatch strategy class to use.
     * @return The dispatch results.
     */
    public DispatchResults dispatch(Object object, List<Handler> handlers, Class<? extends DispatchStrategy> defaultDispatchStrategyClass)
    {
        return this.dispatch(object, handlers, this.dispatchStrategies.get(defaultDispatchStrategyClass));
    }
    
    /**
     * Dispatches the given object to the handlers, with the specified dispatch strategy class.
     * 
     * @param object The object to dispatch.
     * @param handlers The handlers to dispatch to.
     * @param dispatchStrategyClass  The dispatch strategy class to use.
     * @return The dispatch results.
     */
    public DispatchResults dispatchWith(Object object, List<Handler> handlers, Class<? extends DispatchStrategy> dispatchStrategyClass)
    {
        return this.dispatchWith(object, handlers, this.dispatchStrategies.get(dispatchStrategyClass));
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
}
