package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SORT;

import java.util.Locale;

import io.vavr.control.Option;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.friend.FriendSortCriterion;
import seedu.address.model.friend.GameNameMatchesPredicate;
import seedu.address.model.game.GameName;

/**
 * Parses input arguments and creates a new {@code ListCommand} object.
 */
public class ListCommandParser implements Parser<ListCommand> {

    /**
     * Parses the given {@code args} in the context of {@code ListCommand}.
     *
     * @throws ParseException if the user input does not conform to the expected format.
     */
    public ListCommand parse(String args) throws ParseException {
        if (args.trim().isEmpty()) {
            return new ListCommand();
        }

        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_GAME, PREFIX_SORT);
        Option<String> gameNameValue = argMultimap.getValue(PREFIX_GAME);
        Option<String> sortValue = argMultimap.getValue(PREFIX_SORT);
        if (!argMultimap.getPreamble().isEmpty()
                || (gameNameValue.isEmpty() && sortValue.isEmpty())) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_GAME, PREFIX_SORT);
        if (gameNameValue.isDefined() && gameNameValue.get().isBlank()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        }

        FriendSortCriterion sortCriterion = null;
        if (sortValue.isDefined()) {
            sortCriterion = parseSortCriterion(sortValue.get());
        }

        if (gameNameValue.isEmpty()) {
            return new ListCommand(sortCriterion);
        }

        GameName gameName = ParserUtil.parseGameName(gameNameValue.get());
        return new ListCommand(new GameNameMatchesPredicate(gameName), sortCriterion);
    }

    private FriendSortCriterion parseSortCriterion(String value) throws ParseException {
        try {
            return FriendSortCriterion.valueOf(value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        }
    }
}
