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
package com.lrezek.easydispatch.dispatch.result;

import com.lrezek.easydispatch.handle.Handler;

/**
 * Result object for a single dispatch invocation.
 * 
 * @author Lukas Rezek
 */
public class DispatchResult 
{
    /** The handler wrapper for the object the method was invoked in. */
    private final Handler handler;
    
    /** The object that was dispatched. */
    private final Object dispatched;
    
    /** The return object of the invoked method. */
    private final Object result;
    
    /**
     * Constructs the result from a handler, dispatched object, and result.
     * 
     * @param handler The handler wrapper for the object the method was invoked in.
     * @param dispatched The object that was dispatched.
     * @param result The return object of the invocation.
     */
    public DispatchResult(Handler handler, Object dispatched, Object result)
    {
        this.handler = handler;
        this.dispatched = dispatched;
        this.result = result;
    }
    
    /**
     * Gets the handler wrapper for the object this handle was invoked in.
     * 
     * @return The handler.
     */
    public Handler getHandler() 
    {
        return handler;
    }

    /**
     * Gets the object that was dispatched to the handle.
     * 
     * @return The dispatched object. 
     */
    public Object getDispatched() 
    {
        return dispatched;
    }
    
    /**
     * Gets the result of the handle method invocation.
     * 
     * @return The result of the handle method invocation.
     */
    public Object getResult() 
    {
        return result;
    }
}