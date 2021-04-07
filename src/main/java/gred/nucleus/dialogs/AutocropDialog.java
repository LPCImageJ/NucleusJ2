package gred.nucleus.dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;


public class AutocropDialog extends JFrame implements ActionListener, ItemListener {
	
	private static final long                 serialVersionUID   = 1L;
	private static final String               INPUT_CHOOSER      = "inputChooser";
	private static final String               OUTPUT_CHOOSER     = "outputChooser";
	private static final String               CONFIG_CHOOSER     = "configChooser";
	private final        JButton              jButtonConfig      = new JButton("Config");
	private final        Container            container;
	private final        JTextField           jInputFileChooser  = new JTextField();
	private final        JTextField           jOutputFileChooser = new JTextField();
	private final        JTextField           jConfigFileChooser = new JTextField();
	private final        JFileChooser         fc                 = new JFileChooser();
	private final        JLabel               defConf            = new JLabel("Default configuration");
	private final        AutocropConfigDialog autocropConfigFileDialog;
	private final        JRadioButton         rdoDefault         = new JRadioButton();
	private final        JRadioButton         rdoAddConfigFile   = new JRadioButton();
	private final        JRadioButton         rdoAddConfigDialog = new JRadioButton();
	private              boolean              start              = false;
	private              JButton              confButton;
	private              int                  configMode         = 0;
	
	
	/** Architecture of the graphical windows */
	public AutocropDialog() {
		JButton jButtonStart = new JButton("Start");
		JButton jButtonQuit  = new JButton("Quit");
		this.setTitle("Autocrop NucleusJ2");
		this.setSize(500, 300);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		autocropConfigFileDialog = new AutocropConfigDialog(this);
		autocropConfigFileDialog.setVisible(false);
		container = getContentPane();
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.1};
		gridBagLayout.rowHeights = new int[]{60, 60, 60, 120};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0};
		gridBagLayout.columnWidths = new int[]{250, 250};
		container.setLayout(gridBagLayout);
		JLabel jLabelInput = new JLabel();
		container.add(jLabelInput,
		              new GridBagConstraints(0,
		                                     0,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(10, 10, 0, 0),
		                                     0,
		                                     0));
		jLabelInput.setText("Input directory:");
		container.add(jInputFileChooser,
		              new GridBagConstraints(0,
		                                     0,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(30, 10, 0, 0),
		                                     0,
		                                     0));
		jInputFileChooser.setPreferredSize(new java.awt.Dimension(300, 20));
		jInputFileChooser.setMinimumSize(new java.awt.Dimension(300, 20));
		JButton sourceButton = new JButton("...");
		container.add(sourceButton,
		              new GridBagConstraints(0,
		                                     0,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(30, 330, 0, 0),
		                                     0,
		                                     0));
		sourceButton.addActionListener(this);
		sourceButton.setName(INPUT_CHOOSER);
		JLabel jLabelOutput = new JLabel();
		container.add(jLabelOutput,
		              new GridBagConstraints(0,
		                                     1,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(10, 10, 0, 0),
		                                     0,
		                                     0));
		jLabelOutput.setText("Output directory:");
		container.add(jOutputFileChooser,
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
		jOutputFileChooser.setPreferredSize(new java.awt.Dimension(300, 20));
		jOutputFileChooser.setMinimumSize(new java.awt.Dimension(300, 20));
		JButton destButton = new JButton("...");
		container.add(destButton,
		              new GridBagConstraints(0,
		                                     1,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(30, 330, 0, 0),
		                                     0,
		                                     0));
		destButton.addActionListener(this);
		destButton.setName(OUTPUT_CHOOSER);
		JLabel jLabelConfig = new JLabel();
		container.add(jLabelConfig,
		              new GridBagConstraints(0,
		                                     2,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(10, 10, 0, 0),
		                                     0,
		                                     0));
		jLabelConfig.setText("Config file (optional):");
//        container.add(addConfigBox, new GridBagConstraints(0, 2, 0, 0, 0.0, 0.0,
//                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
//                new Insets(10, 200, 0, 0), 0, 0));
//        addConfigBox.addItemListener(this);
		
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(rdoDefault);
		rdoDefault.setSelected(true);
		rdoDefault.addItemListener(this);
		itemStateChanged(new ItemEvent(rdoDefault, 0, rdoDefault, ItemEvent.SELECTED));
		container.add(rdoDefault,
		              new GridBagConstraints(0,
		                                     2,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(10, 200, 0, 0),
		                                     0,
		                                     0));
		buttonGroup.add(rdoAddConfigDialog);
		rdoAddConfigDialog.addItemListener(this);
		container.add(rdoAddConfigDialog,
		              new GridBagConstraints(0,
		                                     2,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(10, 230, 0, 0),
		                                     0,
		                                     0));
		buttonGroup.add(rdoAddConfigFile);
		rdoAddConfigFile.addItemListener(this);
		container.add(rdoAddConfigFile,
		              new GridBagConstraints(0,
		                                     2,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(10, 260, 0, 0),
		                                     0,
		                                     0));
		container.add(jButtonStart,
		              new GridBagConstraints(0,
		                                     3,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(40, 80, 0, 0),
		                                     0,
		                                     0));
		jButtonStart.setPreferredSize(new java.awt.Dimension(60, 21));
		container.add(jButtonQuit,
		              new GridBagConstraints(0,
		                                     3,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(40, 10, 0, 0),
		                                     0,
		                                     0));
		jButtonQuit.setPreferredSize(new java.awt.Dimension(60, 21));
		this.setVisible(true);
		AutocropDialog.QuitListener quitListener = new AutocropDialog.QuitListener(this);
		jButtonQuit.addActionListener(quitListener);
		AutocropDialog.StartListener startListener = new AutocropDialog.StartListener(this);
		jButtonStart.addActionListener(startListener);
		AutocropDialog.ConfigListener configListener = new AutocropDialog.ConfigListener(this);
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
	
	
	//    public boolean isConfigBoxSelected() { return addConfigBox.isSelected(); }
	public int getConfigMode() {
		return configMode;
	}
	
	
	public AutocropConfigDialog getAutocropConfigFileDialog() {
		return autocropConfigFileDialog;
	}
	
	
	public void actionPerformed(ActionEvent e) {
		switch (((JButton) e.getSource()).getName()) {
			case INPUT_CHOOSER:
				fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				break;
			case OUTPUT_CHOOSER:
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				break;
			case CONFIG_CHOOSER:
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				break;
		}
		fc.setAcceptAllFileFilterUsed(false);
		
		if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			switch (((JButton) e.getSource()).getName()) {
				case INPUT_CHOOSER:
					File selectedInput = fc.getSelectedFile();
					jInputFileChooser.setText(selectedInput.getPath());
					break;
				case OUTPUT_CHOOSER:
					File selectedOutput = fc.getSelectedFile();
					jOutputFileChooser.setText(selectedOutput.getPath());
					break;
				case CONFIG_CHOOSER:
					File selectedConfig = fc.getSelectedFile();
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
			if (autocropConfigFileDialog.isVisible()) autocropConfigFileDialog.setVisible(false);
		} else if (configMode == 2) {
			container.remove(jConfigFileChooser);
			container.remove(confButton);
		}
		Object source = e.getSource();
		if (source == rdoDefault) {
			container.add(defConf,
			              new GridBagConstraints(0,
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
			configMode = 0;
		} else if (source == rdoAddConfigDialog) {
			container.add(jButtonConfig,
			              new GridBagConstraints(0,
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
			configMode = 1;
		} else if (source == rdoAddConfigFile) {
			container.add(jConfigFileChooser,
			              new GridBagConstraints(0,
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
			container.add(confButton,
			              new GridBagConstraints(0,
			                                     2,
			                                     0,
			                                     0,
			                                     0.0,
			                                     0.0,
			                                     GridBagConstraints.NORTHWEST,
			                                     GridBagConstraints.NONE,
			                                     new Insets(40, 330, 0, 0),
			                                     0,
			                                     0));
			confButton.addActionListener(this);
			confButton.setName(CONFIG_CHOOSER);
			configMode = 2;
		}

        /*if(addConfigBox.isSelected()){
            container.add(jConfigFileChooser, new GridBagConstraints(0, 2, 0, 0, 0.0, 0.0,
                    GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                    new Insets(40, 10, 0, 0), 0, 0));
            jConfigFileChooser.setPreferredSize(new java.awt.Dimension(300, 20));
            jConfigFileChooser.setMinimumSize(new java.awt.Dimension(300, 20));

            confButton = new JButton("...");
            container.add(confButton, new GridBagConstraints(0, 2, 0, 0, 0.0, 0.0,
                    GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                    new Insets(40, 330, 0, 0), 0, 0));
            confButton.addActionListener(this);
            confButton.setName(configChooserName);
        } else {
            container.remove(jConfigFileChooser);
            container.remove(confButton);
        }*/
		validate();
		repaint();
	}
	
	
	/** Classes listener to interact with the several elements of the window */
	
	class StartListener implements ActionListener {
		final AutocropDialog autocropDialog;
		
		
		/** @param autocropDialog  */
		public StartListener(AutocropDialog autocropDialog) {
			this.autocropDialog = autocropDialog;
		}
		
		
		public void actionPerformed(ActionEvent actionEvent) {
			start = true;
			autocropDialog.dispose();
			autocropConfigFileDialog.dispose();
		}
		
	}
	
	class QuitListener implements ActionListener {
		final AutocropDialog autocropDialog;
		
		
		/** @param autocropDialog  */
		public QuitListener(AutocropDialog autocropDialog) {
			this.autocropDialog = autocropDialog;
		}
		
		
		public void actionPerformed(ActionEvent actionEvent) {
			autocropDialog.dispose();
			autocropConfigFileDialog.dispose();
		}
		
	}
	
	class ConfigListener implements ActionListener {
		final AutocropDialog autocropDialog;
		
		
		/** @param autocropDialog  */
		public ConfigListener(AutocropDialog autocropDialog) {
			this.autocropDialog = autocropDialog;
		}
		
		
		public void actionPerformed(ActionEvent actionEvent) {
			autocropConfigFileDialog.setVisible(true);
		}
		
	}
	
}
