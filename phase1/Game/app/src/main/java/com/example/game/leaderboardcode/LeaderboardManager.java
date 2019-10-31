package com.example.game.leaderboardcode;

import com.example.game.Games;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class LeaderboardManager {
    private File saveDirectory;
    private ArrayList<String> games = new ArrayList<>();
    private HashMap<Games, String> gameFilename = new HashMap<>();

    public LeaderboardManager(File saveDirectory) {
        this.saveDirectory = saveDirectory;

        for (Games game : Games.values()) {
            String gameName = getGameName(game);
            games.add(gameName);
            gameFilename.put(game, gameName);

            if (!fileExists(game)) {
                File gameFile = new File(saveDirectory, gameName);
                try {
                    gameFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println(saveDirectory.toString());
    }

    public void saveData(Games game, String username, String statistic, String value) throws IOException {
        File gameFile = getGameFile(game);
        BufferedWriter bw = new BufferedWriter(new FileWriter(gameFile.getAbsolutePath()));
        bw.write(username + statistic + value);
    }

    public String[] getGames() {
        return (String[]) games.toArray();
    }

    public String[] getGameStatistics(Games game) throws IOException {
        File gameFile = getGameFile(game);
        BufferedReader br = new BufferedReader(new FileReader(gameFile.getAbsolutePath()));
        ArrayList<String> data = new ArrayList<>();

        String currLine = br.readLine();
        while (currLine != null) {
            String[] sArray = currLine.split(" ");
            data.add(String.join(" ", sArray));

            currLine = br.readLine();
        }

        Collections.sort(data, scoreSorter);
        return (String[]) data.toArray();
    }

    public String[] getGameStatistics(Games game, String statistic) throws IOException {
        File gameFile = getGameFile(game);
        BufferedReader br = new BufferedReader(new FileReader(gameFile.getAbsolutePath()));
        ArrayList<String> data = new ArrayList<>();

        String currLine = br.readLine();
        while (currLine != null) {
            String[] sArray = currLine.split(" ");
            if (statistic.equals(sArray[0])) {
                data.add(String.join(" ", sArray));
            }
            currLine = br.readLine();
        }

        Collections.sort(data, scoreSorter);
        return (String[]) data.toArray();
    }

    private String getGameName(Games game) {
        return game.toString().toLowerCase();
    }

    private File getGameFile(Games game) {
        return new File(saveDirectory, getGameName(game));
    }

    private boolean fileExists(Games game) {
        String gameName = gameFilename.get(game);
        if (gameName == null) {
            return false;
        }
        File temp = new File(saveDirectory, gameName);
        return temp.exists();
    }

    private void retainTopTen() throws IOException {
        for (Games game : Games.values()) {
            File gameFile = getGameFile(game);
            HashMap<String, ArrayList<String>> gameStats = new HashMap<>();

            BufferedReader br = new BufferedReader(new FileReader(gameFile.getAbsolutePath()));
            String currLine = br.readLine();
            while (currLine != null) {
                String[] splitLine = currLine.split(" ");
                if (!gameStats.containsKey(splitLine[0])) {
                    ArrayList<String> temp = new ArrayList<>();
                    temp.add(splitLine[1] + " " + splitLine[2]);
                    gameStats.put(splitLine[0], temp);
                } else {
                    gameStats.get(splitLine[0]).add(splitLine[0] + splitLine[2]);
                }
                currLine = br.readLine();
            }
            br.close();

            for (String statistic : gameStats.keySet()) {
                ArrayList<String> statisticData = gameStats.get(statistic);
                Collections.sort(statisticData, scoreSorter);
                gameStats.put(statistic, (ArrayList<String>) statisticData.subList(0,
                        Math.min(statisticData.size(), 10)));
            }

            BufferedWriter bw = new BufferedWriter(
                    new FileWriter(gameFile.getAbsolutePath(), false));
            for (String statistic : gameStats.keySet()) {
                for (String data : gameStats.get(statistic)) {
                    bw.write(data);
                }
            }
            bw.close();
        }
    }

    private Comparator<String> scoreSorter = new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
            int first = Integer.valueOf(o1.substring(o1.lastIndexOf(" ") + 1));
            int second = Integer.valueOf(o2.substring(o2.lastIndexOf(" ") + 1));
            return Integer.compare(first, second);
        }
    };
}