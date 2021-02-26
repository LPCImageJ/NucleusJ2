package gred.nucleus.dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;


/**
 * Class to construct graphical interface for the Nucleus Segmentation analysis in batch
 *
 * @author Poulet Axel
 */
public class NucleusSegmentationAndAnalysisBatchDialog extends JFrame {
	
	private static final long                serialVersionUID          = 1L;
	private final        JFormattedTextField _jTextFieldXCalibration   = new JFormattedTextField(Number.class);
	private final        JFormattedTextField _jTextFieldYCalibration   = new JFormattedTextField(Number.class);
	private final        JFormattedTextField _jTextFieldZCalibration   = new JFormattedTextField(Number.class);
	private final        JFormattedTextField _jTextFieldMax            = new JFormattedTextField(Number.class);
	private final        JFormattedTextField _jTextFieldMin            = new JFormattedTextField(Number.class);
	private final        JTextField          _jTextFieldUnit           = new JTextField();
	private final        JTextField          _jTextFieldWorkDirectory  = new JTextField();
	private final        JTextField          _jTextFieldRawData        = new JTextField();
	private final        JRadioButton        _jRadioButton2DAnalysis   = new JRadioButton("2D");
	private final        JRadioButton        _jRadioButton3DAnalysis   = new JRadioButton("3D");
	private final        JRadioButton        _jRadioButton2D3DAnalysis = new JRadioButton("2D and 3D");
	private              boolean             _start                    = false;
	private              int                 _nbCpuChosen              = 1;
	
	
	/** Architecture of the graphical windows */
	public NucleusSegmentationAndAnalysisBatchDialog() {
		final Container          container                 = getContentPane();
		final JButton            jButtonWorkDirectory      = new JButton("Output Directory");
		final JButton            jButtonStart              = new JButton("Start");
		final JButton            jButtonQuit               = new JButton("Quit");
		final JButton            jButtonRawData            = new JButton("Raw Data");
		final ButtonGroup        buttonGroupChoiceAnalysis = new ButtonGroup();
		final JComboBox<Integer> comboBoxCpu               = new JComboBox<>();
		final JLabel             jLabelXCalibration        = new JLabel();
		final JLabel             jLabelYCalibration        = new JLabel();
		final JLabel             jLabelZCalibration        = new JLabel();
		final JLabel             jLabelUnit                = new JLabel();
		final JLabel             jLabelSegmentation        = new JLabel();
		final JLabel             jLabelVolumeMin           = new JLabel();
		final JLabel             jLabelVolumeMax           = new JLabel();
		final JLabel             jLabelAnalysis            = new JLabel();
		final JLabel             jLabelWorkDirectory       = new JLabel();
		final JLabel             jLabelCalibration         = new JLabel();
		final JLabel             jLabelNbCpu               = new JLabel();
		
		this.setTitle("Nucleus segmentation & analysis (batch)");
		this.setSize(500, 500);
		this.setLocationRelativeTo(null);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.1};
		gridBagLayout.rowHeights = new int[]{17, 71, 124, 7};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 0.1};
		gridBagLayout.columnWidths = new int[]{236, 109, 72, 20};
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
		
		container.add(jButtonRawData,
		              new GridBagConstraints(0,
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
		                                     new Insets(30, 160, 0, 0),
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
		                                     new Insets(60, 10, 0, 0),
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
		                                     new Insets(60, 160, 0, 0),
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
		jLabelCalibration.setText("Voxel Calibration:");
		
		container.setLayout(gridBagLayout);
		container.add(jLabelXCalibration,
		              new GridBagConstraints(0,
		                                     2,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(40, 20, 0, 0),
		                                     0,
		                                     0));
		jLabelXCalibration.setText("x :");
		jLabelXCalibration.setFont(new java.awt.Font("Albertus Extra Bold (W1)", Font.ITALIC, 12));
		container.add(_jTextFieldXCalibration,
		              new GridBagConstraints(0,
		                                     2,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(40, 60, 0, 0),
		                                     0,
		                                     0));
		_jTextFieldXCalibration.setText("1");
		_jTextFieldXCalibration.setPreferredSize(new java.awt.Dimension(60, 21));
		
		container.add(jLabelYCalibration,
		              new GridBagConstraints(0,
		                                     2,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(65, 20, 0, 0),
		                                     0,
		                                     0));
		jLabelYCalibration.setText("y :");
		jLabelYCalibration.setFont(new java.awt.Font("Albertus Extra Bold (W1)", Font.ITALIC, 12));
		container.add(_jTextFieldYCalibration,
		              new GridBagConstraints(0,
		                                     2,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(65, 60, 0, 0),
		                                     0,
		                                     0));
		_jTextFieldYCalibration.setText("1");
		_jTextFieldYCalibration.setPreferredSize(new java.awt.Dimension(60, 21));
		
		container.add(jLabelZCalibration,
		              new GridBagConstraints(0,
		                                     2,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(90, 20, 0, 0),
		                                     0,
		                                     0));
		jLabelZCalibration.setText("z :");
		jLabelZCalibration.setFont(new java.awt.Font("Albertus Extra Bold (W1)", Font.ITALIC, 12));
		container.add(_jTextFieldZCalibration,
		              new GridBagConstraints(0,
		                                     2,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(90, 60, 0, 0),
		                                     0,
		                                     0));
		_jTextFieldZCalibration.setText("1");
		_jTextFieldZCalibration.setPreferredSize(new java.awt.Dimension(60, 21));
		
		container.add(jLabelUnit,
		              new GridBagConstraints(0,
		                                     2,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(115, 20, 0, 0),
		                                     0,
		                                     0));
		jLabelUnit.setText("unit :");
		jLabelUnit.setFont(new java.awt.Font("Albertus Extra Bold (W1)", Font.ITALIC, 12));
		container.add(_jTextFieldUnit,
		              new GridBagConstraints(0,
		                                     2,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(115, 60, 0, 0),
		                                     0,
		                                     0));
		_jTextFieldUnit.setText("pixel");
		_jTextFieldUnit.setPreferredSize(new java.awt.Dimension(60, 21));
		
		container.add(jLabelSegmentation,
		              new GridBagConstraints(0,
		                                     3,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(20, 10, 0, 0),
		                                     0,
		                                     0));
		jLabelSegmentation.setText("Choose the min and max volumes of the nucleus:");
		
		container.add(jLabelVolumeMin,
		              new GridBagConstraints(0,
		                                     3,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(40, 20, 0, 0),
		                                     0,
		                                     0));
		jLabelVolumeMin.setText("Minimum volume of the segmented nucleus :");
		jLabelVolumeMin.setFont(new java.awt.Font("Albertus Extra Bold (W1)", Font.ITALIC, 12));
		container.add(_jTextFieldMin,
		              new GridBagConstraints(0,
		                                     3,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(40, 320, 0, 0),
		                                     0,
		                                     0));
		_jTextFieldMin.setText("15");
		_jTextFieldMin.setPreferredSize(new java.awt.Dimension(60, 21));
		
		container.add(jLabelVolumeMax,
		              new GridBagConstraints(0,
		                                     3,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(70, 20, 0, 0),
		                                     0,
		                                     0));
		jLabelVolumeMax.setText("Maximum volume of the segmented nucleus :");
		jLabelVolumeMax.setFont(new java.awt.Font("Albertus Extra Bold (W1)", Font.ITALIC, 12));
		container.add(_jTextFieldMax,
		              new GridBagConstraints(0,
		                                     3,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(70, 320, 0, 0),
		                                     0,
		                                     0));
		_jTextFieldMax.setText("2000");
		_jTextFieldMax.setPreferredSize(new java.awt.Dimension(60, 21));
		
		container.add(jLabelAnalysis,
		              new GridBagConstraints(0,
		                                     3,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(100, 10, 0, 0),
		                                     0,
		                                     0));
		jLabelAnalysis.setText("Type of analysis:");
		
		buttonGroupChoiceAnalysis.add(_jRadioButton2DAnalysis);
		buttonGroupChoiceAnalysis.add(_jRadioButton3DAnalysis);
		buttonGroupChoiceAnalysis.add(_jRadioButton2D3DAnalysis);
		_jRadioButton2DAnalysis.setFont(new java.awt.Font("Albertus Extra Bold (W1)", Font.ITALIC, 12));
		_jRadioButton3DAnalysis.setFont(new java.awt.Font("Albertus Extra Bold (W1)", Font.ITALIC, 12));
		_jRadioButton2D3DAnalysis.setFont(new java.awt.Font("Albertus Extra Bold (W1)", Font.ITALIC, 12));
		container.add(_jRadioButton2DAnalysis,
		              new GridBagConstraints(0,
		                                     3,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(130, 170, 0, 0),
		                                     0,
		                                     0));
		container.add(_jRadioButton3DAnalysis,
		              new GridBagConstraints(0,
		                                     3,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(130, 120, 0, 0),
		                                     0,
		                                     0));
		container.add(_jRadioButton2D3DAnalysis,
		              new GridBagConstraints(0,
		                                     3,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(130, 10, 0, 0),
		                                     0,
		                                     0));
		_jRadioButton2D3DAnalysis.setSelected(true);
		
		OperatingSystemMXBean bean   = ManagementFactory.getOperatingSystemMXBean();
		int                   nbProc = bean.getAvailableProcessors();
		for (int i = 1; i <= nbProc; ++i) comboBoxCpu.addItem(i);
		jLabelNbCpu.setText("How many CPU(s) :");
		container.add(jLabelNbCpu,
		              new GridBagConstraints(0,
		                                     3,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(175, 10, 0, 0),
		                                     0,
		                                     0));
		container.add(comboBoxCpu,
		              new GridBagConstraints(0,
		                                     3,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(170, 200, 0, 0),
		                                     0,
		                                     0));
		comboBoxCpu.addItemListener(new ItemState());
		
		container.add(jButtonStart,
		              new GridBagConstraints(0,
		                                     3,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(210, 140, 0, 0),
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
		                                     new Insets(210, 10, 0, 0),
		                                     0,
		                                     0));
		jButtonQuit.setPreferredSize(new java.awt.Dimension(120, 21));
		this.setVisible(true);
		
		WorkDirectoryListener wdListener = new WorkDirectoryListener();
		jButtonWorkDirectory.addActionListener(wdListener);
		RawDataDirectoryListener ddListener = new RawDataDirectoryListener();
		jButtonRawData.addActionListener(ddListener);
		QuitListener quitListener = new QuitListener(this);
		jButtonQuit.addActionListener(quitListener);
		StartListener startListener = new StartListener(this);
		jButtonStart.addActionListener(startListener);
	}
	
	
	/** @param args arguments */
	public static void main(String[] args) {
		NucleusSegmentationAndAnalysisBatchDialog nucleusSegmentationAndAnalysisBatchDialog =
				new NucleusSegmentationAndAnalysisBatchDialog();
		nucleusSegmentationAndAnalysisBatchDialog.setLocationRelativeTo(null);
	}
	
	
	public int getNbCpu() {
		return _nbCpuChosen;
	}
	
	
	public void setNbCpu(int nb) {
		_nbCpuChosen = nb;
	}
	
	
	public double getXCalibration() {
		String xCal = _jTextFieldXCalibration.getText();
		return Double.parseDouble(xCal.replaceAll(",", "."));
	}
	
	
	public double getYCalibration() {
		String yCal = _jTextFieldYCalibration.getText();
		return Double.parseDouble(yCal.replaceAll(",", "."));
	}
	
	
	public double getZCalibration() {
		String zCal = _jTextFieldZCalibration.getText();
		return Double.parseDouble(zCal.replaceAll(",", "."));
	}
	
	
	public String getUnit() {
		return _jTextFieldUnit.getText();
	}
	
	
	public double getMinVolume() {
		return Double.parseDouble(_jTextFieldMin.getText());
	}
	
	
	public double getMaxVolume() {
		return Double.parseDouble(_jTextFieldMax.getText());
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
	
	
	public boolean is2D3DAnalysis() {
		return _jRadioButton2D3DAnalysis.isSelected();
	}
	
	
	public boolean is2D() {
		return _jRadioButton2DAnalysis.isSelected();
	}
	
	
	public boolean is3D() {
		return _jRadioButton3DAnalysis.isSelected();
	}
	
	
	/**
	 *
	 */
	static class QuitListener implements ActionListener {
		NucleusSegmentationAndAnalysisBatchDialog _nucleusSegmentationAndAnalysisBatchDialog;
		
		
		/** @param nucleusSegmentationAndAnalysisBatchDialog nucleusSegmentationAndAnalysisBatchDialog GUI */
		public QuitListener(NucleusSegmentationAndAnalysisBatchDialog nucleusSegmentationAndAnalysisBatchDialog) {
			_nucleusSegmentationAndAnalysisBatchDialog = nucleusSegmentationAndAnalysisBatchDialog;
		}
		
		
		/**
		 *
		 */
		public void actionPerformed(ActionEvent actionEvent) {
			_nucleusSegmentationAndAnalysisBatchDialog.dispose();
		}
		
	}
	
	/** Classes listener to interact with the several elements of the window */
	class ItemState implements ItemListener {
		/**
		 *
		 */
		public void itemStateChanged(ItemEvent e) {
			setNbCpu((Integer) e.getItem());
		}
		
	}
	
	/**
	 *
	 */
	class StartListener implements ActionListener {
		
		NucleusSegmentationAndAnalysisBatchDialog _nucleusSegmentationAndAnalysisBatchDialog;
		
		
		/** @param nucleusSegmentationAndAnalysisBatchDialog nucleusSegmentationAndAnalysisBatchDialog GUI */
		public StartListener(NucleusSegmentationAndAnalysisBatchDialog nucleusSegmentationAndAnalysisBatchDialog) {
			_nucleusSegmentationAndAnalysisBatchDialog = nucleusSegmentationAndAnalysisBatchDialog;
		}
		
		
		/**
		 *
		 */
		public void actionPerformed(ActionEvent actionEvent) {
			if (_jTextFieldWorkDirectory.getText().isEmpty() || _jTextFieldRawData.getText().isEmpty()) {
				JOptionPane.showMessageDialog(null,
				                              "You did not choose a work directory or the raw data",
				                              "Error",
				                              JOptionPane.ERROR_MESSAGE);
			} else {
				_start = true;
				_nucleusSegmentationAndAnalysisBatchDialog.dispose();
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