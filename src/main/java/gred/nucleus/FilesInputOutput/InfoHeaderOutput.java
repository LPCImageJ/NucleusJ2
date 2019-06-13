package gred.nucleus.FilesInputOutput;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class InfoHeaderOutput {

    public InfoHeaderOutput(){

    }
    public String getLocalTime() {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd:HH-mm-ss").format(Calendar.getInstance().getTime());
        return timeStamp;
    }

}
