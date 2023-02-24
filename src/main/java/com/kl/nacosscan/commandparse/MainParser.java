package com.kl.nacosscan.commandparse;

import edu.sysu.pmglab.commandParser.CommandGroup;
import edu.sysu.pmglab.commandParser.CommandOption;
import edu.sysu.pmglab.commandParser.CommandOptions;
import edu.sysu.pmglab.commandParser.CommandParser;
import edu.sysu.pmglab.commandParser.usage.DefaultStyleUsage;
import edu.sysu.pmglab.container.File;
import edu.sysu.pmglab.commandParser.types.*;

import java.io.IOException;
import java.util.*;

import static edu.sysu.pmglab.commandParser.CommandRule.*;
import static edu.sysu.pmglab.commandParser.CommandItem.*;

public class MainParser {
    /**
     * build by: CommandParser-1.1
     * time: 2023-02-24 10:18:47
     */
    private static final CommandParser PARSER = new CommandParser(false);

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                     Parse parameters and initialize variables
     * After calling parser.parse($args) to parse the parameters, the program will return an instance of CommandOptions.
     * CommandOptions has the following three API methods:
     * options.isPassedIn($commandName)          : Whether the command item is passed in (or captured) or not.
     * options.get($commandName)                 : Get the converted value of the passed parameter, please note that the
     *                                             type of the returned value is Object, which needs to be formatted by
     *                                             users.
     * options.getMatchedParameter($commandName) : Get the original string parameter of this command item.
     *
     * CommandOption is a wrapper class for parsing options, it has three properties (isPassedIn, value, matchedParameter),
     * which corresponding to the results of the above three method calls. CommandOption automatically generates variable
     * names with the name of the main command item, and uses the correct format type as a paradigm, and thus no additional
     * format conversion is required by users.
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private final CommandOptions options;
    public final CommandOption<?> help;
    public final CommandOption<String> url;
    public final CommandOption<String> accessToken;
    public final CommandOption<String> username;
    public final CommandOption<String> password;
    public final CommandOption<File> output;

    public MainParser(String... args) {
        this.options = PARSER.parse(args);
        this.help = new CommandOption<>("--help", this.options);
        this.url = new CommandOption<>("-u", this.options);
        this.accessToken = new CommandOption<>("-at", this.options);
        this.username = new CommandOption<>("-user", this.options);
        this.password = new CommandOption<>("-pass", this.options);
        this.output = new CommandOption<>("-o", this.options);
    }

    public static MainParser parse(String... args) {
        return new MainParser(args);
    }

    public static MainParser parse(File argsFile) throws IOException {
        return new MainParser(CommandParser.readFromFile(argsFile));
    }

    /**
     * Get CommandParser
     */
    public static CommandParser getParser() {
        return PARSER;
    }

    /**
     * Get the usage of CommandParser
     */
    public static String usage() {
        return PARSER.toString();
    }

    /**
     * Get CommandOptions
     */
    public CommandOptions getOptions() {
        return this.options;
    }

