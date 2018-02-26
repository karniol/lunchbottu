package com.ttu.lunchbot.menuparser;

import java.math.BigDecimal;
import java.text.DateFormatSymbols;
import java.util.*;

/**
 * MenuParser strategy for parsing Baltic Restaurants' (Daily) menus.
 * @see MenuParser
 */
public class BalticRestaurantMenuParserStrategy implements MenuParserStrategy {

    /**
     * Regular expression pattern for matching lines in the menu text that contain a date.
     */
    private static final String DATE_PATTERN_STRING = ".+\\s[\\d]{1,2}[.]\\s.+";

    /**
     * Array of month names in Estonian, in order, from January (index 0) to December (index 11).
     */
    private static final String[] MONTH_NAMES_ET = DateFormatSymbols.getInstance(Locale.forLanguageTag("et")).getMonths();

    /**
     * Time zone code identifier for Tallinn as listed in the Java TimeZone specification.
     * @see TimeZone
     */
    private static final String TIME_ZONE_ID = "Europe/Tallinn";

    /**
     * Locale language tag for Estonian as listed in the Java Locale specification.
     */
    private static final String LANGUAGE_TAG = "et";

    /**
     * Currency tag for Euros as listed in the Java Currency specification.
     */
    private static final String CURRENCY_TAG = "EUR";

    /**
     * Collection of parsed Menus. A collection is used in case the menu text (input to
     * the parser) contains more than one Menu, since one Menu corresponds to one date.
     * @see Menu
     */
    private final ArrayList<Menu> parsedMenus;

    /**
     * Current Menu that is being updated and written into by the parser.
     */
    private Menu currentMenu;

    /**
     * Current MenuItem that is being updated and written into by the parser.
     * MenuItems are added into the currently updated Menu.
     */
    private MenuItem currentMenuItem;

    /**
     * Create and initialize a new parser strategy.
     */
    BalticRestaurantMenuParserStrategy() {
        this.parsedMenus = new ArrayList<>();
        this.currentMenuItem = null;
        this.currentMenu = null;
    }

    /**
     * Parse a line containing a date into a Calendar instance.
     * The current year is used, based on the system time.
     * @param line Line containing a date in Estonian: month name and the day of month.
     * @return Calendar instance containing the parsed date.
     */
    private Calendar parseDate(String line) {
        String[] parts = line.split(" ");

        final String monthString = parts[parts.length - 1].toLowerCase();
        final String dayString = parts[parts.length - 2].split("[.,]")[0];

        int month = -1;
        for (int m = 0; m < MONTH_NAMES_ET.length; ++m) {
            if (MONTH_NAMES_ET[m].equals(monthString)) {
                month = m;
                break;
            }
        }

        final int day = Integer.parseInt(dayString);

        Calendar date = Calendar.getInstance();

        // TODO: magic month number
        date.set(date.get(Calendar.YEAR), month, day, 0, 0, 0);
        date.setTimeZone(TimeZone.getTimeZone(TIME_ZONE_ID));

        return date;
    }

    /**
     * Parse a line containing the name of the menu item in Estonian and its price in Euros into
     * the currently updated MenuItem.
     * @param line Line containing the name of the menu item in Estonian and its price in Euros.
     * @see MenuItem
     */
    private void addLineContainingPriceToCurrentMenuItem(String line) {
        // Split the line into parts to obtain the name of the food item and its price
        // Example: Caesar salad 4.20
        ArrayList<String> parts = new ArrayList<>(Arrays.asList(line.split(" ")));

        // Obtain: Caesar salad
        String name = String.join(" ", parts.subList(0, parts.size() - 1));

        // Obtain: 4.20
        BigDecimal price = BigDecimal.valueOf(Double.parseDouble(parts.get(parts.size() - 1)));

        this.currentMenuItem.addName(Locale.forLanguageTag(LANGUAGE_TAG), name);
        this.currentMenuItem.addPrice(Currency.getInstance(CURRENCY_TAG), price);
    }

    /**
     * Remove elements from the beginning and end of a collection.
     * @param text ArrayList of Strings.
     * @param fromBeginning Lines to remove from the beginning of the collection.
     * @param fromEnd Lines to remove from the end of the collection.
     */
    private void removeLines(ArrayList<String> text, int fromBeginning, int fromEnd) {
        for (int i = 0; i < fromBeginning; ++i) {
            text.remove(0);
        }

        for (int i = 0; i < fromEnd; ++i) {
            text.remove(text.size() - 1);
        }
    }

    @Override
    public ArrayList<Menu> parse(ArrayList<String> text) {
        if (text.size() == 0) {
            System.out.println("Argument collection is empty, returning empty collection");
            return new ArrayList<>();
        }

        this.parsedMenus.clear();

        final String name = text.get(1);

        removeLines(text, 2, 1);

        // Baltic Restaurants' menus list items twice, first in Estonian and then in English
        // The first line also contains a string representing the price of the item in Euros
        boolean nextLineContainsPrice = true;

        for (String line : text) {
            // Encountering a date, the method creates a new menu with the corresponding date
            if (line.matches(DATE_PATTERN_STRING)) {
                this.currentMenu = new Menu(name, parseDate(line));
                this.parsedMenus.add(this.currentMenu);
                continue;
            }

            if (nextLineContainsPrice) {
                this.currentMenuItem = new MenuItem();

                // TODO: null is bad style
                if (null != this.currentMenu) {
                    this.currentMenu.addItem(this.currentMenuItem);
                }

                addLineContainingPriceToCurrentMenuItem(line);
                nextLineContainsPrice = false;
            } else {
                this.currentMenuItem.addName(Locale.ENGLISH, line);
                nextLineContainsPrice = true;
            }
        }

        return this.parsedMenus;
    }
}
