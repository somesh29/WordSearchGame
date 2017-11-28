package enums;

/**
 * Waiting - if max users have not reached
 * Complted - if winners has been found
 * in_play- means game in progress
 * ready - max players have reached but game yet to be staretd by admin
 */
public enum GameState {
    WAITING, COMPLETED, IN_PLAY, READY
}
