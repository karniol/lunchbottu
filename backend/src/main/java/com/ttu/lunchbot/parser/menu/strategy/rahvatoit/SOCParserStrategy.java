package com.ttu.lunchbot.parser.menu.strategy.rahvatoit;

import com.ttu.lunchbot.parser.menu.strategy.MenuParserStrategy;
import com.ttu.lunchbot.spring.model.Menu;

import java.util.ArrayList;

public class SOCParserStrategy extends RahvaToitMenuParserStrategy implements MenuParserStrategy {

    private static final String PARSER_TYPE = "STRING";

    private static final String PARSER_NAME = "RAHVATOIT_SOC";

    @Override
    public ArrayList<Menu> parse(String text) {
        return super.parseAndSelect(text, PARSE_SOC);
    }

    @Override
    public String getParserName() {
        return PARSER_NAME;
    }

    @Override
    public String getParserType() {
        return PARSER_TYPE;
    }
}
