package com.pi4j.annotation.processor.register;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (API)
 * FILENAME      :  AnalogOutputProcessor.java
 *
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  https://pi4j.com/
 * **********************************************************************
 * %%
 * Copyright (C) 2012 - 2019 Pi4J
 * %%
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
 * #L%
 */

import com.pi4j.Pi4J;
import com.pi4j.annotation.*;
import com.pi4j.annotation.exception.AnnotationException;
import com.pi4j.annotation.impl.ProviderAnnotationProcessor;
import com.pi4j.exception.NotInitializedException;
import com.pi4j.io.gpio.analog.AnalogOutput;
import com.pi4j.io.gpio.analog.AnalogOutputBuilder;
import com.pi4j.io.gpio.analog.AnalogOutputProvider;
import com.pi4j.provider.Provider;
import com.pi4j.provider.exception.ProviderException;
import com.pi4j.registry.exception.RegistryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

public class AnalogOutputProcessor implements RegisterProcessor<AnalogOutput> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public Class<AnalogOutput> getTargetType() { return AnalogOutput.class; }

    @Override
    public AnalogOutput process(Object instance, Field field, Register annotation) throws Exception {

        Class<? extends Provider> providerClass = null;

        // test for required peer annotations
        if (!field.isAnnotationPresent(Address.class)) {
            throw new AnnotationException("Missing required '@Address' annotation for this I/O type.");
        }
        try {
            boolean accessible = field.canAccess(instance);
            if (!accessible) field.trySetAccessible();

            // all supported additional annotations for configuring the digital output
            Address address = field.getAnnotation(Address.class);

            Name name = null;
            if (field.isAnnotationPresent(Name.class)) {
                name = field.getAnnotation(Name.class);
            }

            Description description = null;
            if (field.isAnnotationPresent(Description.class)) {
                description = field.getAnnotation(Description.class);
            }

            ShutdownValue shutdownValue = null;
            if (field.isAnnotationPresent(ShutdownValue.class)) {
                shutdownValue = field.getAnnotation(ShutdownValue.class);
            }

            InitialValue initialValue = null;
            if (field.isAnnotationPresent(InitialValue.class)) {
                initialValue = field.getAnnotation(InitialValue.class);
            }

            StepValue stepValue = null;
            if (field.isAnnotationPresent(StepValue.class)) {
                stepValue = field.getAnnotation(StepValue.class);
            }

            Range range = null;
            if (field.isAnnotationPresent(Range.class)) {
                range = field.getAnnotation(Range.class);
            }

            AnalogOutputBuilder builder = AnalogOutput.builder();
            if (annotation.value() != null) builder.id((annotation).value());
            builder.address(address.value());

            if (name != null) builder.name(name.value());
            if (description != null) builder.description(description.value());
            if (shutdownValue != null) builder.shutdown(shutdownValue.value());
            if (initialValue != null) builder.initial(initialValue.value());
            if (stepValue != null) builder.step(stepValue.value());
            if (range != null) builder.min(range.min());
            if (range != null) builder.max(range.max());

            AnalogOutputProvider provider = null;
            if (field.isAnnotationPresent(com.pi4j.annotation.Provider.class)) {
                provider = ProviderAnnotationProcessor.instance(field, AnalogOutputProvider.class);
            } else {
                provider = Pi4J.providers().getDefault(AnalogOutputProvider.class);
            }

            // create I/O instance
            AnalogOutput output = AnalogOutput.create(provider, builder.build());

            // return the I/O instance reference back on the annotated object
            return output;
        } catch (NotInitializedException e) {
            logger.error(e.getMessage(), e);
            throw new AnnotationException(e);
        } catch (ProviderException e) {
            logger.error(e.getMessage(), e);
            throw new AnnotationException(e);
        } catch (RegistryException e) {
            logger.error(e.getMessage(), e);
            throw new AnnotationException(e);
        }
    }
}
