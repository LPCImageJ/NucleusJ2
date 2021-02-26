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
	private static final long                serialVersionUID         = 1L;
	private final        JTextField          _jTextFieldWorkDirectory = new JTextField();
	private final        JTextField          _jTextFieldRawData       = new JTextField();
	private final        JRadioButton        _jRadioButtonRhfV        = new JRadioButton("VolumeRHF");
	private final        JRadioButton        _jRadioButtonRhfI        = new JRadioButton("IntensityRHF");
	private final        JRadioButton        _jRadioButtonRhfIV       = new JRadioButton("VolumeRHF and IntensityRHF");
	private final        JRadioButton        _jRadioButtonNucCc       = new JRadioButton("Nucleus and chromocenter");
	private final        JRadioButton        _jRadioButtonCc          = new JRadioButton("Chromocenter");
	private final        JRadioButton        _jRadioButtonNuc         = new JRadioButton("Nucleus");
	private final        JLabel              _jLabelUnit              = new JLabel();
	private final        JLabel              _jLabelXCalibration      = new JLabel();
	private final        JLabel              _jLabelYCalibration      = new JLabel();
	private final        JLabel              _jLabelZCalibration      = new JLabel();
	private final        JTextPane           _readUnit                = new JTextPane();
	private final        JTextPane           _readXCalibration        = new JTextPane();
	private final        JTextPane           _readYCalibration        = new JTextPane();
	private final        JTextPane           _readZCalibration        = new JTextPane();
	private final        JCheckBox           _addCalibrationBox       = new JCheckBox();
	private final        JPanel              _calibration;
	private final        JFormattedTextField _jTextFieldXCalibration  = new JFormattedTextField(Number.class);
	private final        JFormattedTextField _jTextFieldYCalibration  = new JFormattedTextField(Number.class);
	private final        JFormattedTextField _jTextFieldZCalibration  = new JFormattedTextField(Number.class);
	private              boolean             _start                   = false;
	
	
	/** Architecture of the graphical windows */
	public ChromocentersAnalysisPipelineBatchDialog() {
		final Container   container                 = getContentPane();
		final JLabel      jLabelWorkDirectory       = new JLabel();
		final JButton     jButtonWorkDirectory      = new JButton("Output Directory");
		final JButton     jButtonStart              = new JButton("Start");
		final JButton     jButtonQuit               = new JButton("Quit");
		final JButton     jButtonRawData            = new JButton("Raw Data");
		final ButtonGroup buttonGroupChoiceAnalysis = new ButtonGroup();
		final ButtonGroup buttonGroupChoiceRhf      = new ButtonGroup();
		JLabel            jLabelAnalysis;
		this.setTitle("Chromocenters Analysis Pipeline (Batch)");
		this.setSize(500, 600);
		this.setLocationRelativeTo(null);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.1};
		gridBagLayout.rowHeights = new int[]{17, 200, 124, 7};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 0.1};
		gridBagLayout.columnWidths = new int[]{236, 120, 72, 20};
		container.setLayout(gridBagLayout);
		container.add(jLabelWorkDirectory,
		              new GridBagConstraints(0,
		                                     1,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(0, 10, 0, 0),
		                                     0,
		                                     0));
		jLabelWorkDirectory.setText("Work directory and data directory choice : ");
		JTextPane jTextPane = new JTextPane();
		jTextPane.setText(
				"The Raw Data directory must contain 3 subdirectories:\n1. for raw nuclei images, named RawDataNucleus. \n2. for segmented nuclei images, named SegmentedDataNucleus.\n3. for segmented images of chromocenters, named SegmentedDataCc.\nPlease keep the same file name during the image processing.");
		jTextPane.setEditable(false);
		container.add(jTextPane,
		              new GridBagConstraints(0,
		                                     1,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(20, 20, 0, 0),
		                                     0,
		                                     0));
		container.add(jButtonRawData,
		              new GridBagConstraints(0,
		                                     1,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(110, 10, 0, 0),
		                                     0,
		                                     0));
		jButtonRawData.setPreferredSize(new java.awt.Dimension(120, 21));
		jButtonRawData.setFont(new java.awt.Font("Albertus", Font.ITALIC, 10));
		container.add(_jTextFieldRawData,
		              new GridBagConstraints(0,
		                                     1,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(110, 160, 0, 0),
		                                     0,
		                                     0));
		_jTextFieldRawData.setPreferredSize(new java.awt.Dimension(280, 21));
		_jTextFieldRawData.setFont(new java.awt.Font("Albertus", Font.ITALIC, 10));
		container.add(jButtonWorkDirectory,
		              new GridBagConstraints(0,
		                                     1,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(150, 10, 0, 0),
		                                     0,
		                                     0));
		jButtonWorkDirectory.setPreferredSize(new java.awt.Dimension(120, 21));
		jButtonWorkDirectory.setFont(new java.awt.Font("Albertus", Font.ITALIC, 10));
		container.add(_jTextFieldWorkDirectory,
		              new GridBagConstraints(0,
		                                     1,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(150, 160, 0, 0),
		                                     0,
		                                     0));
		_jTextFieldWorkDirectory.setPreferredSize(new java.awt.Dimension(280, 21));
		_jTextFieldWorkDirectory.setFont(new java.awt.Font("Albertus", Font.ITALIC, 10));
		_calibration = new JPanel();
		_calibration.setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		gc.weightx = 2;
		gc.weighty = 5;
		gc.anchor = GridBagConstraints.NORTHWEST;
		gc.ipady = GridBagConstraints.NORTHWEST;
		JLabel calibrationLabel = new JLabel("Calibration:");
		gc.gridx = 0;
		gc.gridy = 0;
		calibrationLabel.setAlignmentX(0);
		_calibration.add(calibrationLabel);
		gc.gridx = 1;
		gc.gridy = 0;
		_addCalibrationBox.setSelected(false);
		_addCalibrationBox.addItemListener(this);
		_calibration.add(_addCalibrationBox, gc);
		container.add(_calibration,
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
		jLabelAnalysis = new JLabel();
		container.add(jLabelAnalysis,
		              new GridBagConstraints(0,
		                                     3,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(30, 10, 0, 0),
		                                     0,
		                                     0));
		jLabelAnalysis.setText("Type of Relative Heterochromatin Fraction:");
		buttonGroupChoiceRhf.add(_jRadioButtonRhfV);
		buttonGroupChoiceRhf.add(_jRadioButtonRhfI);
		buttonGroupChoiceRhf.add(_jRadioButtonRhfIV);
		_jRadioButtonRhfV.setFont(new java.awt.Font("Albertus Extra Bold (W1)", Font.ITALIC, 12));
		_jRadioButtonRhfI.setFont(new java.awt.Font("Albertus Extra Bold (W1)", Font.ITALIC, 12));
		_jRadioButtonRhfIV.setFont(new java.awt.Font("Albertus Extra Bold (W1)", Font.ITALIC, 12));
		container.add(_jRadioButtonRhfV,
		              new GridBagConstraints(0,
		                                     3,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(60, 370, 0, 0),
		                                     0,
		                                     0));
		container.add(_jRadioButtonRhfI,
		              new GridBagConstraints(0,
		                                     3,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(60, 250, 0, 0),
		                                     0,
		                                     0));
		container.add(_jRadioButtonRhfIV,
		              new GridBagConstraints(0,
		                                     3,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(60, 20, 0, 0),
		                                     0,
		                                     0));
		_jRadioButtonRhfIV.setSelected(true);
		jLabelAnalysis = new JLabel();
		container.add(jLabelAnalysis,
		              new GridBagConstraints(0,
		                                     3,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(95, 10, 0, 0),
		                                     0,
		                                     0));
		jLabelAnalysis.setText("Results file of interest: ");
		buttonGroupChoiceAnalysis.add(_jRadioButtonNucCc);
		buttonGroupChoiceAnalysis.add(_jRadioButtonCc);
		buttonGroupChoiceAnalysis.add(_jRadioButtonNuc);
		_jRadioButtonNuc.setFont(new java.awt.Font("Albertus Extra Bold (W1)", Font.ITALIC, 12));
		_jRadioButtonCc.setFont(new java.awt.Font("Albertus Extra Bold (W1)", Font.ITALIC, 12));
		_jRadioButtonNucCc.setFont(new java.awt.Font("Albertus Extra Bold (W1)", Font.ITALIC, 12));
		container.add(_jRadioButtonNuc,
		              new GridBagConstraints(0,
		                                     3,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(120, 370, 0, 0),
		                                     0,
		                                     0));
		container.add(_jRadioButtonCc,
		              new GridBagConstraints(0,
		                                     3,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(120, 250, 0, 0),
		                                     0,
		                                     0));
		container.add(_jRadioButtonNucCc,
		              new GridBagConstraints(0,
		                                     3,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(120, 20, 0, 0),
		                                     0,
		                                     0));
		_jRadioButtonNucCc.setSelected(true);
		container.add(jButtonStart,
		              new GridBagConstraints(0,
		                                     3,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(190, 140, 0, 0),
		                                     0,
		                                     0));
		jButtonStart.setPreferredSize(new java.awt.Dimension(120, 21));
		container.add(jButtonQuit,
		              new GridBagConstraints(0,
		                                     3,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(190, 10, 0, 0),
		                                     0,
		                                     0));
		jButtonQuit.setPreferredSize(new java.awt.Dimension(120, 21));
		WorkDirectoryListener wdListener = new WorkDirectoryListener();
		jButtonWorkDirectory.addActionListener(wdListener);
		RawDataDirectoryListener ddListener = new RawDataDirectoryListener();
		jButtonRawData.addActionListener(ddListener);
		QuitListener quitListener = new QuitListener(this);
		jButtonQuit.addActionListener(quitListener);
		StartListener startListener = new StartListener(this);
		jButtonStart.addActionListener(startListener);
		this.setVisible(true);
	}
	
	
	/** @param args arguments */
	public static void main(String[] args) {
		ChromocentersAnalysisPipelineBatchDialog chromocentersAnalysisPipelineBatchDialog =
				new ChromocentersAnalysisPipelineBatchDialog();
		chromocentersAnalysisPipelineBatchDialog.setLocationRelativeTo(null);
	}
	
	
	public double getXCalibration() {
		String xCal = _readXCalibration.getText();
		return Double.parseDouble(xCal.replaceAll(",", "."));
	}
	
	
	public double getYCalibration() {
		String yCal = _readYCalibration.getText();
		return Double.parseDouble(yCal.replaceAll(",", "."));
	}
	
	
	public double getZCalibration() {
		String zCal = _readZCalibration.getText();
		return Double.parseDouble(zCal.replaceAll(",", "."));
	}
	
	
	public boolean getCalibrationStatus() {
		return _addCalibrationBox.isSelected();
	}
	
	
	public String getUnit() {
		return _readUnit.getText();
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
		if (e.getSource() == _addCalibrationBox) {
			if (_addCalibrationBox.isSelected()) {
				
				GridBagConstraints gc = new GridBagConstraints();
				gc.insets = new Insets(0, 0, 5, 0);
				
				_jLabelUnit.setText("Unit :");
				gc.gridx = 0;
				gc.gridy = 1;
				_calibration.add(_jLabelUnit, gc);
				_readUnit.setPreferredSize(new Dimension(100, 20));
				_readUnit.setText("µm");
				gc.gridx = 1;
				gc.gridy = 1;
				_calibration.add(_readUnit, gc);
				_jLabelUnit.setVisible(true);
				_readUnit.setVisible(true);
				
				_jLabelXCalibration.setText("X :");
				gc.gridx = 0;
				gc.gridy = 2;
				_calibration.add(_jLabelXCalibration, gc);
				_readXCalibration.setPreferredSize(new Dimension(100, 20));
				_readXCalibration.setText("1");
				gc.gridx = 1;
				gc.gridy = 2;
				_calibration.add(_readXCalibration, gc);
				_jLabelXCalibration.setVisible(true);
				_readXCalibration.setVisible(true);
				
				_jLabelYCalibration.setText("Y :");
				gc.gridx = 0;
				gc.gridy = 3;
				_calibration.add(_jLabelYCalibration, gc);
				_readYCalibration.setPreferredSize(new Dimension(100, 20));
				_readYCalibration.setText("1");
				gc.gridx = 1;
				gc.gridy = 3;
				_calibration.add(_readYCalibration, gc);
				_jLabelYCalibration.setVisible(true);
				_readYCalibration.setVisible(true);
				
				_jLabelZCalibration.setText("Z :");
				gc.gridx = 0;
				gc.gridy = 4;
				_calibration.add(_jLabelZCalibration, gc);
				_readZCalibration.setPreferredSize(new Dimension(100, 20));
				_readZCalibration.setText("1");
				gc.gridx = 1;
				gc.gridy = 4;
				_calibration.add(_readZCalibration, gc);
				_jLabelZCalibration.setVisible(true);
				_readZCalibration.setVisible(true);
				
				//pack();
				
			} else {
				_jLabelXCalibration.setVisible(false);
				_jLabelYCalibration.setVisible(false);
				_jLabelZCalibration.setVisible(false);
				_jLabelUnit.setVisible(false);
				
				_readXCalibration.setVisible(false);
				_readYCalibration.setVisible(false);
				_readZCalibration.setVisible(false);
				_readUnit.setVisible(false);
			}
			validate();
			repaint();
		}
	}
	
	
	/**
	 *
	 */
	static class QuitListener implements ActionListener {
		final ChromocentersAnalysisPipelineBatchDialog _chromocentersAnalysisPipelineBatchDialog;
		
		
		/** @param chromocentersAnalysisPipelineBatchDialog chromocentersAnalysisPipelineBatchDialog GUI */
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
	
	/** Classes listener to interact with the several elements of the window */
	class StartListener implements ActionListener {
		
		final ChromocentersAnalysisPipelineBatchDialog _chromocentersAnalysisPipelineBatchDialog;
		
		
		/** @param chromocentersAnalysisPipelineBatchDialog chromocentersAnalysisPipelineBatchDialog GUI */
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
				String workDirectory = jFileChooser.getSelectedFile().getAbsolutePath();
				_jTextFieldWorkDirectory.setText(workDirectory);
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
				String rawDataDirectory = jFileChooser.getSelectedFile().getAbsolutePath();
				_jTextFieldRawData.setText(rawDataDirectory);
			}
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
		
	}
	
}