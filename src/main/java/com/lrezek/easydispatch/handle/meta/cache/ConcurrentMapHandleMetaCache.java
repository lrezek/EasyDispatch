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
package com.lrezek.easydispatch.handle.meta.cache;

import com.lrezek.easydispatch.handle.meta.HandleMeta;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple concurrent map based implementation of a HandleMetaCache.
 * 
 * @author Lukas Rezek
 */
public class ConcurrentMapHandleMetaCache implements HandleMetaCache
{    
    /** Map of class -> annotation info. */
    private final Map<Class, Collection<HandleMeta>> cache = new ConcurrentHashMap<>();
    
    /**
     * Determines if a cache entry exists for the specified class.
     * 
     * @param cls The class.
     * @return True if there is a cache entry, false otherwise.
     */
    @Override
    public boolean contains(Class cls) 
    {
        return this.cache.containsKey(cls);
    }

    /**
     * Gets the handle meta collection for the class, or null if not found.
     * 
     * @param cls The class.
     * @return The cached handle meta information, or null if not found.
     */
    @Override
    public Collection<HandleMeta> get(Class cls) 
    {
        return this.cache.get(cls);
    }

    /**
     * Puts a collection of handle meta objects on the cache, for the specified
     * class.
     * 
     * @param cls The class.
     * @param handlerDataCollection The handle meta information for the class. 
     */
    @Override
    public void put(Class cls, Collection<HandleMeta> handlerDataCollection) 
    {
        this.cache.put(cls, handlerDataCollection);
    }

    /**
     * Clears the cache.
     */
    @Override
    public void clear() 
    {
        this.cache.clear();
    }
}
