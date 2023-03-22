package com.gifu.coreservice.enumeration;

import lombok.Getter;
import org.json.simple.JSONObject;

@Getter
public enum VariantTypeEnum {
    COLOR("Color", true),
    COLOR_PACKAGING("Color Packaging", true),
    PACKAGING("Packaging", false),
    ACCESSORIES("Accessories", false),
    PRODUCT_DESIGN("Product Design", false),
    SIZE("Size", true),
    POSITION("Position", true),
    EMBOSS_DESIGN("Emboss Design", true),
    GREETINGS_DESIGN("Greetings Design", true);

    private String text;
    private boolean canBePaired;

    VariantTypeEnum(String text, boolean canBePaired) {
        this.text = text;
        this.canBePaired = canBePaired;
    }

    public String getMeta(){
        JSONObject obj = new JSONObject();
        obj.put("canBePaired", this.canBePaired);
        return obj.toJSONString();
    }
}
