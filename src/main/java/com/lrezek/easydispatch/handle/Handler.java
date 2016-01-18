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

import com.lrezek.easydispatch.handle.meta.HandleMeta;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Object wrapper for an object with @Handles annotations.
 * 
 * @author Lukas Rezek
 */
public class Handler 
{                
    /** Map of handled classes to handles. */
    private final Map<Class, List<Handle>> handles = new HashMap<>();
                
    /** The underlying object. */
    private final Object object;
    
    /**
     * Constructs the handler from an underlying object and a collection of 
     * handle metas.
     * 
     * @param object The underlying object.
     * @param handleMetas The collection of handle metas.
     */
    public Handler(Object object, List<HandleMeta> handleMetas)
    {
        this.object = object;
        
        // Add all the handles to the handles map
        handleMetas.forEach(meta -> {
            
            if(!this.handles.containsKey(meta.getHandledClass()))
            {
                this.handles.put(meta.getHandledClass(), new LinkedList<>());
            }
            
            this.handles.get(meta.getHandledClass()).add(new Handle(this, meta));
        });     
    }
    
    /**
     * Gets all handles that could handle the specified object.
     * 
     * @param object The object to handle.
     * @return Collection of associated handles.
     */
    public List<Handle> getHandles(Object object)
    {
        return this.handles.get(object.getClass());
    }
    
    /**
     * Gets all the classes handled by this Handler.
     * 
     * @return The collection of handled classes.
     */
    public Set<Class> getHandledClasses()
    {
        return this.handles.keySet();
    }
    
    /**
     * Gets the underlying object.
     * 
     * @return The object.
     */
    public Object getObject()
    {
        return this.object;
    }
}