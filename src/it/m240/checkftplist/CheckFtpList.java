import java.util.ArrayList;
import org.apache.commons.cli.*;
import org.apache.commons.net.*;

class CheckFtpList {
    public static final String PROGRAM_NAME = "cckftplst";
    public static final String VERSION = "1.0.dev1";

    public static void main(String[] args) {
        // manage the command line
        Options options = new Options();

        // help option
        Option opt_help = OptionBuilder.withLongOpt("help")
            .withDescription("print this help and exit")
            .create()
        ;
        Option opt_version = OptionBuilder.withLongOpt("version")
            .withDescription("print version and exit")
            .create()
        ;

        // define options: host, port, user, pwd, directory
        Option opt_host = OptionBuilder.withArgName("host")
            .withLongOpt("host")
            .isRequired()
            .hasArg()
            .withDescription("FTP host name")
            .create("h")
        ;

        Option opt_port = OptionBuilder.withArgName("port")
            .withLongOpt("port")
            .hasArg()
            .withType(Number.class)
            .withDescription("FTP port, default=21")
            .create()
        ;

        Option opt_user = OptionBuilder.withArgName("username")
            .withLongOpt("user")
            .isRequired()
            .hasArg()
            .withDescription("your username")
            .create("u")
        ;

        Option opt_pwd = OptionBuilder.withArgName("pwd")
            .withLongOpt("password")
            .isRequired()
            .hasArg()
            .withDescription("your password")
            .create("p")
        ;

        Option opt_remote_dir = OptionBuilder.withArgName("remote dir")
            .withLongOpt("directory")
            .hasArg()
            .withDescription("the remote directory to scan, default='/'")
            .create("d")
        ;

        // list of filenames to check
        String opt_msg = "the list of files to search separated by a comma.";
        Option opt_filenames = OptionBuilder.withArgName("file0 file1 ...")
            .withLongOpt("filelist")
            .isRequired()
            .hasArgs()
            .withDescription(opt_msg)
            .create("f")
        ;

        // add options
        options.addOption(opt_help);
        options.addOption(opt_version);
        options.addOption(opt_host);
        options.addOption(opt_port);
        options.addOption(opt_user);
        options.addOption(opt_pwd);
        options.addOption(opt_remote_dir);
        options.addOption(opt_filenames);

        // create the parser
        CommandLineParser parser = new DefaultParser();
        CommandLine line = null;
        try {
            line = parser.parse(options, args);
        }
        catch(ParseException exc) {
            print_error_help_and_exit(options, exc.getMessage());
        }

        /* check the command line */
        // print help and exit, print version and exit
        if (line.hasOption("help")) {
            print_help_and_exit(options);
        }
        else if (line.hasOption("version")) {
            String msg = "%s version: %s";
            msg = String.format(msg, PROGRAM_NAME, VERSION);
            System.out.println(msg);
            System.exit(0);
        }

        /* retrieving values */
        // set defaults
        int port = 21;
        if (line.hasOption("port")) {
            try {
                port = Integer.parseInt(line.getOptionValue("port"));
            } catch (NumberFormatException exc) {
                String msg = "port must be an integer";
                print_error_help_and_exit(options, msg);
            }
        }

        String remote_dir = null;
        if (line.hasOption("directory")) {
            remote_dir = line.getOptionValue("directory");
        }

        // required
        String host = line.getOptionValue("host");
        String user = line.getOptionValue("user");
        String pwd = line.getOptionValue("password");
        String[] filelist = line.getOptionValues("filelist");

        System.out.println("Checking ftp list ...");

        //System.out.println(String.format("pwd: %s", pwd));

        // connect to ftp
        FtpListGetter ftp_getter = new FtpListGetter();
        ftp_getter.set_host(host);
        ftp_getter.set_port(port);
        ftp_getter.set_password(pwd);
        ftp_getter.set_remote_dir(remote_dir);
        if (!ftp_getter.connect()) {
            System.exit(1);
        }


        // get working dir list
        ArrayList<String> file_list = ftp_getter.get_file_list();

        String entry = "filename: %s";
        for (String item : file_list) {
            System.out.println(String.format(entry, item));
        }


        // FIXME check each file in filelist against remote dir list
    }

    /**
      * Print the error message, than the help and exit with
      * status code = 1
      */
    private static void print_error_help_and_exit(Options opts, String msg) {
        System.err.println(msg);
        System.out.println();
        print_help_and_exit(opts, 1)    ;
    }

    /**
      * Print the command line help and exit
      */
    private static void print_help_and_exit(Options opts, int exit_code) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(PROGRAM_NAME, opts);
        System.exit(exit_code);
    }

    /**
      * Overloading: default exit_code = 0
      */
    private static void print_help_and_exit(Options opts) {
        print_help_and_exit(opts, 0);
    }
}
