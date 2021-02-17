package gred.nucleus.dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;


/**
 * Class to construct graphical interface for the chromocenter analysis pipeline in batch
 *
 * @author pouletaxel
 */

public class ChromocentersAnalysisPipelineBatchDialog extends JFrame implements ItemListener {
	private static final long    serialVersionUID      = 1L;
	private final        JButton _jButtonWorkDirectory = new JButton("Output Directory");
	private final        JButton _jButtonStart         = new JButton("Start");
	private final        JButton _jButtonQuit          = new JButton("Quit");
	private final        JButton _jButtonRawData       = new JButton("Raw Data");
	
	private final Container _container;
	
	private final JFormattedTextField _jTextFieldXCalibration = new JFormattedTextField(Number.class);
	private final JFormattedTextField _jTextFieldYCalibration = new JFormattedTextField(Number.class);
	private final JFormattedTextField _jTextFieldZCalibration = new JFormattedTextField(Number.class);
	
	private final JTextField   _jTextFieldUnit           = new JTextField();
	private final JTextField   _jTextFieldWorkDirectory  = new JTextField();
	private final JTextField   _jTextFieldRawData        = new JTextField();
	private final JLabel       _jLabelWorkDirectory;
	private final ButtonGroup  _buttonGroupChoiceRhf     = new ButtonGroup();
	private final JRadioButton _jRadioButtonRhfV         = new JRadioButton("VolumeRHF");
	private final JRadioButton _jRadioButtonRhfI         = new JRadioButton("IntensityRHF");
	private final JRadioButton _jRadioButtonRhfIV        = new JRadioButton("VolumeRHF and IntensityRHF");
	private final ButtonGroup  buttonGroupChoiceAnalysis = new ButtonGroup();
	private final JRadioButton _jRadioButtonNucCc        = new JRadioButton("Nucleus and chromocenter");
	private final JRadioButton _jRadioButtonCc           = new JRadioButton("Chromocenter");
	private final JRadioButton _jRadioButtonNuc          = new JRadioButton("Nucleus");
	private final JPanel       Calib;
	private final JTextPane    unitRead                  = new JTextPane();
	private final JLabel       unit                      = new JLabel();
	private final JLabel       calibx                    = new JLabel();
	private final JLabel       caliby                    = new JLabel();
	private final JLabel       calibz                    = new JLabel();
	private final JTextPane    calibXRead                = new JTextPane();
	private final JTextPane    calibYRead                = new JTextPane();
	private final JTextPane    calibZRead                = new JTextPane();
	private final JCheckBox    addCalibBox               = new JCheckBox();
	private       JLabel       _jLabelXcalibration;
	private       JLabel       _jLabelYcalibration;
	private       JLabel       _jLabelZcalibration;
	private       JLabel       _jLabelUnit;
	private       JLabel       _jLabelAnalysis;
	private       JLabel       _jLabelCalibration;
	private       String       _workDirectory;
	private       String       _rawDataDirectory;
	private       boolean      _start                    = false;
	
	/**
	 * Architecture of the graphical windows
	 */
	
