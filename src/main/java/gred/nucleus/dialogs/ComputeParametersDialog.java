package gred.nucleus.dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;


public class ComputeParametersDialog extends JFrame implements ItemListener {
	private static final long       serialVersionUID         = 1L;
	private static final JButton    _jButtonWorkDirectory    = new JButton("Seg Data folder");
	private final        JTextField _jTextFieldWorkDirectory = new JTextField();
	private final        JTextField _jTextFieldRawData       = new JTextField();
	private final        JTextPane  _readUnit                = new JTextPane();
	private final        JLabel     _jLabelUnit              = new JLabel();
	private final        JLabel     _jLabelXCalibration      = new JLabel();
	private final        JLabel     _jLabelYCalibration      = new JLabel();
	private final        JLabel     _jLabelZCalibration      = new JLabel();
	private final        JTextPane  _readXCalibration        = new JTextPane();
	private final        JTextPane  _readYCalibration        = new JTextPane();
	private final        JTextPane  _readZCalibration        = new JTextPane();
	private final        JCheckBox  _addCalibrationBox       = new JCheckBox();
	private final        JPanel     _calibration;
	private              boolean    _start                   = false;
	
	
	/** Architecture of the graphical windows */
	public ComputeParametersDialog() {
		final Container container           = getContentPane();
		final JLabel    jLabelWorkDirectory = new JLabel();
		final JLabel    jLabelCalibration   = new JLabel();
		final JButton   jButtonStart        = new JButton("Start");
		final JButton   jButtonQuit         = new JButton("Quit");
		final JButton   JButtonRawData      = new JButton("Raw Data folder");
		this.setTitle("Compute morphological parameters");
		this.setSize(500, 500);
		this.setLocationRelativeTo(null);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowHeights = new int[]{17, 200, 124, 7, 10};
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
		jLabelWorkDirectory.setText("Work directory and Raw data choice : ");
		JTextPane jTextPane = new JTextPane();
		jTextPane.setText("You must select 2 directories:\n" +
		                  "1 containing raw nuclei images. \n" +
		                  "2 containing segmented nuclei images.\n" +
		                  "Images must have same file name.");
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
		container.add(JButtonRawData,
		              new GridBagConstraints(0,
		                                     1,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(100, 10, 0, 0),
		                                     0,
		                                     0));
		JButtonRawData.setPreferredSize(new java.awt.Dimension(120, 21));
		JButtonRawData.setFont(new java.awt.Font("Albertus", Font.ITALIC, 10));
		container.add(_jTextFieldRawData,
		              new GridBagConstraints(0,
		                                     1,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(100, 160, 0, 0),
		                                     0,
		                                     0));
		_jTextFieldRawData.setPreferredSize(new java.awt.Dimension(280, 21));
		_jTextFieldRawData.setFont(new java.awt.Font("Albertus", Font.ITALIC, 10));
		container.add(_jButtonWorkDirectory,
		              new GridBagConstraints(0,
		                                     1,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(140, 10, 0, 0),
		                                     0,
		                                     0));
		_jButtonWorkDirectory.setPreferredSize(new java.awt.Dimension(120, 21));
		_jButtonWorkDirectory.setFont(new java.awt.Font("Albertus", Font.ITALIC, 10));
		container.add(_jTextFieldWorkDirectory,
		              new GridBagConstraints(0,
		                                     1,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(140, 160, 0, 0),
		                                     0,
		                                     0));
		_jTextFieldWorkDirectory.setPreferredSize(new java.awt.Dimension(280, 21));
		_jTextFieldWorkDirectory.setFont(new java.awt.Font("Albertus", Font.ITALIC, 10));
		container.add(jLabelCalibration,
		              new GridBagConstraints(0,
		                                     2,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(20, 10, 0, 0),
		                                     0,
		                                     0));
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
		container.add(jButtonStart,
		              new GridBagConstraints(0,
		                                     2,
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
		                                     2,
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
		this.setVisible(true);
		ComputeParametersDialog.WorkDirectoryListener wdListener = new ComputeParametersDialog.WorkDirectoryListener();
		_jButtonWorkDirectory.addActionListener(wdListener);
		ComputeParametersDialog.RawDataDirectoryListener ddListener =
				new ComputeParametersDialog.RawDataDirectoryListener();
		JButtonRawData.addActionListener(ddListener);
		ComputeParametersDialog.QuitListener quitListener = new QuitListener(this);
		jButtonQuit.addActionListener(quitListener);
		ComputeParametersDialog.StartListener startListener = new ComputeParametersDialog.StartListener(this);
		jButtonStart.addActionListener(startListener);
	}
	
	
	/**
	 * Constructor for segmentation dialog
	 *
	 * @param args arguments
	 */
	public static void main(String[] args) {
		ChromocenterSegmentationPipelineBatchDialog chromocenterSegmentationPipelineBatchDialog =
				new ChromocenterSegmentationPipelineBatchDialog();
		chromocenterSegmentationPipelineBatchDialog.setLocationRelativeTo(null);
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
		final ComputeParametersDialog _computeParametersDialog;
		
		
		/** @param computeParametersDialog Dialog parameters */
		public QuitListener(ComputeParametersDialog computeParametersDialog) {
			_computeParametersDialog = computeParametersDialog;
		}
		
		
		/**
		 *
		 */
		public void actionPerformed(ActionEvent actionEvent) {
			_computeParametersDialog.dispose();
		}
		
	}
	
	class StartListener implements ActionListener {
		final ComputeParametersDialog _computeParametersDialog;
		
		
		/** @param computeParametersDialog Dialog parameters */
		public StartListener(ComputeParametersDialog computeParametersDialog) {
			_computeParametersDialog = computeParametersDialog;
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
				_computeParametersDialog.dispose();
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
