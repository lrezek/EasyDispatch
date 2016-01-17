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

import com.lrezek.easydispatch.dispatch.result.DispatchResult;
import com.lrezek.easydispatch.exception.EasyDispatchException;
import com.lrezek.easydispatch.handle.meta.HandleMeta;
import com.lrezek.easydispatch.dispatch.strategy.DispatchStrategy;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Defines a method handle.
 * 
 * @author Lukas Rezek
 */
public class Handle
{    
    /** The parent handler object. */
    private final Handler handler;
    
    /** The meta information for this handle. */
    private final HandleMeta meta;
        
    /**
     * Constructs a handle from a parent handler and some meta information.
     * 
     * @param handler The parent handler.
     * @param meta The meta information for the handle.
     */
    public Handle(Handler handler, HandleMeta meta)
    {        
        this.handler = handler;
        this.meta = meta;
    }
    
    /**
     * Invokes this handle with the specified parameter.
     * 
     * @param toDispatch The object to dispatch to this handle.
     * @return The return object of the invoked method, wrapped in a DispatchResult.
     */
    public DispatchResult invoke(Object toDispatch)
    {
        Object returnObject;
        
        try
        {
            // If the method is accessible, just invoke it
            if(this.meta.getMethod().isAccessible())
            {
                returnObject = this.meta.getMethod().invoke(this.handler.getObject(), toDispatch);
            }
            
            // Not accessible, temporarily make it accessible
            else
            {
                this.meta.getMethod().setAccessible(true);
                returnObject = this.meta.getMethod().invoke(this.handler.getObject(), toDispatch);
                this.meta.getMethod().setAccessible(false);                
            }
        }
        catch(IllegalAccessException e)
        {
            // Return an exception
            returnObject = new EasyDispatchException("Failed to access " + this.meta.getMethod().getName(), e);
        }
        catch(InvocationTargetException e)
        {
            // Exception in the method, return it
            returnObject = e.getCause();
        }
        
        // Return the dispatch result
        return new DispatchResult(this.handler, toDispatch, returnObject);
    }

    /**
     * Gets meta information for this handle.
     * 
     * @return The handles meta information.
     */
    public HandleMeta getMeta() 
    {
        return meta;
    }
    
    /**
     * Gets the reflection method for this handle.
     * 
     * @return The method.
     */
    public Method getMethod()
    {
        return this.meta.getMethod();
    }
    
    /**
     * Gets the dispatch strategy class for this handle.
     * 
     * @return The dispatch strategy class.
     */
    public Class<? extends DispatchStrategy> getDispatchStrategy()
    {
        return this.meta.getDispatchStrategy();
    }
    
    /**
     * Gets the class this handle handles.
     * 
     * @return The handled class.
     */
    public Class getHandledClass()
    {
        return this.meta.getHandledClass();
    }

    /**
     * Gets this handles parent handler object.
     * 
     * @return The handler object.
     */
    public Handler getHandler() 
    {
        return handler;
    }
}