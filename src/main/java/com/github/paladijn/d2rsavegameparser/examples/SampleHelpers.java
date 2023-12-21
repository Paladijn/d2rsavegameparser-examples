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

import com.github.paladijn.d2rsavegameparser.model.D2Character;
import com.github.paladijn.d2rsavegameparser.parser.CharacterParser;
import com.github.paladijn.d2rsavegameparser.parser.ParseException;
import org.slf4j.Logger;

import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.Optional;

import static org.slf4j.LoggerFactory.getLogger;

public interface SampleHelpers {
    Logger log = getLogger(SampleHelpers.class);

    static Optional<D2Character> getCharacter(final ByteBuffer byteBuffer, final Path savegame) {
        CharacterParser characterParser = new CharacterParser(false);
        try {
            return Optional.of(characterParser.parse(byteBuffer));
        } catch (ParseException pe) {
            log.error("Could not parse file {} due to", savegame, pe);
        }
        return Optional.empty();
    }
}