    static {
        /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
         *                                          Initialize Command Parser
         * program name    : Program name shown in the User Guide.
         *                   default: <main class>
         * offset          : When the input parameter list has mandatory fields, the 'offset' can be used to skip these
         *                   fields.
         *                   e.g., when offset=2, "bgzip compress --input ..." will start parsing from '--input ...'
         *                   default: 0
         * debug           : In debug mode, the commandParser's work log will be printed to the terminal and the stack
         *                   ERROR will be output in detail to help developers troubleshoot errors. In addition,
         *                   command items marked with 'DEBUG' will also be parsed.
         *                   Note that in non-debug mode, command items marked with 'DEBUG' are treated as regular
         *                   parameter values, but not parameter keys. Therefore, the parsing results may be different
         *                   in different modes.
         *                   default: false
         * usingAt         : For parameters starting with @, the program will recognize the content after it as a file
         *                   (i.e., @<file>), and these parameters will be replaced by the text inside the file.
         *                   default: true
         * max matched num : Control the maximum number of the matched command items. The remaining parameters exceeding
         *                   this number will be regarded as the parameters of the last matched command item.
         *                   default: -1 (means no limitation)
         * usage style     : User Guide in Unix-style. The parameters of the 'DefaultStyleUsage' are used to assign the
         *                   display style of the User Guide. The IUsage interface can be inherited to implement
         *                   customized styles.
         * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
        PARSER.setProgramName("NacosScanner");
        PARSER.offset(0);
        PARSER.debug(true);
        PARSER.usingAt(true);
        PARSER.setMaxMatchedNum(-1);
        PARSER.setAutoHelp(false);
        PARSER.setUsageStyle(DefaultStyleUsage.UNIX_TYPE_2);


        /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
         *                                              Add Command Items
         * CommandParser organizes multiple command items by "groups". Command items of the same group have the same
         * purpose (e.g. input, output, functional, complementary) or other customized types.
         * commandParser
         *    -- commandGroup 1
         *       -- commandItem 1
         *       -- commandItem 2
         *       -- ...
         *    -- commandGroup 2
         *       -- commandItem 1
         *       -- commandItem 2
         *       -- ...
         *
         * First, use the 'parser.addCommandGroup($GroupName)' statement to create a command group named $GroupName.
         * Next, use the 'parser.register' statement to add command item(s) to the most recently registered command
         * group. We can also use the returned value of the addCommandGroup to add the command item(s) to a specified
         * command group precisely.
         *
         * group.register(IType type, String... commandNames)
         * type         : Type of the parsed value of the current command item. CommandParser sets 10 basic types of
         *                parameters, including NONE, BOOLEAN, BYTE, SHORT, INTEGER, LONG, FLOAT, DOUBLE, STRING, FILE.
         *                On the basis of these basic types, commandParser has deduced other 16 new types.
         * commandNames : The command name of the corresponding command item. The first name is set as the main name of
         *                the command item, and the subsequent names are used as alias names.
         *
         * The returned value of group.register or parser.register is the command item itself, so users can use the
         * chain call to set the property of the command. For example:
         * group.register(FILE.VALUE, "--build", "-b")
         *      .arity(1)
         *      .addOptions(REQUEST)
         *      .defaultTo("./example/assoc.hg19.vcf.gz")
         *      .validateWith(FILE.validateWith(true, true, true));
         * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
        CommandGroup group001 = PARSER.addCommandGroup("Options");
        group001.register(IType.NONE, "--help", "-help", "-h")
                .addOptions(HELP, HIDDEN);
        group001.register(STRING.VALUE, "-u", "--url", "--target")
                .addOptions(REQUEST)
                .setDescription("目标地址");


        CommandGroup group002 = PARSER.addCommandGroup("Authorization");
        group002.register(STRING.VALUE, "-at", "--access-token")
                .setDescription("Nacos 授权AccessToken");
        group002.register(STRING.VALUE, "-user", "--user")
                .setDescription("Nacos 登录用户名");
        group002.register(STRING.VALUE, "-pass", "--password")
                .setDescription("Nacos 登录密码");


        CommandGroup group003 = PARSER.addCommandGroup("output");
        group003.register(FILE.VALUE, "-o", "--output")
                .defaultTo("./result")
                .validateWith(FILE.validateWith(false, false, true))
                .setDescription("输出路径");


        /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
         *                                               Add Command Rules
         * parser.addRule(String ruleType, int conditionalValue, String... commands)
         * Add an inter-command item rule with a quantity constraint.
         * ruleType         : AT_MOST, AT_LEAST, EQUAL or MUTUAL_EXCLUSION
         * conditionalValue : Number of constraints
         * commands         : The command name used to apply this rule can be either the main command name or an
         *                    alias name. The number of command names added must be greater than 1.
         *
         * parser.addRule(String ruleType, String... commands)
         * Add an inter-command item rule with a dependency constraint.
         * ruleType : SYMBIOSIS, PRECONDITION
         * commands : The command name used to apply this rule can be either the main command name or an
         *            alias name. The number of command names added must be greater than 1.
         *
         * Assume that the command item p1,p2,...pn are constrained, si = 1 means the parameter pi is passed in, k means
         * conditionalValue. The examples of the above rules are as following:
         * AT_MOST          : s1 + s2 + ... sn <= k
         *                    {s1, s2, ..., sn} can be specified with a maximum of k items.
         * AT_LEAST         : s1 + s2 + ... sn >= k
         *                    {s1, s2, ..., sn} should be specified with at least k items.
         * EQUAL            : s1 + s2 + ... sn == k
         *                    {s1, s2, ..., sn} should be specified with k items.
         * MUTUAL_EXCLUSION : k * u >= s1 + s2 + ... + sk >= u
         *                    (n - k) * v >= s(k+1) + s(k+2) + ... + sn >= v
         *                    1 - u >= v and u, v in {0, 1}
         *                    {s1, s2, ..., sk} and {s(k+1), s(k+2), ..., sn} are not allowed to be used together.
         * SYMBIOSIS        : s1 == s2 == ... == sn
         *                    {s1, s2, ..., sn} should be specified concurrently or not at all.
         * PRECONDITION     : s1 >= s2 >= ... >= sn
         *                    when the i-th command item is specified, all the command items before it (i.e., index < i)
         *                    should be specified concurrently.
         * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
        PARSER.addRule(MUTUAL_EXCLUSION, 2, "-user", "-pass", "-at");
        PARSER.addRule(SYMBIOSIS, "-pass", "-user");
        PARSER.addRule(AT_LEAST, 1,"-user","-pass","-at");
    }
}