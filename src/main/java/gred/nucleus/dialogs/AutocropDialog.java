package gred.nucleus.dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

public class AutocropDialog extends JFrame implements ActionListener, ItemListener {
	
	private static final long         serialVersionUID    = 1L;
	static private final String       newline             = "\n";
	private final        JButton      _jButtonStart       = new JButton("Start");
	private final        JButton      _jButtonQuit        = new JButton("Quit");
	private final        JButton      _jButtonConfig      = new JButton("Config");
	private final        Container    _container;
	private final        JLabel       _jLabelOutput;
	private final        JLabel       _jLabelConfig;
	private final        JLabel       _jLabelInput;
	private final        JTextField   _jInputFileChooser  = new JTextField();
	private final        JTextField   _jOutputFileChooser = new JTextField();
	private final        JTextField   _jConfigFileChooser = new JTextField();
	private final        JFileChooser fc                  = new JFileChooser();
	private final        JCheckBox    addConfigBox        = new JCheckBox();
	private final        JButton      sourceButton;
	private final        JButton      destButton;
	private final        JLabel       defConf             = new JLabel("Default configuration");
	private final AutocropConfigDialog autocropConfigFileDialog;
	private final ButtonGroup  buttonGroup        = new ButtonGroup();
	private final JRadioButton rdoDefault         = new JRadioButton();
	private final JRadioButton rdoAddConfigFile   = new JRadioButton();
	private final JRadioButton rdoAddConfigDialog = new JRadioButton();
	private final String inputChooserName  = "inputChooser";
	private final String outputChooserName = "outputChooser";
	private final String configChooserName = "configChooser";
	private              boolean      _start              = false;
	private              JButton      confButton;
	private       int                  configMode   = 0;
	private       boolean              manualConfig = false;
	private       File   selectedInput;
	private       File   selectedOutput;
	private       File   selectedConfig;
	
	
	/**
	 * Architecture of the graphical windows
	 */
	
	public AutocropDialog() {
		this.setTitle("Autocrop NucleusJ2");
		this.setSize(500, 300);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		autocropConfigFileDialog = new AutocropConfigDialog(this);
		autocropConfigFileDialog.setVisible(false);
		_container = getContentPane();
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.1};
		gridBagLayout.rowHeights = new int[]{60, 60, 60, 120};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0};
		gridBagLayout.columnWidths = new int[]{250, 250};
		_container.setLayout(gridBagLayout);
		
		_jLabelInput = new JLabel();
		_container.add(_jLabelInput, new GridBagConstraints(0, 0, 0, 0, 0.0, 0.0,
		                                                    GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
		                                                    new Insets(10, 10, 0, 0), 0, 0));
		_jLabelInput.setText("Input directory:");
		
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
		
		_jLabelOutput = new JLabel();
		_container.add(_jLabelOutput, new GridBagConstraints(0, 1, 0, 0, 0.0, 0.0,
		                                                     GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
		                                                     new Insets(10, 10, 0, 0), 0, 0));
		_jLabelOutput.setText("Output directory:");
		_container.add(_jOutputFileChooser, new GridBagConstraints(0,
		                                                           1,
		                                                           0,
		                                                           0,
		                                                           0.0,
		                                                           0.0,
		                                                           GridBagConstraints.NORTHWEST,
		                                                           GridBagConstraints.NONE,
		                                                           new Insets(30, 10, 0, 0),
		                                                           0,
		                                                           0));
		_jOutputFileChooser.setPreferredSize(new java.awt.Dimension(300, 20));
		_jOutputFileChooser.setMinimumSize(new java.awt.Dimension(300, 20));
		
		destButton = new JButton("...");
		_container.add(destButton, new GridBagConstraints(0, 1, 0, 0, 0.0, 0.0,
		                                                  GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
		                                                  new Insets(30, 330, 0, 0), 0, 0));
		destButton.addActionListener(this);
		destButton.setName(outputChooserName);
		
		_jLabelConfig = new JLabel();
		_container.add(_jLabelConfig, new GridBagConstraints(0, 2, 0, 0, 0.0, 0.0,
		                                                     GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
		                                                     new Insets(10, 10, 0, 0), 0, 0));
		_jLabelConfig.setText("Config file (optional):");
