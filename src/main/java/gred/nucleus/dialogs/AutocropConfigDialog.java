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
	private final JTextField slicesOTSUcomputing         = new JTextField();
	private final JTextField boxesPercentSurfaceToFilter = new JTextField();
	private final JCheckBox  boxesRegroupement           = new JCheckBox();
	private final JCheckBox  addCalibBox                 = new JCheckBox();
	private final Container  _container;
	private final JButton    buttonOK                    = new JButton("Done");
	private final JPanel     cropBoxPane;
	private final AutocropDialog caller;
	private       Boolean    isBoxesRegroupementSelected = true;
	private       JPanel     XCalib;
	private       JPanel     YCalib;
	private       JPanel     ZCalib;
	
	public AutocropConfigDialog(AutocropDialog caller) {
		this.caller = caller;
		this.setTitle("Autocrop NucleusJ2");
		this.setSize(500, 350);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		_container = getContentPane();
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowWeights = new double[]{1.0};
		gridBagLayout.rowHeights = new int[]{300};
		gridBagLayout.columnWeights = new double[]{0.0, 0.3};
		gridBagLayout.columnWidths = new int[]{180, 500};
		
		_container.setLayout(gridBagLayout);
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
		
		
		JPanel calibPanel = new JPanel();
		JLabel calibLabel = new JLabel("Calibration:");
		calibLabel.setAlignmentX(0);
		calibPanel.add(calibLabel);
		addCalibBox.setSelected(false);
		addCalibBox.setMinimumSize(new Dimension(100, 10));
		addCalibBox.addItemListener(this);
		calibPanel.add(addCalibBox);
		cropBoxPane.add(calibPanel);
		
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
		
		
		JPanel thresholdOSTUcomputingPanel = new JPanel();
		thresholdOSTUcomputingPanel.setLayout(new BoxLayout(thresholdOSTUcomputingPanel, BoxLayout.X_AXIS));
		thresholdOSTUcomputingPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		JLabel tOTSUValue = new JLabel("Threshold OSTU computing:");
		thresholdOSTUcomputingPanel.add(tOTSUValue);
		thresholdOSTUcomputingPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		thresholdOTSUComputing.setText("20");
		thresholdOTSUComputing.setMinimumSize(new Dimension(100, 10));
		thresholdOSTUcomputingPanel.add(thresholdOTSUComputing);
		
		JPanel channelToComputeThresholdPanel = new JPanel();
		channelToComputeThresholdPanel.setLayout(new BoxLayout(channelToComputeThresholdPanel, BoxLayout.X_AXIS));
		channelToComputeThresholdPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		JLabel thresholdChannelsValue = new JLabel("Channels to compute threshold:");
		channelToComputeThresholdPanel.add(thresholdChannelsValue);
		channelToComputeThresholdPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		channelToComputeThreshold.setText("0");
		channelToComputeThreshold.setMinimumSize(new Dimension(100, 10));
		channelToComputeThresholdPanel.add(channelToComputeThreshold);
		
		JPanel slicesOTSUcomputingPanel = new JPanel();
		slicesOTSUcomputingPanel.setLayout(new BoxLayout(slicesOTSUcomputingPanel, BoxLayout.X_AXIS));
		slicesOTSUcomputingPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		JLabel slicesOTSUValue = new JLabel("Slices OTSU computing:");
		slicesOTSUcomputingPanel.add(slicesOTSUValue);
		slicesOTSUcomputingPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		slicesOTSUcomputing.setText("0");
		slicesOTSUcomputing.setMinimumSize(new Dimension(100, 10));
		slicesOTSUcomputingPanel.add(slicesOTSUcomputing);
		
		JPanel boxesPercentSurfacePanel = new JPanel();
		boxesPercentSurfacePanel.setLayout(new BoxLayout(boxesPercentSurfacePanel, BoxLayout.X_AXIS));
		boxesPercentSurfacePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		JLabel boxesSurfaceValue = new JLabel("Boxes percent surface to filter:");
		boxesPercentSurfacePanel.add(boxesSurfaceValue);
		boxesPercentSurfacePanel.add(Box.createRigidArea(new Dimension(10, 0)));
		boxesPercentSurfaceToFilter.setText("50");
		boxesPercentSurfaceToFilter.setMinimumSize(new Dimension(100, 10));
		boxesPercentSurfacePanel.add(boxesPercentSurfaceToFilter);
		
		JPanel boxesRegroupementPanel = new JPanel();
		boxesRegroupementPanel.setLayout(new BoxLayout(boxesRegroupementPanel, BoxLayout.X_AXIS));
		boxesRegroupementPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		JLabel boxesRegroupementValue = new JLabel("Boxes regroupement:");
		boxesRegroupementPanel.add(boxesRegroupementValue);
		boxesRegroupementPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		boxesRegroupement.setSelected(true);
		boxesRegroupement.setMinimumSize(new Dimension(100, 10));
		boxesRegroupement.addItemListener(this);
		boxesRegroupementPanel.add(boxesRegroupement);
		
		volumeNucleus.add(thresholdOSTUcomputingPanel);
		volumeNucleus.add(channelToComputeThresholdPanel);
		volumeNucleus.add(slicesOTSUcomputingPanel);
		volumeNucleus.add(boxesPercentSurfacePanel);
		volumeNucleus.add(boxesRegroupementPanel);



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
		
		AutocropConfigDialog.StartListener startListener = new AutocropConfigDialog.StartListener(this);
		buttonOK.addActionListener(startListener);
	}
	
	public String getxCropBoxSize() {
		return xCropBoxSize.getText();
	}
	
	public String getyCropBoxSize() {
		return yCropBoxSize.getText();
	}
	
	public String getzCropBoxSize() {
		return zCropBoxSize.getText();
	}
	
	public String getxCalibration() {
		return xCalibration.getText();
	}
	
	public String getyCalibration() {
		return yCalibration.getText();
	}
	
	public String getzCalibration() {
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
	
	public String getSlicesOTSUcomputing() {
		return slicesOTSUcomputing.getText();
	}
	
	public String getBoxesPercentSurfaceToFilter() {
		return boxesPercentSurfaceToFilter.getText();
	}
	
	public boolean getBoxesRegroupementSelected() {
		return isBoxesRegroupementSelected;
	}
	
	public boolean isCalibSelected() {
		return addCalibBox.isSelected();
	}
	
	
	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getSource() == boxesRegroupement) {
			isBoxesRegroupementSelected = boxesRegroupement.isSelected();
		} else if (e.getSource() == addCalibBox) {
			if (addCalibBox.isSelected()) {
				
				XCalib = new JPanel();
				XCalib.setLayout(new BoxLayout(XCalib, BoxLayout.X_AXIS));
				XCalib.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
				JLabel xBox2 = new JLabel("X:");
				XCalib.add(xBox2);
				XCalib.add(Box.createRigidArea(new Dimension(10, 0)));
				xCalibration.setText("1");
				xCalibration.setMinimumSize(new Dimension(60, 10));
				XCalib.add(xCalibration);
				
				YCalib = new JPanel();
				YCalib.setLayout(new BoxLayout(YCalib, BoxLayout.X_AXIS));
				YCalib.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
				JLabel yBox2 = new JLabel("Y:");
				YCalib.add(yBox2);
				YCalib.add(Box.createRigidArea(new Dimension(10, 0)));
				yCalibration.setText("1");
				yCalibration.setMinimumSize(new Dimension(60, 10));
				YCalib.add(yCalibration);
				
				ZCalib = new JPanel();
				ZCalib.setLayout(new BoxLayout(ZCalib, BoxLayout.X_AXIS));
				ZCalib.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
				JLabel zBox2 = new JLabel("Z:");
				ZCalib.add(zBox2);
				ZCalib.add(Box.createRigidArea(new Dimension(10, 0)));
				zCalibration.setText("1");
				zCalibration.setMinimumSize(new Dimension(60, 10));
				ZCalib.add(zCalibration);
				
				cropBoxPane.add(XCalib);
				cropBoxPane.add(YCalib);
				cropBoxPane.add(ZCalib);
			} else {
				try {
					cropBoxPane.remove(XCalib);
					cropBoxPane.remove(YCalib);
					cropBoxPane.remove(ZCalib);
				} catch (NullPointerException nullPointerException) {
					// Do nothing
				}
			}
		}
		validate();
		repaint();
	}
	
	
	class StartListener implements ActionListener {
		AutocropConfigDialog _autocropDialog;
		
		/**
		 * @param autocropDialog
		 */
		public StartListener(AutocropConfigDialog autocropDialog) {
			_autocropDialog = autocropDialog;
		}
		
		public void actionPerformed(ActionEvent actionEvent) {
			_autocropDialog.setVisible(false);
		}
	}
}