	public ChromocentersAnalysisPipelineBatchDialog() {
		this.setTitle("Chromocenters Analysis Pipeline (Batch)");
		this.setSize(500, 600);
		this.setLocationRelativeTo(null);
		_container = getContentPane();
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.1};
		gridBagLayout.rowHeights = new int[]{17, 200, 124, 7};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 0.1};
		gridBagLayout.columnWidths = new int[]{236, 120, 72, 20};
		_container.setLayout(gridBagLayout);
		
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
		_jLabelWorkDirectory.setText("Work directory and data directory choice : ");
		
		JTextPane jTextPane = new JTextPane();
		jTextPane.setText("The Raw Data directory must contain 3 subdirectories:"
		                  + "\n1. for raw nuclei images, named RawDataNucleus. "
		                  + "\n2. for segmented nuclei images, named SegmentedDataNucleus."
		                  + "\n3. for segmented images of chromocenters, named SegmentedDataCc."
		                  + "\nPlease keep the same file name during the image processing.");
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
						_jButtonRawData,
						new GridBagConstraints
								(
										0, 1, 0, 0, 0.0, 0.0,
										GridBagConstraints.NORTHWEST,
										GridBagConstraints.NONE,
										new Insets(110, 10, 0, 0), 0, 0
								)
				);
		_jButtonRawData.setPreferredSize(new java.awt.Dimension(120, 21));
		_jButtonRawData.setFont(new java.awt.Font("Albertus", 2, 10));
		
		_container.add
				(
						_jTextFieldRawData,
						new GridBagConstraints
								(
										0, 1, 0, 0, 0.0, 0.0,
										GridBagConstraints.NORTHWEST,
										GridBagConstraints.NONE,
										new Insets(110, 160, 0, 0), 0, 0
								)
				);
		_jTextFieldRawData.setPreferredSize(new java.awt.Dimension(280, 21));
		_jTextFieldRawData.setFont(new java.awt.Font("Albertus", 2, 10));
		
		_container.add
				(
						_jButtonWorkDirectory,
						new GridBagConstraints
								(
										0, 1, 0, 0, 0.0, 0.0,
										GridBagConstraints.NORTHWEST,
										GridBagConstraints.NONE,
										new Insets(150, 10, 0, 0), 0, 0
								)
				);
		_jButtonWorkDirectory.setPreferredSize(new java.awt.Dimension(120, 21));
		_jButtonWorkDirectory.setFont(new java.awt.Font("Albertus", 2, 10));
		
		_container.add
				(
						_jTextFieldWorkDirectory,
						new GridBagConstraints
								(
										0, 1, 0, 0, 0.0, 0.0,
										GridBagConstraints.NORTHWEST,
										GridBagConstraints.NONE,
										new Insets(150, 160, 0, 0), 0, 0
								)
				);
		_jTextFieldWorkDirectory.setPreferredSize(new java.awt.Dimension(280, 21));
		_jTextFieldWorkDirectory.setFont(new java.awt.Font("Albertus", 2, 10));
		
		
		Calib = new JPanel();
		Calib.setLayout(new GridBagLayout());
		
		GridBagConstraints gc = new GridBagConstraints();
		gc.weightx = 2;
		gc.weighty = 5;
		gc.ipady = gc.anchor = GridBagConstraints.NORTHWEST;
		
		JLabel calibLabel = new JLabel("Calibration:");
		gc.gridx = 0;
		gc.gridy = 0;
		calibLabel.setAlignmentX(0);
		Calib.add(calibLabel);
		
		gc.gridx = 1;
		gc.gridy = 0;
		
		addCalibBox.setSelected(false);
		addCalibBox.addItemListener(this);
		Calib.add(addCalibBox, gc);
		_container.add(Calib,
		               new GridBagConstraints(0,
		                                      2,
		                                      2,
		                                      0,
		                                      0.0,
		                                      0.0,
		                                      GridBagConstraints.NORTHWEST,
		                                      GridBagConstraints.NONE,
		                                      new Insets(0, 0, 0, 0),
		                                      0,
		                                      0));
		
		
		_jLabelAnalysis = new JLabel();
		_container.add
				(
						_jLabelAnalysis,
						new GridBagConstraints
								(
										0, 3, 0, 0, 0.0, 0.0,
										GridBagConstraints.NORTHWEST,
										GridBagConstraints.NONE,
										new Insets(30, 10, 0, 0), 0, 0
								)
				);
		_jLabelAnalysis.setText("Type of Relative Heterochromatin Fraction:");
		
		_buttonGroupChoiceRhf.add(_jRadioButtonRhfV);
		_buttonGroupChoiceRhf.add(_jRadioButtonRhfI);
		_buttonGroupChoiceRhf.add(_jRadioButtonRhfIV);
		_jRadioButtonRhfV.setFont(new java.awt.Font("Albertus Extra Bold (W1)", 2, 12));
		_jRadioButtonRhfI.setFont(new java.awt.Font("Albertus Extra Bold (W1)", 2, 12));
		_jRadioButtonRhfIV.setFont(new java.awt.Font("Albertus Extra Bold (W1)", 2, 12));
		_container.add
				(
						_jRadioButtonRhfV,
						new GridBagConstraints
								(
										0, 3, 0, 0, 0.0, 0.0,
										GridBagConstraints.NORTHWEST,
										GridBagConstraints.NONE,
										new Insets(60, 370, 0, 0), 0, 0
								)
				);
		_container.add
				(
						_jRadioButtonRhfI,
						new GridBagConstraints
								(
										0, 3, 0, 0, 0.0, 0.0,
										GridBagConstraints.NORTHWEST,
										GridBagConstraints.NONE,
										new Insets(60, 250, 0, 0), 0, 0
								)
				);
		_container.add
				(
						_jRadioButtonRhfIV,
						new GridBagConstraints
								(
										0, 3, 0, 0, 0.0, 0.0,
										GridBagConstraints.NORTHWEST,
										GridBagConstraints.NONE,
										new Insets(60, 20, 0, 0), 0, 0
								)
				);
		_jRadioButtonRhfIV.setSelected(true);
		
		_jLabelAnalysis = new JLabel();
		_container.add
				(
						_jLabelAnalysis,
						new GridBagConstraints
								(
										0, 3, 0, 0, 0.0, 0.0,
										GridBagConstraints.NORTHWEST,
										GridBagConstraints.NONE,
										new Insets(95, 10, 0, 0), 0, 0
								)
				);
		_jLabelAnalysis.setText("Results file of interest: ");
		
		buttonGroupChoiceAnalysis.add(_jRadioButtonNucCc);
		buttonGroupChoiceAnalysis.add(_jRadioButtonCc);
		buttonGroupChoiceAnalysis.add(_jRadioButtonNuc);
		_jRadioButtonNuc.setFont(new java.awt.Font("Albertus Extra Bold (W1)", 2, 12));
		_jRadioButtonCc.setFont(new java.awt.Font("Albertus Extra Bold (W1)", 2, 12));
		_jRadioButtonNucCc.setFont(new java.awt.Font("Albertus Extra Bold (W1)", 2, 12));
		_container.add
				(
						_jRadioButtonNuc,
						new GridBagConstraints
								(
										0, 3, 0, 0, 0.0, 0.0,
										GridBagConstraints.NORTHWEST,
										GridBagConstraints.NONE,
										new Insets(120, 370, 0, 0), 0, 0
								)
				);
		_container.add
				(
						_jRadioButtonCc,
						new GridBagConstraints
								(
										0, 3, 0, 0, 0.0, 0.0,
										GridBagConstraints.NORTHWEST,
										GridBagConstraints.NONE,
										new Insets(120, 250, 0, 0), 0, 0
								)
				);
		_container.add
				(
						_jRadioButtonNucCc,
						new GridBagConstraints
								(
										0, 3, 0, 0, 0.0, 0.0,
										GridBagConstraints.NORTHWEST,
										GridBagConstraints.NONE,
										new Insets(120, 20, 0, 0), 0, 0
								)
				);
		_jRadioButtonNucCc.setSelected(true);
		
		_container.add
				(
						_jButtonStart,
						new GridBagConstraints
								(
										0, 3, 0, 0, 0.0, 0.0,
										GridBagConstraints.NORTHWEST,
										GridBagConstraints.NONE,
										new Insets(190, 140, 0, 0), 0, 0
								)
				);
		_jButtonStart.setPreferredSize(new java.awt.Dimension(120, 21));
		_container.add
				(
						_jButtonQuit,
						new GridBagConstraints
								(
										0, 3, 0, 0, 0.0, 0.0,
										GridBagConstraints.NORTHWEST,
										GridBagConstraints.NONE,
										new Insets(190, 10, 0, 0), 0, 0
								)
				);
		_jButtonQuit.setPreferredSize(new java.awt.Dimension(120, 21));
		
		
		WorkDirectoryListener wdListener = new WorkDirectoryListener();
		_jButtonWorkDirectory.addActionListener(wdListener);
		RawDataDirectoryListener ddListener = new RawDataDirectoryListener();
		_jButtonRawData.addActionListener(ddListener);
		QuitListener quitListener = new QuitListener(this);
		_jButtonQuit.addActionListener(quitListener);
		StartListener startListener = new StartListener(this);
		_jButtonStart.addActionListener(startListener);
		this.setVisible(true);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ChromocentersAnalysisPipelineBatchDialog chromocentersAnalysisPipelineBatchDialog =
				new ChromocentersAnalysisPipelineBatchDialog();
		chromocentersAnalysisPipelineBatchDialog.setLocationRelativeTo(null);
	}
	
	public double getXCalibration() {
		String xCal = calibXRead.getText();
		return Double.parseDouble(xCal.replaceAll(",", "."));
	}
	
	public double getYCalibration() {
		String yCal = calibYRead.getText();
		return Double.parseDouble(yCal.replaceAll(",", "."));
	}
	
	public double getZCalibration() {
		String zCal = calibZRead.getText();
		return Double.parseDouble(zCal.replaceAll(",", "."));
	}
	
	public boolean getCalibrationStatus() {
		return addCalibBox.isSelected();
	}
	
	public String getUnit() {
		return _jTextFieldUnit.getText();
	}
	
	public String getWorkDirectory() {
		return _jTextFieldWorkDirectory.getText();
	}
	
	public String getRawDataDirectory() {
		return _jTextFieldRawData.getText();
	}
	
	public boolean isStart() {
		return _start;
	}
	
	public boolean isNucAndCcAnalysis() {
		return _jRadioButtonNucCc.isSelected();
	}
	
	public boolean isNucAnalysis() {
		return _jRadioButtonNuc.isSelected();
	}
	
	public boolean isCcAnalysis() {
		return _jRadioButtonCc.isSelected();
	}
	
	public boolean isRHFVolumeAndIntensity() {
		return _jRadioButtonRhfIV.isSelected();
	}
	
	public boolean isRhfVolume() {
		return _jRadioButtonRhfV.isSelected();
	}
	
	public boolean isRhfIntensity() {
		return _jRadioButtonRhfI.isSelected();
	}
	
	
	public void itemStateChanged(ItemEvent e) {
		if (e.getSource() == addCalibBox) {
			if ((addCalibBox.isSelected())) {
				
				GridBagConstraints gc = new GridBagConstraints();
				gc.insets = new Insets(0, 0, 5, 0);
				
				unit.setText("Unit :");
				gc.gridx = 0;
				gc.gridy = 1;
				Calib.add(unit, gc);
				unitRead.setPreferredSize(new Dimension(100, 20));
				unitRead.setText("µm");
				gc.gridx = 1;
				gc.gridy = 1;
				Calib.add(unitRead, gc);
				unit.setVisible(true);
				unitRead.setVisible(true);
				
				calibx.setText("X :");
				gc.gridx = 0;
				gc.gridy = 2;
				Calib.add(calibx, gc);
				calibXRead.setPreferredSize(new Dimension(100, 20));
				calibXRead.setText("1");
				gc.gridx = 1;
				gc.gridy = 2;
				Calib.add(calibXRead, gc);
				calibx.setVisible(true);
				calibXRead.setVisible(true);
				
				caliby.setText("Y :");
				gc.gridx = 0;
				gc.gridy = 3;
				Calib.add(caliby, gc);
				calibYRead.setPreferredSize(new Dimension(100, 20));
				calibYRead.setText("1");
				gc.gridx = 1;
				gc.gridy = 3;
				Calib.add(calibYRead, gc);
				caliby.setVisible(true);
				calibYRead.setVisible(true);
				
				calibz.setText("Z :");
				gc.gridx = 0;
				gc.gridy = 4;
				Calib.add(calibz, gc);
				calibZRead.setPreferredSize(new Dimension(100, 20));
				calibZRead.setText("1");
				gc.gridx = 1;
				gc.gridy = 4;
				Calib.add(calibZRead, gc);
				calibz.setVisible(true);
				calibZRead.setVisible(true);
				
				validate();
				//pack();
				
				repaint();
			} else {
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
	
	
	/**
	 * Classes listener to interact with the several elements of the window
	 */
	class StartListener implements ActionListener {
		
		ChromocentersAnalysisPipelineBatchDialog _chromocentersAnalysisPipelineBatchDialog;
		
		/**
		 * @param chromocentersAnalysisPipelineBatchDialog
		 */
		public StartListener(ChromocentersAnalysisPipelineBatchDialog chromocentersAnalysisPipelineBatchDialog) {
			_chromocentersAnalysisPipelineBatchDialog = chromocentersAnalysisPipelineBatchDialog;
		}
		
		/**
		 *
		 */
		public void actionPerformed(ActionEvent actionEvent) {
			if (_jTextFieldWorkDirectory.getText().isEmpty() || _jTextFieldRawData.getText().isEmpty()) {
				JOptionPane.showMessageDialog
						(
								null,
								"You did not choose a work directory or the raw data",
								"Error",
								JOptionPane.ERROR_MESSAGE
						);
			} else {
				_start = true;
				_chromocentersAnalysisPipelineBatchDialog.dispose();
			}
		}
	}
	
	/**
	 *
	 */
	class QuitListener implements ActionListener {
		ChromocentersAnalysisPipelineBatchDialog _chromocentersAnalysisPipelineBatchDialog;
		
		/**
		 * @param chromocentersAnalysisPipelineBatchDialog
		 */
		public QuitListener(ChromocentersAnalysisPipelineBatchDialog chromocentersAnalysisPipelineBatchDialog) {
			_chromocentersAnalysisPipelineBatchDialog = chromocentersAnalysisPipelineBatchDialog;
		}
		
		/**
		 *
		 */
		public void actionPerformed(ActionEvent actionEvent) {
			_chromocentersAnalysisPipelineBatchDialog.dispose();
		}
	}
	
	/**
	 *
	 */
	class WorkDirectoryListener implements ActionListener {
		/**
		 *
		 */
		public void actionPerformed(ActionEvent actionEvent) {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			JFileChooser jFileChooser = new JFileChooser();
			jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnValue = jFileChooser.showOpenDialog(getParent());
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				@SuppressWarnings("unused")
				String run = jFileChooser.getSelectedFile().getName();
				_workDirectory = jFileChooser.getSelectedFile().getAbsolutePath();
				_jTextFieldWorkDirectory.setText(_workDirectory);
			}
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}
	
	/**
	 *
	 */
	class RawDataDirectoryListener implements ActionListener {
		/**
		 *
		 */
		public void actionPerformed(ActionEvent actionEvent) {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			JFileChooser jFileChooser = new JFileChooser();
			jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnValue = jFileChooser.showOpenDialog(getParent());
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				@SuppressWarnings("unused")
				String run = jFileChooser.getSelectedFile().getName();
				_rawDataDirectory = jFileChooser.getSelectedFile().getAbsolutePath();
				_jTextFieldRawData.setText(_rawDataDirectory);
			}
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}
}