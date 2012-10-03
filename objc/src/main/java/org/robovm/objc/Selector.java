/*
 * Copyright (C) 2012 RoboVM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.robovm.objc;

import java.util.HashMap;
import java.util.Map;

import org.robovm.rt.bro.Struct;
import org.robovm.rt.bro.annotation.Pointer;
import org.robovm.rt.bro.annotation.StructMember;
import org.robovm.rt.bro.ptr.BytePtr;

/**
 * Represents an Objective-C selector. This is mapped as an opaque {@link Struct}. The actual 
 * members of the internal struct are unknown.
 */
public class Selector extends Struct<Selector> {

    private static final Map<String, Selector> selectors = new HashMap<String, Selector>();

    private Selector() {
    }

    @StructMember(0)
    private native @Pointer long value();
    
    public String getName() {
        return ObjCRuntime.sel_getName(this).toStringAsciiZ();
    }
    
    public static Selector register(String name) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        synchronized (selectors) {
            Selector sel = selectors.get(name);
            if (sel == null) {
                sel = ObjCRuntime.sel_registerName(BytePtr.toBytePtrAsciiZ(name));
                if (sel == null) {
                    // sel_registerName should never return nil
                    throw new IllegalStateException("Objective-C failed to register selector '" + name + "'");
                }
                selectors.put(name, sel);
            }
            return sel;
        }
    }
    
}
