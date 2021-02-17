package gred.nucleus.dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class CropFromCoodinateDialog extends JFrame implements ActionListener {
	private static final long       serialVersionUID   = 1L;
	static private final String     newline            = "\n";
	private final        JButton    _jButtonStart      = new JButton("Start");
	private final        JButton    _jButtonQuit       = new JButton("Quit");
	private final        Container  _container;
	private final        JLabel     _jLabelLink;
	private              JLabel     _jLabelImage;
	private              JLabel     _jLabelCoord;
	private final        JTextField _jLinkFileChooser  = new JTextField();
	private final        JTextField _jImageChooser     = new JTextField();
	private final        JTextField _jCoordFileChooser = new JTextField();
	private              boolean    _start             = false;
	private final        JFileChooser fc                 = new JFileChooser();
	private final        JButton      linkFileButton;
	private              JButton      imageButton;
	private              JButton      coordButton;
	
	private       File   selectedInput;
	private       File   selectedOutput;
	private       File   selectedConfig;
	private final String linkChooserName  = "linkChooser";
	private final String imageChooserName = "imageChooser";
	private final String coordChooserName = "coordChooser";
	
	public CropFromCoodinateDialog() {
		this.setTitle("Autocrop NucleusJ2");
		this.setSize(500, 300);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		_container = getContentPane();
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.1};
		gridBagLayout.rowHeights = new int[]{60, 60, 60, 120};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0};
		gridBagLayout.columnWidths = new int[]{250, 250};
		_container.setLayout(gridBagLayout);


        /*/\*\
        ------------------------------ Coordinate file -----------------------------------------
        \*\/*/
		
		
		_jLabelLink = new JLabel();
		_container.add(_jLabelLink, new GridBagConstraints(0, 0, 0, 0, 0.0, 0.0,
		                                                   GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
		                                                   new Insets(10, 10, 0, 0), 0, 0));
		_jLabelLink.setText("Path to coordinate file:");
		
		_container.add(_jLinkFileChooser, new GridBagConstraints(0, 0, 0, 0, 0.0, 0.0,
		                                                         GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
		                                                         new Insets(30, 10, 0, 0), 0, 0));
		_jLinkFileChooser.setPreferredSize(new java.awt.Dimension(300, 20));
		_jLinkFileChooser.setMinimumSize(new java.awt.Dimension(300, 20));
		
		linkFileButton = new JButton("...");
		_container.add(linkFileButton, new GridBagConstraints(0, 0, 0, 0, 0.0, 0.0,
		                                                      GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
		                                                      new Insets(30, 330, 0, 0), 0, 0));
		linkFileButton.addActionListener(this);
		linkFileButton.setName(linkChooserName);


        /*/\*\
        ------------------------------ Image + coordinates -----------------------------------------
        \*\/*/


        /*
        JLabel imageFileLabel = new JLabel();
        _container.add(imageFileLabel, new GridBagConstraints(0, 1, 0, 0, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(10, 10, 0, 0), 0, 0));
        imageFileLabel.setText("Path to image:");

        _container.add(_jImageChooser, new GridBagConstraints(0, 1, 0, 0, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(30, 10, 0, 0), 0, 0));
        _jImageChooser.setPreferredSize(new java.awt.Dimension(300, 20));
        _jImageChooser.setMinimumSize(new java.awt.Dimension(300, 20));

        imageButton = new JButton("...");
        _container.add(linkFileButton, new GridBagConstraints(0, 1, 0, 0, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(30, 330, 0, 0), 0, 0));
        imageButton.addActionListener(this);
        imageButton.setName(imageChooserName);

        _jLabelCoord = new JLabel();
        _container.add(_jLabelCoord, new GridBagConstraints(0, 0, 0, 0, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(10, 10, 0, 0), 0, 0));
        _jLabelCoord.setText("Path to coordinates:");

        _container.add(_jCoordFileChooser, new GridBagConstraints(0, 0, 0, 0, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(30, 10, 0, 0), 0, 0));
        _jCoordFileChooser.setPreferredSize(new java.awt.Dimension(300, 20));
        _jCoordFileChooser.setMinimumSize(new java.awt.Dimension(300, 20));

        coordButton = new JButton("...");
        _container.add(coordButton, new GridBagConstraints(0, 0, 0, 0, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(30, 330, 0, 0), 0, 0));
        coordButton.addActionListener(this);
        coordButton.setName(coordChooserName);
        */

        /*/\*\
        ------------------------------ Buttons -----------------------------------------
        \*\/*/
		
		_container.add(_jButtonStart, new GridBagConstraints(0, 3, 0, 0, 0.0, 0.0,
		                                                     GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
		                                                     new Insets(40, 80, 0, 0), 0, 0));
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
	
	public boolean isStart() {
		return _start;
	}
	
	public String getLink() {
		return _jLinkFileChooser.getText();
	}
	
	public String getImage() {
		return _jImageChooser.getText();
	}
	
	public String getCoord() {
		return _jCoordFileChooser.getText();
	}
	
	public void actionPerformed(ActionEvent e) {
		if (((JButton) e.getSource()).getName().equals(linkChooserName)) {
			fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		}
		fc.setAcceptAllFileFilterUsed(false);
		
		if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			if (((JButton) e.getSource()).getName().equals(linkChooserName)) {
				selectedInput = fc.getSelectedFile();
				_jLinkFileChooser.setText(selectedInput.getPath());
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
	
	class StartListener implements ActionListener {
		CropFromCoodinateDialog _autocropDialog;
		
		/**
		 * @param autocropDialog
		 */
		public StartListener(CropFromCoodinateDialog autocropDialog) {
			_autocropDialog = autocropDialog;
		}
		
		public void actionPerformed(ActionEvent actionEvent) {
			_start = true;
			_autocropDialog.dispose();
		}
	}
	
	class QuitListener implements ActionListener {
		CropFromCoodinateDialog _autocropDialog;
		
		/**
		 * @param autocropDialog
		 */
		public QuitListener(CropFromCoodinateDialog autocropDialog) {
			_autocropDialog = autocropDialog;
		}
		
		public void actionPerformed(ActionEvent actionEvent) {
			_autocropDialog.dispose();
		}
	}
	
}
