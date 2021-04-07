package gred.nucleus.dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;


public class ComputeParametersDialog extends JFrame implements ItemListener {
	private static final long       serialVersionUID        = 1L;
	private static final JButton    jButtonWorkDirectory    = new JButton("Seg Data folder");
	private final        JTextField jTextFieldWorkDirectory = new JTextField();
	private final        JTextField jTextFieldRawData       = new JTextField();
	private final        JTextPane  readUnit                = new JTextPane();
	private final        JLabel     jLabelUnit              = new JLabel();
	private final        JLabel     jLabelXCalibration      = new JLabel();
	private final        JLabel     jLabelYCalibration      = new JLabel();
	private final        JLabel     jLabelZCalibration      = new JLabel();
	private final        JTextPane  readXCalibration        = new JTextPane();
	private final        JTextPane  readYCalibration        = new JTextPane();
	private final        JTextPane  readZCalibration        = new JTextPane();
	private final        JCheckBox  addCalibrationBox       = new JCheckBox();
	private final        JPanel     calibration;
	private              boolean    start                   = false;
	
	
	/** Architecture of the graphical windows */
	public ComputeParametersDialog() {
		final Container container           = getContentPane();
		final JLabel    jLabelWorkDirectory = new JLabel();
		final JLabel    jLabelCalibration   = new JLabel();
		final JButton   jButtonStart        = new JButton("Start");
		final JButton   jButtonQuit         = new JButton("Quit");
		final JButton   jButtonRawData      = new JButton("Raw Data folder");
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
		container.add(jButtonRawData,
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
		jButtonRawData.setPreferredSize(new java.awt.Dimension(120, 21));
		jButtonRawData.setFont(new java.awt.Font("Albertus", Font.ITALIC, 10));
		container.add(jTextFieldRawData,
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
		jTextFieldRawData.setPreferredSize(new java.awt.Dimension(280, 21));
		jTextFieldRawData.setFont(new java.awt.Font("Albertus", Font.ITALIC, 10));
		container.add(jButtonWorkDirectory,
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
		jButtonWorkDirectory.setPreferredSize(new java.awt.Dimension(120, 21));
		jButtonWorkDirectory.setFont(new java.awt.Font("Albertus", Font.ITALIC, 10));
		container.add(jTextFieldWorkDirectory,
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
		jTextFieldWorkDirectory.setPreferredSize(new java.awt.Dimension(280, 21));
		jTextFieldWorkDirectory.setFont(new java.awt.Font("Albertus", Font.ITALIC, 10));
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
		calibration = new JPanel();
		calibration.setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		gc.weightx = 2;
		gc.weighty = 5;
		gc.anchor = GridBagConstraints.NORTHWEST;
		gc.ipady = GridBagConstraints.NORTHWEST;
		JLabel calibrationLabel = new JLabel("Calibration:");
		gc.gridx = 0;
		gc.gridy = 0;
		calibrationLabel.setAlignmentX(0);
		calibration.add(calibrationLabel);
		gc.gridx = 1;
		addCalibrationBox.setSelected(false);
		addCalibrationBox.addItemListener(this);
		calibration.add(addCalibrationBox, gc);
		container.add(calibration,
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
		jButtonWorkDirectory.addActionListener(wdListener);
		ComputeParametersDialog.RawDataDirectoryListener ddListener =
				new ComputeParametersDialog.RawDataDirectoryListener();
		jButtonRawData.addActionListener(ddListener);
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
		String xCal = readXCalibration.getText();
		return Double.parseDouble(xCal.replace(",", "."));
	}
	
	
	public double getYCalibration() {
		String yCal = readYCalibration.getText();
		return Double.parseDouble(yCal.replace(",", "."));
	}
	
	
	public double getZCalibration() {
		String zCal = readZCalibration.getText();
		return Double.parseDouble(zCal.replace(",", "."));
	}
	
	
	public boolean getCalibrationStatus() {
		return addCalibrationBox.isSelected();
	}
	
	
	public String getUnit() {
		return readUnit.getText();
	}
	
	
	public String getWorkDirectory() {
		return jTextFieldWorkDirectory.getText();
	}
	
	
	public String getRawDataDirectory() {
		return jTextFieldRawData.getText();
	}
	
	
	public boolean isStart() {
		return start;
	}
	
	
	public void itemStateChanged(ItemEvent e) {
		if (e.getSource() == addCalibrationBox) {
			if (addCalibrationBox.isSelected()) {
				
				GridBagConstraints gc = new GridBagConstraints();
				gc.insets = new Insets(0, 0, 5, 0);
				
				jLabelUnit.setText("Unit :");
				gc.gridx = 0;
				gc.gridy = 1;
				calibration.add(jLabelUnit, gc);
				readUnit.setPreferredSize(new Dimension(100, 20));
				readUnit.setText("µm");
				gc.gridx = 1;
				gc.gridy = 1;
				calibration.add(readUnit, gc);
				jLabelUnit.setVisible(true);
				readUnit.setVisible(true);
				
				jLabelXCalibration.setText("X :");
				gc.gridx = 0;
				gc.gridy = 2;
				calibration.add(jLabelXCalibration, gc);
				readXCalibration.setPreferredSize(new Dimension(100, 20));
				readXCalibration.setText("1");
				gc.gridx = 1;
				gc.gridy = 2;
				calibration.add(readXCalibration, gc);
				jLabelXCalibration.setVisible(true);
				readXCalibration.setVisible(true);
				
				jLabelYCalibration.setText("Y :");
				gc.gridx = 0;
				gc.gridy = 3;
				calibration.add(jLabelYCalibration, gc);
				readYCalibration.setPreferredSize(new Dimension(100, 20));
				readYCalibration.setText("1");
				gc.gridx = 1;
				gc.gridy = 3;
				calibration.add(readYCalibration, gc);
				jLabelYCalibration.setVisible(true);
				readYCalibration.setVisible(true);
				
				jLabelZCalibration.setText("Z :");
				gc.gridx = 0;
				gc.gridy = 4;
				calibration.add(jLabelZCalibration, gc);
				readZCalibration.setPreferredSize(new Dimension(100, 20));
				readZCalibration.setText("1");
				gc.gridx = 1;
				gc.gridy = 4;
				calibration.add(readZCalibration, gc);
				jLabelZCalibration.setVisible(true);
				readZCalibration.setVisible(true);
				
			} else {
				jLabelXCalibration.setVisible(false);
				jLabelYCalibration.setVisible(false);
				jLabelZCalibration.setVisible(false);
				jLabelUnit.setVisible(false);
				
				readXCalibration.setVisible(false);
				readYCalibration.setVisible(false);
				readZCalibration.setVisible(false);
				readUnit.setVisible(false);
				
			}
			validate();
			repaint();
		}
	}
	
	
	/**
	 *
	 */
	static class QuitListener implements ActionListener {
		final ComputeParametersDialog computeParametersDialog;
		
		
		/** @param computeParametersDialog Dialog parameters */
		public QuitListener(ComputeParametersDialog computeParametersDialog) {
			this.computeParametersDialog = computeParametersDialog;
		}
		
		
		/**
		 *
		 */
		public void actionPerformed(ActionEvent actionEvent) {
			computeParametersDialog.dispose();
		}
		
	}
	
	class StartListener implements ActionListener {
		final ComputeParametersDialog computeParametersDialog;
		
		
		/** @param computeParametersDialog Dialog parameters */
		public StartListener(ComputeParametersDialog computeParametersDialog) {
			this.computeParametersDialog = computeParametersDialog;
		}
		
		
		/**
		 *
		 */
		public void actionPerformed(ActionEvent actionEvent) {
			if (jTextFieldWorkDirectory.getText().isEmpty() || jTextFieldRawData.getText().isEmpty()) {
				JOptionPane.showMessageDialog
						(
								null,
								"You did not choose a work directory or the raw data",
								"Error",
								JOptionPane.ERROR_MESSAGE
						);
			} else {
				start = true;
				computeParametersDialog.dispose();
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
				jTextFieldWorkDirectory.setText(workDirectory);
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
				jTextFieldRawData.setText(rawDataDirectory);
			}
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
		
	}
	
}
