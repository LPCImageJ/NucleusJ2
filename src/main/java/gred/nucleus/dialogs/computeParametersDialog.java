package gred.nucleus.dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class computeParametersDialog extends JFrame implements ItemListener {
    private static final long serialVersionUID = 1L;
    private static JButton _jButtonWorkDirectory = new JButton("Seg Data folder");
    private JButton _jButtonStart = new JButton("Start");
    private JButton _jButtonQuit = new JButton("Quit");
    private JButton _JButtonRawData = new JButton("Raw Data folder");
    private Container _container;
    private JTextField _jTextFieldWorkDirectory  =  new JTextField();
    private JTextField _jTextFieldRawData = new JTextField();
    private JLabel _jLabelWorkDirectory;
    private JLabel _jLabelCalibration;
    private String _workDirectory;
    private String _rawDataDirectory;
    private boolean _start = false;

    private JPanel Calib ;

    private JTextPane unitRead = new JTextPane();
    private JLabel unit = new JLabel();

    private JLabel calibx = new JLabel();
    private JLabel caliby = new JLabel();
    private JLabel calibz = new JLabel();
    private JTextPane calibXRead = new JTextPane();
    private JTextPane calibYRead = new JTextPane();
    private JTextPane calibZRead = new JTextPane();


    private JCheckBox addCalibBox = new JCheckBox();

    /**
     * Constructor for segmentation dialog
     * @param args arguments
     */
    public static void main(String[] args)
    {
        ChromocenterSegmentationPipelineBatchDialog chromocenterSegmentationPipelineBatchDialog = new ChromocenterSegmentationPipelineBatchDialog();
        chromocenterSegmentationPipelineBatchDialog.setLocationRelativeTo(null);
    }


    /**
     * Architecture of the graphical windows
     *
     */

    public computeParametersDialog ()
    {
        this.setTitle("Compute morphological parameters");
        this.setSize(500, 500);
        this.setLocationRelativeTo(null);
        _container = getContentPane();
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.rowHeights = new int[] {17, 200, 124, 7,10};
        gridBagLayout.columnWidths = new int[] {236, 120, 72, 20};
        _container.setLayout (gridBagLayout);

        _jLabelWorkDirectory = new JLabel();
        _container.add
                (
                        _jLabelWorkDirectory,
                        new GridBagConstraints
                                (
                                        0, 1, 0, 0, 0.0, 0.0,
                                        GridBagConstraints.NORTHWEST,
                                        GridBagConstraints.NONE,
                                        new Insets(0, 10, 0, 0), 0, 0
                                )
                );
        _jLabelWorkDirectory.setText("Work directory and Raw data choice : ");

        JTextPane jTextPane = new JTextPane();
        jTextPane.setText("You must select 2 directories:"
                + "\n1 containing raw nuclei images. "
                + "\n2 containing segmented nuclei images."
                + "\nImages must have same file name.");
        jTextPane.setEditable(false);
        _container.add
                (
                        jTextPane,
                        new GridBagConstraints
                                (
                                        0, 1, 0, 0, 0.0, 0.0,
                                        GridBagConstraints.NORTHWEST,
                                        GridBagConstraints.NONE,
                                        new Insets(20, 20, 0, 0), 0, 0
                                )
                );

        _container.add
                (
                        _JButtonRawData,
                        new GridBagConstraints
                                (
                                        0, 1, 0, 0, 0.0, 0.0,
                                        GridBagConstraints.NORTHWEST,
                                        GridBagConstraints.NONE,
                                        new Insets(100, 10, 0, 0), 0, 0
                                )
                );
        _JButtonRawData.setPreferredSize(new java.awt.Dimension(120, 21));
        _JButtonRawData.setFont(new java.awt.Font("Albertus",2,10));

        _container.add
                (
                        _jTextFieldRawData,
                        new GridBagConstraints
                                (
                                        0, 1, 0, 0, 0.0, 0.0,
                                        GridBagConstraints.NORTHWEST,
                                        GridBagConstraints.NONE,
                                        new Insets(100, 160, 0, 0), 0, 0
                                )
                );
        _jTextFieldRawData.setPreferredSize(new java.awt.Dimension(280, 21));
        _jTextFieldRawData.setFont(new java.awt.Font("Albertus",2,10));

        _container.add
                (
                        _jButtonWorkDirectory,
                        new GridBagConstraints
                                (
                                        0, 1, 0, 0, 0.0, 0.0,
                                        GridBagConstraints.NORTHWEST,
                                        GridBagConstraints.NONE,
                                        new Insets(140, 10, 0, 0), 0, 0
                                )
                );
        _jButtonWorkDirectory.setPreferredSize(new java.awt.Dimension(120, 21));
        _jButtonWorkDirectory.setFont(new java.awt.Font("Albertus",2,10));

        _container.add
                (
                        _jTextFieldWorkDirectory,
                        new GridBagConstraints
                                (
                                        0, 1, 0, 0, 0.0, 0.0,
                                        GridBagConstraints.NORTHWEST,
                                        GridBagConstraints.NONE,
                                        new Insets(140, 160, 0, 0), 0, 0
                                )
                );
        _jTextFieldWorkDirectory.setPreferredSize(new java.awt.Dimension(280, 21));
        _jTextFieldWorkDirectory.setFont(new java.awt.Font("Albertus",2,10));

        _jLabelCalibration = new JLabel();
        _container.add
                (
                        _jLabelCalibration,
                        new GridBagConstraints
                                (
                                        0, 2, 0, 0, 0.0, 0.0,
                                        GridBagConstraints.NORTHWEST,
                                        GridBagConstraints.NONE,
                                        new Insets(20, 10, 0, 0), 0, 0
                                )
                );

        Calib= new JPanel();
        Calib.setLayout(new GridBagLayout());

        GridBagConstraints gc = new GridBagConstraints();
        gc.weightx = 2;
        gc.weighty = 5;
        gc.ipady = gc.anchor = GridBagConstraints.NORTHWEST;

        JLabel calibLabel = new JLabel("Calibration:");
        gc.gridx =0 ;gc.gridy = 0;
        calibLabel.setAlignmentX(0);
        Calib.add(calibLabel);

        gc.gridx =1 ;

        addCalibBox.setSelected(false);
        addCalibBox.addItemListener(this);
        Calib.add(addCalibBox,gc);


        _container.add(Calib,
                new GridBagConstraints(0, 2, 2, 0, 0.0, 0.0,
                        GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));




        _container.add
                (
                        _jButtonStart,
                        new GridBagConstraints
                                (
                                        0, 2, 0, 0, 0.0, 0.0,
                                        GridBagConstraints.NORTHWEST,
                                        GridBagConstraints.NONE,
                                        new Insets(190, 140, 0,0), 0, 0
                                )
                );
        _jButtonStart.setPreferredSize(new java.awt.Dimension(120, 21));
        _container.add
                (
                        _jButtonQuit,
                        new GridBagConstraints
                                (
                                        0, 2, 0, 0,  0.0, 0.0,
                                        GridBagConstraints.NORTHWEST,
                                        GridBagConstraints.NONE,
                                        new Insets(190, 10, 0, 0), 0, 0
                                )
                );
        _jButtonQuit.setPreferredSize(new java.awt.Dimension(120, 21));
        this.setVisible(true);

        computeParametersDialog.WorkDirectoryListener wdListener = new computeParametersDialog.WorkDirectoryListener();
        _jButtonWorkDirectory.addActionListener(wdListener);
        computeParametersDialog.RawDataDirectoryListener ddListener = new computeParametersDialog.RawDataDirectoryListener();
        _JButtonRawData.addActionListener(ddListener);
        computeParametersDialog.QuitListener quitListener = new computeParametersDialog.QuitListener(this);
        _jButtonQuit.addActionListener(quitListener);
        computeParametersDialog.StartListener startListener = new computeParametersDialog.StartListener(this);
        _jButtonStart.addActionListener(startListener);
    }

    public double getXCalibration()
    {
        String xCal = calibXRead.getText();
        return Double.parseDouble(xCal.replaceAll(",", "."));
    }
    public double getYCalibration()
    {
        String yCal = calibYRead.getText();
        return Double.parseDouble(yCal.replaceAll(",", "."));
    }
    public double getZCalibration()
    {
        String zCal = calibZRead.getText();
        return Double.parseDouble(zCal.replaceAll(",", "."));
    }

    public boolean getCalibrationStatus(){
        return addCalibBox.isSelected();
    }

    public String getUnit(){ return unitRead.getText(); }
    public String getWorkDirectory(){return _jTextFieldWorkDirectory.getText();}
    public String getRawDataDirectory(){return _jTextFieldRawData.getText();}
    public boolean isStart() {	return _start; }


    class StartListener implements ActionListener
    {
        computeParametersDialog _computeParametersDialog;
        /**
         *
         * @param computeParametersDialog Dialog parameters
         */
        public  StartListener (computeParametersDialog  computeParametersDialog )
        {
            _computeParametersDialog = computeParametersDialog;
        }
        /**
         *
         */
        public void actionPerformed(ActionEvent actionEvent)
        {
            if (_jTextFieldWorkDirectory.getText().isEmpty() || _jTextFieldRawData.getText().isEmpty())
                JOptionPane.showMessageDialog
                        (
                                null,
                                "You did not choose a work directory or the raw data",
                                "Error",
                                JOptionPane.ERROR_MESSAGE
                        );
            else
            {
                _start=true;
                _computeParametersDialog.dispose();
            }
        }
    }

    /**
     *
     */
    class QuitListener implements ActionListener
    {
         computeParametersDialog _computeParametersDialog ;
        /**
         *
         * @param computeParametersDialog  Dialog parameters
         */
        public  QuitListener (computeParametersDialog computeParametersDialog)
        {
            _computeParametersDialog =  computeParametersDialog;
        }
        /**
         *
         */
        public void actionPerformed(ActionEvent actionEvent)
        {
            _computeParametersDialog.dispose();
        }
    }

    /**
     *
     *
     */
    class WorkDirectoryListener implements ActionListener
    {
        /**
         *
         */
        public void actionPerformed(ActionEvent actionEvent)
        {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            JFileChooser jFileChooser = new JFileChooser();
            jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnValue = jFileChooser.showOpenDialog(getParent());
            if(returnValue == JFileChooser.APPROVE_OPTION)
            {
                @SuppressWarnings("unused")
                String run = jFileChooser.getSelectedFile().getName();
                _workDirectory = jFileChooser.getSelectedFile().getAbsolutePath();
                _jTextFieldWorkDirectory.setText(_workDirectory);
            }
            setCursor (Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }

    /**
     *
     *
     */
    class RawDataDirectoryListener implements ActionListener
    {
        /**
         *
         */
        public void actionPerformed(ActionEvent actionEvent)
        {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            JFileChooser jFileChooser = new JFileChooser();
            jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnValue = jFileChooser.showOpenDialog(getParent());
            if(returnValue == JFileChooser.APPROVE_OPTION)
            {
                @SuppressWarnings("unused")
                String run = jFileChooser.getSelectedFile().getName();
                _rawDataDirectory = jFileChooser.getSelectedFile().getAbsolutePath();
                _jTextFieldRawData.setText(_rawDataDirectory);
            }
            setCursor (Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }


    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == addCalibBox) {
            if ((addCalibBox.isSelected())) {

                GridBagConstraints gc = new GridBagConstraints();
                gc.insets = new Insets(0, 0, 5, 0);

                unit.setText("Unit :");
                gc.gridx =0 ;gc.gridy = 1;
                Calib.add(unit,gc);
                unitRead.setPreferredSize(new Dimension( 100, 20 ));
                unitRead.setText("µm");
                gc.gridx =1 ;gc.gridy = 1;
                Calib.add(unitRead,gc);
                unit.setVisible(true);
                unitRead.setVisible(true);

                calibx.setText("X :");
                gc.gridx =0 ;gc.gridy = 2;
                Calib.add(calibx,gc);
                calibXRead.setPreferredSize(new Dimension( 100, 20 ));
                calibXRead.setText("1");
                gc.gridx =1 ;gc.gridy = 2;
                Calib.add(calibXRead,gc);
                calibx.setVisible(true);
                calibXRead.setVisible(true);

                caliby.setText("Y :");
                gc.gridx =0 ;gc.gridy = 3;
                Calib.add(caliby,gc);
                calibYRead.setPreferredSize(new Dimension( 100, 20 ));
                calibYRead.setText("1");
                gc.gridx =1 ;gc.gridy = 3;
                Calib.add(calibYRead,gc);
                caliby.setVisible(true);
                calibYRead.setVisible(true);

                calibz.setText("Z :");
                gc.gridx =0 ;gc.gridy = 4;
                Calib.add(calibz,gc);
                calibZRead.setPreferredSize(new Dimension( 100, 20 ));
                calibZRead.setText("1");
                gc.gridx =1 ;gc.gridy = 4;
                Calib.add(calibZRead,gc);
                calibz.setVisible(true);
                calibZRead.setVisible(true);

                validate();
                repaint();
            }
            else {
                calibx.setVisible(false);
                caliby.setVisible(false);
                calibz.setVisible(false);
                unit.setVisible(false);

                calibXRead.setVisible(false);
                calibYRead.setVisible(false);
                calibZRead.setVisible(false);
                unitRead.setVisible(false);

                validate();
                repaint();

            }
        }
    }


}
