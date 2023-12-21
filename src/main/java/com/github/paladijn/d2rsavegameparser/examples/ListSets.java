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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.paladijn.d2rsavegameparser.examples.model.ItemName;
import com.github.paladijn.d2rsavegameparser.model.D2Character;
import com.github.paladijn.d2rsavegameparser.model.Item;
import com.github.paladijn.d2rsavegameparser.model.ItemQuality;
import com.github.paladijn.d2rsavegameparser.model.SharedStashTab;
import com.github.paladijn.d2rsavegameparser.parser.SharedStashParser;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static com.github.paladijn.d2rsavegameparser.examples.SampleHelpers.getCharacter;
import static org.slf4j.LoggerFactory.getLogger;

public class ListSets {
    private static final Logger log = getLogger(ListSets.class);

    /**
     * This will parse a savegame folder for all .d2s files along with the shared stash and list all the set items in them. It will also translate the item names.
     * @param saveGameLocation the Diablo II resurrected savegame folder
     * @param language the language to use, supported values: enUS, deDE, esES, esMX, frFR, itIT, jaJP, koKR, plPL, ptBR, ruRU, zhCN, zhTW
     * @return a list of set items, with their translated name
     */
    public String call(final String saveGameLocation, final String language) {
        List<SavegameWithItem> setItems = new ArrayList<>();

        // we now filter on softCore, if you want to filter hardcore only adjust the two lines below to true and HARDCORE_SHARED_STASH
        filterSetItemsInCharacterFiles(saveGameLocation, setItems, false);
        filterSetItemsInSharedStash(saveGameLocation, SharedStashParser.SOFTCORE_SHARED_STASH, setItems);

        log.info("{} set items found", setItems.size());
        setItems.sort(new ItemSortedById());

        final TranslationService translationService = new TranslationService();

        StringBuilder result = new StringBuilder("Set items found");
        setItems.forEach(setItem ->
            result.append("\n")
                    .append(translationService.getItemTranslation(language, setItem.item().itemName()))
                    .append(" -> ").append(setItem.savegame())
                    .append(" at ").append(setItem.item().location()).append(" (").append(setItem.item().container())
                    .append(") [").append(setItem.item().x()).append(", ").append(setItem.item().y()).append("]")
        );

        return result.toString();
    }

    private static void filterSetItemsInCharacterFiles(final String saveGameLocation, final List<SavegameWithItem> setItems, final boolean filterHardcore) {
        try (Stream<Path> pathStream = Files.list(Path.of(saveGameLocation))) {
            final List<Path> savegames = pathStream
                    .filter(file -> !Files.isDirectory(file) && file.toString().endsWith(".d2s"))
                    .toList();

            log.info("{} savegame files found", savegames.size());

            for (Path savegame: savegames) {
                final ByteBuffer byteBuffer = ByteBuffer.wrap(Files.readAllBytes(savegame));

                getCharacter(byteBuffer, savegame)
                        .ifPresent(d2Character -> {
                            if (d2Character.hardcore() == filterHardcore) {
                                filterSetItems(savegame.getFileName(), d2Character, setItems);
                            }
                        });
            }
        } catch (IOException e) {
            throw new RuntimeException("Problem occurred reading savegame files", e);
        }
    }

    private static void filterSetItemsInSharedStash(final String saveGameLocation, final String stashLocation,  final List<SavegameWithItem> setItems) {
        final SharedStashParser sharedStashParser = new SharedStashParser(false);
        final Path sharedStash = Path.of(saveGameLocation, stashLocation);

        final ByteBuffer byteBuffer;
        try {
            byteBuffer = ByteBuffer.wrap(Files.readAllBytes(sharedStash));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final List<SharedStashTab> sharedStashTabs = sharedStashParser.parse(byteBuffer);
        sharedStashTabs.forEach(sharedStashTab -> sharedStashTab.items().stream()
                .filter(item -> item.quality() == ItemQuality.SET)
                .forEach(item -> setItems.add(new SavegameWithItem(stashLocation, item)))
        );
    }

    private static void filterSetItems(Path savegame, D2Character d2Character, List<SavegameWithItem> setItems) {
        d2Character.items().stream()
                .filter(item -> item.quality() == ItemQuality.SET)
                .forEach(item -> setItems.add(new SavegameWithItem(savegame.toString(), item)));

        // mercenaries can have set items too!
        Optional.ofNullable(d2Character.mercenary())
                .ifPresent(mercenary -> mercenary.items().stream()
                    .filter(item -> item.quality() == ItemQuality.SET)
                    .forEach(item -> setItems.add(new SavegameWithItem(savegame.toString(), item))));
    }

}

record SavegameWithItem(String savegame, Item item) {}

class ItemSortedById implements Comparator<SavegameWithItem> {
    @Override
    public int compare(SavegameWithItem o1, SavegameWithItem o2) {
        return Short.compare(o1.item().setItemId(), o2.item().setItemId());
    }
}

class TranslationService {
    private static final Logger log = getLogger(TranslationService.class);

    private final Map<String, ItemName> namesMappedByKey = new HashMap<>();

    public TranslationService() {
        final ObjectMapper objectMapper = new ObjectMapper();

        try {
            final InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("item-names.json");
            final List<ItemName> itemNames = objectMapper.readValue(resourceAsStream, new TypeReference<>() {});
            itemNames.forEach(itemName -> namesMappedByKey.put(itemName.key(), itemName));
            log.debug("found {} names", itemNames.size());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getItemTranslation(final String language, final String key) {
        final ItemName itemName = namesMappedByKey.get(key);
        if (itemName == null) {
            if (!key.endsWith("Rune")) {
                log.error("could not find mapping for {}", key);
            }
            return key;
        }

        final String value = switch (language) {
            case "enUS" -> itemName.enUS();
            case "zhTW" -> itemName.zhTW();
            case "deDE" -> itemName.deDE();
            case "esES" -> itemName.esES();
            case "frFR" -> itemName.frFR();
            case "itIT" -> itemName.itIT();
            case "koKR" -> itemName.koKR();
            case "plPL" -> itemName.plPL();
            case "esMX" -> itemName.esMX();
            case "jaJP" -> itemName.jaJP();
            case "ptBR" -> itemName.ptBR();
            case "ruRU" -> itemName.ruRU();
            case "zhCN" -> itemName.zhCN();

            default -> throw new RuntimeException("unsupported language: " + language);
        };

        return sanitiseResponse(value);
    }

    private String sanitiseResponse(String value) {
        if (!value.startsWith("[")) {
            return value;
        }
        int nextBlock = value.indexOf("[", 1);
        if (nextBlock == -1) {
            return value.substring(4);
        }
        return value.substring(4, nextBlock - 1);
    }
}

