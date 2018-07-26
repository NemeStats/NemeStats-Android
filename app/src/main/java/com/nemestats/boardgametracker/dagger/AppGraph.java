package com.nemestats.boardgametracker.dagger;

import com.nemestats.boardgametracker.activities.MainActivity;
import com.nemestats.boardgametracker.activities.SearchGameDefinitionActivity;
import com.nemestats.boardgametracker.activities.EditGameDefinitionActivity;
import com.nemestats.boardgametracker.activities.EditPlayerActivity;
import com.nemestats.boardgametracker.activities.GameDefinitionDetailsActivity;
import com.nemestats.boardgametracker.activities.NemeStatsMainActivity;
import com.nemestats.boardgametracker.activities.PlayerDetailsActivity;
import com.nemestats.boardgametracker.fragments.CreateAccountFragment;
import com.nemestats.boardgametracker.fragments.GameDefinitionsFragment;
import com.nemestats.boardgametracker.fragments.GamingGroupsFragment;
import com.nemestats.boardgametracker.fragments.LoginFragment;
import com.nemestats.boardgametracker.fragments.PlayedGamesFragment;
import com.nemestats.boardgametracker.fragments.PlayersInGamingGroupFragment;
import com.nemestats.boardgametracker.viewModels.RecordPlayedGameViewModel;

/**
 * Created by mehegeo on 9/23/17.
 */

public interface AppGraph {

    void inject(LoginFragment loginFragment);

    void inject(CreateAccountFragment createAccountFragment);

    void inject(NemeStatsMainActivity nemeStatsMainActivity);

    void inject(GameDefinitionsFragment gameDefinitionsFragment);

    void inject(GameDefinitionDetailsActivity gameDefinitionDetailsActivity);

    void inject(PlayedGamesFragment playedGamesFragment);

    void inject(RecordPlayedGameViewModel recordPlayedGameViewModel);

    void inject(SearchGameDefinitionActivity searchGameDefinitionActivity);

    void inject(GamingGroupsFragment gamingGroupsFragment);

    void inject(PlayersInGamingGroupFragment playersInGamingGroupFragment);

    void inject(EditPlayerActivity editPlayerActivity);

    void inject(PlayerDetailsActivity playerDetailsActivity);

    void inject(EditGameDefinitionActivity editGameDefinitionActivity);

    void inject(MainActivity mainActivity);
}
