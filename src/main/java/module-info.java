/**
 * Sample project for io.github.paladijn.d2rsavegameparser implementations
 */
module d2rsavegameparser.samples {
    requires io.github.paladijn.d2rsavegameparser;
    requires org.slf4j;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;

    exports io.github.paladijn.d2rsavegameparser.examples.model to com.fasterxml.jackson.databind;
}