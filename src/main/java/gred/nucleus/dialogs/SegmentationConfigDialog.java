package gred.nucleus.dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class SegmentationConfigDialog extends JFrame implements ItemListener {
	private final JTextField minVolume      = new JTextField();
	private final JTextField maxVolume      = new JTextField();
	private final JCheckBox  giftWrapping   = new JCheckBox();
	private final JTextField xCalibration   = new JTextField();
	private final JTextField yCalibration   = new JTextField();
	private final JTextField zCalibration   = new JTextField();
	private final JCheckBox  addCalibBox    = new JCheckBox();
	private final Container  _container;
	private final JButton    buttonOK       = new JButton("Done");
	private final JPanel     volumePane;
	private final SegmentationDialog caller;
	private       Boolean    isGiftWrapping = true;
	private       JPanel     XCalib;
	private       JPanel     YCalib;
	private       JPanel     ZCalib;
	
	public SegmentationConfigDialog(SegmentationDialog caller) {
		this.caller = caller;
		this.setTitle("Segmentation NucleusJ2");
		this.setSize(300, 340);
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
		
		
		volumePane = new JPanel();
		volumePane.setLayout(new BoxLayout(volumePane, BoxLayout.Y_AXIS));
		volumePane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		volumePane.setAlignmentX(0);
		
		JPanel minVolumePane = new JPanel();
		minVolumePane.setLayout(new BoxLayout(minVolumePane, BoxLayout.X_AXIS));
		minVolumePane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		JLabel xBox = new JLabel("Min:");
		minVolumePane.add(xBox);
		minVolumePane.add(Box.createRigidArea(new Dimension(10, 0)));
		minVolume.setText("1");
		minVolume.setMinimumSize(new Dimension(60, 10));
		minVolumePane.add(minVolume);
		
		JPanel maxVolumePane = new JPanel();
		maxVolumePane.setLayout(new BoxLayout(maxVolumePane, BoxLayout.X_AXIS));
		maxVolumePane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		JLabel yBox = new JLabel("Max:");
		maxVolumePane.add(yBox);
		maxVolumePane.add(Box.createRigidArea(new Dimension(10, 0)));
		maxVolume.setText("3000000");
		maxVolume.setMinimumSize(new Dimension(60, 10));
		maxVolumePane.add(maxVolume);
		
		JPanel giftWrappingPane = new JPanel();
		giftWrappingPane.setLayout(new BoxLayout(giftWrappingPane, BoxLayout.X_AXIS));
		giftWrappingPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		JLabel zBox = new JLabel("Gift wrapping:");
		giftWrappingPane.add(zBox);
		giftWrappingPane.add(Box.createRigidArea(new Dimension(10, 0)));
		giftWrapping.setSelected(true);
		giftWrapping.setMinimumSize(new Dimension(100, 10));
		giftWrapping.addItemListener(this);
		giftWrappingPane.add(giftWrapping);
		
		JLabel volumeLabel = new JLabel("Volume:");
		volumeLabel.setAlignmentX(0);
		volumePane.add(volumeLabel);
		volumePane.add(minVolumePane);
		volumePane.add(maxVolumePane);
		volumePane.add(giftWrappingPane);
		volumePane.add(Box.createRigidArea(new Dimension(0, 20)));


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
		volumePane.add(calibPanel);


        /*/\*\
        -------------------------- Validation Button -----------------------------------
        \*\/*/
		
		
		buttonOK.setPreferredSize(new java.awt.Dimension(80, 21));
		volumePane.add(Box.createRigidArea(new Dimension(0, 10)));
		volumePane.add(buttonOK);
		
		_container.add(volumePane, new GridBagConstraints(0,
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
		
		SegmentationConfigDialog.StartListener startListener = new SegmentationConfigDialog.StartListener(this);
		buttonOK.addActionListener(startListener);
	}
	
	public String getMinVolume() {
		return minVolume.getText();
	}
	
	public String getMaxVolume() {
		return maxVolume.getText();
	}
	
	public boolean getGiftWrapping() {
		return giftWrapping.isSelected();
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
	
	public boolean isCalibSelected() {
		return addCalibBox.isSelected();
	}
	
	
	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getSource() == giftWrapping) {
			isGiftWrapping = giftWrapping.isSelected();
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
				
				volumePane.remove(buttonOK);
				volumePane.add(XCalib);
				volumePane.add(YCalib);
				volumePane.add(ZCalib);
				volumePane.add(buttonOK);
			} else {
				try {
					volumePane.remove(buttonOK);
					volumePane.remove(XCalib);
					volumePane.remove(YCalib);
					volumePane.remove(ZCalib);
					volumePane.add(buttonOK);
				} catch (NullPointerException nullPointerException) {
					// Do nothing
				}
			}
		}
		validate();
		repaint();
	}
	
	
	class StartListener implements ActionListener {
		SegmentationConfigDialog _segmentationDialog;
		
		/** @param segmentationDialog */
		public StartListener(SegmentationConfigDialog segmentationDialog) {
			_segmentationDialog = segmentationDialog;
		}
		
		public void actionPerformed(ActionEvent actionEvent) {
			_segmentationDialog.setVisible(false);
		}
	}
}