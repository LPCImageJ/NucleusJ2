package gred.nucleus.dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;


public class SegmentationDialog extends JFrame implements ActionListener, ItemListener {
	
	private static final long                     serialVersionUID          = 1L;
	private static final String                   newline                   = "\n";
	private final        JButton                  jButtonStart              = new JButton("Start");
	private final        JButton                  jButtonQuit               = new JButton("Quit");
	private final        JButton                  jButtonConfig             = new JButton("Config");
	private final        Container                container;
	private final        JFormattedTextField      jTextFieldXCalibration    = new JFormattedTextField(Number.class);
	private final        JFormattedTextField      jTextFieldYCalibration    = new JFormattedTextField(Number.class);
	private final        JFormattedTextField      jTextFieldZCalibration    = new JFormattedTextField(Number.class);
	private final        JFormattedTextField      jTextFieldMax             = new JFormattedTextField(Number.class);
	private final        JFormattedTextField      jTextFieldMin             = new JFormattedTextField(Number.class);
	private final        JTextField               jTextFieldUnit            = new JTextField();
	private final        JLabel                   jLabelOutput;
	private final        JLabel                   jLabelConfig;
	private final        JLabel                   jLabelInput;
	private final        ButtonGroup              buttonGroupChoiceAnalysis = new ButtonGroup();
	private final        JTextField               jInputFileChooser         = new JTextField();
	private final        JTextField               jOutputFileChooser        = new JTextField();
	private final        JTextField               jConfigFileChooser        = new JTextField();
	private final        JFileChooser             fc                        = new JFileChooser();
	private final        JCheckBox                addConfigBox              = new JCheckBox();
	private final        JButton                  sourceButton;
	private final        JButton                  destButton;
	private final        JLabel                   defConf                   = new JLabel("Default configuration");
	private final        SegmentationConfigDialog segmentationConfigFileDialog;
	private final        ButtonGroup              buttonGroup               = new ButtonGroup();
	private final        JRadioButton             rdoDefault                = new JRadioButton();
	private final        JRadioButton             rdoAddConfigFile          = new JRadioButton();
	private final        JRadioButton             rdoAddConfigDialog        = new JRadioButton();
	private final        String                   inputChooserName          = "inputChooser";
	private final        String                   outputChooserName         = "outputChooser";
	private final        String                   configChooserName         = "configChooser";
	private              boolean                  start                     = false;
	private              JButton                  confButton;
	private              int                      configMode                = 0;
	private              boolean                  manualConfig              = false;
	private              File                     selectedInput;
	private              File                     selectedOutput;
	private              File                     selectedConfig;
	
	
	/** Architecture of the graphical windows */
	public SegmentationDialog() {
		this.setTitle("Segmentation NucleusJ2");
		this.setSize(500, 300);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		segmentationConfigFileDialog = new SegmentationConfigDialog(this);
		segmentationConfigFileDialog.setVisible(false);
		container = getContentPane();
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.1};
		gridBagLayout.rowHeights = new int[]{60, 60, 60, 120};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0};
		gridBagLayout.columnWidths = new int[]{250, 250};
		container.setLayout(gridBagLayout);
		
		jLabelInput = new JLabel();
		container.add(jLabelInput, new GridBagConstraints(0, 0, 0, 0, 0.0, 0.0,
		                                                  GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
		                                                  new Insets(10, 10, 0, 0), 0, 0));
		jLabelInput.setText("Input directory:");
		
		container.add(jInputFileChooser, new GridBagConstraints(0, 0, 0, 0, 0.0, 0.0,
		                                                        GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
		                                                        new Insets(30, 10, 0, 0), 0, 0));
		jInputFileChooser.setPreferredSize(new java.awt.Dimension(300, 20));
		jInputFileChooser.setMinimumSize(new java.awt.Dimension(300, 20));
		
		sourceButton = new JButton("...");
		container.add(sourceButton, new GridBagConstraints(0, 0, 0, 0, 0.0, 0.0,
		                                                   GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
		                                                   new Insets(30, 330, 0, 0), 0, 0));
		sourceButton.addActionListener(this);
		sourceButton.setName(inputChooserName);
		
		jLabelOutput = new JLabel();
		container.add(jLabelOutput, new GridBagConstraints(0, 1, 0, 0, 0.0, 0.0,
		                                                   GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
		                                                   new Insets(10, 10, 0, 0), 0, 0));
		jLabelOutput.setText("Output directory:");
		container.add(jOutputFileChooser, new GridBagConstraints(0,
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
		jOutputFileChooser.setPreferredSize(new java.awt.Dimension(300, 20));
		jOutputFileChooser.setMinimumSize(new java.awt.Dimension(300, 20));
		
		destButton = new JButton("...");
		container.add(destButton, new GridBagConstraints(0, 1, 0, 0, 0.0, 0.0,
		                                                 GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
		                                                 new Insets(30, 330, 0, 0), 0, 0));
		destButton.addActionListener(this);
		destButton.setName(outputChooserName);
		
		jLabelConfig = new JLabel();
		container.add(jLabelConfig, new GridBagConstraints(0, 2, 0, 0, 0.0, 0.0,
		                                                   GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
		                                                   new Insets(10, 10, 0, 0), 0, 0));
		jLabelConfig.setText("Config file (optional):");
		
		buttonGroup.add(rdoDefault);
		rdoDefault.setSelected(true);
		rdoDefault.addItemListener(this);
		itemStateChanged(new ItemEvent(rdoDefault, 0, rdoDefault, ItemEvent.SELECTED));
		container.add(rdoDefault, new GridBagConstraints(0, 2, 0, 0, 0.0, 0.0,
		                                                 GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
		                                                 new Insets(10, 200, 0, 0), 0, 0));
		buttonGroup.add(rdoAddConfigDialog);
		rdoAddConfigDialog.addItemListener(this);
		container.add(rdoAddConfigDialog, new GridBagConstraints(0, 2, 0, 0, 0.0, 0.0,
		                                                         GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
		                                                         new Insets(10, 230, 0, 0), 0, 0));
		buttonGroup.add(rdoAddConfigFile);
		rdoAddConfigFile.addItemListener(this);
		container.add(rdoAddConfigFile, new GridBagConstraints(0, 2, 0, 0, 0.0, 0.0,
		                                                       GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
		                                                       new Insets(10, 260, 0, 0), 0, 0));
		
		container.add(jButtonStart, new GridBagConstraints(0, 3, 0, 0, 0.0, 0.0,
		                                                   GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
		                                                   new Insets(40, 80, 0, 0), 0, 0));
		jButtonStart.setPreferredSize(new java.awt.Dimension(60, 21));
		container.add(jButtonQuit, new GridBagConstraints(0, 3, 0, 0, 0.0, 0.0,
		                                                  GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
		                                                  new Insets(40, 10, 0, 0), 0, 0));
		jButtonQuit.setPreferredSize(new java.awt.Dimension(60, 21));
		this.setVisible(true);
		
		SegmentationDialog.QuitListener quitListener = new QuitListener(this);
		jButtonQuit.addActionListener(quitListener);
		SegmentationDialog.StartListener startListener = new SegmentationDialog.StartListener(this);
		jButtonStart.addActionListener(startListener);
		SegmentationDialog.ConfigListener configListener = new SegmentationDialog.ConfigListener(this);
		jButtonConfig.addActionListener(configListener);
	}
	
	
	public boolean isStart() {
		return start;
	}
	
	
	public String getInput() {
		return jInputFileChooser.getText();
	}
	
	
	public String getOutput() {
		return jOutputFileChooser.getText();
	}
	
	
	public String getConfig() {
		return jConfigFileChooser.getText();
	}
	
	
	public int getConfigMode() {
		return configMode;
	}
	
	
	public SegmentationConfigDialog getSegmentationConfigFileDialog() {
		return segmentationConfigFileDialog;
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
					jInputFileChooser.setText(selectedInput.getPath());
					break;
				case outputChooserName:
					selectedOutput = fc.getSelectedFile();
					jOutputFileChooser.setText(selectedOutput.getPath());
					break;
				case configChooserName:
					selectedConfig = fc.getSelectedFile();
					jConfigFileChooser.setText(selectedConfig.getPath());
					break;
			}
		}
		fc.setSelectedFile(null);
	}
	
	
	@Override
	public void itemStateChanged(ItemEvent e) {
		if (configMode == 0) {
			container.remove(defConf);
		} else if (configMode == 1) {
			container.remove(jButtonConfig);
			if (segmentationConfigFileDialog.isVisible()) {
				segmentationConfigFileDialog.setVisible(false);
			}
		} else if (configMode == 2) {
			container.remove(jConfigFileChooser);
			container.remove(confButton);
		}
		
		Object source = e.getSource();
		if (source == rdoDefault) {
			container.add(defConf, new GridBagConstraints(0, 2, 0, 0, 0.0, 0.0,
			                                              GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
			                                              new Insets(40, 10, 0, 0), 0, 0));
			configMode = 0;
			manualConfig = false;
		} else if (source == rdoAddConfigDialog) {
			container.add(jButtonConfig, new GridBagConstraints(0, 2, 0, 0, 0.0, 0.0,
			                                                    GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
			                                                    new Insets(40, 10, 0, 0), 0, 0));
			manualConfig = true;
			configMode = 1;
		} else if (source == rdoAddConfigFile) {
			container.add(jConfigFileChooser, new GridBagConstraints(0,
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
			jConfigFileChooser.setPreferredSize(new java.awt.Dimension(300, 20));
			jConfigFileChooser.setMinimumSize(new java.awt.Dimension(300, 20));
			
			confButton = new JButton("...");
			container.add(confButton, new GridBagConstraints(0, 2, 0, 0, 0.0, 0.0,
			                                                 GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
			                                                 new Insets(40, 330, 0, 0), 0, 0));
			confButton.addActionListener(this);
			confButton.setName(configChooserName);
			configMode = 2;
			manualConfig = false;
		}
		validate();
		repaint();
	}
	
	
	/**
	 *
	 */
	static class QuitListener implements ActionListener {
		SegmentationDialog segmentationDialog;
		
		
		/** @param segmentationDialog  */
		public QuitListener(SegmentationDialog segmentationDialog) {
			this.segmentationDialog = segmentationDialog;
		}
		
		
		public void actionPerformed(ActionEvent actionEvent) {
			segmentationDialog.dispose();
		}
		
	}
	
	/** Classes listener to interact with the several elements of the window */
	class StartListener implements ActionListener {
		SegmentationDialog segmentationDialog;
		
		
		/** @param autocropDialog  */
		public StartListener(SegmentationDialog autocropDialog) {
			segmentationDialog = autocropDialog;
		}
		
		
		public void actionPerformed(ActionEvent actionEvent) {
			start = true;
			segmentationDialog.dispose();
		}
		
	}
	
	class ConfigListener implements ActionListener {
		SegmentationDialog segmentationDialog;
		
		
		/** @param segmentationDialog  */
		public ConfigListener(SegmentationDialog segmentationDialog) {
			this.segmentationDialog = segmentationDialog;
		}
		
		
		public void actionPerformed(ActionEvent actionEvent) {
			segmentationConfigFileDialog.setVisible(true);
		}
		
	}
	
}
