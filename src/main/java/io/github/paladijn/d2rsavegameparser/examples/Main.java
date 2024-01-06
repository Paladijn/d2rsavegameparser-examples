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
package io.github.paladijn.d2rsavegameparser.examples;

import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class Main {
    private static final Logger log = getLogger(Main.class);

    public static void main(String[] args) {
        if (args.length < 2) {
            log.error("Insufficient parameters\nUsage: Main <type> <saveGameLocation> [other options]");
            System.exit(1);
        }

        final SampleTypes sampleType = SampleTypes.findByParameter(args[0]);

        final long start = System.currentTimeMillis();
        final String result = switch (sampleType) {
            case SOCKET_REWARDS -> {
                final SocketRewards socketRewards = new SocketRewards();
                yield socketRewards.call(args[1]);
            }
            case LIST_SETS -> {
                final ListSets listSets = new ListSets();
                yield listSets.call(args[1], args[2]);
            }
            case UNKNOWN -> String.format("Unknown type %s", args[0]);
        };

        log.info("processing took {} ms", System.currentTimeMillis() - start);

        log.info(result);
    }
}
