package com.pi4j.test.registry;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: UNITTEST :: Unit/Integration Tests
 * FILENAME      :  RegistryGetIoInstancesByProvider.java
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
import com.pi4j.context.Context;
import com.pi4j.exception.Pi4JException;
import com.pi4j.io.IOType;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalInputProvider;
import com.pi4j.mock.provider.gpio.digital.MockDigitalInputProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RegistryGetIoInstancesByProvider {

    public static final int PIN_ADDRESS_1 = 1;
    public static final String PIN_ID_1 = "my-custom-pin-1";

    public static final int PIN_ADDRESS_2 = 2;
    public static final String PIN_ID_2 = "my-custom-pin-2";

    private Context pi4j;

    @Before
    public void beforeTest() throws Pi4JException {

        System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "DEBUG");

        // Initialize Pi4J with a manually configured context
        // Disable AUTO-DETECT loading to prevent automatic
        // loading of any detected Pi4J binding libraries
        // in the class path for this test case
        // ...
        // Explicitly add single provider implementation
        // into the context for testing
        // ...
        // Also, inject this class instance into the Pi4J context
        // for annotation processing and dependency injection
        pi4j = Pi4J.newContext()
                .add(new MockDigitalInputProvider())
                .build().inject(this);

        // create simple I/O instances
        DigitalInput input1 = DigitalInput.create(pi4j, PIN_ADDRESS_1, PIN_ID_1);
        DigitalInput input2 = DigitalInput.create(pi4j, PIN_ADDRESS_2, PIN_ID_2);
    }

    @After
    public void afterTest() {
        try {
            pi4j.shutdown();
        } catch (Pi4JException e) { /* do nothing */ }
    }

    @Test
    public void testGetIoInstancesByProviderClass() throws Pi4JException {

        // attempt to get I/O instance from registry
        var retrieved = pi4j.registry().allByProvider(DigitalInputProvider.class);

        // verify the retrieved I/O instance is the same count we registered
        assertEquals("The I/O instances retrieved from registry is not a match.", 2, retrieved.size());
    }

    @Test
    public void testGetIoInstancesByProviderType() throws Pi4JException {

        // attempt to get I/O instance from registry
        var retrieved = pi4j.registry().allByIoType(IOType.DIGITAL_INPUT);

        // verify the retrieved I/O instance is the same count we registered
        assertEquals("The I/O instances retrieved from registry is not a match.", 2, retrieved.size());
    }

    @Test
    public void testGetIoInstancesByProviderId() throws Pi4JException {

        // attempt to get I/O instance from registry
        var retrieved = pi4j.registry().allByProvider(MockDigitalInputProvider.ID);

        // verify the retrieved I/O instance is the same count we registered
        assertEquals("The I/O instances retrieved from registry is not a match.", 2, retrieved.size());
    }

    @Test
    public void testGetIoInstancesFromInvalidProvider() throws Pi4JException {

        // attempt to get I/O instance from registry
        var retrieved = pi4j.registry().allByIoType(IOType.ANALOG_INPUT);

        // verify the retrieved I/O instance is ZERO
        assertEquals("No I/O instances should have retrieved from registry using this IO type.", 0, retrieved.size());
    }

}
