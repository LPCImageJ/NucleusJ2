package gred.nucleus.dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class AutocropConfigDialog extends JFrame implements ItemListener {
	private final JTextField xCropBoxSize                = new JTextField();
	private final JTextField yCropBoxSize                = new JTextField();
	private final JTextField zCropBoxSize                = new JTextField();
	private final JTextField xCalibration                = new JTextField();
	private final JTextField yCalibration                = new JTextField();
	private final JTextField zCalibration                = new JTextField();
	private final JTextField minVolume                   = new JTextField();
	private final JTextField maxVolume                   = new JTextField();
	private final JTextField thresholdOTSUComputing      = new JTextField();
	private final JTextField channelToComputeThreshold   = new JTextField();
	private final JTextField slicesOTSUComputing         = new JTextField();
	private final JTextField boxesPercentSurfaceToFilter = new JTextField();
	private final JCheckBox  regroupBoxes                = new JCheckBox();
	private final JCheckBox  addCalibrationBox           = new JCheckBox();
	private final JPanel     cropBoxPane;
	private       Boolean    isRegroupBoxesSelected      = true;
	private       JPanel     XCalibration;
	private       JPanel     YCalibration;
	private       JPanel     ZCalibration;
	
	public AutocropConfigDialog(AutocropDialog caller) {
		this.setTitle("Autocrop NucleusJ2");
		this.setSize(500, 350);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		Container     _container    = getContentPane();
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowWeights = new double[]{1.0};
		gridBagLayout.rowHeights = new int[]{300};
		gridBagLayout.columnWeights = new double[]{0.0, 0.3};
		gridBagLayout.columnWidths = new int[]{180, 500};
		
		_container.setLayout(gridBagLayout);
		JButton buttonOK = new JButton("Done");
		getRootPane().setDefaultButton(buttonOK);


        /*/\*\
        -------------------------- Crop Box -----------------------------------
        \*\/*/
		
		
		cropBoxPane = new JPanel();
		cropBoxPane.setLayout(new BoxLayout(cropBoxPane, BoxLayout.Y_AXIS));
		cropBoxPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		cropBoxPane.setAlignmentX(0);
		
		JPanel XCropBoxPane = new JPanel();
		XCropBoxPane.setLayout(new BoxLayout(XCropBoxPane, BoxLayout.X_AXIS));
		XCropBoxPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		JLabel xBox = new JLabel("X:");
		XCropBoxPane.add(xBox);
		XCropBoxPane.add(Box.createRigidArea(new Dimension(10, 0)));
		xCropBoxSize.setText("40");
		xCropBoxSize.setMinimumSize(new Dimension(60, 10));
		XCropBoxPane.add(xCropBoxSize);
		
		JPanel YCropBoxPane = new JPanel();
		YCropBoxPane.setLayout(new BoxLayout(YCropBoxPane, BoxLayout.X_AXIS));
		YCropBoxPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		JLabel yBox = new JLabel("Y:");
		YCropBoxPane.add(yBox);
		YCropBoxPane.add(Box.createRigidArea(new Dimension(10, 0)));
		yCropBoxSize.setText("40");
		yCropBoxSize.setMinimumSize(new Dimension(60, 10));
		YCropBoxPane.add(yCropBoxSize);
		
		JPanel ZCropBoxPane = new JPanel();
		ZCropBoxPane.setLayout(new BoxLayout(ZCropBoxPane, BoxLayout.X_AXIS));
		ZCropBoxPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		JLabel zBox = new JLabel("Z:");
		ZCropBoxPane.add(zBox);
		ZCropBoxPane.add(Box.createRigidArea(new Dimension(10, 0)));
		zCropBoxSize.setText("20");
		zCropBoxSize.setMinimumSize(new Dimension(60, 10));
		ZCropBoxPane.add(zCropBoxSize);
		
		JLabel cropBoxLabel = new JLabel("Crop Box Size:");
		cropBoxLabel.setAlignmentX(0);
		cropBoxPane.add(cropBoxLabel);
		cropBoxPane.add(XCropBoxPane);
		cropBoxPane.add(YCropBoxPane);
		cropBoxPane.add(ZCropBoxPane);
		cropBoxPane.add(Box.createRigidArea(new Dimension(0, 20)));


        /*/\*\
        -------------------------- Calibration -----------------------------------
        \*\/*/
		
		
		JPanel calibrationPanel = new JPanel();
		JLabel calibrationLabel = new JLabel("Calibration:");
		calibrationLabel.setAlignmentX(0);
		calibrationPanel.add(calibrationLabel);
		addCalibrationBox.setSelected(false);
		addCalibrationBox.setMinimumSize(new Dimension(100, 10));
		addCalibrationBox.addItemListener(this);
		calibrationPanel.add(addCalibrationBox);
		cropBoxPane.add(calibrationPanel);
		
		_container.add(cropBoxPane, new GridBagConstraints(0,
		                                                   0,
		                                                   0,
		                                                   0,
		                                                   0.0,
		                                                   0.0,
		                                                   GridBagConstraints.NORTHWEST,
		                                                   GridBagConstraints.NONE,
		                                                   new Insets(0, 0, 0, 0),
		                                                   0,
		                                                   0));



        /*/\*\
        -------------------------- Nucleus Volume -----------------------------------
        \*\/*/
		
		
		JPanel volumeNucleus = new JPanel();
		volumeNucleus.setLayout(new BoxLayout(volumeNucleus, BoxLayout.Y_AXIS));
		volumeNucleus.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		volumeNucleus.setAlignmentX(0);
		
		JPanel minVolumeNucleus = new JPanel();
		minVolumeNucleus.setLayout(new BoxLayout(minVolumeNucleus, BoxLayout.X_AXIS));
		minVolumeNucleus.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		JLabel min = new JLabel("Min:");
		minVolumeNucleus.add(min);
		minVolumeNucleus.add(Box.createRigidArea(new Dimension(10, 0)));
		minVolume.setText("1");
		minVolume.setMinimumSize(new Dimension(100, 10));
		minVolumeNucleus.add(minVolume);
		
		JPanel maxVolumeNucleus = new JPanel();
		maxVolumeNucleus.setLayout(new BoxLayout(maxVolumeNucleus, BoxLayout.X_AXIS));
		maxVolumeNucleus.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		JLabel max = new JLabel("Max:");
		maxVolumeNucleus.add(max);
		maxVolumeNucleus.add(Box.createRigidArea(new Dimension(10, 0)));
		maxVolume.setText("2147483647");
		maxVolume.setMinimumSize(new Dimension(100, 10));
		maxVolumeNucleus.add(maxVolume);
		
		JLabel volumeLabel = new JLabel("Volume Nucleus:");
		volumeLabel.setAlignmentX(0);
		volumeNucleus.add(volumeLabel);
		volumeNucleus.add(minVolumeNucleus);
		volumeNucleus.add(maxVolumeNucleus);
		volumeNucleus.add(Box.createRigidArea(new Dimension(0, 20)));


        /*/\*\
        -------------------------- Other -----------------------------------
        \*\/*/
		
		
		JPanel thresholdOTSUComputingPanel = new JPanel();
		thresholdOTSUComputingPanel.setLayout(new BoxLayout(thresholdOTSUComputingPanel, BoxLayout.X_AXIS));
		thresholdOTSUComputingPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		JLabel tOTSUValue = new JLabel("Threshold OTSU computing:");
		thresholdOTSUComputingPanel.add(tOTSUValue);
		thresholdOTSUComputingPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		thresholdOTSUComputing.setText("20");
		thresholdOTSUComputing.setMinimumSize(new Dimension(100, 10));
		thresholdOTSUComputingPanel.add(thresholdOTSUComputing);
		
		JPanel channelToComputeThresholdPanel = new JPanel();
		channelToComputeThresholdPanel.setLayout(new BoxLayout(channelToComputeThresholdPanel, BoxLayout.X_AXIS));
		channelToComputeThresholdPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		JLabel thresholdChannelsValue = new JLabel("Channels to compute threshold:");
		channelToComputeThresholdPanel.add(thresholdChannelsValue);
		channelToComputeThresholdPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		channelToComputeThreshold.setText("0");
		channelToComputeThreshold.setMinimumSize(new Dimension(100, 10));
		channelToComputeThresholdPanel.add(channelToComputeThreshold);
		
		JPanel slicesOTSUComputingPanel = new JPanel();
		slicesOTSUComputingPanel.setLayout(new BoxLayout(slicesOTSUComputingPanel, BoxLayout.X_AXIS));
		slicesOTSUComputingPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		JLabel slicesOTSUValue = new JLabel("Slices OTSU computing:");
		slicesOTSUComputingPanel.add(slicesOTSUValue);
		slicesOTSUComputingPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		slicesOTSUComputing.setText("0");
		slicesOTSUComputing.setMinimumSize(new Dimension(100, 10));
		slicesOTSUComputingPanel.add(slicesOTSUComputing);
		
		JPanel boxesPercentSurfacePanel = new JPanel();
		boxesPercentSurfacePanel.setLayout(new BoxLayout(boxesPercentSurfacePanel, BoxLayout.X_AXIS));
		boxesPercentSurfacePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		JLabel boxesSurfaceValue = new JLabel("Boxes percent surface to filter:");
		boxesPercentSurfacePanel.add(boxesSurfaceValue);
		boxesPercentSurfacePanel.add(Box.createRigidArea(new Dimension(10, 0)));
		boxesPercentSurfaceToFilter.setText("50");
		boxesPercentSurfaceToFilter.setMinimumSize(new Dimension(100, 10));
		boxesPercentSurfacePanel.add(boxesPercentSurfaceToFilter);
		
		JPanel regroupBoxesPanel = new JPanel();
		regroupBoxesPanel.setLayout(new BoxLayout(regroupBoxesPanel, BoxLayout.X_AXIS));
		regroupBoxesPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		JLabel regroupBoxesValue = new JLabel("Boxes regrouping:");
		regroupBoxesPanel.add(regroupBoxesValue);
		regroupBoxesPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		regroupBoxes.setSelected(true);
		regroupBoxes.setMinimumSize(new Dimension(100, 10));
		regroupBoxes.addItemListener(this);
		regroupBoxesPanel.add(regroupBoxes);
		
		volumeNucleus.add(thresholdOTSUComputingPanel);
		volumeNucleus.add(channelToComputeThresholdPanel);
		volumeNucleus.add(slicesOTSUComputingPanel);
		volumeNucleus.add(boxesPercentSurfacePanel);
		volumeNucleus.add(regroupBoxesPanel);



        /*/\*\
        -------------------------- Validation Button -----------------------------------
        \*\/*/
		
		
		volumeNucleus.add(Box.createRigidArea(new Dimension(0, 20)));
		buttonOK.setPreferredSize(new java.awt.Dimension(80, 21));
		volumeNucleus.add(buttonOK);
		
		_container.add(volumeNucleus, new GridBagConstraints(1,
		                                                     0,
		                                                     0,
		                                                     0,
		                                                     0.0,
		                                                     0.0,
		                                                     GridBagConstraints.NORTHWEST,
		                                                     GridBagConstraints.NONE,
		                                                     new Insets(0, 0, 0, 0),
		                                                     0,
		                                                     0));
		
		
		this.setVisible(false);
		
		AutocropConfigDialog.StartListener startListener = new StartListener(this);
		buttonOK.addActionListener(startListener);
	}
	
	
	public String getXCropBoxSize() {
		return xCropBoxSize.getText();
	}
	
	
	public String getYCropBoxSize() {
		return yCropBoxSize.getText();
	}
	
	
	public String getZCropBoxSize() {
		return zCropBoxSize.getText();
	}
	
	
	public String getXCalibration() {
		return xCalibration.getText();
	}
	
	
	public String getYCalibration() {
		return yCalibration.getText();
	}
	
	
	public String getZCalibration() {
		return zCalibration.getText();
	}
	
	
	public String getMinVolume() {
		return minVolume.getText();
	}
	
	
	public String getMaxVolume() {
		return maxVolume.getText();
	}
	
	
	public String getThresholdOTSUComputing() {
		return thresholdOTSUComputing.getText();
	}
	
	
	public String getChannelToComputeThreshold() {
		return channelToComputeThreshold.getText();
	}
	
	
	public String getSlicesOTSUComputing() {
		return slicesOTSUComputing.getText();
	}
	
	
	public String getBoxesPercentSurfaceToFilter() {
		return boxesPercentSurfaceToFilter.getText();
	}
	
	
	public boolean isRegroupBoxesSelected() {
		return isRegroupBoxesSelected;
	}
	
	
	public boolean isCalibrationSelected() {
		return addCalibrationBox.isSelected();
	}
	
	
	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getSource() == regroupBoxes) {
			isRegroupBoxesSelected = regroupBoxes.isSelected();
		} else if (e.getSource() == addCalibrationBox) {
			if (addCalibrationBox.isSelected()) {
				
				XCalibration = new JPanel();
				XCalibration.setLayout(new BoxLayout(XCalibration, BoxLayout.X_AXIS));
				XCalibration.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
				JLabel xBox2 = new JLabel("X:");
				XCalibration.add(xBox2);
				XCalibration.add(Box.createRigidArea(new Dimension(10, 0)));
				xCalibration.setText("1");
				xCalibration.setMinimumSize(new Dimension(60, 10));
				XCalibration.add(xCalibration);
				
				YCalibration = new JPanel();
				YCalibration.setLayout(new BoxLayout(YCalibration, BoxLayout.X_AXIS));
				YCalibration.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
				JLabel yBox2 = new JLabel("Y:");
				YCalibration.add(yBox2);
				YCalibration.add(Box.createRigidArea(new Dimension(10, 0)));
				yCalibration.setText("1");
				yCalibration.setMinimumSize(new Dimension(60, 10));
				YCalibration.add(yCalibration);
				
				ZCalibration = new JPanel();
				ZCalibration.setLayout(new BoxLayout(ZCalibration, BoxLayout.X_AXIS));
				ZCalibration.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
				JLabel zBox2 = new JLabel("Z:");
				ZCalibration.add(zBox2);
				ZCalibration.add(Box.createRigidArea(new Dimension(10, 0)));
				zCalibration.setText("1");
				zCalibration.setMinimumSize(new Dimension(60, 10));
				ZCalibration.add(zCalibration);
				
				cropBoxPane.add(XCalibration);
				cropBoxPane.add(YCalibration);
				cropBoxPane.add(ZCalibration);
			} else {
				try {
					cropBoxPane.remove(XCalibration);
					cropBoxPane.remove(YCalibration);
					cropBoxPane.remove(ZCalibration);
				} catch (NullPointerException nullPointerException) {
					// Do nothing
				}
			}
		}
		validate();
		repaint();
	}
	
	
	static class StartListener implements ActionListener {
		AutocropConfigDialog _autocropDialog;
		
		/** @param autocropDialog  */
		public StartListener(AutocropConfigDialog autocropDialog) {
			_autocropDialog = autocropDialog;
		}
		
		
		public void actionPerformed(ActionEvent actionEvent) {
			_autocropDialog.setVisible(false);
		}
	}
}