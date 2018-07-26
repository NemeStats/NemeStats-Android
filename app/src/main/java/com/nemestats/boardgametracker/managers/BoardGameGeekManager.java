package com.nemestats.boardgametracker.managers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;

import com.nemestats.boardgametracker.dal.remote.BoardGameGeekRemoteRepo;
import com.nemestats.boardgametracker.domain.GameDefinition;
import com.nemestats.boardgametracker.webservices.dto.GameDetailsXMLDTO;
import com.nemestats.boardgametracker.webservices.dto.SearchGameXMLDTO;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * Created by mehegeo on 10/8/17.
 */

public class BoardGameGeekManager {

    private BoardGameGeekRemoteRepo mBoardGameGeekRemoteRepo;
    private List<Target> mTargetList;
    private Picasso mPicassoInstance;
    private Context mContext;

    @Inject
    public BoardGameGeekManager(Context context, BoardGameGeekRemoteRepo boardGameGeekRemoteRepo) {
        mBoardGameGeekRemoteRepo = boardGameGeekRemoteRepo;
        mTargetList = new ArrayList<>();
        mContext = context;
        mPicassoInstance = Picasso.with(mContext);
    }

    public Observable<GameDefinition> downloadGameDefinitionDetails(GameDefinition gameDefinition) {
        return mBoardGameGeekRemoteRepo.getThumbnailPathForBBGObjectId(gameDefinition.getBoardGameGeekObjectId())
                .map(gameDetailsXMLDTO -> {
                    gameDefinition.setThumbnailRemotePath(gameDetailsXMLDTO.getThumbnail());
                    gameDefinition.setMinPlayers(gameDetailsXMLDTO.getMinPlayers());
                    gameDefinition.setMaxPlayers(gameDetailsXMLDTO.getMaxPlayers());
                    gameDefinition.setMinPlayTime(gameDetailsXMLDTO.getMinPlayTime());
                    gameDefinition.setMaxPlayTime(gameDetailsXMLDTO.getMaxPlayTime());
                    gameDefinition.setDescription(gameDetailsXMLDTO.getDescription());
                    gameDefinition.setYearPublished(gameDetailsXMLDTO.getYearPublished());

                    String boardGameCategoryList = null;

                    if (gameDetailsXMLDTO.getBoardgameCategoryList() != null) {
                        for (GameDetailsXMLDTO.BoardGameCategory boardGameCategory : gameDetailsXMLDTO.getBoardgameCategoryList()) {
                            if (boardGameCategory != null && boardGameCategory.getBoardgamecategory() != null) {
                                if (boardGameCategoryList == null) {
                                    boardGameCategoryList = "";
                                }
                                boardGameCategoryList += GameDefinition.CATEGORY_DIVIDER + boardGameCategory.getBoardgamecategory();
                            }
                        }
                        if (boardGameCategoryList != null) {
                            boardGameCategoryList = boardGameCategoryList.substring(3, boardGameCategoryList.length());
                        }
                    }

                    gameDefinition.setCategories(boardGameCategoryList);

                    String boardGameMechanicsList = null;
                    if (gameDetailsXMLDTO.getBoardgameMechanicList() != null) {
                        for (GameDetailsXMLDTO.BoardGameMechanics boardGameMechanics : gameDetailsXMLDTO.getBoardgameMechanicList()) {
                            if (boardGameMechanics != null && boardGameMechanics.getBoardgamemechanic() != null) {
                                if (boardGameMechanicsList == null) {
                                    boardGameMechanicsList = "";
                                }
                                boardGameMechanicsList += GameDefinition.CATEGORY_DIVIDER + boardGameMechanics.getBoardgamemechanic();
                            }
                        }
                        if (boardGameMechanicsList != null) {
                            boardGameMechanicsList = boardGameMechanicsList.substring(3, boardGameMechanicsList.length());
                        }
                    }
                    gameDefinition.setMechanics(boardGameMechanicsList);
                    return gameDefinition;
                })
                .concatMap(gameDefinition1 -> Observable.create(e -> {
                    if (gameDefinition1.getThumbnailRemotePath() != null) {
                        Target target = new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                try {
                                    String filePath = mContext.getFilesDir().getAbsolutePath() + "/" + String.valueOf(System.currentTimeMillis() + ".jpg");
                                    saveBitmapToDisk(filePath, bitmap);
                                    gameDefinition1.setThumbnailLocalPath(filePath);
                                    e.onNext(gameDefinition1);
                                    e.onComplete();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {

                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        };

                        mTargetList.add(target);
                        Handler mainHandler = new Handler(mContext.getMainLooper());

                        Runnable myRunnable = () -> mPicassoInstance.load(gameDefinition1.getThumbnailRemotePath()).into(target);
                        mainHandler.post(myRunnable);
                    } else {
                        e.onNext(gameDefinition1);
                        e.onComplete();
                    }
                }));

    }

    private void saveBitmapToDisk(String filename, Bitmap bitmap) throws IOException {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filename);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public Observable<SearchGameXMLDTO> searchForGamesByName(String boardGameName) {
        return mBoardGameGeekRemoteRepo.searchForGamesByName(boardGameName);
    }
}
