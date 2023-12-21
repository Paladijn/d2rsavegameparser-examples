/*
   Copyright 2023 Paladijn (paladijn2960+d2rsavegameparser@gmail.com)

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package com.github.paladijn.d2rsavegameparser.examples;

import java.util.Arrays;

public enum SampleTypes {
    SOCKET_REWARDS("socket"),
    LIST_SETS("sets"),

    UNKNOWN("none");

    private final String parameter;

    SampleTypes(String parameter) {
        this.parameter = parameter;
    }

    public static SampleTypes findByParameter(String parameter) {
        return Arrays.stream(SampleTypes.values())
                .filter(sampleType -> sampleType.parameter.equals(parameter))
                .findAny()
                .orElse(UNKNOWN);
    }
}
