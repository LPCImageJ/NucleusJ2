package gred.nucleus.CLI;

import fr.igred.omero.Client;
import org.apache.commons.cli.Options;

public class CLIRunActionOMERO {
    /**
     * List of options
     */
    Options m_options= new Options();

    /**
     * OMERO client information
     * see fr.igred.omero.Client
     */
    Client client = new Client();


    public CLIRunActionOMERO(Options options){
        this.m_options=options;
        
    }

}
