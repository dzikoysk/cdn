/*
 * Copyright (c) 2021 dzikoysk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.dzikoysk.cdn.model;

import panda.std.Subscriber;
import java.util.ArrayList;
import java.util.Collection;

public class MutableReferenceImpl<V> implements MutableReference<V> {

    private V value;
    private final Collection<Subscriber<? super V>> subscribers = new ArrayList<>();

    public MutableReferenceImpl(V value) {
        if (value == null) {
            throw new IllegalArgumentException("Reference does not support null values");
        }

        this.value = value;
    }

    @Override
    public MutableReference<V> update(V value) {
        if (value == null) {
            throw new IllegalArgumentException("Cannot update reference using null value");
        }

        subscribers.forEach(subscriber -> subscriber.onComplete(value));
        this.value = value;
        return this;
    }

    @Override
    public Reference<V> subscribe(Subscriber<? super V> subscriber) {
        subscribers.add(subscriber);
        return this;
    }

    @Override
    public V get() {
        return value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<V> getType() {
        return (Class<V>) value.getClass();
    }

}