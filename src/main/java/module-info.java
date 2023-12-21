/**
 * Sample project for com.github.paladijn.d2rsavegameparser implementations
 */
module d2rsavegameparser.samples {
    requires com.github.paladijn.d2rsavegameparser;
    requires org.slf4j;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;

    exports com.github.paladijn.d2rsavegameparser.examples.model to com.fasterxml.jackson.databind;
}