//        _container.add(addConfigBox, new GridBagConstraints(0, 2, 0, 0, 0.0, 0.0,
//                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
//                new Insets(10, 200, 0, 0), 0, 0));
//        addConfigBox.addItemListener(this);
		
		buttonGroup.add(rdoDefault);
		rdoDefault.setSelected(true);
		rdoDefault.addItemListener(this);
		itemStateChanged(new ItemEvent(rdoDefault, 0, rdoDefault, ItemEvent.SELECTED));
		_container.add(rdoDefault, new GridBagConstraints(0, 2, 0, 0, 0.0, 0.0,
		                                                  GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
		                                                  new Insets(10, 200, 0, 0), 0, 0));
		buttonGroup.add(rdoAddConfigDialog);
		rdoAddConfigDialog.addItemListener(this);
		_container.add(rdoAddConfigDialog, new GridBagConstraints(0, 2, 0, 0, 0.0, 0.0,
		                                                          GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
		                                                          new Insets(10, 230, 0, 0), 0, 0));
		buttonGroup.add(rdoAddConfigFile);
		rdoAddConfigFile.addItemListener(this);
		_container.add(rdoAddConfigFile, new GridBagConstraints(0, 2, 0, 0, 0.0, 0.0,
		                                                        GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
		                                                        new Insets(10, 260, 0, 0), 0, 0));
		
		
		_container.add(_jButtonStart, new GridBagConstraints(0, 3, 0, 0, 0.0, 0.0,
		                                                     GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
		                                                     new Insets(40, 80, 0, 0), 0, 0));
		_jButtonStart.setPreferredSize(new java.awt.Dimension(60, 21));
		_container.add(_jButtonQuit, new GridBagConstraints(0, 3, 0, 0, 0.0, 0.0,
		                                                    GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
		                                                    new Insets(40, 10, 0, 0), 0, 0));
		_jButtonQuit.setPreferredSize(new java.awt.Dimension(60, 21));
		this.setVisible(true);
		
		AutocropDialog.QuitListener quitListener = new AutocropDialog.QuitListener(this);
		_jButtonQuit.addActionListener(quitListener);
		AutocropDialog.StartListener startListener = new AutocropDialog.StartListener(this);
		_jButtonStart.addActionListener(startListener);
		AutocropDialog.ConfigListener configListener = new AutocropDialog.ConfigListener(this);
		_jButtonConfig.addActionListener(configListener);
	}
	
	public boolean isStart() {
		return _start;
	}
	
	public String getInput() {
		return _jInputFileChooser.getText();
	}
	
	public String getOutput() {
		return _jOutputFileChooser.getText();
	}
	
	public String getConfig() {
		return _jConfigFileChooser.getText();
	}
	
	//    public boolean isConfigBoxSelected() { return addConfigBox.isSelected(); }
	public int getConfigMode() {
		return configMode;
	}
	
	public AutocropConfigDialog getAutocropConfigFileDialog() {
		return autocropConfigFileDialog;
	}
	
	public void actionPerformed(ActionEvent e) {
		switch (((JButton) e.getSource()).getName()) {
			case inputChooserName:
				fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				break;
			case outputChooserName:
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				break;
			case configChooserName:
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				break;
		}
		fc.setAcceptAllFileFilterUsed(false);
		
		if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			switch (((JButton) e.getSource()).getName()) {
				case inputChooserName:
					selectedInput = fc.getSelectedFile();
					_jInputFileChooser.setText(selectedInput.getPath());
					break;
				case outputChooserName:
					selectedOutput = fc.getSelectedFile();
					_jOutputFileChooser.setText(selectedOutput.getPath());
					break;
				case configChooserName:
					selectedConfig = fc.getSelectedFile();
					_jConfigFileChooser.setText(selectedConfig.getPath());
					break;
			}
		}
		fc.setSelectedFile(null);
	}
	
	@Override
	public void itemStateChanged(ItemEvent e) {
		if (configMode == 0) {
			_container.remove(defConf);
		} else if (configMode == 1) {
			_container.remove(_jButtonConfig);
			if (autocropConfigFileDialog.isVisible()) {
				autocropConfigFileDialog.setVisible(false);
			}
		} else if (configMode == 2) {
			_container.remove(_jConfigFileChooser);
			_container.remove(confButton);
		}
		
		Object source = e.getSource();
		if (source == rdoDefault) {
			_container.add(defConf, new GridBagConstraints(0, 2, 0, 0, 0.0, 0.0,
			                                               GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
			                                               new Insets(40, 10, 0, 0), 0, 0));
			configMode = 0;
			manualConfig = false;
		} else if (source == rdoAddConfigDialog) {
			_container.add(_jButtonConfig, new GridBagConstraints(0, 2, 0, 0, 0.0, 0.0,
			                                                      GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
			                                                      new Insets(40, 10, 0, 0), 0, 0));
			manualConfig = true;
			configMode = 1;
		} else if (source == rdoAddConfigFile) {
			_container.add(_jConfigFileChooser, new GridBagConstraints(0,
			                                                           2,
			                                                           0,
			                                                           0,
			                                                           0.0,
			                                                           0.0,
			                                                           GridBagConstraints.NORTHWEST,
			                                                           GridBagConstraints.NONE,
			                                                           new Insets(40, 10, 0, 0),
			                                                           0,
			                                                           0));
			_jConfigFileChooser.setPreferredSize(new java.awt.Dimension(300, 20));
			_jConfigFileChooser.setMinimumSize(new java.awt.Dimension(300, 20));
			
			confButton = new JButton("...");
			_container.add(confButton, new GridBagConstraints(0, 2, 0, 0, 0.0, 0.0,
			                                                  GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
			                                                  new Insets(40, 330, 0, 0), 0, 0));
			confButton.addActionListener(this);
			confButton.setName(configChooserName);
			configMode = 2;
			manualConfig = false;
		}

        /*if(addConfigBox.isSelected()){
            _container.add(_jConfigFileChooser, new GridBagConstraints(0, 2, 0, 0, 0.0, 0.0,
                    GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                    new Insets(40, 10, 0, 0), 0, 0));
            _jConfigFileChooser.setPreferredSize(new java.awt.Dimension(300, 20));
            _jConfigFileChooser.setMinimumSize(new java.awt.Dimension(300, 20));

            confButton = new JButton("...");
            _container.add(confButton, new GridBagConstraints(0, 2, 0, 0, 0.0, 0.0,
                    GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                    new Insets(40, 330, 0, 0), 0, 0));
            confButton.addActionListener(this);
            confButton.setName(configChooserName);
        } else {
            _container.remove(_jConfigFileChooser);
            _container.remove(confButton);
        }*/
		validate();
		repaint();
	}
	
	/*******************************************************************************************************************************************
	 Classes listener to interact with the several element of the window
	 */
	/********************************************************************************************************************************************
	 /********************************************************************************************************************************************
	 /********************************************************************************************************************************************
	 /********************************************************************************************************************************************/
	
	class StartListener implements ActionListener {
		AutocropDialog _autocropDialog;
		
		/**
		 * @param autocropDialog
		 */
		public StartListener(AutocropDialog autocropDialog) {
			_autocropDialog = autocropDialog;
		}
		
		public void actionPerformed(ActionEvent actionEvent) {
			_start = true;
			_autocropDialog.dispose();
			autocropConfigFileDialog.dispose();
		}
	}
	
	class QuitListener implements ActionListener {
		AutocropDialog _autocropDialog;
		
		/**
		 * @param autocropDialog
		 */
		public QuitListener(AutocropDialog autocropDialog) {
			_autocropDialog = autocropDialog;
		}
		
		public void actionPerformed(ActionEvent actionEvent) {
			_autocropDialog.dispose();
			autocropConfigFileDialog.dispose();
		}
	}
	
	class ConfigListener implements ActionListener {
		AutocropDialog _autocropDialog;
		
		/**
		 * @param autocropDialog
		 */
		public ConfigListener(AutocropDialog autocropDialog) {
			_autocropDialog = autocropDialog;
		}
		
		public void actionPerformed(ActionEvent actionEvent) {
			autocropConfigFileDialog.setVisible(true);
		}
	}
}
