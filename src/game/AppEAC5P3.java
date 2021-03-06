package game;

/**
 * Play the classic game Rock Paper Scissors against the CPU, as well
 * as the extended version Rock Paper Scissors Lizard Spock
 */
public class AppEAC5P3 {

    private static final String PLAYER_OPTIONS = "1. PLAY%n2. LIST LOG FILES%" + "n3. VIEW PLAYER HISTORY%n4. SHOW RULES%n0. EXIT%n";
    private static final String PUNTUACIO_INICIAL = "0";
    private static final int MAX_PLAYERS = 40;
    private static final int DATA_FIELDS = 2;
    private static final String NO_MORE_SPACE_ERROR = """
                         NO QUEDA ESPAI PER REGISTRAR MES JUGADORS
                                   PERO ELS JUGADORS EXISTENTS ENCARA PODEN JUGAR
            """;
    private static final String RPS_RULES = """
             *ROCK PAPER SCISSORS RULES:
                - Rock wins against scissors.
                - Scissors win against paper.
                - Paper wins against rock.
            """;
    private static final String RPSLS_RULES = """
             * ROCK PAPER SCISSORS LIZARD SPOCK RULES
                - Scissors cuts paper.
                - Paper covers rock.
                - Rock crushes lizard.
                - Lizard poisons Spock.
                - Spock smashes scissors.
                - Scissors decapitates lizard.
                - Lizard eats paper.
                - Paper disproves Spock.
                - Spock vaporizes rock.
                - Rock crushes scissors.
            """;
    private static final String GAME_TITLE = """
            ____   ___   ____ _  __   ____   _    ____  _____ ____     ____   ____ ___ ____ ____   ___  ____  ____ \s
                       |  _ \\ / _ \\ / ___| |/ /  |  _ \\ / \\  |  _ \\| ____|  _ \\   / ___| / ___|_ _/ ___/ ___| / _ \\|  _ \\/ ___|\s
                       | |_) | | | | |   | ' /   | |_) / _ \\ | |_) |  _| | |_) |  \\___ \\| |    | |\\___ \\___ \\| | | | |_) \\___ \\\s
                       |  _ <| |_| | |___| . \\   |  __/ ___ \\|  __/| |___|  _ <    ___) | |___ | | ___) |__) | |_| |  _ < ___) |
                       |_| \\_\\\\___/ \\____|_|\\_\\  |_| /_/   \\_\\_|   |_____|_| \\_\\  |____/ \\____|___|____/____/ \\___/|_| \\_\\____/\s
                        _     ___ _____   _    ____  ____     ____  ____   ___   ____ _  __     __                             \s
                       | |   |_ _|__  /  / \\  |  _ \\|  _ \\   / ___||  _ \\ / _ \\ / ___| |/ /  _  \\ \\                            \s
                       | |    | |  / /  / _ \\ | |_) | | | |  \\___ \\| |_) | | | | |   | ' /  (_)  | |                           \s
                       | |___ | | / /_ / ___ \\|  _ <| |_| |   ___) |  __/| |_| | |___| . \\   _   | |                           \s
                       |_____|___/____/_/   \\_\\_| \\_\\____/   |____/|_|    \\___/ \\____|_|\\_\\ (_)  | |                           \s
                                                                                                /_/   \s""";

    public static void main(String[] args) {
        AppEAC5P3 app = new AppEAC5P3();
        app.start();
    }

    public void start() {

        // Checks for (or creates) data directory structure
        FileUtils.inicialitza();
        // Loads players from file
        String[][] dadesJugadors = FileUtils.loadPlayers();
        // if no file is found, generates a working 40*2 array to store data
        if (dadesJugadors == null) dadesJugadors = UtilsES.initializeEmptyArray(MAX_PLAYERS, DATA_FIELDS);

        int options;
        do {
            options = getUserMenuOption();
            switch (options) {
                case 1 -> playTheGame(dadesJugadors);
                case 2 -> listGameFiles();
                case 3 -> listPlayerGames();
                case 4 -> viewGameRules();
            }
        } while (options != 0);
    }

    /**
     * Show rock paper scissors and rock paper scissors lizard spock rules
     */
    public void viewGameRules() {
        int option = UtilsES.getInteger("""
                **********************************************************************
                | 0. ROCK PAPER SCISSORS \t|\t1. ROCK PAPER SCISSORS LIZARD SPOCK |
                **********************************************************************
                """, "Wrong option, try again!", 0, 1);
        switch (option) {
            case 0 -> System.out.println(RPS_RULES);
            case 1 -> System.out.println(RPSLS_RULES);
        }
        UtilsES.nextGame();
    }

