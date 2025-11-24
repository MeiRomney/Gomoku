import org.example.command.Command;
import org.example.model.GamePhase;
import org.example.model.GameState;
import org.example.service.BoardService;
import org.example.service.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {

    private GameState state;
    private TestCommand validCommand;
    private TestCommand invalidCommand;
    private TestCommand exitCommand;   // NEW
    private BoardService boardService;

    // Simple Test Command
    private static class TestCommand extends Command {
        private final String name;
        private final Pattern pattern;
        private final GamePhase phase;
        private boolean applied = false;

        TestCommand(String name, GamePhase phase, String regex) {
            super(name, regex, Set.of(phase));
            this.name = name;
            this.pattern = Pattern.compile(regex);
            this.phase = phase;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public boolean isApplicableInPhase(GamePhase p) {
            return p == phase;
        }

        @Override
        public Matcher match(String input) {
            Matcher m = pattern.matcher(input.trim());
            return m.matches() ? m : null;
        }

        @Override
        public void apply(GameState state, Matcher matcher) {
            applied = true;
        }

        public boolean wasApplied() {
            return applied;
        }
    }

    // Fake GameState
    private static class TestGameState extends GameState {
        private GamePhase phase = GamePhase.PLAYING;
        private boolean exit = false;
        private BoardService boardService;

        @Override public GamePhase getGamePhase() { return phase; }
        @Override public void setGamePhase(GamePhase p) { this.phase = p; }

        @Override public boolean isExitRequested() { return exit; }
        public void requestExit() { exit = true; }

        @Override public BoardService getBoardService() { return boardService; }
        public void setBoardService(BoardService bs) { boardService = bs; }
    }

    @BeforeEach
    public void setup() {
        state = new TestGameState();

        validCommand = new TestCommand("valid", GamePhase.PLAYING, "doit");
        invalidCommand = new TestCommand("invalid", GamePhase.EDIT, "no");

        // NEW: Exit command
        exitCommand = new TestCommand("exit", GamePhase.PLAYING, "exit") {
            @Override
            public void apply(GameState st, Matcher m) {
                ((TestGameState) st).requestExit();
            }
        };

        boardService = new BoardService(null) {
            boolean xwins = false, owins = false;
            @Override public boolean xWins() { return xwins; }
            @Override public boolean oWins() { return owins; }
        };
        ((TestGameState) state).setBoardService(boardService);
    }

    private GameService makeServiceWithCommands(List<Command> cmds, String inputScript, ByteArrayOutputStream out) {
        InputStream in = new ByteArrayInputStream(inputScript.getBytes());
        System.setIn(in);
        System.setOut(new PrintStream(out));

        return new GameService(state) {
            @Override
            public void run() {
                try {
                    var f = GameService.class.getDeclaredField("commands");
                    f.setAccessible(true);
                    f.set(this, cmds);
                } catch (Exception e) {
                    fail(e);
                }
                super.run();
            }
        };
    }

    // ---------------- TESTS ----------------

    @Test
    public void testShowCommandsPrinted() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // exit immediately
        ((TestGameState) state).requestExit();

        GameService service = makeServiceWithCommands(
                List.of(validCommand),
                "\n",
                out
        );

        service.run();
        String output = out.toString();

        assertTrue(output.contains("Available commands"));
        assertTrue(output.contains("valid"));
    }

    @Test
    public void testInvalidCommandMessage() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // invalid input, then exit command
        String inputs = "wrong\nexit\n";

        GameService service = makeServiceWithCommands(
                List.of(validCommand, exitCommand),
                inputs,
                out
        );

        service.run();
        String output = out.toString();

        assertTrue(output.contains("Invalid command"));
    }

    @Test
    public void testValidCommandGetsApplied() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        String inputs = "doit\nexit\n";

        GameService service = makeServiceWithCommands(
                List.of(validCommand, exitCommand),
                inputs,
                out
        );

        service.run();

        assertTrue(validCommand.wasApplied(), "Command should have been executed");
    }

    @Test
    public void testAutoDetectEndOfGame_XWins() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        BoardService bs = new BoardService(null) {
            @Override public boolean xWins() { return true; }
            @Override public boolean oWins() { return false; }
        };
        ((TestGameState) state).setBoardService(bs);

        String inputs = "doit\nexit\n";

        GameService svc = makeServiceWithCommands(
                List.of(validCommand, exitCommand),
                inputs,
                out
        );

        svc.run();

        assertEquals(GamePhase.GAME_ENDED, state.getGamePhase());
        assertTrue(out.toString().contains("X wins!"));
    }

    @Test
    public void testAutoDetectEndOfGame_OWins() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        BoardService bs = new BoardService(null) {
            @Override public boolean xWins() { return false; }
            @Override public boolean oWins() { return true; }
        };
        ((TestGameState) state).setBoardService(bs);

        String inputs = "doit\nexit\n";

        GameService svc = makeServiceWithCommands(
                List.of(validCommand, exitCommand),
                inputs,
                out
        );

        svc.run();

        assertEquals(GamePhase.GAME_ENDED, state.getGamePhase());
        assertTrue(out.toString().contains("O wins!"));
    }

    @Test
    public void testLoopExitsGracefully() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        ((TestGameState) state).requestExit();

        GameService service = makeServiceWithCommands(
                List.of(validCommand),
                "",
                out
        );

        service.run();

        assertTrue(out.toString().contains("Game terminated"));
    }
}
