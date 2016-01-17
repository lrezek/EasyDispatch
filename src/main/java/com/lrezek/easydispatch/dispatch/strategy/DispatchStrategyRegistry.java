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
package com.lrezek.easydispatch.dispatch.strategy;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry for dispatcher strategies.
 * 
 * @author Lukas Rezek
 */
public class DispatchStrategyRegistry 
{
    /** The key to use for the default dispatch strategy in the map of strategies. */
    private static final Class<? extends DispatchStrategy> DEFAULT_DISPATCH_STRATEGY_CLASS = DispatchStrategy.class;
    
    /** Map of dispatch strategies. */
    private final Map<Class<? extends DispatchStrategy>, DispatchStrategy> dispatchStrategies = new HashMap<>();
    
    /**
     * Adds a dispatch strategy to the registry.
     * 
     * @param dispatchStrategies The strategies to add.
     * @return This, for chaining.
     */
    public DispatchStrategyRegistry add(DispatchStrategy... dispatchStrategies)
    {
        // Add all the specified dispatch strategies
        if(dispatchStrategies != null && dispatchStrategies.length != 0)
        {
            for(DispatchStrategy dispatchStrategy : dispatchStrategies)
            {
                this.dispatchStrategies.put(dispatchStrategy.getClass(), dispatchStrategy);
            }
        }
       
        // Return this, for chaining
        return this;
    }
    
    /**
     * Removes dispatch strategies from the registry.
     * 
     * @param dispatchStrategyClasses The classes of dispatch strategies to remove.
     * @return This, for chaining.
     */
    public DispatchStrategyRegistry remove(Class<? extends DispatchStrategy>... dispatchStrategyClasses)
    {
        // Remove all the specified dispatch strategies
        if(dispatchStrategyClasses != null && dispatchStrategyClasses.length != 0)
        {
            for(Class<? extends DispatchStrategy> dispatchStrategyClass : dispatchStrategyClasses)
            {
                this.dispatchStrategies.remove(dispatchStrategyClass);
            }
        }
        
        // Return this, for chaining
        return this;
    }
    
    /**
     * Sets a new dispatch strategy as the default strategy.
     * 
     * @param dispatchStrategy The dispatch strategy.
     * @return This, for chaining.
     */
    public DispatchStrategyRegistry setDefault(DispatchStrategy dispatchStrategy)
    {
        // Add it to the registry
        this.add(dispatchStrategy);
        
        // Set it as the default
        this.setDefault(dispatchStrategy.getClass());
        
        // Return this, for chaining
        return this;
    }
    
    /**
     * Sets the default dispatch strategy from the set of existing ones.
     * 
     * @param defaultDispatchStrategyClass The default strategy to use.
     * @return This, for chaining.
     */
    public DispatchStrategyRegistry setDefault(Class<? extends DispatchStrategy> defaultDispatchStrategyClass)
    {
        // Get the specified dispatch strategy
        DispatchStrategy dispatchStrategy = this.dispatchStrategies.get(defaultDispatchStrategyClass);
        
        // If it doesn't exist, throw an illegal argument exception
        if(dispatchStrategy == null)
        {
            throw new IllegalArgumentException("Please add the required dispatch strategy before setting it as the default.");
        }
        
        // Set the dispatch strategy as the default one
        this.dispatchStrategies.put(DEFAULT_DISPATCH_STRATEGY_CLASS, dispatchStrategy);
        
        // Return this, for chaining
        return this;
    }
    
    /**
     * Gets the configured default dispatch strategy.
     * 
     * @return The configured default dispatch strategy. 
     */
    public DispatchStrategy getDefault()
    {
        return this.dispatchStrategies.get(DEFAULT_DISPATCH_STRATEGY_CLASS);
    }
    
    /**
     * Gets the specified dispatch strategy, or the configured default if it isn't found.
     * 
     * @param dispatchStrategyClass Dispatch strategy to find.
     * @return The dispatch strategy, or the default strategy if none of the specified class are found.
     */
    public DispatchStrategy get(Class<? extends DispatchStrategy> dispatchStrategyClass)
    {
        return this.get(dispatchStrategyClass, this.getDefault());
    }
    
    /**
     * Gets the specified dispatch strategy, or the specified default if it's not found.
     * 
     * @param dispatchStrategyClass Dispatch strategy to find.
     * @param defaultDispatchStrategy Default dispatch strategy to use when the specified one is not found.
     * @return The dispatch strategy, or the default strategy if none of the specified class are found.
     */
    public DispatchStrategy get(Class<? extends DispatchStrategy> dispatchStrategyClass, DispatchStrategy defaultDispatchStrategy)
    {
        // Try to find the specified strategy
        if(dispatchStrategyClass != null && dispatchStrategyClass != DEFAULT_DISPATCH_STRATEGY_CLASS)
        {
            if(this.dispatchStrategies.containsKey(dispatchStrategyClass))
            {
                return this.dispatchStrategies.get(dispatchStrategyClass);
            }
        }

        // Return the specified default
        return defaultDispatchStrategy;
    }
}