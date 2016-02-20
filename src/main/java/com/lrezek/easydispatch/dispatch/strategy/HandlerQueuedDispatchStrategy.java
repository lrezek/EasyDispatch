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

import com.lrezek.easydispatch.dispatch.result.DispatchResult;
import com.lrezek.easydispatch.dispatch.result.DispatchResults;
import com.lrezek.easydispatch.handle.Handle;
import com.lrezek.easydispatch.handle.Handler;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Dispatch strategy that queues dispatches on a per-handler basis.
 * 
 * This dispatch strategy dispatches asynchronously to all the handlers, but if
 * a handler receives multiple dispatches concurrently, they are queued.
 * 
 * @author Lukas Rezek
 */
public class HandlerQueuedDispatchStrategy implements DispatchStrategy
{
    /** Map of handlers -> executors. */
    private final Map<Handler, ExecutorService> handlerExecutors = new ConcurrentHashMap<>();
    
    /**
     * Dispatches the object to the handle, by invoking the handle asynchronously
     * in an executor shared by all dispatches to the handler.
     * 
     * @param handle The handle to invoke.
     * @param object The object to dispatch.
     * @param previousResults The previous dispatch results.
     * @return The future result, wrapped in a dispatch result.
     */
    @Override
    public DispatchResult dispatch(Handle handle, Object object, DispatchResults previousResults) 
    {
        // If we don't have an executor for this handler yet, make one
        if(!this.handlerExecutors.containsKey(handle.getHandler()))
        {
            this.handlerExecutors.put(handle.getHandler(), Executors.newSingleThreadExecutor());
        }
        
        // Dispatch the object and return a future wrapped in a dispatch result
        return new DispatchResult(handle, object, this.handlerExecutors.get(handle.getHandler()).submit(() -> {
            return handle.invoke(object);
        }));
    }
}
