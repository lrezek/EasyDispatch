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

import com.lrezek.easydispatch.dispatch.strategy.DispatchStrategy;
import java.lang.reflect.Method;

/**
 * Defines a meta information object for a Handle.
 * 
 * @author Lukas Rezek
 */
public class HandleMeta 
{    
    /** The reflection method for the handle. */
    private final Method method;
    
    /** The class this handle handles. */
    private final Class handledClass;
        
    /** The dispatch strategy class to use for the handle. */
    private final Class<? extends DispatchStrategy> dispatchStrategy;
        
    /**
     * Constructs from a reflection method, handled class, and dispatch strategy class.
     * 
     * @param method The reflection method.
     * @param handledClass The handled class.
     * @param dispatchStrategy The dispatch strategy class.
     */
    public HandleMeta(Method method, Class handledClass, Class<? extends DispatchStrategy> dispatchStrategy)
    {
        this.method = method;
        this.handledClass = handledClass;
        this.dispatchStrategy = dispatchStrategy;
    }

    /**
     * Gets the reflection method associated with the Handle.
     * 
     * @return The method.
     */
    public Method getMethod() 
    {
        return method;
    }
    
    /**
     * Gets the dispatch strategy class associated with the Handle.
     * 
     * @return The dispatch strategy class.
     */
    public Class<? extends DispatchStrategy> getDispatchStrategy() 
    {
        return dispatchStrategy;
    }
    
    /**
     * Gets the class this Handle handles.
     * 
     * @return The handled class.
     */
    public Class getHandledClass() 
    {
        return handledClass;
    }
}