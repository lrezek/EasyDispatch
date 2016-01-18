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

import com.lrezek.easydispatch.handle.meta.cache.HandleMetaCache;
import com.lrezek.easydispatch.exception.EasyDispatchReflectionException;
import com.lrezek.easydispatch.handle.meta.HandleMeta;
import com.lrezek.easydispatch.handle.meta.HandleMetaFactory;
import com.lrezek.easydispatch.handle.meta.cache.ConcurrentMapHandleMetaCache;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Registry of handlers.
 * 
 * @author Lukas Rezek
 */
public class HandlerRegistry
{    
    /** Map of handled class -> Handlers. */
    private final Map<Class, List<Handler>> handlers = new HashMap();
    
    /** Meta factory to use for creating meta information. */
    private final HandleMetaFactory metaFactory = new HandleMetaFactory();
    
    /** Handle meta cache to use. */
    private HandleMetaCache metaCache;
    
    /** Whether to use the meta cache. */
    private boolean useMetaCache = false;
        
    /**
     * Sets the default method name to use for class annotations that don't have
     * a specified method name.
     * 
     * @param defaultMethodName The default method name.
     */
    public void setDefaultMethodName(String defaultMethodName)
    {
        this.metaFactory.setDefaultMethodName(defaultMethodName);
    }
    
    /**
     * Adds handlers.
     * 
     * @param handlers Handlers to add.
     * @throws EasyDispatchReflectionException If there is a problem creating the handler.
     * @return This, for chaining.
     */
    public HandlerRegistry add(Object... handlers) throws EasyDispatchReflectionException
    {
        if(handlers != null && handlers.length != 0)
        {
            // Prepare a collection of handlers to add
            List<Handler> handlersToAdd = new LinkedList<>();
            
            // Loop over the supplied handlers and convert them all to real handlers
            for(Object handler : handlers)
            {   
                handlersToAdd.add(new Handler(handler, this.getMetaInfo(handler.getClass()))); 
            }

            // No exceptions at this point, actually add all the handlers
            handlersToAdd.forEach(handler -> handler.getHandledClasses().forEach(handledClass -> {

                if(!this.handlers.containsKey(handledClass))
                {
                    this.handlers.put(handledClass, new ArrayList<>());
                }

                this.handlers.get(handledClass).add(handler);
                
            }));
        }
        
        // Return this, for chaining
        return this;
    }
    
    /**
     * Removes the specified list of handlers.
     * 
     * @param handlers The handlers to remove.
     * @return This, for chaining.
     */
    public HandlerRegistry remove(Object... handlers)
    {
        if(handlers != null && handlers.length != 0)
        {            
            // Loop over the supplied handlers
            for(Object handlerToRemove : handlers)
            {
                // Remove the handler and all of its handled class mappings
                this.get().stream()
                        .filter(handler -> handler.getObject().equals(handlerToRemove))
                        .forEach(handler -> this.remove(handler));
            }
        }
        
        // Return this, for chaining
        return this;
    }
    
    /**
     * Removes handlers that are instances of the specified classes.
     * 
     * @param handlerClasses The handler classes to remove.
     * @return This, for chaining.
     */
    public HandlerRegistry remove(Class... handlerClasses)
    {
        if(handlerClasses != null && handlerClasses.length != 0)
        {            
            // Loop over the supplied handler classes
            for(Class handlerClassToRemove : handlerClasses)
            {
                // Remove the handler and all its handled mappings
                this.get().stream()
                        .filter(handler -> handlerClassToRemove.isInstance(handler))
                        .forEach(handler -> this.remove(handler));
            }
        }
        
        // Return this, for chaining
        return this;
    }
   
    /**
     * Gets handlers for a specified dispatch object.
     * 
     * @param object Dispatch object.
     * @return The handlers.
     */
    public List<Handler> get(Object object)
    {
        List<Handler> handlerList = this.handlers.get(object.getClass());
        
        if(handlerList == null)
        {
            handlerList = new LinkedList<>();
        }
        
        return handlerList;
    }
    
    /**
     * Gets all handlers.
     * 
     * @return Collection of all handlers.
     */
    public List<Handler> get()
    {
        List<Handler> toReturn = new LinkedList<>();
        
        this.handlers.entrySet().forEach(handlerCollectionEntry -> handlerCollectionEntry.getValue().forEach(handler -> {
            toReturn.add(handler);
        }));
        
        return toReturn.stream().distinct().collect(Collectors.toList());
    }
    
    /**
     * Disables meta caching.
     * 
     * @return This, for chaining.
     */
    public HandlerRegistry disableMetaCaching()
    {
        // Clear all cache entries
        if(this.metaCache != null)
        {
            this.metaCache.clear();
            this.metaCache = null;
        }
        
        // Disable caching
        this.useMetaCache = false;
        
        // Return this, for chaining
        return this;
    }
    
    /**
     * Enables meta caching, using the default ConcurrentMapHandleMetaCache.
     * 
     * @return This, for chaining.
     */
    public HandlerRegistry enableMetaCaching()
    {
        return this.enableMetaCaching(new ConcurrentMapHandleMetaCache());
    }
    
    /**
     * Enables meta caching, using the specified HandleMetaCache.
     * 
     * @param handleMetaCache The meta cache to use.
     * @return This, for chaining.
     */
    public HandlerRegistry enableMetaCaching(HandleMetaCache handleMetaCache)
    {
        // Clear out the old cache
        this.disableMetaCaching();
        
        // Enable with the new cache
        this.metaCache = handleMetaCache;
        this.useMetaCache = true;
        
        // Return this for chaining
        return this;
    }
    
    /**
     * Removes a handler, as well as all its handle mappings.
     * 
     * @param handler The handler to remove.
     */
    private void remove(Handler handler)
    {
        handler.getHandledClasses().forEach(handledClass -> {
            this.handlers.get(handledClass).remove(handler);
        });
    }
    
    /**
     * Gets meta information for a specified class, using the cache as required.
     * 
     * @param cls The class to get meta information for.
     * @return The collection of meta information for the class.
     */
    private List<HandleMeta> getMetaInfo(Class cls) throws EasyDispatchReflectionException
    {        
        // Retreive from cache if present
        if(this.useMetaCache)
        {
            // Create in cache if not
            if(!this.metaCache.contains(cls))
            {
                this.metaCache.put(cls, this.metaFactory.createHandleMetas(cls));
            }
            
            return this.metaCache.get(cls);
        }
        
        // Recreate, no caching
        else
        {
            return this.metaFactory.createHandleMetas(cls);
        }
    }
}