    /**
     * @param dadesJugadors Array with loaded players from disk, if there's no players to load
     *                      it's passed as an empty array that will fit 40 players and their score
     *                      this method will ask for the players name, how many rounds of the game he/she wants to play
     *                      and what variation, it will play all the rounds player vs cpu random bets
     */
    public void playTheGame(String[][] dadesJugadors) {
        UtilsES.showTitle("GAME CONFIGURATION");
        String nom = UtilsES.getName("What's your name? ");
        int posicio = isValidPlayer(nom, dadesJugadors);
        if (posicio != -1) {
            Game partida;
            int tornsPartida = UtilsES.getRounds();
            UtilsES.separadorLinies();
            int joc = chooseGameMenu();
            UtilsES.showTitle("LET'S GO!");
            partida = (joc == 0) ? new RockPaperScissors() : new RockPaperScissorsSpockLizard();
            GameData partidaActual = partida.createGameData(nom, tornsPartida, partida.getGameType(joc));
            partida.playGame(partidaActual);
            UtilsES.showGameWinner(partidaActual);
            UtilsES.updateScore(partidaActual.getWinner(), posicio, dadesJugadors);
            UtilsES.showScore(posicio, dadesJugadors);
            // Only update files when a new game has been played
            FileUtils.savePlayers(dadesJugadors);
            FileUtils.guardarPartidaEnHistoric(nom, partidaActual.getTorns(), partidaActual.getWinner());
            UtilsES.nextGame();
        }
    }

    /**
     * List all the game files containing historic player data
     */
    public void listGameFiles() {
        String[][] games = FileUtils.getGameFiles();
        if (games != null) {
            UtilsES.showTitle("SAVED PLAYER FILES");
            System.out.println("FILE \t\t SIZE (bytes)");
            UtilsES.separadorLinies();
            for (String[] game : games) {
                System.out.println(game[0] + "\t\t" + game[1]);
            }
        } else {
            System.out.println("NO GAME FILE FOUND");
        }
        UtilsES.nextGame();
    }

    /**
     * View historic game data for the selected player name
     */
    public void listPlayerGames() {
        String playerName = UtilsES.getName("Enter a player name to show his/her game history...");
        int[][] playerHistory = FileUtils.getPlayerHistory(playerName);
        if (playerHistory != null) {
            UtilsES.separadorLinies();
            System.out.println("Game list for player: " + playerName.toUpperCase());
            UtilsES.separadorLinies();
            System.out.println("GAME#" + "\t" + "ROUNDS" + "\t" + "WINNER");
            UtilsES.separadorLinies();
            for (int i = 0; i < playerHistory.length; i++) {
                System.out.println(i + 1 + "\t\t" + playerHistory[i][0] + "\t\t" + Game.OUTCOMES[playerHistory[i][1]]);
            }
        } else {
            System.out.println("Game file not found for player " + playerName.toUpperCase());
        }
        UtilsES.nextGame();
    }

    /**
     * @param name       name of the player to locate in playerData
     * @param playerData array of players
     * @return returns position of player {name} in {playerData} or {-1} if it's not found
     */
    public int findPlayerPosition(String name, String[][] playerData) {
        boolean found = false;
        int i = 0;
        while (!found && i < playerData.length) {
            if (playerData[i][0].trim().equalsIgnoreCase(name)) {
                found = true;
            } else {
                i++;
            }
        }
        return found ? i : -1;
    }

    /**
     * @param name       name of the player to record in playerData
     * @param playerData array of player names and score
     * @return position where {name} was recorded or {-1} if array was full and recording failed
     */
    public int recordNewPlayer(String name, String[][] playerData) {
        boolean espaiBuit = false;
        int i = 0;
        while (!espaiBuit && i < playerData.length) {
            if (playerData[i][0].trim().equalsIgnoreCase("")) {
                espaiBuit = true;
                playerData[i][0] = name;
                playerData[i][1] = PUNTUACIO_INICIAL;
            } else {
                i++;
            }
        }
        return espaiBuit ? i : -1;
    }

    /**
     * @return option choosen by user
     */
    public int getUserMenuOption() {
        UtilsES.showTitle(GAME_TITLE);
        return UtilsES.getInteger(PLAYER_OPTIONS, "Escull una opcio valida. (%d o %d)%n", 0, 4);
    }

    /**
     * @return type of game to be played
     */
    public int chooseGameMenu() {
        return UtilsES.getInteger("""
                **********************************************************************
                | 0. ROCK PAPER SCISSORS \t|\t1. ROCK PAPER SCISSORS LIZARD SPOCK |
                **********************************************************************
                """, "Wrong option, try again!", 0, 1);
    }

    public int isValidPlayer(String nom, String[][] dadesJugadors) {
        int posicio = findPlayerPosition(nom, dadesJugadors);
        if (posicio == -1) {
            posicio = recordNewPlayer(nom, dadesJugadors);
        }
        if (posicio == -1) {
            UtilsES.showErrorMessage(NO_MORE_SPACE_ERROR);
        }
        return posicio;
    }

}
