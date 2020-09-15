package gred.nucleus.dialogs;

import javax.swing.*;
import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.awt.*;

public class dialogue_train extends JFrame implements ItemListener {
    private Container _container;
    private JCheckBox addCalibBox = new JCheckBox();
    private JLabel calibx = new JLabel();
    private JButton workdir = new JButton("Output Directory");


    public dialogue_train() {
    run();
    }

    public void run() {


        this.setTitle("Chromocenters segmentation pipeline (Batch)");
        this.setSize(500, 500);
        this.setLocationRelativeTo(null);
        _container = getContentPane();
        _container.setLayout(new GridBagLayout());
        JLabel chooseLabel = new JLabel("Choose Download Folder :");
        JLabel folderLabel = new JLabel("Folder :");
        JTextField folderTextField = new JTextField();
        JButton makeNewFolderButton = new JButton("Make New Folder");
        JButton okButton = new JButton("Ok");
        JButton cancelButton = new JButton("Cancel");
        okButton.setPreferredSize(cancelButton.getPreferredSize());
        okButton.setMinimumSize(cancelButton.getMinimumSize());


        JLabel intro = new JLabel();
        intro.setText("Work directory and Raw data choice : ");
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = gbc.gridy = 0;
        gbc.gridx = gbc.gridy = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.LINE_START;
        //gbc.insets = new Insets(10, 15, 0, 0);
        _container.add(intro,gbc);


        JTextPane jTextPane = new JTextPane();
        jTextPane.setText("The Raw Data directory must contain 2 subdirectories:"
                + "\n1.for raw nuclei images, named RawDataNucleus. "
                + "\n2.for segmented nuclei images, named SegmentedDataNucleus."
                + "\nPlease keep the same file name during the image processing.");
        jTextPane.setEditable(false);
        gbc.gridx =0; gbc.gridy = 1;
        _container.add(jTextPane,gbc);

        JLabel calibLabel = new JLabel();
        calibLabel.setText("Calibration:");
        gbc.gridx =0 ;gbc.gridy = 2;
        _container.add(calibLabel,gbc);

        gbc.gridx =1 ;gbc.gridy = 2;
        gbc.insets = new Insets(0, 100, 0,0 );
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        addCalibBox.setSelected(false);

        addCalibBox.addItemListener(this);

        _container.add(addCalibBox,gbc);


        calibx.setText("X :");
        gbc.gridx =0 ;gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 0,0 );
        calibx.setVisible(false);
        _container.add(calibx,gbc);


        gbc.gridx =0 ;gbc.gridy = 5;
        _container.add(workdir,gbc);






        this.setVisible(true);



    }

    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == addCalibBox) {
            if ((addCalibBox.isSelected())) {
                calibx.setVisible(true);
                //this.validate();
                //this.repaint();
               // invalidate();
               // validate();

            } else {
                calibx.setVisible(false);
                //validate();

               // repaint();
            }


        }
    }
}

