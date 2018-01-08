import java.io.IOException;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

class FtpListGetter {
    private String host = null;
    private String user = null;
    private String pwd = null;
    private int port = 0;
    private String remote_dir = "/";
    private boolean passive_mode = false;
    private FTPClient client = null;

    public void FtpListGetter(String host, int port, String user, String pwd,
                              String remote_dir, boolean passive_mode) {

        this.host = host;
        this.port = port;
        this.user = user;
        this.pwd = pwd;
        this.remote_dir = remote_dir;
        this.passive_mode = passive_mode;
    }

    // overload for default args
    public void FtpListGetter(String host, String user, String pwd,
                              String remote_dir) {
        this.FtpListGetter(host, this.port, user, pwd, this.remote_dir,
                           this.passive_mode);
    }

    public String[] FtpListGetter(String host, String user, String pwd) {
        String[] list = String[];
        for (FTPFile f : 

        this.FtpListGetter(host, user, pwd, this.remote_dir);
    }

    /**
      * Connect and loging
      */
    private boolean _connect() {
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
            boolean success = this.client.login(this.user, this.pwd);
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
            exc.printStackTrace();
            return false;
        }

        // everyhing's ok
        return true;
    }

    /**
      * Return the remote listing
      */
    public get_file_list() {
        return this.client.mlistDir(this.remote_dir);
    }
}
