package com.ttu.lunchbot.parser.menu;

import com.ttu.lunchbot.parser.menu.strategy.MenuParserStrategy;
import com.ttu.lunchbot.spring.model.Menu;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * PDFMenuParser takes a PDF document containing menu texts, extracts the document
 * into a collection of lines of text and uses the given parsing strategy to parse
 * the texts into MenuItems and Menus that can later be used for easy data retrieval.
 * @see MenuParser
 */
public class PDFMenuParser extends MenuParser {

    /**
     * Object with methods for extracting text from PDF documents.
     */
    private final PDFTextStripper stripper;

    /**
     * Create a new PDF menu parser.
     * @param strategy Strategy for parsing menu texts into MenuItems and Menus.
     * @throws IOException When there is a problem with creating or configuring a PDFTextStripper.
     * @see PDFTextStripper
     */
    public PDFMenuParser(MenuParserStrategy strategy) throws IOException {
        super(strategy);
        this.stripper = new PDFTextStripper();
    }

    /**
     * Extract lines of text from a PDF document and store them in a collection.
     * @param file File object representing a PDF document.
     * @return Collection of Strings extracted in top-to-bottom order from the given file.
     */
    private String extractText(File file) {
        this.stripper.setSortByPosition(true);

        try (PDDocument document = PDDocument.load(file)) {
            return this.stripper.getText(document);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param file File object representing a menu document.
     * @return Collection of parsed Menus.
     * @see Menu
     */
    @Override
    public ArrayList<Menu> parseMenus(File file) {
        return this.getStrategy().parse(this.extractText(file));
    }

}
