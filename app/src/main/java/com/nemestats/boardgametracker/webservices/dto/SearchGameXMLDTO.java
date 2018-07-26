package com.nemestats.boardgametracker.webservices.dto;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;

/**
 * Created by geomehedeniuc on 3/25/18.
 */

@Root(name = "boardgames", strict = false)
public class SearchGameXMLDTO {


    @ElementList(entry = "boardgame", required = false, inline = true)
    public ArrayList<BoardGameEntry> mBoardGameList;

    public ArrayList<BoardGameEntry> getBoardGameList() {
        return mBoardGameList;
    }

    public static class BoardGameEntry {

        @Attribute(name = "objectid", required = false)
        private String objectid;

        @Element(name = "name", required = false)
        String mName;

        @Element(name = "yearpublished", required = false)
        String yearPublished;

        public String getObjectid() {
            return objectid;
        }

        public String getName() {
            return mName;
        }

        public String getYearPublished() {
            return yearPublished;
        }
    }


}
