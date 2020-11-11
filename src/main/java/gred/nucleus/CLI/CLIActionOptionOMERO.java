package gred.nucleus.CLI;

import ome.model.units.Conversion;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class CLIActionOptionOMERO extends CLIActionOptions {
    Option m_hostname = Option.builder("ho")
            .longOpt("hostname")
            .required()
            .type(String.class)
            .desc("Hostname of the OMERO serveur")
            .numberOfArgs(1)
            .build();
    Option m_port = Option.builder("pt")
            .longOpt("port")
            .required()
            .type(Conversion.Int.class)
            .desc("Port used by OMERO")
            .numberOfArgs(1)
            .build();
    Option m_username = Option.builder("u")
            .longOpt("username")
            .type(String.class)
            .desc("Username in OMERO")
            .numberOfArgs(1)
            .build();
    Option m_password = Option.builder("p")
            .longOpt("password")
            .type(String.class)
            .desc("Password in OMERO")
            .numberOfArgs(1)
            .build();
    Option m_group = Option.builder("g")
            .longOpt("group")
            .required()
            .type(String.class)
            .desc("Group in OMERO")
            .numberOfArgs(1)
            .build();
    Option m_omero = Option.builder("ome")
            .longOpt("omero")
            .required()
            .type(boolean.class)
            .desc("Use of NucleusJ2.0 in omero, 2 actions available :\n" +
                    " autocrop : crop wide field images\n" +
                    " segmentation : nucleus segmentation\n")
            .build();

    public CLIActionOptionOMERO(String[] args) throws Exception {
        super(args);
        this.m_options.addOption(this.m_omero);
        this.m_options.addOption(this.m_port);
        this.m_options.addOption(this.m_hostname);
        this.m_options.addOption(this.m_username);
        this.m_options.addOption(this.m_password);
        this.m_options.addOption(this.m_group);
        try {
            this.m_cmd = this.m_parser.parse(this.m_options, args);
        } catch (Exception exp) {
            System.out.println(exp.getMessage() + "\n");
            System.out.println(getHelperInfos());
            System.exit(1);

        }
    }


    public String getHelperInfos() {
        return "More details :\n" +
                "java -jar NucleusJ_2-"+NJversion+" -hOme \n" +
                "or \n"+
                "java -jar NucleusJ_2-"+NJversion+" -helpOmero \n";
    }
}