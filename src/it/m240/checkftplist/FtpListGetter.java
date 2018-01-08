import java.io.IOException;
import java.util.ArrayList;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPFile;

class FtpListGetter {
    private String host = null;
    private String user = null;
    private String password = null;
    private int port = 0;
    private String remote_dir = "/";
    private boolean passive_mode = false;
    private FTPClient client = null;

    public void FtpListGetter() {}

    /**
      * Connect and loging
      */
    public boolean connect() {
        String msg = null;

        // instantiate new client from apache.commons.net
        this.client = new FTPClient();

        try {
            // connect
            if (this.port > 0) {
                this.client.connect(this.host, this.port);
            }
            else {
                this.client.connect(this.host);
            }

            // check server response
            int reply_code = this.client.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply_code)) {
                this.client.disconnect();
                msg = "Error attempting to connect to %s:%d";
                msg = String.format(msg, this.host, this.port);
                System.err.println(msg);
                return false;
            }

            // login
            boolean success = this.client.login(this.user, this.password);
            if (!success) {
                this.client.logout();
                msg = "Error while attempting to login. Please check ";
                msg += "username and password.";
                System.err.println(msg);
                return false;
            }

            // active or passive mode
            if (this.passive_mode) {
                this.client.enterLocalPassiveMode();
            }
            else {
                this.client.enterLocalActiveMode();
            }
        }
        catch (IOException exc) {
            // disconnect if necessary
            if (this.client.isConnected()) {
                try {
                    this.client.disconnect();
                }
                catch (IOException e) {
                    // do nothing
                }
            }

            // generic error message
            msg = "Error while attempting to connect to FTP server. ";
            msg += "Details:%s";
            String newline = System.getProperty("line.separator");
            msg = String.format(msg, newline);
            System.err.println(msg);
            exc.printStackTrace();
            return false;
        }

        // everyhing's ok
        return true;
    }

    /**
      * Return the remote listing
      */
    public ArrayList<String> get_file_list() {
        ArrayList<String> file_list = new ArrayList<>();

        // get the listing
        try {
            for (FTPFile f : this.client.mlistDir(this.get_remote_dir())) {
                file_list.add(f.getRawListing());
            }
        } catch (IOException exc) {
            String msg = "Error while getting file listing. Details:%s";
            String newline = System.getProperty("line.separator");
            msg = String.format(msg, newline);
            System.err.println(msg);
            exc.printStackTrace();
            return null;
        }

        return file_list;
    }


    /** getters and setters **/
    public void set_host(String host) {
        this.host = host;
    }
    public String get_host() {
        return this.host;
    }

    public void set_port(int port) {
        this.port = port;
    }
    public int get_port() {
        if (this.port > 0) {
            return this.port;
        }

        return 21;
    }

    public void set_user(String user) {
        this.user = user;
    }
    public String get_user() {
        return this.user;
    }

    public void set_password(String pwd) {
        this.password = pwd;
    }
    public String get_password() {
        return this.password;
    }

    public void set_remote_dir(String remote_dir) {
        this.remote_dir = remote_dir;
    }
    public String get_remote_dir() {
        if (this.remote_dir != null) {
            return this.remote_dir;
        }

        // return the default
        return "/";
    }
}
