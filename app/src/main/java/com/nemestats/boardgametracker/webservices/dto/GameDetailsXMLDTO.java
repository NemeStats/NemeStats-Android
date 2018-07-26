package com.nemestats.boardgametracker.webservices.dto;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

import java.util.ArrayList;

/**
 * Created by mehegeo on 10/8/17.
 */

@Root(name = "boardgames", strict = false)
public class GameDetailsXMLDTO {

    @Path("boardgame")
    @Element(name = "minplayers", required = false)
    int minPlayers;

    @Path("boardgame")
    @Element(name = "maxplayers", required = false)
    int maxPlayers;

    @Path("boardgame")
    @Element(name = "description", required = false)
    String description;

    @Path("boardgame")
    @Element(name = "minplaytime", required = false)
    float minPlayTime;

    @Path("boardgame")
    @Element(name = "maxplaytime", required = false)
    float maxPlayTime;

    @Path("boardgame")
    @Element(name = "yearpublished", required = false)
    String yearPublished;

    @Path("boardgame")
    @Element(name = "thumbnail", required = false)
    String thumbnail;

    @Path("boardgame")
    @ElementList(entry = "boardgamecategory", required = false, inline = true)
    public ArrayList<BoardGameCategory> boardgamecategoryList;

    public static class BoardGameCategory {
        @Text(required = false)
        private String boardgamecategory;

        @Attribute(name = "objectid", required = false)
        private String objectid;

        public String getBoardgamecategory() {
            return boardgamecategory;
        }
    }

    @Path("boardgame")
    @ElementList(entry = "boardgamemechanic", required = false, inline = true)
    public ArrayList<BoardGameMechanics> boardgamemechanicList;

    public static class BoardGameMechanics {
        @Text(required = false)
        private String boardgamemechanic;

        @Attribute(name = "objectid", required = false)
        private String objectid;

        public String getBoardgamemechanic() {
            return boardgamemechanic;
        }
    }

    public ArrayList<BoardGameCategory> getBoardgameCategoryList() {
        return boardgamecategoryList;
    }

    public ArrayList<BoardGameMechanics> getBoardgameMechanicList() {
        return boardgamemechanicList;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public String getDescription() {
        return description;
    }

    public float getMinPlayTime() {
        return minPlayTime;
    }

    public float getMaxPlayTime() {
        return maxPlayTime;
    }

    public String getYearPublished() {
        return yearPublished;
    }

    public String getThumbnail() {
        return thumbnail;
    }
}
