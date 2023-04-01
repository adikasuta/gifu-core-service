package com.gifu.coreservice.enumeration;

import lombok.Getter;
import org.json.simple.JSONObject;

@Getter
public enum VariantTypeEnum {
    COLOR("Color"),
    COLOR_PACKAGING_1("Color Packaging 1"),
    COLOR_PACKAGING_2("Color Packaging 2"),
    PACKAGING("Packaging"),
    ACCESSORIES("Accessories"),
    PRODUCT_DESIGN("Product Design"),
    SIZE("Size"),
    POSITION("Position"),
    EMBOSS_DESIGN("Emboss Design"),
    GREETINGS_DESIGN("Greetings Design"),
    BOARD_PAPER("Board Paper"),
    ENVELOPE_PAPER("Envelope Paper"),
    ORIENTATION_BOARD("Board Orientation"),
    ORIENTATION_ENVELOPE("Envelope Orientation"),
    LANGUAGE("Language"),
    FOIL_COLOR("Foil Color"),
    FOIL_POSITION("Foil Position"),
    WAX_SEALS("Wax Seals"),
    DRIED_FLOWERS("Dried Flowers"),
    ENVELOPE("Envelope"),
    ADDITIONAL_PAPER("Additional Paper"),
    VELLUM_WRAP("Vellum Wrap"),
    RIBBON("Ribbon"),
    RIBBON_COLOR("Ribbon Color"),
    PACKAGING_SERVICE("Packaging Service");
    private String text;

    VariantTypeEnum(String text) {
        this.text = text;
    }

}
