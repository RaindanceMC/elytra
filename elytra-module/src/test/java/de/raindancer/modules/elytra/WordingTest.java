package de.raindancer.modules.elytra;

import de.raindancer.modules.api.WordingContract;

import java.nio.file.Path;

/** The wording rules, kept by this module. See {@code WordingContract} for what is actually checked. */
class WordingTest implements WordingContract {

    @Override
    public Path moduleSource() {
        return Path.of("src/main/java/de/raindancer/modules/elytra");
    }

    @Override
    public Path messagesFile() {
        return Path.of("src/main/resources/de/raindancer/modules/elytra/messages.yml");
    }

    @Override
    public int fewestWordingLines() {
        return 6;
    }

    @Override
    public int fewestSourceFiles() {
        return 10;
    }
}
