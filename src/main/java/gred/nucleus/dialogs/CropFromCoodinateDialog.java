package gred.nucleus.dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

public class CropFromCoodinateDialog extends JFrame implements ActionListener {
    static private String newline = "\n";
    private static final long serialVersionUID = 1L;
    private JButton _jButtonStart = new JButton("Start");
    private JButton _jButtonQuit = new JButton("Quit");
    private Container _container;
    private JLabel _jLabelInput;
    private JTextField _jInputFileChooser = new JTextField();
    private JTextField _jOutputFileChooser = new JTextField();
    private JTextField _jConfigFileChooser = new JTextField();
    private boolean _start = false;
    private JFileChooser fc = new JFileChooser();
    private JButton sourceButton;

    private File selectedInput;
    private File selectedOutput;
    private File selectedConfig;
    private String inputChooserName = "inputChooser";
    private String outputChooserName = "outputChooser";
    private String configChooserName = "configChooser";

    public CropFromCoodinateDialog(){
        this.setTitle("Autocrop NucleusJ2");
        this.setSize(500, 300);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        _container = getContentPane();
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.rowWeights = new double[] {0.0, 0.0, 0.0, 0.1};
        gridBagLayout.rowHeights = new int[] {60, 60, 60, 120};
        gridBagLayout.columnWeights = new double[] {0.0, 0.0};
        gridBagLayout.columnWidths = new int[] {250, 250};
        _container.setLayout(gridBagLayout);


        /*/\*\
        ------------------------------ Coordinate file -----------------------------------------
        \*\/*/


        _jLabelInput = new JLabel();
        _container.add(_jLabelInput, new GridBagConstraints(0, 0, 0, 0, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(10, 10, 0, 0), 0, 0));
        _jLabelInput.setText("Path to coordinate file:");

        _container.add(_jInputFileChooser, new GridBagConstraints(0, 0, 0, 0, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(30, 10, 0, 0), 0, 0));
        _jInputFileChooser.setPreferredSize(new java.awt.Dimension(300, 20));
        _jInputFileChooser.setMinimumSize(new java.awt.Dimension(300, 20));

        sourceButton = new JButton("...");
        _container.add(sourceButton, new GridBagConstraints(0, 0, 0, 0, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(30, 330, 0, 0), 0, 0));
        sourceButton.addActionListener(this);
        sourceButton.setName(inputChooserName);

        /*/\*\
        ------------------------------ Buttons -----------------------------------------
        \*\/*/

        _container.add(_jButtonStart, new GridBagConstraints(0, 3, 0, 0,0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(40, 80, 0,0), 0, 0));
        _jButtonStart.setPreferredSize(new java.awt.Dimension(60, 21));
        _container.add(_jButtonQuit, new GridBagConstraints(0, 3, 0, 0, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(40, 10, 0, 0), 0, 0));
        _jButtonQuit.setPreferredSize(new java.awt.Dimension(60, 21));
        this.setVisible(true);

        CropFromCoodinateDialog.QuitListener quitListener = new CropFromCoodinateDialog.QuitListener(this);
        _jButtonQuit.addActionListener(quitListener);
        CropFromCoodinateDialog.StartListener startListener = new CropFromCoodinateDialog.StartListener(this);
        _jButtonStart.addActionListener(startListener);
    }

    public boolean isStart() {	return _start; }
    public String getInput() { return _jInputFileChooser.getText(); }
    public String getOutput() { return _jOutputFileChooser.getText(); }
    public String getConfig() { return _jConfigFileChooser.getText(); }

    public void actionPerformed(ActionEvent e) {
        if(((JButton)e.getSource()).getName().equals(inputChooserName)){
            fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        }
        fc.setAcceptAllFileFilterUsed(false);

        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            if(((JButton)e.getSource()).getName().equals(inputChooserName)){
                selectedInput = fc.getSelectedFile();
                _jInputFileChooser.setText(selectedInput.getPath());
            }
        }
        fc.setSelectedFile(null);
    }


    /********************************************************************************************************************************************
     * 	Classes listener to interact with the several element of the window
     */
    /********************************************************************************************************************************************
     /********************************************************************************************************************************************
     /********************************************************************************************************************************************
     /********************************************************************************************************************************************/

    class StartListener implements ActionListener
    {
        CropFromCoodinateDialog _autocropDialog;
        /**
         *
         * @param autocropDialog
         */
        public  StartListener (CropFromCoodinateDialog autocropDialog) { _autocropDialog = autocropDialog; }
        public void actionPerformed(ActionEvent actionEvent) {
            _start=true;
            _autocropDialog.dispose();
        }
    }

    class QuitListener implements ActionListener
    {
        CropFromCoodinateDialog _autocropDialog;
        /**
         *
         * @param autocropDialog
         */
        public  QuitListener (CropFromCoodinateDialog autocropDialog) { _autocropDialog = autocropDialog; }
        public void actionPerformed(ActionEvent actionEvent) {
            _autocropDialog.dispose();
        }
    }

}
