package com.nemestats.boardgametracker.webservices.dto.players;

import com.nemestats.boardgametracker.webservices.dto.players.PlayerResponseDTO;

import java.util.List;

/**
 * Created by mehegeo on 10/6/17.
 */

public class PlayersDTO {

    private List<PlayerResponseDTO> players;


    public List<PlayerResponseDTO> getPlayers() {
        return players;
    }

}
