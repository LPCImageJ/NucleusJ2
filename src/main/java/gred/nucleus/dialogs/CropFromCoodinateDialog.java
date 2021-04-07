package gred.nucleus.dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;


public class CropFromCoodinateDialog extends JFrame implements ActionListener {
	private static final long         serialVersionUID  = 1L;
	static private final String       newline           = "\n";
	private final        JButton      jButtonStart      = new JButton("Start");
	private final        JButton      jButtonQuit       = new JButton("Quit");
	private final        Container    container;
	private final        JLabel       jLabelLink;
	private final        JTextField   jLinkFileChooser  = new JTextField();
	private final        JTextField   jImageChooser     = new JTextField();
	private final        JTextField   jCoordFileChooser = new JTextField();
	private final        JFileChooser fc                = new JFileChooser();
	private final        JButton      linkFileButton;
	private final        String       linkChooserName   = "linkChooser";
	private final        String       imageChooserName  = "imageChooser";
	private final        String       coordChooserName  = "coordChooser";
	private              JLabel       jLabelImage;
	private              JLabel       jLabelCoord;
	private              boolean      start             = false;
	private              JButton      imageButton;
	private              JButton      coordButton;
	private              File         selectedInput;
	private              File         selectedOutput;
	private              File         selectedConfig;
	
	
	public CropFromCoodinateDialog() {
		this.setTitle("Autocrop NucleusJ2");
		this.setSize(500, 300);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		container = getContentPane();
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.1};
		gridBagLayout.rowHeights = new int[]{60, 60, 60, 120};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0};
		gridBagLayout.columnWidths = new int[]{250, 250};
		container.setLayout(gridBagLayout);


        /*/\*\
        ------------------------------ Coordinate file -----------------------------------------
        \*\/*/
		
		
		jLabelLink = new JLabel();
		container.add(jLabelLink, new GridBagConstraints(0, 0, 0, 0, 0.0, 0.0,
		                                                 GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
		                                                 new Insets(10, 10, 0, 0), 0, 0));
		jLabelLink.setText("Path to coordinate file:");
		
		container.add(jLinkFileChooser, new GridBagConstraints(0, 0, 0, 0, 0.0, 0.0,
		                                                       GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
		                                                       new Insets(30, 10, 0, 0), 0, 0));
		jLinkFileChooser.setPreferredSize(new java.awt.Dimension(300, 20));
		jLinkFileChooser.setMinimumSize(new java.awt.Dimension(300, 20));
		
		linkFileButton = new JButton("...");
		container.add(linkFileButton, new GridBagConstraints(0, 0, 0, 0, 0.0, 0.0,
		                                                     GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
		                                                     new Insets(30, 330, 0, 0), 0, 0));
		linkFileButton.addActionListener(this);
		linkFileButton.setName(linkChooserName);


        /*/\*\
        ------------------------------ Image + coordinates -----------------------------------------
        \*\/*/


        /*
        JLabel imageFileLabel = new JLabel();
        container.add(imageFileLabel, new GridBagConstraints(0, 1, 0, 0, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(10, 10, 0, 0), 0, 0));
        imageFileLabel.setText("Path to image:");

        container.add(jImageChooser, new GridBagConstraints(0, 1, 0, 0, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(30, 10, 0, 0), 0, 0));
        jImageChooser.setPreferredSize(new java.awt.Dimension(300, 20));
        jImageChooser.setMinimumSize(new java.awt.Dimension(300, 20));

        imageButton = new JButton("...");
        container.add(linkFileButton, new GridBagConstraints(0, 1, 0, 0, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(30, 330, 0, 0), 0, 0));
        imageButton.addActionListener(this);
        imageButton.setName(imageChooserName);

        jLabelCoord = new JLabel();
        container.add(jLabelCoord, new GridBagConstraints(0, 0, 0, 0, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(10, 10, 0, 0), 0, 0));
        jLabelCoord.setText("Path to coordinates:");

        container.add(jCoordFileChooser, new GridBagConstraints(0, 0, 0, 0, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(30, 10, 0, 0), 0, 0));
        jCoordFileChooser.setPreferredSize(new java.awt.Dimension(300, 20));
        jCoordFileChooser.setMinimumSize(new java.awt.Dimension(300, 20));

        coordButton = new JButton("...");
        container.add(coordButton, new GridBagConstraints(0, 0, 0, 0, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(30, 330, 0, 0), 0, 0));
        coordButton.addActionListener(this);
        coordButton.setName(coordChooserName);
        */

        /*/\*\
        ------------------------------ Buttons -----------------------------------------
        \*\/*/
		
		container.add(jButtonStart, new GridBagConstraints(0, 3, 0, 0, 0.0, 0.0,
		                                                   GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
		                                                   new Insets(40, 80, 0, 0), 0, 0));
		jButtonStart.setPreferredSize(new java.awt.Dimension(60, 21));
		container.add(jButtonQuit, new GridBagConstraints(0, 3, 0, 0, 0.0, 0.0,
		                                                  GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
		                                                  new Insets(40, 10, 0, 0), 0, 0));
		jButtonQuit.setPreferredSize(new java.awt.Dimension(60, 21));
		this.setVisible(true);
		
		CropFromCoodinateDialog.QuitListener quitListener = new QuitListener(this);
		jButtonQuit.addActionListener(quitListener);
		CropFromCoodinateDialog.StartListener startListener = new CropFromCoodinateDialog.StartListener(this);
		jButtonStart.addActionListener(startListener);
	}
	
	
	public boolean isStart() {
		return start;
	}
	
	
	public String getLink() {
		return jLinkFileChooser.getText();
	}
	
	
	public String getImage() {
		return jImageChooser.getText();
	}
	
	
	public String getCoord() {
		return jCoordFileChooser.getText();
	}
	
	
	public void actionPerformed(ActionEvent e) {
		if (((JButton) e.getSource()).getName().equals(linkChooserName)) {
			fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		}
		fc.setAcceptAllFileFilterUsed(false);
		
		if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			if (((JButton) e.getSource()).getName().equals(linkChooserName)) {
				selectedInput = fc.getSelectedFile();
				jLinkFileChooser.setText(selectedInput.getPath());
			}
		}
		fc.setSelectedFile(null);
	}
	
	
	static class QuitListener implements ActionListener {
		CropFromCoodinateDialog autocropDialog;
		
		
		/** @param autocropDialog  */
		public QuitListener(CropFromCoodinateDialog autocropDialog) {
			this.autocropDialog = autocropDialog;
		}
		
		
		public void actionPerformed(ActionEvent actionEvent) {
			autocropDialog.dispose();
		}
		
	}
	
	/** Classes listener to interact with the several elements of the window */
	class StartListener implements ActionListener {
		CropFromCoodinateDialog autocropDialog;
		
		
		/** @param autocropDialog  */
		public StartListener(CropFromCoodinateDialog autocropDialog) {
			this.autocropDialog = autocropDialog;
		}
		
		
		public void actionPerformed(ActionEvent actionEvent) {
			start = true;
			autocropDialog.dispose();
		}
		
	}
	
}
